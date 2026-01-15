package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.CitaMedica;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.repository.CitaMedicaRepository;
import saitel.medicina.service.CitaMedicaService;
import saitel.medicina.service.DatosEmpleadoService;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CitaMedicaServiceImpLog implements CitaMedicaService {

    private final CitaMedicaRepository citaMedicaRepository;
    private final DatosEmpleadoService datosEmpleadoService;

    @Override
    @Transactional(readOnly = true)
    public List<CitaMedica> listaCitaMedicas() {
        return citaMedicaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CitaMedica> findById(Integer id) {
        if (id == null) throw new IllegalArgumentException("El Id no puede ser nulo");
        return Optional.ofNullable(citaMedicaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita médica no encontrada con ID: " + id)));
    }

    @Override
    public CitaMedica save(String sucursal, String departamento, String param, CitaMedica citaMedica) {
        if (citaMedica == null) {
            throw new IllegalArgumentException("La cita médica no puede ser nula");
        }
        if (param == null || param.isBlank()) {
            throw new IllegalArgumentException("Debe especificar un valor identificador (cédula, nombre o apellido).");
        }

        List<DatosEmpleados> listaDato = datosEmpleadoService.buscarEmpleado(sucursal, departamento, param);
        if (listaDato.isEmpty()) {
            throw new EntityNotFoundException("No se encontró ningún empleado con esos datos.");
        }
        DatosEmpleados empleado = listaDato.get(0);
        citaMedica.setIdEmpleado(empleado.getIdEmpleado());

        return citaMedicaRepository.save(citaMedica);
    }

    @Override
    public CitaMedica update(Integer id, CitaMedica citaMedica) {
        if (id == null || citaMedica == null) {
            throw new IllegalArgumentException("El ID y la cita médica no pueden ser nulos");
        }
        if (!citaMedicaRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la cita médica con ID: " + id);
        }
        citaMedica.setId(id);

        return citaMedicaRepository.save(citaMedica);
    }

    @Override
    public Boolean delete(Integer id) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");

        try {
            citaMedicaRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la cita médica con ID: " + id, e);
        }
    }
}
