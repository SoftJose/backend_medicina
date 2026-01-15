package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name = "tbl_antecedentes")
@NoArgsConstructor
@AllArgsConstructor
public class Antecedentes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antecedente", nullable = false)
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @JsonProperty("idEvaluacion")
    public Integer getIdEvaluacionId() {
    return idEvaluacion != null ? idEvaluacion.getId() : null;
    }

    @Column(name = "hijos_vivos")
    private Integer hijosVivos;

    @Column(name = "hijos_muertos")
    private Integer hijosMuertos;

    @Column(name = "metodo_planificacion")
    private Boolean MetodoPlanificacion;

    @Column(name = "tipo_metodo_planificacion", length = 100)
    private String tipoMetodoPlanificacion;

    // ANTECEDENTES CLÍNICO-QUIRÚRGICOS
    @Column(name = "descripcion_clinico_quirurgico", columnDefinition = "text")
    private String descripcionClinicoQuirurgico;

    // ANTECEDENTES GINECO-OBSTÉTRICOS
    @Column(name = "menarquia_edad")
    private Integer menarquiaEdad;

    @Column(name = "ciclos_menstruales", length = 50)
    private String ciclosMenstruales;

    @Column(name = "fecha_ultima_menstruacion")
    private LocalDate fechaUltimaMenstruacion;

    @Column(name = "numero_gestas")
    private Integer numeroGestas;

    @Column(name = "numero_partos")
    private Integer numeroPartos;

    @Column(name = "numero_cesareas")
    private Integer numeroCesareas;

    @Column(name = "numero_abortos")
    private Integer numeroAbortos;

    @Column(name = "vida_sexual_activa")
    private Boolean vidaSexualActiva;

    // EXÁMENES REALIZADOS
    @Column(name = "examen_papanicolaou")
    private Boolean examenPapanicolaou;

    @Column(name = "tiempo_papanicolaou_anios")
    private Integer tiempoPapanicolaouAnios;

    @Column(name = "resultado_papanicolaou", length = 255)
    private String resultadoPapanicolaou;

    @Column(name = "examen_colposcopia")
    private Boolean examenColposcopia;

    @Column(name = "tiempo_colposcopia_anios")
    private Integer tiempoColposcopiaAnios;

    @Column(name = "resultado_colposcopia", length = 255)
    private String resultadoColposcopia;

    @Column(name = "examen_mamografia")
    private Boolean examenMamografia;

    @Column(name = "tiempo_mamografia_anios")
    private Integer tiempoMamografiaAnios;

    @Column(name = "resultado_mamografia", length = 255)
    private String resultadoMamografia;

    @Column(name = "examen_eco_mamario")
    private Boolean examenEcoMamario;

    @Column(name = "tiempo_eco_mamario_anios")
    private Integer tiempoEcoMamarioAnios;

    @Column(name = "resultado_eco_mamario", length = 255)
    private String resultadoEcoMamario;

    // ANTECEDENTES REPRODUCTIVOS MASCULINOS
    @Column(name = "examen_antigeno_prostatico")
    private Boolean examenAntigenoProstatico;

    @Column(name = "tiempo_antigeno_prostatico_anios")
    private Integer tiempoAntigenoProstaticoAnios;

    @Column(name = "resultado_antigeno_prostatico", length = 255)
    private String resultadoAntigenoProstatico;

    @Column(name = "examen_eco_prostatico")
    private Boolean examenEcoProstatico;

    @Column(name = "tiempo_eco_prostatico_anios")
    private Integer tiempoEcoProstaticoAnios;

    @Column(name = "resultado_eco_prostatico", length = 255)
    private String resultadoEcoProstatico;

    // HÁBITOS TÓXICOS
    @Column(name = "consumo_tabaco")
    private Boolean consumoTabaco;

    @Column(name = "tiempo_tabaco_meses")
    private Integer tiempoTabacoMeses;

    @Column(name = "cantidad_tabaco", length = 100)
    private String cantidadTabaco;

    @Column(name = "ex_consumidor_tabaco")
    private Boolean exConsumidorTabaco;

    @Column(name = "abstinencia_tabaco_meses")
    private Integer abstinenciaTabacoMeses;

    @Column(name = "consumo_alcohol")
    private Boolean consumoAlcohol;

    @Column(name = "tiempo_alcohol_meses")
    private Integer tiempoAlcoholMeses;

    @Column(name = "cantidad_alcohol", length = 100)
    private String cantidadAlcohol;

    @Column(name = "ex_consumidor_alcohol")
    private Boolean exConsumidorAlcohol;

    @Column(name = "abstinencia_alcohol_meses")
    private Integer abstinenciaAlcoholMeses;

    @Column(name = "consumo_otras_drogas")
    private Boolean consumoOtrasDrogas;

    @Column(name = "tipo_otras_drogas", length = 100)
    private String tipoOtrasDrogas;

    @Column(name = "cantidad_otras_drogas", length = 100)
    private String cantidadOtrasDrogas;

    @Column(name = "ex_consumidor_otras_drogas")
    private Boolean exConsumidorOtrasDrogas;

    @Column(name = "abstinencia_otras_drogas_meses")
    private Integer abstinenciaOtrasDrogasMeses;

    @Column(name = "tiempo_otras_drogas_meses")
    private Integer tiempoOtrasDrogasMeses;
    

    // ESTILO DE VIDA
    @Column(name = "actividad_fisica")
    private Boolean actividadFisica;

    @Column(name = "descripcion_actividad_fisica", length = 255)
    private String descripcionActividadFisica;

    @Column(name = "actividad_fisica_dias_semana")
    private Integer actividadFisicaDiasSemana;

    // MEDICACIÓN HABITUAL
    @Column(name = "medicacion_habitual")
    private Boolean medicacionHabitual;

    @Column(name = "descripcion_medicacion", length = 255)
    private String descripcionMedicacion;

    @Column(name = "cantidad_medicacion_unidad", length = 100)
    private String cantidadMedicacionUnidad;

    // INCIDENTES
    @Column(name = "incidentes", columnDefinition = "text")
    private String incidentes;

    @Column(name = "fecha_registro")
    private Instant fechaRegistro;


}