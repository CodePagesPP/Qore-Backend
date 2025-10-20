package com.example.Qore.DTO;

import com.example.Qore.model.Enum.EstadoSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassSessionComentarioDTO {
    private Long id;
    private String comentario;
    private EstadoSession estado;
    private LocalDateTime comentarioAt;
}
