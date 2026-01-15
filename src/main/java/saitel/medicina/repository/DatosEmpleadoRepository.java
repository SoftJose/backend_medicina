package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saitel.medicina.entity.DatosEmpleados;

import java.util.List;
import java.util.Optional;

public interface DatosEmpleadoRepository extends JpaRepository <DatosEmpleados, Integer>{
    @Query("""
    SELECT d FROM DatosEmpleados d
    WHERE d.estado = true
      AND (:sucursal IS NULL OR d.sucursal = :sucursal)
      AND (:departamento IS NULL OR d.departamento = :departamento)
      AND (
        :param IS NULL OR (
          LOWER(d.cedula) = LOWER(:param)
          OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :param, '%'))
          OR LOWER(d.apellido) LIKE LOWER(CONCAT('%', :param, '%'))
        )
      )""")
    List<DatosEmpleados> buscarEmpleado(
            @Param("sucursal") String sucursal,
            @Param("departamento") String departamento,
            @Param("param") String param
    );

    Optional<DatosEmpleados> findByAlias(String alias);
}
