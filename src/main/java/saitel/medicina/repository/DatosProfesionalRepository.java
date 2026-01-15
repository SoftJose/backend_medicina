package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saitel.medicina.entity.DatosProfesional;
import java.util.List;
public interface DatosProfesionalRepository extends JpaRepository<DatosProfesional, Integer> {
	List<DatosProfesional> findByIdEvaluacion_Id(Integer idEvaluacion);
	
}

