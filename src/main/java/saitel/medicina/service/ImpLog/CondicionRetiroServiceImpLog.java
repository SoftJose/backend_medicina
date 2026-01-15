package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.CondicionesRetiro;
import saitel.medicina.repository.CondicionesRetiroRepository;
import saitel.medicina.service.CondicionRetiroService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CondicionRetiroServiceImpLog implements CondicionRetiroService {
    
    private final CondicionesRetiroRepository repository;

    @Override
    public CondicionesRetiro save(CondicionesRetiro entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La condición de retiro no puede ser nula");
        }
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CondicionesRetiro> findByIdEvaluacion(Integer idEvaluacion) {
        if (idEvaluacion == null) {
            throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
        }
        return repository.findByIdEvaluacion_Id(idEvaluacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CondicionesRetiro> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CondicionesRetiro> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return repository.findById(id);
    }

    @Override
    public CondicionesRetiro updateCondicionRetiro(Integer id, CondicionesRetiro entity) {
        if (id == null || entity == null) {
            throw new IllegalArgumentException("El ID y la condición de retiro no pueden ser nulos");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la condición de retiro con ID: " + id);
        }

        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            repository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la condición de retiro con ID: " + id, e);
        }
    }
}