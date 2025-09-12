package com.example.Qore.service;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;

import java.util.List;

public interface ClassSessionService {
    List<ClassSessionDTO> getAll();
    ClassSessionDTO getById(Long id);
    ClassSessionDTO create(ClassSessionDTO dto);
    ClassSessionDTO update(Long id, ClassSessionUpdateDTO dto);
    void delete(Long id);
    void addClientToClass(Long classId, Long clientId);
    long getCurrentWeekClasses();
}
