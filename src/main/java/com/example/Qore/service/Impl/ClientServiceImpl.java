package com.example.Qore.service.Impl;

import com.example.Qore.DTO.*;
import com.example.Qore.model.Client;
import com.example.Qore.model.RoleE;
import com.example.Qore.repository.ClassSessionRepository;
import com.example.Qore.repository.ClientRepository;
import com.example.Qore.repository.RoleRepository;
import com.example.Qore.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ClassSessionRepository classRepository;
    @Override
    public ClientResponseDTO registerClient(ClientRegisterDTO dto) {


        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        if(userRepository.existsByDni(dto.getDni())){
            throw new IllegalArgumentException("El dni ya esta en uso");
        }


        RoleE clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Rol CLIENT no existe en la base de datos"));


        Client user = Client.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dni(dto.getDni())
                .sex(dto.getSex())
                .country(dto.getCountry())
                .city(dto.getCity())
                .birthday(dto.getBirthday())
                .role(clientRole)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .active(true)
                .build();

        Client savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    public List<ClientResponseDTO> getAllActiveClients() {
        return userRepository.findActiveClientsByRoleName("CLIENT").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDTO getClientByDni(String dni) {
        Client user = userRepository.findActiveClientByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Active CLIENT with DNI not found"));

        return mapToDTO(user);
    }

    @Override
    public ClientResponseDTO updateClient(String dni, ClientUpdateDTO dto) {


        Client user = userRepository.findClientByDniAndRole(dni)
                .orElseThrow(() -> new EntityNotFoundException("Client not found or is not a CLIENT"));

        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getBirthday() != null) user.setBirthday(dto.getBirthday());
        if (dto.getSex() != null) user.setSex(dto.getSex());
        if (dto.getCountry() != null) user.setCountry(dto.getCountry());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getDni() != null) user.setDni(dto.getDni());
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return mapToDTO(userRepository.save(user));
    }

    @Override
    public void disableClient(String id) {
        Client user = userRepository.findActiveClientByDni(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found or is not a CLIENT"));

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<ClientResponseDTO> getClientsWithBirthdayInNextWeek() {
        LocalDate today = LocalDate.now();

        List<Client> clients = userRepository.findAll();

        return clients.stream()
                .filter(c -> {
                    LocalDate birthday = c.getBirthday(); // asume LocalDate
                    if (birthday == null) return false;

                    // cumpleaños para este año
                    LocalDate nextBirthday = birthday.withYear(today.getYear());

                    // si ya pasó este año, usar el próximo año
                    if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
                        nextBirthday = birthday.withYear(today.getYear() + 1);
                    }

                    long daysBetween = ChronoUnit.DAYS.between(today, nextBirthday);
                    return daysBetween >= 0 && daysBetween <= 7; // dentro de 7 días
                })
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ClientEndingSoon> getClientsWithSubscriptionEndingSoon() {
        LocalDate today = LocalDate.now();

        List<Client> clients = userRepository.findBySubscriptionEndIsNotNull();

        return clients.stream()
                .filter(c -> {
                    LocalDate endDate = c.getSubscriptionEnd();
                    if (endDate == null) return false;
                    long daysBetween = ChronoUnit.DAYS.between(today, endDate);
                    return daysBetween >= 0 && daysBetween <= 7;
                })
                .map(this::mapToDTOEnding)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientRegisterNewDTO> getClientsRegisteredInMonth(Integer month, Integer year) {
        LocalDate today = LocalDate.now();
        if (month == null) month = today.getMonthValue();
        if (year == null) year = today.getYear();

        return userRepository.findClientsRegisteredInMonth(month, year)
                .stream()
                .map(c -> ClientRegisterNewDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .lastName(c.getLastName())
                        .email(c.getEmail())
                        .phoneNumber(c.getPhoneNumber())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();
    }


    @Override
    public List<ClientRegistrationStats> getClientRegistrationsByMonth() {
        List<Object[]> results = userRepository.countClientsByMonth();
        List<ClientRegistrationStats> stats = new ArrayList<>();

        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long total = ((Number) row[2]).longValue();

            stats.add(new ClientRegistrationStats(year, month, total));
        }
        return stats;
    }

    public List<ClientSubscriptionEndedDTO> getClientsWithSubscriptionEndedMoreThanTwoMonths() {
        LocalDate twoMonthsAgo = LocalDate.now().minusMonths(2);

        return userRepository.findBySubscriptionEndBefore(twoMonthsAgo)
                .stream()
                .map(c -> ClientSubscriptionEndedDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .lastName(c.getLastName())
                        .email(c.getEmail())
                        .phoneNumber(c.getPhoneNumber())
                        .subscriptionEnd(c.getSubscriptionEnd())
                        .build())
                .toList();
    }

    @Override
    public long countClientsWithSubscriptionEndedMoreThanTwoMonths() {
        return getClientsWithSubscriptionEndedMoreThanTwoMonths().size();
    }

    private ClientResponseDTO mapToDTO(Client user){
        return ClientResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .birthday(user.getBirthday())
                .dni(user.getDni())
                .country(user.getCountry())
                .city(user.getCity())
                .sex(user.getSex())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private ClientEndingSoon mapToDTOEnding(Client client) {
        long totalClasses = client.getPlan() != null ? client.getPlan().getSessions() : 0;

        long classesTaken = 0;
        if (client.getSubscriptionStart() != null && client.getSubscriptionEnd() != null) {
            classesTaken = classRepository.countClassesByClientAndPeriod(
                    client.getId(),
                    client.getSubscriptionStart(),
                    client.getSubscriptionEnd()
            );
        }

        long classesRemaining = totalClasses - classesTaken;
        if (classesRemaining < 0) classesRemaining = 0;

        return ClientEndingSoon.builder()
                .id(client.getId())
                .name(client.getName())
                .lastName(client.getLastName())
                .phoneNumber(client.getPhoneNumber())
                .email(client.getEmail())
                .subscriptionEnd(client.getSubscriptionEnd())
                .totalClasses(totalClasses)
                .classesTaken(classesTaken)
                .classesRemaining(classesRemaining)
                .build();
    }
}
