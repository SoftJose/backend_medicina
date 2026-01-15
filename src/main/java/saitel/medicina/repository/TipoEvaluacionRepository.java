package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saitel.medicina.entity.TipoEvaluacion;

public interface TipoEvaluacionRepository extends JpaRepository<TipoEvaluacion, Integer> {
}