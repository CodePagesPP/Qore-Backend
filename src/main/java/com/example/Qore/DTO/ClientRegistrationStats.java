package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRegistrationStats {
    private int year;
    private int month;
    private long totalClients;
}
