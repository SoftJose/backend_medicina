package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.AntecedentesTrabajo;
import java.util.List;

@Repository
public interface AntecedentesTrabajoRepository extends JpaRepository <AntecedentesTrabajo, Integer>{
	List<AntecedentesTrabajo> findByIdEvaluacion_Id(Integer idEvaluacion);
}
