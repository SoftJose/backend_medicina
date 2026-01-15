package saitel.medicina.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_configuracion_medicina")
public class ConfiguracionMedicina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion_medicina", nullable = false)
    private Integer id;

    @Column(name = "dias_dentro_rango")
    private Integer diasDentroRango;

    @Column(name = "dias_fuera_rango")
    private Integer diasFueraRango;

}