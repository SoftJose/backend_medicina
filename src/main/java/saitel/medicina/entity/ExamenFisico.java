package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_examen_fisico")
public class ExamenFisico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen_fisico", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @Column(name = "cicatrices")
    private Boolean cicatrices;

    @Column(name = "tatuajes")
    private Boolean tatuajes;

    @Column(name = "piel_faneras")
    private Boolean pielFaneras;

    @Column(name = "parpados")
    private Boolean parpados;

    @Column(name = "conjuntivas")
    private Boolean conjuntivas;

    @Column(name = "pupilas")
    private Boolean pupilas;

    @Column(name = "cornea")
    private Boolean cornea;

    @Column(name = "motilidad_ocular")
    private Boolean motilidadOcular;

    @Column(name = "conducto_auditivo_externo")
    private Boolean conductoAuditivoExterno;

    @Column(name = "pabellon")
    private Boolean pabellon;

    @Column(name = "timpanos")
    private Boolean timpanos;

    @Column(name = "labios")
    private Boolean labios;

    @Column(name = "lengua")
    private Boolean lengua;

    @Column(name = "faringe")
    private Boolean faringe;

    @Column(name = "amigdalas")
    private Boolean amigdalas;

    @Column(name = "dentadura")
    private Boolean dentadura;

    @Column(name = "tabique")
    private Boolean tabique;

    @Column(name = "cornetes")
    private Boolean cornetes;

    @Column(name = "mucosas")
    private Boolean mucosas;

    @Column(name = "senos_paranasales")
    private Boolean senosParanasales;

    @Column(name = "tiroides_masas")
    private Boolean tiroidesMasas;

    @Column(name = "movilidad_cuello")
    private Boolean movilidadCuello;

    @Column(name = "mamas")
    private Boolean mamas;

    @Column(name = "corazon")
    private Boolean corazon;

    @Column(name = "pulmones")
    private Boolean pulmones;

    @Column(name = "parrilla_costal")
    private Boolean parrillaCostal;

    @Column(name = "visceras")
    private Boolean visceras;

    @Column(name = "pared_abdominal")
    private Boolean paredAbdominal;

    @Column(name = "flexibilidad")
    private Boolean flexibilidad;

    @Column(name = "desviacion")
    private Boolean desviacion;

    @Column(name = "dolor")
    private Boolean dolor;

    @Column(name = "pelvis")
    private Boolean pelvis;

    @Column(name = "genitales")
    private Boolean genitales;

    @Column(name = "vascular")
    private Boolean vascular;

    @Column(name = "miembros_superiores")
    private Boolean miembrosSuperiores;

    @Column(name = "miembros_inferiores")
    private Boolean miembrosInferiores;

    @Column(name = "fuerza")
    private Boolean fuerza;

    @Column(name = "sensibilidad")
    private Boolean sensibilidad;

    @Column(name = "marcha")
    private Boolean marcha;

    @Column(name = "reflejos")
    private Boolean reflejos;

    @Column(name = "observaciones")
    private String observaciones;

}