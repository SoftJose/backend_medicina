package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.RevisionOrganosSistemas;
import java.util.List;
@Repository
public interface RevisionOrganosSistemasRepository extends JpaRepository<RevisionOrganosSistemas, Integer>{
	List<RevisionOrganosSistemas> findByIdEvaluacion_Id(Integer idEvaluacion);
}
