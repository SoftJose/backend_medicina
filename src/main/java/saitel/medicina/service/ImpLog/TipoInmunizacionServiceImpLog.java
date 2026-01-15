package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.TipoInmunizacion;
import saitel.medicina.repository.TipoInmunizacionRepository;
import saitel.medicina.service.TipoInmunizacionService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoInmunizacionServiceImpLog implements TipoInmunizacionService {

    private final TipoInmunizacionRepository tipoInmunizacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoInmunizacion> listarTodos() {
        return tipoInmunizacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoInmunizacion> obtenerPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return tipoInmunizacionRepository.findById(id);
    }

    @Override
    @Transactional
    public TipoInmunizacion guardar(TipoInmunizacion tipoInmunizacion) {
        if (tipoInmunizacion == null) {
            throw new IllegalArgumentException("El tipo de inmunización no puede ser nulo");
        }
        return tipoInmunizacionRepository.save(tipoInmunizacion);
    }

    @Override
    @Transactional
    public Boolean eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!tipoInmunizacionRepository.existsById(id)) {
                return false;
            }
            tipoInmunizacionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el tipo de inmunización con ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public TipoInmunizacion actualizar(Integer id, TipoInmunizacion tipoInmunizacion) {
        if (id == null || tipoInmunizacion == null) {
            throw new IllegalArgumentException("El ID y el tipo de inmunización no pueden ser nulos");
        }

        if (!tipoInmunizacionRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el tipo de inmunización con ID: " + id);
        }

        tipoInmunizacion.setId(id);
        return tipoInmunizacionRepository.save(tipoInmunizacion);
    }
}
