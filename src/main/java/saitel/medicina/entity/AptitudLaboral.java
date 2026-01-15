package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_aptitud_laboral")
public class AptitudLaboral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concepto_aptitud", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @Size(max = 30)
    @Column(name = "resultado_aptitud", length = 30)
    private String resultadoAptitud;

    @Column(name = "detalle_observaciones", length = Integer.MAX_VALUE)
    private String detalleObservaciones;

    @Column(name = "limitacion", length = Integer.MAX_VALUE)
    private String limitacion;

        @Column(name = "reubicacion")
        private String reubicacion;
}