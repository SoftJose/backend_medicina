package saitel.medicina.service.ImpLog;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saitel.medicina.entity.AptitudLaboral;
import saitel.medicina.repository.AptitudLaboralRepository;
import saitel.medicina.service.AptitudMedicaService;

@Service
@RequiredArgsConstructor
public class AptitudMedicaServiceImpLog implements AptitudMedicaService {
    @Override
    public Optional<AptitudLaboral> findByIdEvaluacion(Integer idEvaluacion) {
        if (idEvaluacion == null) {
            throw new IllegalArgumentException("El idEvaluacion no puede ser nulo");
        }
        return aptitudLaboralRepository.findByIdEvaluacion_Id(idEvaluacion);
    }

    private final AptitudLaboralRepository aptitudLaboralRepository;

    @Override
    public AptitudLaboral guardar(AptitudLaboral aptitudMedica) {
        if (aptitudMedica == null) {
            throw new IllegalArgumentException("La aptitud médica no puede ser nula");
        }
        return aptitudLaboralRepository.save(aptitudMedica);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AptitudLaboral> findAll() {
        return aptitudLaboralRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AptitudLaboral> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return aptitudLaboralRepository.findById(id);
    }

    @Override
    public AptitudLaboral updateAptitud(Integer id, AptitudLaboral aptitudMedica) {
        if (id == null || aptitudMedica == null) {
            throw new IllegalArgumentException("El ID y la aptitud médica no pueden ser nulos");
        }

        if (!aptitudLaboralRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la aptitud médica con ID: " + id);
        }

        aptitudMedica.setId(id);
        return aptitudLaboralRepository.save(aptitudMedica);
    }

    @Override
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            aptitudLaboralRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la aptitud médica con ID: " + id, e);
        }
    }
}