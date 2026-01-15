package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saitel.medicina.entity.DetalleEvaluacionRetiro;
import java.util.List;


public interface DetalleEvaluacionRetiroRepository extends JpaRepository <DetalleEvaluacionRetiro, Integer> {
	List<DetalleEvaluacionRetiro> findByIdEvaluacion_Id(Integer idEvaluacion);
}
