package com.example.Qore.service.Impl;

import com.example.Qore.DTO.AttendanceDTO;
import com.example.Qore.model.Attendance;
import com.example.Qore.model.ClassSession;
import com.example.Qore.model.Client;
import com.example.Qore.model.Enum.AttendanceStatus;
import com.example.Qore.repository.AttendanceRepository;
import com.example.Qore.repository.ClassSessionRepository;
import com.example.Qore.repository.ClientRepository;
import com.example.Qore.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ClassSessionRepository classSessionRepository;
    private final ClientRepository clientRepository;

    public List<AttendanceDTO> getByClassDTO(Long classId) {
        List<Attendance> list = attendanceRepository.findByClassSessionId(classId);
        return list.stream()
                .map(a -> new AttendanceDTO(
                        a.getClient().getId(),
                        a.getClient().getName(),
                        a.getClient().getEmail(),
                        a.getStatus().name()
                ))
                .toList();
    }


    public void markAttendance(Long classId, Long clientId, AttendanceStatus status) {
        ClassSession session = classSessionRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        LocalDate today = LocalDate.now();

        // usa el método del repo para buscar si ya existe
        Optional<Attendance> existing = attendanceRepository
                .findByClassSessionIdAndClientIdAndDate(classId, clientId, today);

        Attendance attendance = existing.orElseGet(Attendance::new);

        attendance.setClassSession(session);
        attendance.setClient(client);
        attendance.setDate(today);
        attendance.setStatus(status);

        attendanceRepository.save(attendance);
    }
}
