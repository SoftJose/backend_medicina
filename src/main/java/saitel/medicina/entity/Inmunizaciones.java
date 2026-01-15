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
@Table(name = "tbl_inmunizaciones")
public class Inmunizaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inmunizaciones", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_inmunizacion", nullable = false)
    private TipoInmunizacion idTipoInmunizacion;

    @Size(max = 20)
    @NotNull
    @Column(name = "dosis", nullable = false, length = 20)
    private String dosis;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Size(max = 50)
    @Column(name = "lote", length = 50)
    private String lote;

    @Column(name = "esquema_completo")
    private Boolean esquemaCompleto;

    @Size(max = 150)
    @Column(name = "responsable_vacunacion", length = 150)
    private String responsableVacunacion;

    @Size(max = 150)
    @Column(name = "establecimiento_salud", length = 150)
    private String establecimientoSalud;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_evaluacion", nullable = false)
    private TipoEvaluacion idTipoEvaluacion;

    @NotNull
    @Column(name = "id_empleado", nullable = false)
    private Integer idEmpleado;

}