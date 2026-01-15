package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "tbl_atencion_seguimiento_psicologico")
public class AtencionSeguimientoPsicologico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_historia", nullable = false)
    private HistoriaClinicaPsicologica historiaClinicaPsicologica;

    @Size(max = 20)
    @NotNull
    @Column(name = "tipo_atencion", nullable = false, length = 20)
    private String tipoAtencion;

    @NotNull
    @Column(name = "psicopatologia", nullable = false, columnDefinition = "TEXT")
    private String psicopatologia;

    @Size(max = 50)
    @NotNull
    @Column(name = "sesion", nullable = false, length = 50)
    private String sesion;

    @Size(max = 20)
    @NotNull
    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "consumo_as", columnDefinition = "TEXT")
    private String consumoAs;

    @NotNull
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;

    @NotNull
    @Column(name = "hora_atencion", nullable = false)
    private LocalTime horaAtencion;

    @Column(name = "riesgo_social", columnDefinition = "TEXT")
    private String riesgoSocial;

    @Column(name = "otros", columnDefinition = "TEXT")
    private String otros;

    @Column(name = "temas_tratados", columnDefinition = "TEXT")
    private String temasTratados;

    @Column(name = "reactivos_aplicados")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object reactivosAplicados;

    @Column(name = "resultados", columnDefinition = "TEXT")
    private String resultados;

    @Column(name = "herramientas_enfoques", columnDefinition = "TEXT")
    private String herramientasEnfoques;

    @Column(name = "avances", columnDefinition = "TEXT")
    private String avances;

    @Column(name = "seguimiento_social", columnDefinition = "TEXT")
    private String seguimientoSocial;

    @Column(name = "familiar_contactado")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object familiarContactado;

    @Column(name = "verificadores_seguimiento", columnDefinition = "TEXT")
    private String verificadoresSeguimiento;

    @Column(name = "firma_psicologo", columnDefinition = "BOOLEAN")
    private Boolean firmaPsicologo = false;

    @Column(name = "firma_empleado", columnDefinition = "BOOLEAN")
    private Boolean firmaEmpleado = false;
    @Column(name = "fecha_registro", columnDefinition = "TIMESTAMP")
    private java.time.LocalDateTime fechaRegistro;

}