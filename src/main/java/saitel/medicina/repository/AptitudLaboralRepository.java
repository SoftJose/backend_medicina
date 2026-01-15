package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.AptitudLaboral;
import java.util.Optional;
@Repository
public interface AptitudLaboralRepository extends JpaRepository<AptitudLaboral, Integer>{

Optional<AptitudLaboral> findByIdEvaluacion_Id(Integer idEvaluacion);

}
