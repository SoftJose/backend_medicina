package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.DetalleEvaluacionRetiro;
import saitel.medicina.repository.DetalleEvaluacionRetiroRepository;
import saitel.medicina.service.DetalleEvaluacionService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DetallesEvaluacionRetiroImpLog implements DetalleEvaluacionService {

    private final DetalleEvaluacionRetiroRepository detalleEvaluacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleEvaluacionRetiro> ListarDetallesEvaluacion() {
        return detalleEvaluacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetalleEvaluacionRetiro> ListarIdDatellesEvaluacion(Integer id) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");

        return Optional.ofNullable(detalleEvaluacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de evaluación no encontrado con ID: " + id)));
    }

    @Override
    public DetalleEvaluacionRetiro guardar(DetalleEvaluacionRetiro detalleEvaluacionRetiro) {
        if (detalleEvaluacionRetiro == null)
            throw new IllegalArgumentException("El detalle de evaluación no puede ser nulo");

        return detalleEvaluacionRepository.save(detalleEvaluacionRetiro);
    }

    @Override
    public DetalleEvaluacionRetiro actualizar(Integer id, DetalleEvaluacionRetiro detalleEvaluacionRetiro) {
        if (id == null || detalleEvaluacionRetiro == null)
            throw new IllegalArgumentException("El ID y el detalle de evaluación no pueden ser nulos");

        ListarIdDatellesEvaluacion(id);

        detalleEvaluacionRetiro.setId(id);
        return detalleEvaluacionRepository.save(detalleEvaluacionRetiro);
    }

    @Override
    public Boolean eliminar(Integer id) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");

        try {
            detalleEvaluacionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false; 
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el detalle de evaluación con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<DetalleEvaluacionRetiro> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return detalleEvaluacionRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}
