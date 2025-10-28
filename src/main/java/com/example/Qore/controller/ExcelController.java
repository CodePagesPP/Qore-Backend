package com.example.Qore.controller;

import com.example.Qore.model.Client;
import com.example.Qore.repository.ClientRepository;
import com.example.Qore.service.ExcelReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
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

    @GetMapping("/monthly-income")
    public void downloadMonthlyIncome(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=REPORTE_MENSUAL.xlsx");

        Workbook workbook = excelReportService.generateIncomeReport();
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/report/clients")
    public ResponseEntity<InputStreamResource> downloadClientsExcel() throws IOException {
        try {
            System.out.println("📘 Generando Excel de clientes...");
            List<Client> clients = clientRepository.findAllWithPlan();
            System.out.println("✅ Total clientes encontrados: " + clients.size());

            ByteArrayInputStream excelStream = excelReportService.generateAllClientsReport(clients);
            System.out.println("💾 Excel generado correctamente.");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=all-clients.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(excelStream));
        } catch (Exception e) {
            System.err.println("❌ Error generando Excel: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
