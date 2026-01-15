package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saitel.medicina.entity.MotivoConsulta;

import java.util.List;

public interface MotivoConsultaRepository extends JpaRepository<MotivoConsulta, Integer> {
    List<MotivoConsulta> findByIdEvaluacion_Id(Integer idEvaluacionId);
}
