package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.MotivoConsulta;
import saitel.medicina.repository.MotivoConsultaRepository;
import saitel.medicina.service.MotivoConsultaService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MotivoConsultaServiceImpLog implements MotivoConsultaService {
    
    private final MotivoConsultaRepository repository;

    @Override
    @Transactional
    public MotivoConsulta save(MotivoConsulta motivoConsulta) {
        if (motivoConsulta == null) {
            throw new IllegalArgumentException("El motivo de consulta no puede ser nulo");
        }
        return repository.save(motivoConsulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotivoConsulta> findByIdEvaluacion(Integer idEvaluacion) {
        if (idEvaluacion == null) {
            throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
        }
        return repository.findByIdEvaluacion_Id(idEvaluacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotivoConsulta> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MotivoConsulta> findById(Integer idMotivo) {
        if (idMotivo == null) {
            throw new IllegalArgumentException("El ID del motivo no puede ser nulo");
        }
        return repository.findById(idMotivo);
    }

    @Override
    @Transactional
    public MotivoConsulta updateMotivoConsulta(Integer idMotivo, MotivoConsulta motivoConsulta) {
        if (idMotivo == null || motivoConsulta == null) {
            throw new IllegalArgumentException("El ID y el motivo de consulta no pueden ser nulos");
        }

        if (!repository.existsById(idMotivo)) {
            throw new EntityNotFoundException("No se encontró el motivo de consulta con ID: " + idMotivo);
        }

        motivoConsulta.setId(idMotivo);
        return repository.save(motivoConsulta);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer idMotivo) {
        if (idMotivo == null) {
            throw new IllegalArgumentException("El ID del motivo no puede ser nulo");
        }

        try {
            if (!repository.existsById(idMotivo)) {
                return false;
            }
            repository.deleteById(idMotivo);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el motivo de consulta con ID: " + idMotivo, e);
        }
    }
}