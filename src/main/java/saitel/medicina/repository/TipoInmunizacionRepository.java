package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.TipoInmunizacion;

@Repository
public interface TipoInmunizacionRepository extends JpaRepository <TipoInmunizacion, Integer>{
}
