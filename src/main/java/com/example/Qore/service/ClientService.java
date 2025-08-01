package com.example.Qore.service;

import com.example.Qore.DTO.ClientRegisterDTO;
import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.ClientUpdateDTO;

import java.util.List;

public interface ClientService {
    ClientResponseDTO registerClient(ClientRegisterDTO dto);
    List<ClientResponseDTO> getAllActiveClients();
    ClientResponseDTO getClientByDni(String dni);
    ClientResponseDTO updateClient(String dni, ClientUpdateDTO dto);
    void disableClient(String id);
}
