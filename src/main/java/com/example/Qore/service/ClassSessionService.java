package com.example.Qore.service;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;
import com.example.Qore.DTO.ClientClassDTO;
import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.model.ClassSession;
import com.example.Qore.model.Client;

import java.util.List;

public interface ClassSessionService {
    List<ClassSessionDTO> getAll();
    ClassSessionDTO getById(Long id);
    List<ClassSessionDTO> create(ClassSessionDTO dto);
    ClassSessionDTO update(Long id, ClassSessionUpdateDTO dto);
    void delete(Long id);
    void addClientToClass(Long classId, Long clientId);
    long getCurrentWeekClasses();
    long countClassesTaken(Client client);
    List<ClientClassDTO> getClientClasses(Long clientId);
    List<ClassSessionDTO> getClassesByInstructor(Long instructorId);
    List<ClassSessionDTO> getPendingClassesToday(Long instructorId);
    List<ClassSessionDTO> getClassesForClient(Long clientId);
    ClassSession joinClassClient(Long classId, Long clientId);
    List<ClientResponseDTO> getClientsByClass(Long classId);
}
