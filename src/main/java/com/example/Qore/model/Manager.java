package com.example.Qore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="manager")
@Data
@NoArgsConstructor
@SuperBuilder
public class Manager extends User{
}
