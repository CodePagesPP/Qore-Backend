package com.example.Qore.service.Impl;

import com.example.Qore.DTO.*;
import com.example.Qore.model.*;
import com.example.Qore.model.Enum.EstadoSession;
import com.example.Qore.model.payment.Payment;
import com.example.Qore.repository.*;
import com.example.Qore.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository userRepository;
    private final PlanRepository planRepository;
    private final ClientPlanHistoryRepository clientPlanHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ClassSessionRepository classRepository;
    private final DisciplineRepository disciplineRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
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

        List<Discipline> disciplines = new ArrayList<>();
        if (dto.getDisciplineIds() != null && !dto.getDisciplineIds().isEmpty()) {
            disciplines = disciplineRepository.findAllById(dto.getDisciplineIds());
        }

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
                .disciplines(disciplines)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .active(true)
                .build();

        Client savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    public Page<ClientResponseDTO> getAllActiveClients(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Client> clientPage = userRepository.findActiveClientsByRoleName("CLIENT", pageable);


        return clientPage.map(this::mapToDTO);
    }


    public List<ClientClassDTO> getClassesByClientHistory(Long clientId, LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {
            endDate = LocalDate.now().plusYears(100);
        }

        List<ClassSession> sessions = classRepository.findJoinedClassesByClientAndDateRange(clientId, startDate, endDate);


        List<Attendance> attendances = attendanceRepository.findByClientIdAndDateBetween(clientId, startDate, endDate);


        Map<Long, String> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(
                        a -> a.getClassSession().getId(),
                        a -> a.getStatus().name(),
                        (existing, replacement) -> existing
                ));


        return sessions.stream().map(s -> {
            ClientClassDTO dto = new ClientClassDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setStartDate(s.getStartDate());
            dto.setStartTime(s.getStartTime());
            dto.setEndTime(s.getEndTime());
            dto.setInstructorName(s.getInstructor().getName());
            dto.setDisciplineName(s.getDiscipline().getName());
            dto.setStatus(s.getEstado().name()); // Estado de la clase
            String asis = attendanceMap.get(s.getId());
            dto.setAttendance(asis != null ? asis : "PENDIENTE");

            return dto;
        }).toList();
    }

    @Override
    public Page<ClientResponseDTO> searchClients(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return userRepository.searchClients(search, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public List<ClientResponseDTO> getClientsByDiscipline(Long disciplineId) {
        return userRepository.findActiveClientsByDiscipline(disciplineId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public void assignPlanManually(Long clientId, Long planId, String paymentMethod, BigDecimal discount) {
        Client client = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));


        if (!client.isTrialCompleted()) {
            throw new IllegalStateException("No se puede asignar un plan. El cliente aún no ha completado su clase de prueba.");
        }

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));

        BigDecimal discountValue = (discount != null) ? discount : BigDecimal.ZERO;

        BigDecimal planPrice = BigDecimal.valueOf(plan.getPrice());

        BigDecimal finalPrice = planPrice.subtract(discountValue);

        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(plan.getDuration());

        client.setPlan(plan);
        client.setSubscriptionStart(start);
        client.setSubscriptionEnd(end);
        client.setActive(true);
        userRepository.save(client);


        ClientPlanHistory history = ClientPlanHistory.builder()
                .client(client)
                .planName(plan.getName())
                .startDate(start)
                .endDate(end)
                .pricePaid(finalPrice.floatValue())
                .assignedAt(LocalDateTime.now())
                .assignedBy("MANUAL")
                .paymentMethod(paymentMethod)
                .build();

        clientPlanHistoryRepository.save(history);

        String fakePaymentId = "MANUAL-" + UUID.randomUUID().toString();
        Payment manualPayment = Payment.builder()
                .client(client)
                .plan(plan)
                .amount(finalPrice.floatValue())
                .status("approved")
                .paymentDate(LocalDateTime.now())
                .mpPaymentId(fakePaymentId)
                .subscriptionId("MANUAL_SUBSCRIPTION")
                .paymentMethod(paymentMethod)
                .build();

        paymentRepository.save(manualPayment);
    }

    @Override
    public List<ClientPlanHistoryDTO> getHistoryByClient(Long clientId) {

        return clientPlanHistoryRepository.findByClientIdOrderByAssignedAtDesc(clientId)
                .stream().map(this::mapHistoryToDTO).collect(Collectors.toList());
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
        if (dto.getDisciplineIds() != null) {
            List<Discipline> newDisciplines = disciplineRepository.findAllById(dto.getDisciplineIds());
            user.setDisciplines(newDisciplines);
        }
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

    @Override
    public ClientPlanInfoDTO getClientPlanInfo(Long clientId) {
        Client client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        ClientEndingSoon dto = mapToDTOEnding(client);

        boolean hasPlan = client.getPlan() != null;
        LocalDate today = LocalDate.now();
        boolean isExpired = dto.getSubscriptionEnd() != null && dto.getSubscriptionEnd().isBefore(today);
        boolean hasRemainingClasses = dto.getClassesRemaining() > 0;


        boolean allClassesDictated = classRepository.findByClientId(clientId)
                .stream()
                .allMatch(s -> s.getEstado().equals(EstadoSession.DICTADA));


        boolean canPurchase = !hasPlan || isExpired || (hasPlan && !hasRemainingClasses && allClassesDictated);


        return ClientPlanInfoDTO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .dni(dto.getDni())
                .phoneNumber(dto.getPhoneNumber())
                .planName(client.getPlan() != null ? client.getPlan().getName() : "No cuentas con plan")
                .subscriptionEnd(dto.getSubscriptionEnd())
                .totalClasses(dto.getTotalClasses())
                .classesTaken(dto.getClassesTaken())
                .classesRemaining(dto.getClassesRemaining())
                .trialCompleted(client.isTrialCompleted())
                .canPurchasePlan(canPurchase)
                .build();
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
                .disciplines(user.getDisciplines())
                .trialCompleted(user.isTrialCompleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .plan(user.getPlan() != null ? mapPlanToDTO(user.getPlan()) : null)
                .subscriptionStart(user.getSubscriptionStart())
                .subscriptionEnd(user.getSubscriptionEnd())
                .build();
    }

    private PlanResponseDTO mapPlanToDTO(Plan plan) {
        return PlanResponseDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .price(plan.getPrice())
                .duration(plan.getDuration())
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
                .dni(client.getDni())
                .subscriptionEnd(client.getSubscriptionEnd())
                .totalClasses(totalClasses)
                .classesTaken(classesTaken)
                .classesRemaining(classesRemaining)
                .build();
    }

    private ClientPlanHistoryDTO mapHistoryToDTO(ClientPlanHistory history) {
        if (history == null) {
            return null;
        }

        return ClientPlanHistoryDTO.builder()
                .id(history.getId())
                .planName(history.getPlanName())
                .startDate(history.getStartDate())
                .endDate(history.getEndDate())
                .pricePaid(history.getPricePaid())
                .assignedAt(history.getAssignedAt())
                .assignedBy(history.getAssignedBy())
                .paymentMethod(history.getPaymentMethod())
                .build();
    }
}
