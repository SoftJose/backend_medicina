package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Subselect;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "tbl_signo_vital")
@Subselect("SELECT * FROM tbl_signo_vital")
public class SignoVital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_signo_vital", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "id_empleado", nullable = false)
    private Integer idEmpleado;

    @NotNull
    @Column(name = "presion_arterial_a", nullable = false)
    private Integer presionArterialA;

    @ColumnDefault("false")
    @Column(name = "rango_pa_a")
    private Boolean rangoPaA;

    @NotNull
    @Column(name = "presion_arterial_b", nullable = false)
    private Integer presionArterialB;

    @ColumnDefault("false")
    @Column(name = "rango_pa_b")
    private Boolean rangoPaB;

    @NotNull
    @Column(name = "frecuencia_cardicaca", nullable = false)
    private Integer frecuenciaCardicaca;

    @ColumnDefault("false")
    @Column(name = "rango_fc")
    private Boolean rangoFc;

    @NotNull
    @Column(name = "saturacion_oxg", nullable = false)
    private Integer saturacionOxg;

    @ColumnDefault("false")
    @Column(name = "rango_so")
    private Boolean rangoSo;

    @Column(name = "test_romberg")
    private Boolean testRomberg;

    @Column(name = "fecha_signos")
    private LocalDate fechaSignos;

    @Column(name = "hora_signos")
    private LocalTime horaSignos;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "fuera_de_rango", nullable = false)
    private Boolean fueraDeRango = false;

    @ColumnDefault("1")
    @Column(name = "dias_sin_test")
    private Integer diasSinTest;

    @Column(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria;

    @Column(name = "masa_corporal")
    private Double masaCorporal;

    @Column(name = "perimetro_abdominal")
    private Double perimetroAbdominal;

    @Column(name = "peso")
    private Double peso;

    @Column(name = "talla")
    private Double talla;

    @Column(name = "temperatura")
    private Double temperatura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_evaluacion")
    private Evaluacion idEvaluacion;

}