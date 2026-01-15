package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tbl_antecedentes_trabajo")
public class AntecedentesTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antecedente_trabajo", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @Column(name = "empresa", length = Integer.MAX_VALUE)
    private String empresa;

    @Column(name = "puesto_trabajo", length = Integer.MAX_VALUE)
    private String puestoTrabajo;

    @Column(name = "actividades", length = Integer.MAX_VALUE)
    private String actividades;

    @Column(name = "tiempo_trabajo_meses")
    private Integer tiempoTrabajoMeses;

    @Column(name = "riesgo_fisico")
    private Boolean riesgoFisico;

    @Column(name = "riesgo_mecanico")
    private Boolean riesgoMecanico;

    @Column(name = "riesgo_quimico")
    private Boolean riesgoQuimico;

    @Column(name = "riesgo_biologico")
    private Boolean riesgoBiologico;

    @Column(name = "riesgo_ergonomico")
    private Boolean riesgoErgonomico;

    @Column(name = "riesgo_psicosocial")
    private Boolean riesgoPsicosocial;

    @Column(name = "observaciones_empleo", length = Integer.MAX_VALUE)
    private String observacionesEmpleo;

    @Column(name = "accidente_descripcion", length = Integer.MAX_VALUE)
    private String accidenteDescripcion;

    @Column(name = "accidente_calificado")
    private Boolean accidenteCalificado;

    @Column(name = "accidente_especificar", length = Integer.MAX_VALUE)
    private String accidenteEspecificar;

    @Column(name = "accidente_fecha")
    private LocalDate accidenteFecha;

    @Column(name = "accidente_observaciones", length = Integer.MAX_VALUE)
    private String accidenteObservaciones;

    @Column(name = "enfermedad_calificada")
    private Boolean enfermedadCalificada;

    @Column(name = "enfermedad_especificar", length = Integer.MAX_VALUE)
    private String enfermedadEspecificar;

    @Column(name = "enfermedad_fecha")
    private LocalDate enfermedadFecha;

    @Column(name = "enfermedad_observaciones", length = Integer.MAX_VALUE)
    private String enfermedadObservaciones;

    @Column(name = "enfermedad_descripcion", columnDefinition = "TEXT")
    private String enfermedadDescripcion;

}