package com.example.Qore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Manager extends User{
}
