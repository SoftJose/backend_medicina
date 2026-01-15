package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.Recomendaciones;
import saitel.medicina.repository.RecomendacionesRepository;
import saitel.medicina.service.RecomendacionTratamientoService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecomendacionTratamientoServiceImpLog implements RecomendacionTratamientoService {
    
    private final RecomendacionesRepository recomendacionRepository;

    @Override
    @Transactional
    public Recomendaciones guardar(Recomendaciones recomendacionTratamiento) {
        if (recomendacionTratamiento == null) {
            throw new IllegalArgumentException("La recomendación no puede ser nula");
        }
        return recomendacionRepository.save(recomendacionTratamiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recomendaciones> findAll() {
        return recomendacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recomendaciones> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return recomendacionRepository.findById(id);
    }

    @Override
    @Transactional
    public Recomendaciones updateRecomendacion(Integer id, Recomendaciones recomendacionTratamiento) {
        if (id == null || recomendacionTratamiento == null) {
            throw new IllegalArgumentException("El ID y la recomendación no pueden ser nulos");
        }

        if (!recomendacionRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la recomendación con ID: " + id);
        }

        recomendacionTratamiento.setId(id);
        return recomendacionRepository.save(recomendacionTratamiento);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!recomendacionRepository.existsById(id)) {
                return false;
            }
            recomendacionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la recomendación con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<Recomendaciones> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return recomendacionRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}