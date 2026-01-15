package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saitel.medicina.entity.AntecedentesFamiliares;

import java.util.List;

public interface AntecedenteFamiliarRepository extends JpaRepository<AntecedentesFamiliares, Integer> {
	List<AntecedentesFamiliares> findByIdEvaluacion_Id(Integer idEvaluacion);
}
