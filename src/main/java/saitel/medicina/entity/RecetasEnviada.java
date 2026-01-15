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
@Table(name = "tbl_recetas_enviadas")
public class RecetasEnviada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @Size(max = 50)
    @Column(name = "numero_receta", nullable = false, length = 50)
    private String numeroReceta;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Size(max = 100)
    @Column(name = "doctor_a", nullable = false, length = 100)
    private String doctorA;

    @Column(name = "diagnostico", length = Integer.MAX_VALUE)
    private String diagnostico;

    @Column(name = "receta", length = Integer.MAX_VALUE)
    private String receta;
    
    @Column(name = "impresa", nullable = false)
    private boolean impresa;
    
    @Column(name = "firma", nullable = false)
    private boolean firma = false;

        @Column(name = "indicaciones", columnDefinition = "TEXT", nullable = true)
        private String indicaciones;
    
}