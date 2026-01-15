package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "tbl_cita_medica")
public class CitaMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "id_empleado", nullable = false)
    private Integer idEmpleado;

    @NotNull
    @Size(max = 25)
    @Column(name = "alias_usuario", nullable = false, length = 25)
    private String aliasUsuario;

    @NotNull
    @Column(name = "tipo_profesional", nullable = false, length = 20)
    private String tipoProfesional;

    @Size(max = 50)
    @Column(name = "departamento", length = 50)
    private String departamento;

    @NotNull
    @Column(name = "motivo_consulta", nullable = false, columnDefinition = "TEXT")
    private String motivoConsulta;

    @NotNull
    @Column(name = "fecha_cita", nullable = false)
    private LocalDate fechaCita;

    @NotNull
    @Column(name = "hora_cita", nullable = false)
    private LocalTime horaCita;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "notificacion_enviada")
    private Boolean notificacionEnviada = false;

    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

}