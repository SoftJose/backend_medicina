package saitel.medicina.service;

import saitel.medicina.entity.Examenes;

import java.util.List;
import java.util.Optional;

public interface ExamenService {
    List<Examenes> findByIdEvaluacion(Integer idEvaluacion);
    Examenes guardar(Examenes examen);
    List<Examenes> findAll();
    Optional<Examenes> findById(Integer id);
    Examenes updateExamen(Integer id, Examenes examen);
    Boolean deleteById(Integer id);
}
