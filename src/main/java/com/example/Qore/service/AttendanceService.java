package com.example.Qore.service;

import com.example.Qore.DTO.AttendanceDTO;
import com.example.Qore.model.Attendance;
import com.example.Qore.model.Enum.AttendanceStatus;

import java.util.List;

public interface AttendanceService {
    List<AttendanceDTO> getByClassDTO(Long classId);
    void markAttendance(Long classId, Long clientId, String statusStr);
}
