package saitel.medicina.service.ImpLog;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import saitel.medicina.dto.PacienteInfoDto;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.repository.DatosEmpleadoRepository;
import saitel.medicina.service.DatosEmpleadoService;

@Service
@RequiredArgsConstructor
public class DatosEmpleadoServiceImpLog implements DatosEmpleadoService {

    private final DatosEmpleadoRepository datosEmpleadoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DatosEmpleados> obtenerTodos() {
        return datosEmpleadoRepository.findAll();
    }

    @Override
    public Optional<DatosEmpleados> obtenerPorId(Integer idEmpleado) {
        if (idEmpleado == null) {
        throw new IllegalArgumentException("El idEmpleado no puede ser nulo");
        }
        return Optional.ofNullable(datosEmpleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + idEmpleado)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatosEmpleados> buscarEmpleado(String sucursal, String departamento, String param) {
        return datosEmpleadoRepository.buscarEmpleado(sucursal, departamento, param);
    }
    
    /**
     * @param alias
     * @return
     */
    @Override
    public Optional<DatosEmpleados> obtenerEmpleadoPorAlias(String alias) {
       return datosEmpleadoRepository.findByAlias(alias);
    }

    @Override
    public List<PacienteInfoDto> obtenerInfoPacientes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerInfoPacientes'");
    }
    
    }
