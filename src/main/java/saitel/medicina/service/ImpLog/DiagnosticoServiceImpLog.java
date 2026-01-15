package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.Diagnostico;
import saitel.medicina.repository.DiagnosticoRepository;
import saitel.medicina.service.DiagnosticoService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiagnosticoServiceImpLog implements DiagnosticoService {
    
    private final DiagnosticoRepository diagnosticoRepository;

    @Override
    @Transactional
    public Diagnostico guardar(Diagnostico diagnostico) {
        if (diagnostico == null) {
            throw new IllegalArgumentException("El diagnóstico no puede ser nulo");
        }
        return diagnosticoRepository.save(diagnostico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Diagnostico> findAll() {
        return diagnosticoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Diagnostico> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return diagnosticoRepository.findById(id);
    }

    @Override
    @Transactional
    public Diagnostico updateDiagnostico(Integer id, Diagnostico diagnostico) {
        if (id == null || diagnostico == null) {
            throw new IllegalArgumentException("El ID y el diagnóstico no pueden ser nulos");
        }

        if (!diagnosticoRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el diagnóstico con ID: " + id);
        }

        diagnostico.setId(id);
        return diagnosticoRepository.save(diagnostico);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            diagnosticoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el diagnóstico con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<Diagnostico> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return diagnosticoRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}