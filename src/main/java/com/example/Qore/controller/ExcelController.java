package com.example.Qore.controller;

import com.example.Qore.model.Client;
import com.example.Qore.repository.ClientRepository;
import com.example.Qore.service.ExcelReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {
    private final ClientRepository clientRepository;
    private final ExcelReportService excelReportService;

    @GetMapping("/report/birthdays-clients")
    public ResponseEntity<InputStreamResource> downloadBirthdaysExcel() throws IOException {
        List<Client> clients = clientRepository.findAll();

        ByteArrayInputStream excelStream = excelReportService.generateClientBirthdaysReport(clients);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=birthday-clients.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelStream));
    }
}
