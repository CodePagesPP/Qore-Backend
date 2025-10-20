package com.example.Qore.service;

import com.example.Qore.DTO.InstructorRegisterDTO;
import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.DTO.InstructorStatsDTO;
import com.example.Qore.DTO.InstructorUpdateDTO;
import com.example.Qore.model.ClassSession;

import java.util.List;

public interface InstructorService {
    InstructorResponseDTO registerInstructor(InstructorRegisterDTO instructor);
    List<InstructorResponseDTO> getAllInstructors();
    InstructorResponseDTO updateInstructor(String dni, InstructorUpdateDTO instructor);
    void deleteInstructor(String dni);
    InstructorResponseDTO getInstructorByDni(String dni);
    InstructorStatsDTO getInstructorStats(Long instructorId, Integer month, Integer year);
    void sendComentarioNotificationEmail(ClassSession session);
}
