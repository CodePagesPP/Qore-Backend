package com.example.Qore.service;

import com.example.Qore.model.Client;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ExcelReportService {
    ByteArrayInputStream generateClientBirthdaysReport(List<Client> clients) throws IOException;
    Workbook generateIncomeReport();
    ByteArrayInputStream generateAllClientsReport(List<Client> clients) throws IOException;
}
