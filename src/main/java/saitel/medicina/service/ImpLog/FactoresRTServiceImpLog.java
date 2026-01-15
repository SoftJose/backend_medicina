package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.FactoresRiesgoTrabajo;
import saitel.medicina.repository.FactoresRTRepository;
import saitel.medicina.service.FactoresRTService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactoresRTServiceImpLog implements FactoresRTService {
    
    private final FactoresRTRepository factoresRTRepository;

    @Override
    @Transactional
    public FactoresRiesgoTrabajo guardar(FactoresRiesgoTrabajo factoresRiesgoTrabajo) {
        if (factoresRiesgoTrabajo == null) {
            throw new IllegalArgumentException("Los factores de riesgo de trabajo no pueden ser nulos");
        }
        return factoresRTRepository.save(factoresRiesgoTrabajo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactoresRiesgoTrabajo> findAll() {
        return factoresRTRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FactoresRiesgoTrabajo> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return factoresRTRepository.findById(id);
    }

    @Override
    @Transactional
    public FactoresRiesgoTrabajo updateFactores(Integer id, FactoresRiesgoTrabajo factoresRiesgoTrabajo) {
        if (id == null || factoresRiesgoTrabajo == null) {
            throw new IllegalArgumentException("El ID y los factores de riesgo no pueden ser nulos");
        }

        if (!factoresRTRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontraron los factores de riesgo con ID: " + id);
        }

        factoresRiesgoTrabajo.setId(id);
        return factoresRTRepository.save(factoresRiesgoTrabajo);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!factoresRTRepository.existsById(id)) {
                return false;
            }
            factoresRTRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar los factores de riesgo con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<FactoresRiesgoTrabajo> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return factoresRTRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}