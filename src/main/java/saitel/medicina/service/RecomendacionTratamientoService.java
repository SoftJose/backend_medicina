package saitel.medicina.service;

import saitel.medicina.entity.Recomendaciones;

import java.util.List;
import java.util.Optional;

public interface RecomendacionTratamientoService {
    List<Recomendaciones> findByIdEvaluacion(Integer idEvaluacion);
    Recomendaciones guardar(Recomendaciones recomendacionTratamiento);
    List<Recomendaciones> findAll();
    Optional<Recomendaciones> findById(Integer id);
    Recomendaciones updateRecomendacion(Integer id, Recomendaciones recomendacionTratamiento);
    Boolean deleteById(Integer id);
}
