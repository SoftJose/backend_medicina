package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.Examenes;
import java.util.List;
@Repository
public interface ExamenesRepository extends JpaRepository<Examenes, Integer>{
	List<Examenes> findByIdEvaluacion_Id(Integer idEvaluacion);
}
