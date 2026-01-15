package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.ExamenFisico;
import saitel.medicina.repository.ExamenFisicoRepository;
import saitel.medicina.service.ExamenFisicoService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamenFisicoServiceImpLog implements ExamenFisicoService {

    private final ExamenFisicoRepository repository;

    @Override
    @Transactional
    public ExamenFisico guardar(ExamenFisico examen) {
        if (examen == null) {
            throw new IllegalArgumentException("El examen físico no puede ser nulo");
        }
        return repository.save(examen);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamenFisico> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamenFisico> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return repository.findById(id);
    }

    @Override
    @Transactional
    public ExamenFisico updateExamenFisico(Integer id, ExamenFisico examen) {
        if (id == null || examen == null) {
            throw new IllegalArgumentException("El ID y el examen físico no pueden ser nulos");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el examen físico con ID: " + id);
        }

        examen.setId(id);
        return repository.save(examen);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!repository.existsById(id)) {
                return false;
            }
            repository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el examen físico con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<ExamenFisico> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return repository.findByIdEvaluacion_Id(idEvaluacion);
        }
}