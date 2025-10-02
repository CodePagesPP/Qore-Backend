package com.example.Qore.service.Impl;

import com.example.Qore.model.Client;
import com.example.Qore.model.payment.Payment;
import com.example.Qore.repository.PaymentRepository;
import com.example.Qore.service.ExcelReportService;
import com.example.Qore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelReportServiceImpl implements ExcelReportService {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    @Override
    public ByteArrayInputStream generateClientBirthdaysReport(List<Client> clients) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Birthdays Clients");

            // Encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Apellido", "Email", "DNI", "Teléfono", "Cumpleaños"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Contenido
            int rowIdx = 1;
            for (Client client : clients) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(client.getId());
                row.createCell(1).setCellValue(client.getName());
                row.createCell(2).setCellValue(client.getLastName());
                row.createCell(3).setCellValue(client.getEmail());
                row.createCell(4).setCellValue(client.getDni());
                row.createCell(5).setCellValue(client.getPhoneNumber());
                row.createCell(6).setCellValue(client.getBirthday().toString());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Override
    public Workbook generateIncomeReport() {
        Workbook workbook = new XSSFWorkbook();


        Sheet monthlySheet = workbook.createSheet("Resumen Mensual");
        Row header1 = monthlySheet.createRow(0);
        header1.createCell(0).setCellValue("Mes");
        header1.createCell(1).setCellValue("Ingresos Totales");

        Map<YearMonth, Double> monthlyIncomes = paymentService.getMonthlyIncomes();
        int rowIdx1 = 1;
        for (Map.Entry<YearMonth, Double> entry : monthlyIncomes.entrySet()) {
            Row row = monthlySheet.createRow(rowIdx1++);
            row.createCell(0).setCellValue(entry.getKey().toString());
            row.createCell(1).setCellValue(entry.getValue());
        }


        Sheet weeklySheet = workbook.createSheet("Resumen Semanal");
        Row header2 = weeklySheet.createRow(0);
        header2.createCell(0).setCellValue("Año-Semana");
        header2.createCell(1).setCellValue("Ingresos Totales");

        Map<String, Double> weeklyIncomes = paymentService.getWeeklyIncomes();
        int rowIdx2 = 1;
        for (Map.Entry<String, Double> entry : weeklyIncomes.entrySet()) {
            Row row = weeklySheet.createRow(rowIdx2++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }


        Sheet detailSheet = workbook.createSheet("Detalle Pagos");
        Row header3 = detailSheet.createRow(0);
        header3.createCell(0).setCellValue("ID Pago");
        header3.createCell(1).setCellValue("Cliente");
        header3.createCell(2).setCellValue("Email");
        header3.createCell(3).setCellValue("Plan");
        header3.createCell(4).setCellValue("Monto");
        header3.createCell(5).setCellValue("Método de Pago");
        header3.createCell(6).setCellValue("Estado");
        header3.createCell(7).setCellValue("Fecha");

        List<Payment> payments = paymentRepository.findAll();
        int rowIdx3 = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Payment p : payments) {
            Row row = detailSheet.createRow(rowIdx3++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getClient().getName() + " " + p.getClient().getLastName());
            row.createCell(2).setCellValue(p.getClient().getEmail());
            row.createCell(3).setCellValue(p.getPlan().getName());
            row.createCell(4).setCellValue(p.getAmount());
            row.createCell(5).setCellValue(p.getPlan().getPayMethod());
            row.createCell(6).setCellValue(p.getStatus());
            row.createCell(7).setCellValue(p.getPaymentDate().format(formatter));
        }

        return workbook;
    }


    @Override
    public ByteArrayInputStream generateAllClientsReport(List<Client> clients) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes");

            // cabecera
            Row header = sheet.createRow(0);
            String[] columns = {"ID","Nombre","Apellido","Email","Teléfono","DNI","Activo","País","Ciudad","Dirección","Cumpleaños","Plan","Inicio Subscripción","Fin Subscripción"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // filas
            int rowIdx = 1;
            for (Client c : clients) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getName() != null ? c.getName() : "");
                row.createCell(2).setCellValue(c.getLastName() != null ? c.getLastName() : "");
                row.createCell(3).setCellValue(c.getEmail() != null ? c.getEmail() : "");
                row.createCell(4).setCellValue(c.getPhoneNumber() != null ? c.getPhoneNumber() : "");
                row.createCell(5).setCellValue(c.getDni() != null ? c.getDni() : "");
                row.createCell(6).setCellValue(c.isActive() ? "Sí" : "No");
                row.createCell(7).setCellValue(c.getCountry() != null ? c.getCountry() : "");
                row.createCell(8).setCellValue(c.getCity() != null ? c.getCity() : "");
                row.createCell(9).setCellValue(c.getAddress() != null ? c.getAddress() : "");
                row.createCell(10).setCellValue(c.getBirthday() != null ? c.getBirthday().toString() : "");
                row.createCell(11).setCellValue(c.getPlan() != null ? c.getPlan().getName() : "");
                row.createCell(12).setCellValue(c.getSubscriptionStart() != null ? c.getSubscriptionStart().toString() : "");
                row.createCell(13).setCellValue(c.getSubscriptionEnd() != null ? c.getSubscriptionEnd().toString() : "");
            }

            // auto-size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

}
