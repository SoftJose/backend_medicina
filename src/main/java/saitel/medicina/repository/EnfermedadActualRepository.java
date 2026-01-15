package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.EnfermedadActual;
import java.util.List;
@Repository
public interface EnfermedadActualRepository extends JpaRepository<EnfermedadActual, Integer> {
	List<EnfermedadActual> findByIdEvaluacion_Id(Integer idEvaluacion);
}