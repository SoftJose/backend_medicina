package saitel.medicina.service;

import saitel.medicina.entity.TipoEvaluacion;

import java.util.List;
import java.util.Optional;

public interface TipoEvaluacionService {
    List<TipoEvaluacion> findAll();
    Optional<TipoEvaluacion> findById(Integer id);
    TipoEvaluacion save(TipoEvaluacion tipoEvaluacion);
    Boolean deleteById(Integer id);
}
