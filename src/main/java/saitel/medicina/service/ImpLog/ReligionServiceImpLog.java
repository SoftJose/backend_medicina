package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.Religion;
import saitel.medicina.repository.ReligionRepository;
import saitel.medicina.service.ReligionService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReligionServiceImpLog implements ReligionService {

    private final ReligionRepository religionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Religion> obtenerTodas() {
        return religionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Religion> obtenerPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return Optional.ofNullable(religionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Religión no encontrada con ID: " + id)));
    }

    @Override
    @Transactional
    public Religion guardarReligion(Religion religion) {
        if (religion == null) {
            throw new IllegalArgumentException("La religión no puede ser nula");
        }
        return religionRepository.save(religion);
    }

    @Override
    @Transactional
    public Religion actualizarReligion(Integer id, Religion religion) {
        if (id == null || religion == null) {
            throw new IllegalArgumentException("El ID y la religión no pueden ser nulos");
        }

        if (!religionRepository.existsById(id)) {
            throw new EntityNotFoundException("Religión no encontrada con ID: " + id);
        }

        religion.setId(id);
        return religionRepository.save(religion);
    }

    @Override
    @Transactional
    public Boolean eliminarReligion(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!religionRepository.existsById(id)) {
                return false;
            }
            religionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la religión con ID: " + id, e);
        }
    }
}