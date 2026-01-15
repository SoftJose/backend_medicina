package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.RevisionOrganosSistemas;
import saitel.medicina.repository.RevisionOrganosSistemasRepository;
import saitel.medicina.service.RevisionOrganosSistemasService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RevisionOrganosSistemasServiceImpLog implements RevisionOrganosSistemasService {
    
    private final RevisionOrganosSistemasRepository revisionOrganosSistemasRepository;

    @Override
    @Transactional
    public RevisionOrganosSistemas guardarRevision(RevisionOrganosSistemas revision) {
        if (revision == null) {
            throw new IllegalArgumentException("La revisión de órganos y sistemas no puede ser nula");
        }
        return revisionOrganosSistemasRepository.save(revision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionOrganosSistemas> findAll() {
        return revisionOrganosSistemasRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RevisionOrganosSistemas> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return revisionOrganosSistemasRepository.findById(id);
    }

    @Override
    @Transactional
    public RevisionOrganosSistemas updateRevision(Integer id, RevisionOrganosSistemas revisionOrganosSistemas) {
        if (id == null || revisionOrganosSistemas == null) {
            throw new IllegalArgumentException("El ID y la revisión no pueden ser nulos");
        }

        if (!revisionOrganosSistemasRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la revisión con ID: " + id);
        }

        revisionOrganosSistemas.setId(id);
        return revisionOrganosSistemasRepository.save(revisionOrganosSistemas);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!revisionOrganosSistemasRepository.existsById(id)) {
                throw new EntityNotFoundException("No se encontró la revisión con ID: " + id);
            }
            revisionOrganosSistemasRepository.deleteById(id);
            return true; 
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("No se encontró la revisión con ID: " + id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la revisión con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<RevisionOrganosSistemas> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return revisionOrganosSistemasRepository.findByIdEvaluacion_Id(idEvaluacion);
        }

}