package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.EnfermedadActual;
import saitel.medicina.repository.EnfermedadActualRepository;
import saitel.medicina.service.EnfermedadActualService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnfermedadActualServiceImpLog implements EnfermedadActualService {
    
    private final EnfermedadActualRepository enfermedadActualRepository;

    @Override
    @Transactional
    public EnfermedadActual guardar(EnfermedadActual enfermedadActual) {
        if (enfermedadActual == null) {
            throw new IllegalArgumentException("La enfermedad actual no puede ser nula");
        }
        return enfermedadActualRepository.save(enfermedadActual);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnfermedadActual> findAll() {
        return enfermedadActualRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnfermedadActual> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return enfermedadActualRepository.findById(id);
    }

    @Override
    @Transactional
    public EnfermedadActual updateEnfermedadActual(Integer id, EnfermedadActual enfermedadActual) {
        if (id == null || enfermedadActual == null) {
            throw new IllegalArgumentException("El ID y la enfermedad actual no pueden ser nulos");
        }

        if (!enfermedadActualRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la enfermedad actual con ID: " + id);
        }

        enfermedadActual.setId(id);
        return enfermedadActualRepository.save(enfermedadActual);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            enfermedadActualRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la enfermedad actual con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<EnfermedadActual> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return enfermedadActualRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}