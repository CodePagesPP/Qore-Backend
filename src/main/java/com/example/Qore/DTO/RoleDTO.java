package com.example.Qore.DTO;

import lombok.Data;

import java.util.Set;

@Data
public class RoleDTO {
    private String name;
    private String description;
    private Set<Long> permissionIds;
}
