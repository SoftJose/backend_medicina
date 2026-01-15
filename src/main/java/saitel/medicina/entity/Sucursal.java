package saitel.medicina.entity;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Immutable
@Table(name="f_vta_sucursal", schema = "medicina")
@Subselect("SELECT * FROM f_vta_sucursal")
@Getter
@Setter
public class Sucursal {
    @Id
    
    @Column(name="id_sucursal")
    private Long idSucursal;

    @Column(name="sucursal")
    private String sucursal;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "estado")
    private Boolean estado;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "eliminado")
    private Boolean eliminado;
}

