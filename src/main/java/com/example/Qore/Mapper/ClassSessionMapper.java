package com.example.Qore.Mapper;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.model.*;
import com.example.Qore.model.Enum.EstadoSession;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClassSessionMapper {

    public ClassSessionDTO toDTO(ClassSession session) {

        Set<Long> clientIds = session.getClients() != null
                ? session.getClients().stream()
                .map(Client::getId)
                .collect(Collectors.toSet())
                : Set.of();

        return ClassSessionDTO.builder()
                .id(session.getId())
                .name(session.getName())
                .disciplineId(session.getDiscipline().getId())
                .instructorId(session.getInstructor().getId())
                .roomId(session.getRoom().getId())
                .clientIds(clientIds)
                .capacity(session.getCapacity())
                .startDate(session.getStartDate())
                .startTime(session.getStartTime())
                .estado(session.getEstado().name())
                .endTime(session.getEndTime())
                .repeat(session.isRepeat())
                .repeatUntil(session.getRepeatUntil())
                .repeatDays(session.getRepeatDays())
                .repeatInterval(session.getRepeatInterval())
                .comentario(session.getComentario())
                .build();
    }

    public ClassSession toEntity(ClassSessionDTO dto, Discipline discipline, Instructor instructor, Room room, Set<Client> clients) {
        return ClassSession.builder()
                .name(dto.getName())
                .discipline(discipline)
                .instructor(instructor)
                .room(room)
                .capacity(dto.getCapacity())
                .estado(dto.getEstado() == null || dto.getEstado().isBlank()
                        ? EstadoSession.PENDIENTE
                        : EstadoSession.valueOf(dto.getEstado().toUpperCase()))
                .startDate(dto.getStartDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .repeat(dto.isRepeat())
                .repeatUntil(dto.getRepeatUntil())
                .repeatDays(dto.getRepeatDays())
                .repeatInterval(dto.getRepeatInterval())
                .build();
    }
}
