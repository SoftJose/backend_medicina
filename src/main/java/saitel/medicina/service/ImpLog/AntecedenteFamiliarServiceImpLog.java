package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.AntecedentesFamiliares;
import saitel.medicina.repository.AntecedenteFamiliarRepository;
import saitel.medicina.service.AntecedenteFamiliarService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AntecedenteFamiliarServiceImpLog implements AntecedenteFamiliarService {
    private final AntecedenteFamiliarRepository repository;
    @Override
    public AntecedentesFamiliares save(AntecedentesFamiliares antecedente) {
        if (antecedente == null) {
            throw new IllegalArgumentException("El antecedente familiar no puede ser nulo");
        }
        return repository.save(antecedente);
    }

    @Override
    public AntecedentesFamiliares updateAntecedente(Integer id, AntecedentesFamiliares antecedente) {
        if (id == null || antecedente == null)
            throw new IllegalArgumentException("El ID y el antecedente familiar no pueden ser nulos");

        findById(id);

        antecedente.setId(id);
        return repository.save(antecedente);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AntecedentesFamiliares> findAll() {
        return repository.findAll();
    }


    @Override
    public Optional<AntecedentesFamiliares> findById(Integer id) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");

        return Optional.ofNullable(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Antecedente familiar no encontrado con ID: " + id)));
    }


    @Override
    public Boolean deleteById(Integer id) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");

        try {
            repository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el registro con ID: " + id, e);
        }
    }
        @Override
        @Transactional(readOnly = true)
        public List<AntecedentesFamiliares> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return repository.findByIdEvaluacion_Id(idEvaluacion);
        }


}
