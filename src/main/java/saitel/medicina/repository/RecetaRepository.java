package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.RecetasEnviada;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<RecetasEnviada, Integer>{
    List<RecetasEnviada> findByIdEvaluacion_Id(Integer idEvaluacion);
    @Query("SELECT r.numeroReceta FROM RecetasEnviada r WHERE FUNCTION('YEAR', r.fecha) = :anio ORDER BY r.fecha DESC, r.id DESC LIMIT 1")
    String obtenerUltimaRecetaPorAnio(int anio);

    @Query("SELECT COUNT(r) FROM RecetasEnviada r")
    Long contarRecetasGlobal();

}
