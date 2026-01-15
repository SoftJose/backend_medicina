package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_condiciones_retiro")
public class CondicionesRetiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condicion_retiro", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @Size(max = 20)
    @Column(name = "resultado_condicion", length = 20)
    private String resultadoCondicion;

    @Column(name = "observaciones_retiro", length = Integer.MAX_VALUE)
    private String observacionesRetiro;

}