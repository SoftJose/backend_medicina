package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saitel.medicina.entity.DatosGeneralesCertificados;
import java.util.List;

public interface DatosGeneralesRepository extends JpaRepository<DatosGeneralesCertificados, Integer> {
    List<DatosGeneralesCertificados> findByEvaluacion_Id(Integer idEvaluacion);
}
