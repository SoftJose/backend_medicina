package saitel.medicina.service.ImpLog;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.AntecedentesTrabajo;
import saitel.medicina.repository.AntecedentesTrabajoRepository;
import saitel.medicina.service.AnTrabajoService;

@Service
@RequiredArgsConstructor
public class AntecedenteTrabajoServiceImpLog implements AnTrabajoService {

    private final AntecedentesTrabajoRepository repository;

    @Override
    public AntecedentesTrabajo guardar(AntecedentesTrabajo antecedentesTrabajo) {
        if (antecedentesTrabajo == null) {
            throw new IllegalArgumentException("El antecedente de trabajo no puede ser nulo");
        }
        return repository.save(antecedentesTrabajo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AntecedentesTrabajo> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AntecedentesTrabajo> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return repository.findById(id);
    }

    @Override
    public AntecedentesTrabajo updateAntecedente(Integer id, AntecedentesTrabajo antecedentesTrabajo) {
        if (id == null || antecedentesTrabajo == null) {
            throw new IllegalArgumentException("El ID y el antecedente de trabajo no pueden ser nulos");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el antecedente de trabajo con ID: " + id);
        }

        antecedentesTrabajo.setId(id);
        return repository.save(antecedentesTrabajo);
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
            throw new RuntimeException("Error al eliminar el antecedente de trabajo con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<AntecedentesTrabajo> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return repository.findByIdEvaluacion_Id(idEvaluacion);
        }
}

