package com.example.Qore.service.Impl;
import com.example.Qore.model.*;
import com.example.Qore.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @Async
    public void sendJoinNotification(ClassSession session, Client client) {
        try {

            String subject = "Nuevo cliente inscrito: " + session.getName();

            String htmlTemplate = """
        <div style="font-family: Arial, sans-serif; color: #333;">
            <h2 style="color:#5C6BC0;">Nuevo cliente inscrito 🧘‍♂️</h2>
            <p>Se ha inscrito un nuevo cliente a la clase <strong>%s</strong>.</p>
            
            <h3>🧾 Detalles del cliente:</h3>
            <ul>
                <li><b>Nombre:</b> %s %s</li>
                <li><b>Email:</b> %s</li>
            </ul>
            
            <h3>📅 Detalles de la clase:</h3>
            <ul>
                <li><b>Disciplina:</b> %s</li>
                <li><b>Instructor:</b> %s</li>
                <li><b>Fecha:</b> %s</li>
                <li><b>Horario:</b> %s - %s</li>
                <li><b>Sala:</b> %s</li>
                <li><b>Inscritos:</b> %d / %d</li>
            </ul>
            
            <hr>
            <p style="font-size:0.9em;color:#777;">Atentamente,<br>Equipo Qore</p>
        </div>
    """;

            String htmlBody = String.format(
                    htmlTemplate,
                    session.getName(),
                    client.getName(), client.getLastName(),
                    client.getEmail(),
                    session.getDiscipline().getName(),
                    session.getInstructor().getName(),
                    session.getStartDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getRoom().getName(),
                    session.getClients().size(),
                    session.getCapacity()
            );


            if (session.getInstructor() != null && session.getInstructor().getEmail() != null) {
                emailService.sendHtmlEmail(session.getInstructor().getEmail(), subject, htmlBody);
            }


            List<User> recipients = userRepository.findAll().stream()
                    .filter(u -> u instanceof Manager || u instanceof Staff)
                    .toList();

            for (User u : recipients) {
                if (u.getEmail() != null && !u.getEmail().isBlank()) {
                    emailService.sendHtmlEmail(u.getEmail(), subject, htmlBody);
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️ Falló el envío de correo, pero el cliente se registró bien: " + e.getMessage());
        }
    }
}
