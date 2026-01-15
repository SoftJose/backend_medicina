package saitel.medicina.service.ImpLog;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.Antecedentes;
import saitel.medicina.repository.AntecedentesRepository;
import saitel.medicina.service.AntecedentesService;

@Service
@RequiredArgsConstructor
public class AntecedentesServiceImpLog implements AntecedentesService {

    private final AntecedentesRepository antecedentesRepository;

    @Override
    public Antecedentes guardarAntecedentes(Antecedentes antecedentes) {
        if (antecedentes == null) {
            throw new IllegalArgumentException("Los antecedentes no pueden ser nulos");
        }
        antecedentes.setFechaRegistro(Instant.now());
        System.out.println("Fecha de registro: " + antecedentes.getId());
        return antecedentesRepository.save(antecedentes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Antecedentes> findAll() {
        return antecedentesRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Antecedentes> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return antecedentesRepository.findById(id);
    }

    @Override
    public Antecedentes updateAntecedente(Integer id, Antecedentes antecedentes) {
        if (id == null || antecedentes == null) {
            throw new IllegalArgumentException("El ID y los antecedentes no pueden ser nulos");
        }

        if (!antecedentesRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el antecedente con ID: " + id);
        }

        antecedentes.setId(id);
        return antecedentesRepository.save(antecedentes);
    }

    @Override
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            antecedentesRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar los antecedentes con ID: " + id, e);
        }
    }
        @Override
        @Transactional(readOnly = true)
        public List<Antecedentes> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return antecedentesRepository.findByEvaluacion_Id(idEvaluacion);
        }
}

