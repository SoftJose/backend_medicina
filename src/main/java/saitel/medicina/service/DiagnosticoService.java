package saitel.medicina.service;

import java.util.List;

import saitel.medicina.entity.Diagnostico;

public interface DiagnosticoService {
    List<Diagnostico> findByIdEvaluacion(Integer idEvaluacion);
    Diagnostico guardar(Diagnostico diagnostico);
    java.util.List<Diagnostico> findAll();
    java.util.Optional<Diagnostico> findById(Integer id);
    Diagnostico updateDiagnostico(Integer id, Diagnostico diagnostico);
    Boolean deleteById(Integer id);
}
