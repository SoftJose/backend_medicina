package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tbl_historia_clinica_psicologica")
public class HistoriaClinicaPsicologica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historia", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "id_empleado", nullable = false)
    private Integer idEmpleado;

    @NotNull
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;

    @Column(name = "fecha_registro")
    @ColumnDefault("now()")
    private LocalDateTime fechaRegistro;

    @Column(name = "motivo_consulta")
    private String motivoConsulta;

    @Column(name = "observacion_general")
    private String observacionGeneral;

    @Column(name = "aspectos_motivo")
    private String aspectosMotivo;

    @Column(name = "historial_situacion")
    private String historialSituacion;

    @Column(name = "intentos_previos")
    private String intentosPrevios;

    @Column(name = "redes_apoyo")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object redesApoyo;

    @Column(name = "aspectos_familia")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object aspectosFamilia;

    @Column(name = "observaciones_familia")
    private String observacionesFamilia;

    @Column(name = "familiograma")
    private String familiograma;

    @Lob
    @Column(name = "area_personal")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object areaPersonal;

    @Column(name = "historia_academica")
    private String historiaAcademica;

    @Column(name = "area_academica")
    private String areaAcademica;

    @Column(name = "historial_laboral")
    private String historialLaboral;

    @Column(name = "antecedentes_personales")
    private String antecedentesPersonales;

    @Column(name = "relaciones_pareja")
    private String relacionesPareja;

    @Column(name = "conductas_riesgo")
    private String conductasRiesgo;

    @Column(name = "impresion_diagnostica")
    private String impresionDiagnostica;

    @Column(name = "reactivos_aplicados")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object reactivosAplicados;

    @Column(name = "plan_tratamiento")
    private String planTratamiento;
}