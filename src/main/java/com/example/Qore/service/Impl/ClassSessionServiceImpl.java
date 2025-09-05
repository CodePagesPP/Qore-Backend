package com.example.Qore.service.Impl;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;
import com.example.Qore.Mapper.ClassSessionMapper;
import com.example.Qore.model.*;
import com.example.Qore.repository.*;
import com.example.Qore.service.ClassSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public ClassSessionDTO create(ClassSessionDTO dto) {
        Discipline discipline = disciplineRepository.findById(dto.getDisciplineId())
                .orElseThrow(() -> new RuntimeException("Discipline not found"));
        Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!instructor.getDiscipline().contains(discipline)) {
            throw new RuntimeException("Instructor does not belong to the given discipline");
        }

        // no se cargan clientes aquí
        Set<Client> clients = new HashSet<>(); // vacío

        ClassSession session = mapper.toEntity(dto, discipline, instructor, room, clients);
        return mapper.toDTO(repository.save(session));
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

        if (!instructor.getDiscipline().contains(discipline)) {
            throw new RuntimeException("Instructor does not belong to the given discipline");
        }

        existing.setName(dto.getName());
        existing.setDiscipline(discipline);
        existing.setInstructor(instructor);
        existing.setRoom(room);
        existing.setCapacity(dto.getCapacity());
        existing.setStartDate(dto.getStartDate());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setRepeat(dto.isRepeat());

        return mapper.toDTO(repository.save(existing));
    }


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

        // Validación de plan
        if (client.getPlan() == null) {
            throw new IllegalStateException("Client has no active plan");
        }

        LocalDate today = LocalDate.now();

        // Si el plan venció o ya no tiene clases disponibles → requiere nuevo pack
        boolean planVencido = client.getSubscriptionEnd() == null || client.getSubscriptionEnd().isBefore(today);
        long clasesTomadas = repository.findByClients_Id(clientId).stream()
                .filter(c -> !c.getStartDate().isBefore(client.getSubscriptionStart())
                        && !c.getStartDate().isAfter(client.getSubscriptionEnd()))
                .count();
        boolean clasesAgotadas = clasesTomadas >= client.getPlan().getSessions();

        if (planVencido || clasesAgotadas) {
            throw new IllegalStateException("Client must purchase a new pack before enrolling in more classes");
        }

        // Activar vigencia desde la primera clase si aún no existe
        if (client.getSubscriptionStart() == null) {
            LocalDate start = classSession.getStartDate();
            LocalDate end = start.plusDays(client.getPlan().getDuration());
            client.setSubscriptionStart(start);
            client.setSubscriptionEnd(end);
            clientRepository.save(client);
        }

        //  Finalmente agregar al cliente
        classSession.getClients().add(client);
        repository.save(classSession);
    }




    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
