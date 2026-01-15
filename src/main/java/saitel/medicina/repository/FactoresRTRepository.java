package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.FactoresRiesgoTrabajo;
import java.util.List;
@Repository
public interface FactoresRTRepository extends JpaRepository<FactoresRiesgoTrabajo, Integer> {
	List<FactoresRiesgoTrabajo> findByIdEvaluacion_Id(Integer idEvaluacion);
}
