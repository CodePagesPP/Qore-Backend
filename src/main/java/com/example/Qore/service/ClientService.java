package com.example.Qore.service;

import com.example.Qore.DTO.*;
import com.example.Qore.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientService {
    ClientResponseDTO registerClient(ClientRegisterDTO dto);
    Page<ClientResponseDTO> getAllActiveClients(int page, int size);
    ClientResponseDTO getClientByDni(String dni);
    ClientResponseDTO updateClient(String dni, ClientUpdateDTO dto);
    void disableClient(String id);
    List<ClientResponseDTO> getClientsWithBirthdayInNextWeek();
    List<ClientPlanHistoryDTO> getHistoryByClient(Long clientId);
    void assignPlanManually(Long clientId, Long planId, String paymentMethod);
    Page<ClientResponseDTO> searchClients(String search, int page, int size);
    List<ClientEndingSoon> getClientsWithSubscriptionEndingSoon();
    List<ClientResponseDTO> getClientsByDiscipline(Long disciplineId);
    List<ClientRegisterNewDTO> getClientsRegisteredInMonth(Integer month, Integer year);

    List<ClientRegistrationStats> getClientRegistrationsByMonth();

    List<ClientSubscriptionEndedDTO> getClientsWithSubscriptionEndedMoreThanTwoMonths();
    long countClientsWithSubscriptionEndedMoreThanTwoMonths();
    ClientPlanInfoDTO getClientPlanInfo(Long clientId);
}
