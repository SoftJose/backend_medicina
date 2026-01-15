package saitel.medicina.service;

import saitel.medicina.entity.RevisionOrganosSistemas;

import java.util.List;
import java.util.Optional;

public interface RevisionOrganosSistemasService {
    List<RevisionOrganosSistemas> findByIdEvaluacion(Integer idEvaluacion);
    RevisionOrganosSistemas guardarRevision(RevisionOrganosSistemas revisionOrganosSistemas);
    List<RevisionOrganosSistemas> findAll();
    Optional<RevisionOrganosSistemas> findById(Integer id);
    RevisionOrganosSistemas updateRevision(Integer id, RevisionOrganosSistemas revisionOrganosSistemas);
    Boolean deleteById(Integer id);
}
