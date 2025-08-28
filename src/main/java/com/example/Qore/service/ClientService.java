package com.example.Qore.service;

import com.example.Qore.DTO.ClientRegisterDTO;
import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.ClientUpdateDTO;
import com.example.Qore.model.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientService {
    ClientResponseDTO registerClient(ClientRegisterDTO dto);
    List<ClientResponseDTO> getAllActiveClients();
    ClientResponseDTO getClientByDni(String dni);
    ClientResponseDTO updateClient(String dni, ClientUpdateDTO dto);
    void disableClient(String id);
    List<ClientResponseDTO> getClientsByBirthdayMonth(int month);
}
