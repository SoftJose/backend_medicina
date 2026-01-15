package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.SignoVital;
import java.util.List;
@Repository
public interface SignoVitalRepository extends JpaRepository<SignoVital, Integer>{
	List<SignoVital> findByIdEvaluacion_Id(Integer idEvaluacion);
}
