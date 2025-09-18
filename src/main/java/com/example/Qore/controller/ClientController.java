package com.example.Qore.controller;

import com.example.Qore.DTO.*;
import com.example.Qore.model.Client;
import com.example.Qore.model.User;
import com.example.Qore.repository.ClassSessionRepository;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.ClassSessionService;
import com.example.Qore.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientService clientService;
    @Autowired
    private ClassSessionService classSessionService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<ClientResponseDTO> getClient(@PathVariable String id) {
        return ResponseEntity.ok(clientService.getClientByDni(id));
    }

    @PatchMapping("/client/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable String id, @RequestBody ClientUpdateDTO dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @DeleteMapping("/client/{id}")
    public ResponseEntity<Void> disableClient(@PathVariable String id) {
        clientService.disableClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/birthdays")
    public ResponseEntity<List<ClientResponseDTO>> getClientsWithBirthdayInNextWeek() {
        List<ClientResponseDTO> clients = clientService.getClientsWithBirthdayInNextWeek();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/subscriptions/ending-soon")
    public ResponseEntity<List<ClientEndingSoon>> getClientsWithSubscriptionEndingSoon() {
        List<ClientEndingSoon> clients = clientService.getClientsWithSubscriptionEndingSoon();
        return ResponseEntity.ok(clients);
    }

    // Lista clientes del mes actual si no pasas parámetros
    @GetMapping("/registered")
    public ResponseEntity<List<ClientRegisterNewDTO>> getClientsRegisteredInMonth(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(clientService.getClientsRegisteredInMonth(month, year));
    }


    // Estadísticas de registros por mes
    @GetMapping("/registrations/stats")
    public ResponseEntity<List<ClientRegistrationStats>> getClientRegistrationsByMonth() {
        List<ClientRegistrationStats> stats = clientService.getClientRegistrationsByMonth();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/subscription-ended-2months")
    public ResponseEntity<List<ClientSubscriptionEndedDTO>> getClientsWithSubscriptionEndedMoreThanTwoMonths() {
        List<ClientSubscriptionEndedDTO> clients = clientService.getClientsWithSubscriptionEndedMoreThanTwoMonths();
        return ResponseEntity.ok(clients);
    }


    @GetMapping("/subscription-ended-2months/count")
    public ResponseEntity<Map<String, Long>> countClientsWithSubscriptionEndedMoreThanTwoMonths() {
        long count = clientService.countClientsWithSubscriptionEndedMoreThanTwoMonths();
        Map<String, Long> response = new HashMap<>();
        response.put("countSubscriptionEnded", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/plan-info")
    public ResponseEntity<ClientPlanInfoDTO> getClientPlanInfo(@PathVariable Long id) {
        ClientPlanInfoDTO dto = clientService.getClientPlanInfo(id);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/classes")
    public ResponseEntity<List<ClientClassDTO>> getMyClasses(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!(user instanceof Client client)) {
            return ResponseEntity.badRequest().build();
        }

        List<ClientClassDTO> classes = classSessionService.getClientClasses(client.getId());
        return ResponseEntity.ok(classes);
    }
}
