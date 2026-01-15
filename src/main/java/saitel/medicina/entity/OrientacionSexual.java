package saitel.medicina.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "tbl_orientacion_sexual")
public class OrientacionSexual {
    @Id
    @Column(name = "id_orientacion_sexual", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "nombre", nullable = false, length = Integer.MAX_VALUE)
    private String nombre;
        public void setIdOrientacionSexual(Integer idOrientacionSexual) {
            this.id = idOrientacionSexual;
        }

}
