package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.Inmunizaciones;

@Repository
public interface InmunizacionRepository extends JpaRepository<Inmunizaciones, Integer> {
	List<Inmunizaciones> findByIdTipoEvaluacion_Id(Integer idTipoEvaluacion);
	List<Inmunizaciones> findByIdEmpleado(Integer idEmpleado);
}
