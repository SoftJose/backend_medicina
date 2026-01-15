package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import saitel.medicina.entity.Evaluacion;

import java.time.LocalDate;
import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {
	List<Evaluacion> findByIdEmpleado(Integer idEmpleado);
	@Query("SELECT DISTINCT e FROM Evaluacion e " +
       "WHERE e.fecha BETWEEN :fechaInicio AND :fechaFin " +
       "ORDER BY e.fecha")
List<Evaluacion> findByFechaAtencionBetween(
    @Param("fechaInicio") LocalDate fechaInicio,
    @Param("fechaFin") LocalDate fechaFin
);
@Query("SELECT e FROM Evaluacion e " +
       "JOIN DatosEmpleados de ON e.idEmpleado = de.idEmpleado " +
       "WHERE (:sucursal IS NULL OR de.sucursal = :sucursal) " +
       "AND (:paciente IS NULL OR de.nombre LIKE %:paciente%) " +
       "AND (:tipoFormulario IS NULL OR e.tipoEvaluacion.nombreEvaluacion = :tipoFormulario) " +
    "AND (:motivoConsulta IS NULL OR e.resultado LIKE %:motivoConsulta%) " +
       "AND e.fecha BETWEEN :fechaInicio AND :fechaFin")
List<Evaluacion> filtrarEvaluaciones(
    @Param("sucursal") String sucursal,
    @Param("paciente") String paciente,
    @Param("tipoFormulario") String tipoFormulario,
    @Param("motivoConsulta") String motivoConsulta,
    @Param("fechaInicio") LocalDate fechaInicio,
    @Param("fechaFin") LocalDate fechaFin
);
}
