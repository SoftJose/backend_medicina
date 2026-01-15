package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.Antecedentes;
import java.util.List;

@Repository
public interface AntecedentesRepository extends JpaRepository<Antecedentes, Integer>{
	List<Antecedentes> findByEvaluacion_Id(Integer idEvaluacion);
}
