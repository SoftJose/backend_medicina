package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.Evaluacion;
import saitel.medicina.entity.FichaSocial;
import saitel.medicina.repository.EvaluacionRepository;
import saitel.medicina.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements EvaluacionService {
    
    private final EvaluacionRepository evaluacionRepository;
    private final FichaSocialService fichaSocialService;
    private final TipoEvaluacionService tipoEvaluacionService;
    private final DatosEmpleadoService datosEmpleadoService;

    @Override
    @Transactional(readOnly = true)
    public List<Evaluacion> findAll() {
        return evaluacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Evaluacion> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return evaluacionRepository.findById(id);
    }

    @Override
    @Transactional
    public Evaluacion save(String sucursal, String departamento, String param, Evaluacion evaluacion) {
        if (evaluacion == null) {
            throw new IllegalArgumentException("La evaluación no puede ser nula");
        }
        if (param == null || param.isBlank()) {
            throw new IllegalArgumentException("Debe especificar un valor identificador (cédula, nombre o apellido).");
        }
        if (evaluacion.getTipoEvaluacion() == null || evaluacion.getTipoEvaluacion().getId() == null) {
            throw new IllegalArgumentException("Debe especificar un tipo de evaluación válido.");
        }
        tipoEvaluacionService.findById(evaluacion.getTipoEvaluacion().getId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de evaluación no encontrado con ID: " + evaluacion.getTipoEvaluacion().getId()));

        List<DatosEmpleados> listaDato = datosEmpleadoService.buscarEmpleado(sucursal, departamento, param);
        if (listaDato.isEmpty()) {
            throw new EntityNotFoundException("No se encontró ningún empleado con esos datos.");
        }

        DatosEmpleados empleado = listaDato.get(0);
        evaluacion.setIdEmpleado(empleado.getIdEmpleado());

        FichaSocial ficha = fichaSocialService.findByIdEmpleado(empleado.getIdEmpleado())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró una ficha social para el empleado con ID: " + empleado.getIdEmpleado()));

        evaluacion.setFichaSocial(ficha);
        evaluacion.setFecha(LocalDate.now());

        return evaluacionRepository.save(evaluacion);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!evaluacionRepository.existsById(id)) {
                return false;
            }
            evaluacionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la evaluación con ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public Evaluacion update(Integer id, Evaluacion evaluacion) {
        if (id == null || evaluacion == null) {
            throw new IllegalArgumentException("El ID y la evaluación no pueden ser nulos");
        }

        if (!evaluacionRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la evaluación con ID: " + id);
        }


        if (evaluacion.getTipoEvaluacion() == null || evaluacion.getTipoEvaluacion().getId() == null) {
            throw new IllegalArgumentException("Debe especificar un tipo de evaluación válido.");
        }
        tipoEvaluacionService.findById(evaluacion.getTipoEvaluacion().getId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de evaluación no encontrado con ID: " + evaluacion.getTipoEvaluacion().getId()));

  
        if (evaluacion.getFichaSocial() == null || evaluacion.getFichaSocial().getId() == null) {
            throw new IllegalArgumentException("Debe especificar una ficha social válida.");
        }
        fichaSocialService.obtenerPorId(evaluacion.getFichaSocial().getId())
                .orElseThrow(() -> new EntityNotFoundException("Ficha social no encontrada con ID: " + evaluacion.getFichaSocial().getId()));

        evaluacion.setId(id);

        return evaluacionRepository.save(evaluacion);
    }
@Transactional
public Evaluacion firmarEmpleado(Integer id) {
     if (id == null) {
        throw new IllegalArgumentException("El ID no puede ser nulo");
    }
    Evaluacion evaluacion = evaluacionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Evaluación no encontrada con ID: " + id));

    evaluacion.setFirmaEmpleado(true);
    return evaluacionRepository.save(evaluacion);
}

@Override
@Transactional(readOnly = true)
public List<Evaluacion> findByEmpleadoId(Integer idEmpleado) {
    if (idEmpleado == null) {
        throw new IllegalArgumentException("El idEmpleado no puede ser nulo");
    }
    return evaluacionRepository.findByIdEmpleado(idEmpleado);
}
    

}