package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.HistoriaClinicaPsicologica;
import saitel.medicina.repository.HistorialClinicoPsicologicoRepository;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.service.HistorialClinicoPsicologicoService;

import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HistorialClinicoPsicologicoServiceImpLog implements HistorialClinicoPsicologicoService {
    @Override
    public List<HistoriaClinicaPsicologica> findByIdEmpleado(Integer idEmpleado) {
        return historiaClinicaPsicologicaRepository.findByIdEmpleado(idEmpleado);
    }

    private final HistorialClinicoPsicologicoRepository historiaClinicaPsicologicaRepository;
    private final DatosEmpleadoService datosEmpleadoService;

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaPsicologica> obtenerHistorialClinicoPsicologico() {
        return historiaClinicaPsicologicaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoriaClinicaPsicologica obtenerHistorialClinicoPsicologicoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return historiaClinicaPsicologicaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Historia clínica psicológica no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public HistoriaClinicaPsicologica guardarHistorialClinicoPsicologico(String sucursal, String departamento, String param,HistoriaClinicaPsicologica historiaClinicaPsicologica) {
        if (historiaClinicaPsicologica == null) {
            throw new IllegalArgumentException("La historia clínica psicológica no puede ser nula");
        }
        if (param == null || param.isBlank()) {
            throw new IllegalArgumentException("Debe especificar un valor identificador (cédula, nombre o apellido).");
        }

        List<DatosEmpleados> listaDato = datosEmpleadoService.buscarEmpleado(sucursal, departamento, param);
        if (listaDato.isEmpty()) {
            throw new EntityNotFoundException("No se encontró ningún empleado con esos datos.");
        }


        DatosEmpleados empleado = listaDato.get(0);
        historiaClinicaPsicologica.setIdEmpleado(empleado.getIdEmpleado());
        return historiaClinicaPsicologicaRepository.save(historiaClinicaPsicologica);
    }

    @Override
    @Transactional
    public HistoriaClinicaPsicologica actualizarHistorialClinicoPsicologico(Integer id, HistoriaClinicaPsicologica historiaClinicaPsicologica) {
        if (id == null || historiaClinicaPsicologica == null) {
            throw new IllegalArgumentException("El ID y la historia clínica psicológica no pueden ser nulos");
        }

        if (!historiaClinicaPsicologicaRepository.existsById(id)) {
            throw new EntityNotFoundException("Historia clínica psicológica no encontrada con ID: " + id);
        }

        historiaClinicaPsicologica.setId(id);
        return historiaClinicaPsicologicaRepository.save(historiaClinicaPsicologica);
    }

    @Override
    @Transactional
    public Boolean eliminarHistorialClinicoPsicologico(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!historiaClinicaPsicologicaRepository.existsById(id)) {
                return false;
            }
            historiaClinicaPsicologicaRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la historia clínica psicológica con ID: " + id, e);
        }
    }

    @Override
    public List<HistoriaClinicaPsicologica> filtrar(Integer idEmpleado, Integer anioInicio, Integer anioFin) {
        if (idEmpleado == null) {
            throw new IllegalArgumentException("El idEmpleado no puede ser nulo");
        }
        if (anioInicio != null && anioFin != null) {
            if (anioInicio > anioFin) {
                throw new IllegalArgumentException("El año de inicio no puede ser mayor que el año de fin");
            }
            if (anioInicio < 1900 || anioFin < 1900) {
                throw new IllegalArgumentException("Los años deben ser válidos (>= 1900)");
            }
            LocalDate start = LocalDate.of(anioInicio, 1, 1);
            LocalDate end = LocalDate.of(anioFin, 12, 31);
            return historiaClinicaPsicologicaRepository.findByIdEmpleadoAndFechaAtencionBetween(idEmpleado, start, end);
        }
        return historiaClinicaPsicologicaRepository.findByIdEmpleado(idEmpleado);
    }

}