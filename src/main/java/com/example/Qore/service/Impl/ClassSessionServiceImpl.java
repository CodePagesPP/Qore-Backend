package com.example.Qore.service.Impl;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;
import com.example.Qore.DTO.ClientClassDTO;
import com.example.Qore.Mapper.ClassSessionMapper;
import com.example.Qore.model.*;
import com.example.Qore.model.Enum.EstadoSession;
import com.example.Qore.repository.*;
import com.example.Qore.service.ClassSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    @Override
    public List<ClassSessionDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ClassSessionDTO getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("ClassSession not found"));
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
        if (dto.isRepeat() && dto.getRepeatUntil() != null && dto.getRepeatDay() != null) {
            int interval = (dto.getRepeatInterval() == null || dto.getRepeatInterval() < 1) ? 1 : dto.getRepeatInterval();

            // Ajustar la primera fecha al día de la semana correcto
            LocalDate nextDate = dto.getStartDate();
            while (nextDate.getDayOfWeek() != dto.getRepeatDay()) {
                nextDate = nextDate.plusDays(1);
            }

            // Crear todas las sesiones recurrentes
            nextDate = nextDate.plusWeeks(interval); // la primera ya está creada
            while (!nextDate.isAfter(dto.getRepeatUntil())) {
                ClassSession copy = mapper.toEntity(dto, discipline, instructor, room, clients);
                copy.setStartDate(nextDate);
                copy.setRepeat(true);
                copy.setRepeatDay(dto.getRepeatDay());
                copy.setRepeatInterval(dto.getRepeatInterval());

                repository.save(copy);
                createdSessions.add(mapper.toDTO(copy));

                nextDate = nextDate.plusWeeks(interval);
            }
        }

        return createdSessions;
    }


    @Override
    public List<ClassSessionDTO> getClassesByInstructor(Long instructorId) {
        List<ClassSession> sessions = repository.findByInstructorIdOrderByStartDateAscStartTimeAsc(instructorId);
        return sessions.stream()
                .map(mapper::toDTO)
                .toList();
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
        return mapper.toDTO(repository.save(existing));
    }

    @Override
    public void addClientToClass(Long classId, Long clientId) {
        ClassSession classSession = repository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        if (classSession.getClients().size() >= classSession.getCapacity()) {
            throw new IllegalStateException("Class is full");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (classSession.getClients().contains(client)) {
            throw new IllegalStateException("Client is already enrolled in this class");
        }


        if (client.getPlan() == null) {
            throw new IllegalStateException("Client has no active plan");
        }


        ensureSubscriptionDates(client, classSession.getStartDate());


        if (isPlanExpired(client)) {
            throw new IllegalStateException("Your plan has expired. Please renew your plan.");
        }

        if (isClassesExhausted(client)) {
            throw new IllegalStateException("You have already used all your classes.");
        }


        classSession.getClients().add(client);
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


    public List<ClassSessionDTO> getClassesForClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<ClassSession> sessions = repository.findByDisciplineIn(client.getDisciplines());

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
            dto.setJoined(s.getClients().contains(client));
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public ClassSession joinClassClient(Long classId, Long clientId) {
        ClassSession session = repository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        //  1. Validar capacidad
        if (session.getClients().size() >= session.getCapacity()) {
            throw new RuntimeException("La clase ya alcanzó su capacidad máxima");
        }

        //  2. Validar que no esté ya inscrito
        if (session.getClients().contains(client)) {
            throw new RuntimeException("El cliente ya está inscrito en esta clase");
        }

        //  3. Validar que tenga plan
        if (client.getPlan() == null) {
            throw new RuntimeException("El cliente no cuenta con un plan activo");
        }

        //  4. Si no tiene fechas de suscripción (primera clase), inicializarlas
        if (client.getSubscriptionStart() == null || client.getSubscriptionEnd() == null) {
            LocalDate startDate = session.getStartDate();
            int durationDays = client.getPlan().getDuration(); // ya lo tienes en tu entidad Plan
            LocalDate endDate = startDate.plusDays(durationDays);

            client.setSubscriptionStart(startDate);
            client.setSubscriptionEnd(endDate);
            clientRepository.save(client);
        }

        //  5. Validar vencimiento de plan
        if (isPlanExpired(client)) {
            throw new RuntimeException("Tu plan ha vencido. Por favor renueva para continuar asistiendo a clases.");
        }

        // 6. Validar clases restantes
        if (isClassesExhausted(client)) {
            throw new RuntimeException("Has utilizado todas las clases disponibles en tu plan.");
        }

        //  7. Agregar cliente a la clase
        session.getClients().add(client);
        return repository.save(session);
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
