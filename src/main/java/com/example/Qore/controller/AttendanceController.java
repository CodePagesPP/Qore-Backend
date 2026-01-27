package com.example.Qore.controller;

import com.example.Qore.DTO.AttendanceDTO;
import com.example.Qore.model.Attendance;
import com.example.Qore.model.Enum.AttendanceStatus;
import com.example.Qore.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;


    @PostMapping("/mark")
    public ResponseEntity<Map<String, String>> markAttendance(
            @RequestParam Long classId,
            @RequestParam Long clientId,
            @RequestParam String status) {

        attendanceService.markAttendance(classId, clientId, status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Asistencia registrada correctamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<AttendanceDTO>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(attendanceService.getByClassDTO(classId));
    }


}

