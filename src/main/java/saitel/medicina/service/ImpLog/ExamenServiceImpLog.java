package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.Examenes;
import saitel.medicina.repository.ExamenesRepository;
import saitel.medicina.service.ExamenService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamenServiceImpLog implements ExamenService {
    
    private final ExamenesRepository examenesRepository;

    @Override
    @Transactional
    public Examenes guardar(Examenes examen) {
        if (examen == null) {
            throw new IllegalArgumentException("El examen no puede ser nulo");
        }
        return examenesRepository.save(examen);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Examenes> findAll() {
        return examenesRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Examenes> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return examenesRepository.findById(id);
    }

    @Override
    @Transactional
    public Examenes updateExamen(Integer id, Examenes examen) {
        if (id == null || examen == null) {
            throw new IllegalArgumentException("El ID y el examen no pueden ser nulos");
        }

        if (!examenesRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el examen con ID: " + id);
        }

        examen.setId(id);
        return examenesRepository.save(examen);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!examenesRepository.existsById(id)) {
                return false;
            }
            examenesRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el examen con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<Examenes> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return examenesRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}