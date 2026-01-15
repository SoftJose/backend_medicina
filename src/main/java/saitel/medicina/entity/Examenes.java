package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tbl_examenes")
public class Examenes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre_examen", nullable = false, length = 100)
    private String nombreExamen;

    @NotNull
    @Column(name = "fecha_examen", nullable = false)
    private LocalDate fechaExamen;

    @Column(name = "resultado", length = Integer.MAX_VALUE)
    private String resultado;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

}