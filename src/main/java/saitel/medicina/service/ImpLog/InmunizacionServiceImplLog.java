   package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.Inmunizaciones;
import saitel.medicina.repository.InmunizacionRepository;
import saitel.medicina.service.InmunizacionService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InmunizacionServiceImplLog implements InmunizacionService {
    @Override
    @Transactional(readOnly = true)
    public List<Inmunizaciones> findByIdEmpleado(Integer idEmpleado) {
        if (idEmpleado == null) {
            throw new IllegalArgumentException("El idEmpleado no puede ser nulo");
        }
        return inmunizacionRepository.findByIdEmpleado(idEmpleado);
    }
    
    private final InmunizacionRepository inmunizacionRepository;

    @Override
    @Transactional
    public Inmunizaciones save(Inmunizaciones inmunizacion) {
        if (inmunizacion == null) {
            throw new IllegalArgumentException("La inmunización no puede ser nula");
        }
        return inmunizacionRepository.save(inmunizacion);
    }

     @Override
    @Transactional(readOnly = true)
    public List<Inmunizaciones> findByEvaluacionId(Integer idEvaluacion) {
        if (idEvaluacion == null) {
            throw new IllegalArgumentException("El idEvaluacion no puede ser nulo");
        }
    return inmunizacionRepository.findByIdTipoEvaluacion_Id(idEvaluacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inmunizaciones> findAll() {
        return inmunizacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inmunizaciones> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return inmunizacionRepository.findById(id);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!inmunizacionRepository.existsById(id)) {
                return false;
            }
            inmunizacionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la inmunización con ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public Inmunizaciones updateInmunizacion(Integer id, Inmunizaciones inmunizacion) {
        if (id == null || inmunizacion == null) {
            throw new IllegalArgumentException("El ID y la inmunización no pueden ser nulos");
        }

        if (!inmunizacionRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la inmunización con ID: " + id);
        }

        inmunizacion.setId(id);
        return inmunizacionRepository.save(inmunizacion);
    }
}