package com.example.Qore.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> permissionIds;
}
