package com.example.Qore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@Table(name="Staff")
@NoArgsConstructor
@SuperBuilder
public class Staff extends User{
    @Column(unique = false)
    private String area;
}
