package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "tbl_datos_profesional")
public class DatosProfesional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_datos_profesional", nullable = false)
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

    @Column(name = "fecha")
    private LocalDate fecha;

    @NotNull
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombres_apellidos", nullable = false, length = 150)
    private String nombresApellidos;

    @Size(max = 50)
    @Column(name = "codigo_profesional", length = 50)
    private String codigoProfesional;

    @Column(name = "id_profesional")
    private Integer idProfesional;

    @ColumnDefault("false")
    @Column(name = "firma_sello")
    private Boolean firmaSello;

}