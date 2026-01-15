package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.TiposEnfermedadFamiliar;
import saitel.medicina.repository.TipoEnfermedadFamiliarRepository;
import saitel.medicina.service.TiposEnfermedadesFamiliaresService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TiposEnfermedadesFamiliaresImpl implements TiposEnfermedadesFamiliaresService {

    private final TipoEnfermedadFamiliarRepository tiposEnfermedadFamiliarRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TiposEnfermedadFamiliar> findAll() {
        return tiposEnfermedadFamiliarRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TiposEnfermedadFamiliar findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return tiposEnfermedadFamiliarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró enfermedad familiar con ID: " + id));
    }

    @Override
    @Transactional
    public TiposEnfermedadFamiliar save(TiposEnfermedadFamiliar tiposEnfermedadFamiliar) {
        if (tiposEnfermedadFamiliar == null) {
            throw new IllegalArgumentException("La enfermedad familiar no puede ser nula");
        }
        return tiposEnfermedadFamiliarRepository.save(tiposEnfermedadFamiliar);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!tiposEnfermedadFamiliarRepository.existsById(id)) {
                return false;
            }
            tiposEnfermedadFamiliarRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la enfermedad familiar con ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public TiposEnfermedadFamiliar update(Integer id, TiposEnfermedadFamiliar tiposEnfermedadFamiliar) {
        if (id == null || tiposEnfermedadFamiliar == null) {
            throw new IllegalArgumentException("El ID y la enfermedad familiar no pueden ser nulos");
        }

        if (!tiposEnfermedadFamiliarRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la enfermedad familiar con ID: " + id);
        }

        tiposEnfermedadFamiliar.setId(id);
        return tiposEnfermedadFamiliarRepository.save(tiposEnfermedadFamiliar);
    }
}
