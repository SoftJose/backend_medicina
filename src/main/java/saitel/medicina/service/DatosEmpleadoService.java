package saitel.medicina.service;

import java.util.List;
import java.util.Optional;
import saitel.medicina.dto.PacienteInfoDto;
import saitel.medicina.entity.DatosEmpleados;

public interface DatosEmpleadoService {
    List<DatosEmpleados> obtenerTodos();
    Optional<DatosEmpleados> obtenerPorId(Integer id);
    List<DatosEmpleados> buscarEmpleado(String sucursal, String departamento, String param);
    Optional<DatosEmpleados> obtenerEmpleadoPorAlias(String alias);
    List<PacienteInfoDto> obtenerInfoPacientes();
}
