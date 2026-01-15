
package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tbl_evaluacion")
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion", nullable = false)
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_evaluacion", nullable = false)
    private TipoEvaluacion tipoEvaluacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ficha_social", nullable = false)
    private FichaSocial fichaSocial;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "resultado", length = Integer.MAX_VALUE)
    private String resultado;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;


    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @Size(max = 100)
    @Column(name = "alias_usuario", length = 100)
    private String aliasUsuario;


    @Column(name = "evaluacion_completa", nullable = false)
    @ColumnDefault("false")
    private Boolean evaluacionCompleta = false;

    @Column(name = "firma_empleado", nullable = false)
    @ColumnDefault("false")
    private Boolean firmaEmpleado = false;

    @Size(max = 25)
    @Column(name = "puesto_ciuo", length = 25)
    private String puestoCiuo;

    @Column(name = "fecha_ultimo_dia_laboral")
    private LocalDate fechaUltimoDiaLaboral;

    @Column(name = "fecha_reingreso")
    private LocalDate fechaReingreso;

    @Column(name = "total_dias")
    private Integer totalDias;

    @Column(name = "causa_salida", columnDefinition = "TEXT")
    private String causaSalida;

    @Column(name = "lateralidad")
    private String lateralidad;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_orientacion_sexual", nullable = false)
    private OrientacionSexual orientacionSexual;

    @Column(name = "actividades_puesto", columnDefinition = "TEXT")
    private String actividadesPuesto;
}