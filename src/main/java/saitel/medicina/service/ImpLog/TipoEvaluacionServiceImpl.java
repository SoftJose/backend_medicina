package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.TipoEvaluacion;
import saitel.medicina.repository.TipoEvaluacionRepository;
import saitel.medicina.service.TipoEvaluacionService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoEvaluacionServiceImpl implements TipoEvaluacionService {

    private final TipoEvaluacionRepository tipoEvaluacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoEvaluacion> findAll() {
        return tipoEvaluacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoEvaluacion> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return tipoEvaluacionRepository.findById(id);
    }

    @Override
    @Transactional
    public TipoEvaluacion save(TipoEvaluacion tipoEvaluacion) {
        if (tipoEvaluacion == null) {
            throw new IllegalArgumentException("El tipo de evaluación no puede ser nulo");
        }
        return tipoEvaluacionRepository.save(tipoEvaluacion);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!tipoEvaluacionRepository.existsById(id)) {
                return false;
            }
            tipoEvaluacionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el tipo de evaluación con ID: " + id, e);
        }
    }

    @Transactional
    public TipoEvaluacion update(Integer id, TipoEvaluacion tipoEvaluacion) {
        if (id == null || tipoEvaluacion == null) {
            throw new IllegalArgumentException("El ID y el tipo de evaluación no pueden ser nulos");
        }

        if (!tipoEvaluacionRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el tipo de evaluación con ID: " + id);
        }

        tipoEvaluacion.setId(id);
        return tipoEvaluacionRepository.save(tipoEvaluacion);
    }
}