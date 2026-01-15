package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saitel.medicina.entity.CondicionesRetiro;

import java.util.List;

public interface CondicionesRetiroRepository extends JpaRepository<CondicionesRetiro, Integer> {
    List<CondicionesRetiro> findByIdEvaluacion_Id(Integer idEvaluacionId);
}
