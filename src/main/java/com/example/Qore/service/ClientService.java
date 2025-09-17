package com.example.Qore.service;

import com.example.Qore.DTO.*;
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
    List<ClientResponseDTO> getClientsWithBirthdayInNextWeek();

    List<ClientEndingSoon> getClientsWithSubscriptionEndingSoon();

    List<ClientRegisterNewDTO> getClientsRegisteredInMonth(Integer month, Integer year);

    List<ClientRegistrationStats> getClientRegistrationsByMonth();

    List<ClientSubscriptionEndedDTO> getClientsWithSubscriptionEndedMoreThanTwoMonths();
    long countClientsWithSubscriptionEndedMoreThanTwoMonths();
}
