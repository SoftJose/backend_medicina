package saitel.medicina.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
public class PacienteInfoDto {
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private String tipoSangre;
    private String fotoBase64;
}
