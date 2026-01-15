package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.Diagnostico;
import java.util.List;
@Repository
public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Integer> {
	List<Diagnostico> findByIdEvaluacion_Id(Integer idEvaluacion);
}
