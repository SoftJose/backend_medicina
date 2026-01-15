package saitel.medicina.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tbl_ficha_social")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FichaSocial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ficha_social", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "id_empleado", nullable = false)
    private Integer idEmpleado;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "contactos_emergencia")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object contactosEmergencia;

    @Column(name = "genograma", length = Integer.MAX_VALUE)
    private String genograma;

    @Size(max = 50)
    @NotNull
    @Column(name = "numero_historia_clinica", nullable = false, length = 50)
    private String numeroHistoriaClinica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_religion")
    private Religion religion;

}