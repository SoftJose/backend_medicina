package saitel.medicina.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.HistoriaClinicaPsicologica;

@Repository
public interface HistorialClinicoPsicologicoRepository extends JpaRepository <HistoriaClinicaPsicologica, Integer>{
    List<HistoriaClinicaPsicologica> findByIdEmpleado(Integer idEmpleado);
    List<HistoriaClinicaPsicologica> findByIdEmpleadoAndFechaAtencionBetween(Integer idEmpleado, LocalDate inicio, LocalDate fin);
@Query("""
    SELECT h
    FROM HistoriaClinicaPsicologica h
    JOIN DatosEmpleados d ON h.idEmpleado = d.idEmpleado
    WHERE (:sucursal IS NULL OR d.sucursal = :sucursal)
      AND (:departamento IS NULL OR d.departamento = :departamento)
      AND (:idEmpleado IS NULL OR h.idEmpleado = :idEmpleado)
      AND (h.fechaAtencion BETWEEN :fechaInicio AND :fechaFin)
    ORDER BY d.sucursal ASC, h.fechaAtencion ASC
""")
    Page<HistoriaClinicaPsicologica> findHistoriaClinicaPsicologica(
            @Param("sucursal") String sucursal,
            @Param("departamento") String departamento,
            @Param("idEmpleado") Integer idEmpleado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable);
}
