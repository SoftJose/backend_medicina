package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.ActividadesExtraLaborale;

import java.util.List;

@Repository
public interface ActividadesExtraRepository extends JpaRepository <ActividadesExtraLaborale, Integer> {
	List<ActividadesExtraLaborale> findByIdEvaluacion_Id(Integer idEvaluacion);
}
