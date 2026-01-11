package com.example.Qore.service.Impl;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;
import com.example.Qore.DTO.ClientClassDTO;
import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.Mapper.ClassSessionMapper;
import com.example.Qore.model.*;
import com.example.Qore.model.Enum.EstadoSession;
import com.example.Qore.repository.*;
import com.example.Qore.service.ClassSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassSessionServiceImpl implements ClassSessionService {

    private final ClassSessionRepository repository;
    private final DisciplineRepository disciplineRepository;
    private final InstructorRepository instructorRepository;
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;
    private final ClassSessionMapper mapper;
    private final EmailService emailService;
    private final NotificationServiceImpl notificationService;

    @Override
    public List<ClassSessionDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ClassSessionDTO> getClassesByDateRange(String startStr, String endStr) {
        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);

        return repository.findByStartDateBetween(start, end)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClassSessionDTO getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("ClassSession not found"));
    }

    public List<ClientResponseDTO> getClientsByClass(Long classId) {
        List<Client> clients = repository.findClientsByClassId(classId);
        return clients.stream()
                .map(c -> ClientResponseDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .email(c.getEmail())
                        .phoneNumber(c.getPhoneNumber())
                        .build())
                .toList();
    }


    @Override
    public List<ClassSessionDTO> create(ClassSessionDTO dto) {
        Discipline discipline = disciplineRepository.findById(dto.getDisciplineId())
                .orElseThrow(() -> new RuntimeException("Discipline not found"));
        Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Set<Client> clients = new HashSet<>();
        List<ClassSessionDTO> createdSessions = new ArrayList<>();

        // Primera sesión
        ClassSession session = mapper.toEntity(dto, discipline, instructor, room, clients);
        repository.save(session);
        createdSessions.add(mapper.toDTO(session));

        // Crear sesiones recurrentes si aplica
        if (dto.isRepeat() && dto.getRepeatUntil() != null && dto.getRepeatDays() != null && !dto.getRepeatDays().isEmpty()) {
            int interval = (dto.getRepeatInterval() == null || dto.getRepeatInterval() < 1) ? 1 : dto.getRepeatInterval();

            LocalDate start = dto.getStartDate();
            LocalDate end = dto.getRepeatUntil();

            // Para cada día seleccionado (por ejemplo Lunes y Miércoles)
            for (DayOfWeek repeatDay : dto.getRepeatDays()) {
                LocalDate nextDate = start;

                // Mueve la fecha inicial al siguiente día válido
                while (nextDate.getDayOfWeek() != repeatDay) {
                    nextDate = nextDate.plusDays(1);
                }

                // Crear todas las sesiones de ese día
                while (!nextDate.isAfter(end)) {
                    // Evita duplicar la primera si ya la creaste antes
                    if (!nextDate.equals(dto.getStartDate())) {
                        ClassSession copy = mapper.toEntity(dto, discipline, instructor, room, clients);
                        copy.setStartDate(nextDate);
                        copy.setRepeat(true);
                        copy.setRepeatDays(dto.getRepeatDays());
                        copy.setRepeatInterval(dto.getRepeatInterval());

                        repository.save(copy);
                        createdSessions.add(mapper.toDTO(copy));
                    }
                    nextDate = nextDate.plusWeeks(interval);
                }
            }
        }

        return createdSessions;
    }


    @Override
    public List<ClassSessionDTO> getClassesByInstructor(Long instructorId, LocalDate start, LocalDate end) {
        List<ClassSession> sessions = repository.findByInstructorAndDateRange(instructorId, start, end);

        return sessions.stream().map(s -> {
            // Mapeo manual rápido (o usa tu mapper si lo actualizas)
            return ClassSessionDTO.builder()
                    .id(s.getId())
                    .name(s.getName())
                    .disciplineId(s.getDiscipline().getId())
                    .instructorId(s.getInstructor().getId())
                    .roomId(s.getRoom().getId())
                    .capacity(s.getCapacity())
                    .estado(s.getEstado().name())
                    .startDate(s.getStartDate())
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .comentario(s.getComentario())
                    .currentCount(s.getClients().size())
                    .build();
        }).toList();
    }

    @Override
    public List<ClassSessionDTO> getPendingClassesToday(Long instructorId) {
        LocalDate today = LocalDate.now();

        List<ClassSession> sessions = repository
                .findByInstructorIdAndStartDateAndEstado(instructorId, today, EstadoSession.PENDIENTE);

        return sessions.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ClassSessionDTO update(Long id, ClassSessionUpdateDTO dto) {
        ClassSession existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassSession not found"));

        Discipline discipline = disciplineRepository.findById(dto.getDisciplineId())
                .orElseThrow(() -> new RuntimeException("Discipline not found"));
        Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 🧩 Guardar los valores anteriores antes de actualizar
        ClassSession oldData = ClassSession.builder()
                .name(existing.getName())
                .discipline(existing.getDiscipline())
                .instructor(existing.getInstructor())
                .room(existing.getRoom())
                .capacity(existing.getCapacity())
                .startDate(existing.getStartDate())
                .startTime(existing.getStartTime())
                .endTime(existing.getEndTime())
                .repeat(existing.isRepeat())
                .estado(existing.getEstado())
                .build();

        // ✅ Actualizar con los nuevos datos
        existing.setName(dto.getName());
        existing.setDiscipline(discipline);
        existing.setInstructor(instructor);
        existing.setRoom(room);
        existing.setCapacity(dto.getCapacity());
        existing.setStartDate(dto.getStartDate());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setRepeat(dto.isRepeat());

        if (dto.getEstado() != null) {
            existing.setEstado(dto.getEstado());
        }

        ClassSession updated = repository.save(existing);

        // 📨 Enviar correo de notificación si hubo cambios
        sendUpdateNotification(oldData, updated);

        return mapper.toDTO(updated);
    }

    private void sendUpdateNotification(ClassSession oldData, ClassSession updated) {
        if (updated.getClients() == null || updated.getClients().isEmpty()) return;

        String subject = "Actualización en tu clase: " + updated.getName();

        // 🧠 Construir tabla comparativa de cambios
        StringBuilder changesTable = new StringBuilder();
        changesTable.append("<table style='border-collapse:collapse;width:100%;margin-top:10px;'>")
                .append("<tr style='background-color:#5C6BC0;color:white;text-align:left;'>")
                .append("<th style='padding:8px;'>Campo</th>")
                .append("<th style='padding:8px;'>Anterior</th>")
                .append("<th style='padding:8px;'>Nuevo</th>")
                .append("</tr>");

        addChange(changesTable, "Nombre", oldData.getName(), updated.getName());
        addChange(changesTable, "Disciplina", oldData.getDiscipline().getName(), updated.getDiscipline().getName());
        addChange(changesTable, "Instructor", oldData.getInstructor().getName(), updated.getInstructor().getName());
        addChange(changesTable, "Sala", oldData.getRoom().getName(), updated.getRoom().getName());
        addChange(changesTable, "Capacidad", String.valueOf(oldData.getCapacity()), String.valueOf(updated.getCapacity()));
        addChange(changesTable, "Fecha", String.valueOf(oldData.getStartDate()), String.valueOf(updated.getStartDate()));
        addChange(changesTable, "Hora inicio", String.valueOf(oldData.getStartTime()), String.valueOf(updated.getStartTime()));
        addChange(changesTable, "Hora fin", String.valueOf(oldData.getEndTime()), String.valueOf(updated.getEndTime()));
        addChange(changesTable, "Estado", String.valueOf(oldData.getEstado()), String.valueOf(updated.getEstado()));

        changesTable.append("</table>");

        String htmlTemplate = """
        <div style="font-family: Arial, sans-serif; color: #333;">
            <h2 style="color: #5C6BC0;">Tu clase ha sido actualizada 🧘‍♀️</h2>
            <p>Hola 👋, queremos informarte que la clase <strong>%s</strong> ha sido modificada.</p>
            <p><b>Resumen de cambios:</b></p>
            %s
            <p style="margin-top:20px;">Por favor revisa los cambios en tu panel de usuario.</p>
            <hr style="margin-top:25px;">
            <p style="font-size:0.9em;color:#777;">Atentamente,<br>Equipo Qore</p>
        </div>
    """;

        String htmlBody = String.format(htmlTemplate, updated.getName(), changesTable);

        // Enviar correo a todos los clientes inscritos
        updated.getClients().forEach(client -> {
            if (client.getEmail() != null && !client.getEmail().isBlank()) {
                emailService.sendHtmlEmail(client.getEmail(), subject, htmlBody);
            }
        });
    }

    private void addChange(StringBuilder sb, String field, String oldVal, String newVal) {
        if (oldVal == null) oldVal = "-";
        if (newVal == null) newVal = "-";

        // Si el valor cambió, lo resaltamos visualmente
        boolean changed = !oldVal.equals(newVal);
        String bg = changed ? "background-color:#f1f1ff;" : "background-color:#fafafa;";

        sb.append(String.format(
                "<tr style='%s'><td style='padding:8px;border:1px solid #ddd;'>%s</td>" +
                        "<td style='padding:8px;border:1px solid #ddd;'>%s</td>" +
                        "<td style='padding:8px;border:1px solid #ddd;'>%s</td></tr>",
                bg, field, oldVal, newVal
        ));
    }



    @Override
    public void addClientToClass(Long classId, Long clientId) {
        ClassSession classSession = repository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        if (classSession.getClients().size() >= classSession.getCapacity()) {
            throw new IllegalStateException("Clase está llena");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        if (classSession.getClients().contains(client)) {
            throw new IllegalStateException("El cliente ya está inscrito en esta clase");
        }


        if (client.getPlan() == null) {
            throw new IllegalStateException("Cliente no tiene un plana activo");
        }


        ensureSubscriptionDates(client, classSession.getStartDate());


        if (isPlanExpired(client)) {
            throw new IllegalStateException("Plan del cliente expiró.");
        }

        if (isClassesExhausted(client)) {
            throw new IllegalStateException("El cliente usó todas las clases de su plan.");
        }


        classSession.getClients().add(client);
        repository.save(classSession);
    }


    @Override
    public void removeClientFromClass(Long classId, Long clientId) {
        ClassSession classSession = repository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        if (!classSession.getClients().contains(client)) {
            throw new IllegalStateException("El cliente no está inscrito en esta clase");
        }

        classSession.getClients().remove(client);
        repository.save(classSession);
    }


    private void ensureSubscriptionDates(Client client, LocalDate firstClassDate) {
        if (client.getSubscriptionStart() == null) {
            // Primera clase => inicializa sus fechas
            client.setSubscriptionStart(firstClassDate);

            // Asumimos que cada plan tiene una duración en días
            int durationDays = client.getPlan().getDuration(); // este campo deberías tenerlo en tu entidad Plan
            client.setSubscriptionEnd(firstClassDate.plusDays(durationDays));

            clientRepository.save(client);
        }
    }

    private boolean isPlanExpired(Client client) {
        return client.getSubscriptionEnd() != null &&
                client.getSubscriptionEnd().isBefore(LocalDate.now());
    }

    private boolean isClassesExhausted(Client client) {
        long clasesTomadas = countClassesTaken(client);
        return clasesTomadas >= client.getPlan().getSessions();
    }

    public long countClassesTaken(Client client) {
        if (client.getSubscriptionStart() == null || client.getSubscriptionEnd() == null) {
            return 0;
        }
        return repository.countClassesByClientAndPeriod(
                client.getId(),
                client.getSubscriptionStart(),
                client.getSubscriptionEnd()
        );
    }

    @Override
    public List<ClassSessionDTO> getClassesForClient(Long clientId, LocalDate start, LocalDate end) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));


        List<ClassSession> sessions = repository.findByDisciplinesAndDateRange(
                client.getDisciplines(),
                start,
                end
        );

        return sessions.stream().map(s -> {
            ClassSessionDTO dto = new ClassSessionDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setCapacity(s.getCapacity());
            dto.setStartDate(s.getStartDate());
            dto.setStartTime(s.getStartTime());
            dto.setEndTime(s.getEndTime());
            dto.setEstado(s.getEstado().name());
            dto.setDisciplineId(s.getDiscipline().getId());
            dto.setInstructorId(s.getInstructor().getId());
            dto.setRoomId(s.getRoom().getId());


            dto.setClientIds(
                    s.getClients().stream().map(Client::getId).collect(Collectors.toSet())
            );


            dto.setJoined(s.getClients().contains(client));

            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ClassSession joinClassClient(Long classId, Long clientId) {
        ClassSession session = repository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (session.getStartDate().isBefore(today)
                || (session.getStartDate().isEqual(today) && session.getEndTime().isBefore(now))) {
            throw new RuntimeException("No puedes unirte a una clase pasada.");
        }

        if (session.getClients().size() >= session.getCapacity()) {
            throw new RuntimeException("La clase ya alcanzó su capacidad máxima");
        }

        if (session.getClients().contains(client)) {
            throw new RuntimeException("El cliente ya está inscrito en esta clase");
        }

        if (client.getPlan() == null) {
            throw new RuntimeException("El cliente no cuenta con un plan activo");
        }

        if (client.getSubscriptionStart() == null || client.getSubscriptionEnd() == null) {
            LocalDate startDate = session.getStartDate();
            int durationDays = client.getPlan().getDuration();
            LocalDate endDate = startDate.plusDays(durationDays);
            client.setSubscriptionStart(startDate);
            client.setSubscriptionEnd(endDate);
            clientRepository.save(client);
        }

        if (isPlanExpired(client)) {
            throw new RuntimeException("Tu plan ha vencido. Por favor renueva para continuar asistiendo a clases.");
        }

        if (isClassesExhausted(client)) {
            throw new RuntimeException("Has utilizado todas las clases disponibles en tu plan.");
        }

        session.getClients().add(client);
        ClassSession saved = repository.save(session);


        notificationService.sendJoinNotification(saved, client);

        return saved;
    }




    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }



    public long getCurrentWeekClasses() {
        LocalDate now = LocalDate.now();

        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

        return repository.countClassesBetween(startOfWeek, endOfWeek);
    }

    @Override
    public List<ClientClassDTO> getClientClasses(Long clientId) {
        List<ClassSession> sessions = repository.findAllByClientId(clientId);

        return sessions.stream().map(cs -> ClientClassDTO.builder()
                .id(cs.getId())
                .name(cs.getName())
                .room(cs.getRoom().getName())
                .instructorName(cs.getInstructor().getName())
                .startDate(cs.getStartDate())
                .startTime(cs.getStartTime())
                .endTime(cs.getEndTime())
                .build()).toList();
    }
}
