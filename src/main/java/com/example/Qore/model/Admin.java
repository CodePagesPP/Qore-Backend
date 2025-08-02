package com.example.Qore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name="admin")
@Data
@NoArgsConstructor
@SuperBuilder
public class Admin extends User{
}


