package saitel.medicina.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.CitaMedica;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica,Integer>{
       @Query("SELECT DISTINCT c FROM CitaMedica c " +
            "WHERE c.fechaCita BETWEEN :fechaInicio AND :fechaFin " +
            "AND (:departamento IS NULL OR LOWER(c.departamento) = LOWER(:departamento)) " +
            "ORDER BY c.horaCita")
    List<CitaMedica> findByFechaCitaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("departamento") String departamento
    );
}
