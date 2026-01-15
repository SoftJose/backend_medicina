package saitel.medicina.service;

import saitel.medicina.entity.CondicionesRetiro;

import java.util.List;
import java.util.Optional;

public interface CondicionRetiroService {
    CondicionesRetiro save(CondicionesRetiro entity);
    List<CondicionesRetiro> findAll();
    Optional<CondicionesRetiro> findById(Integer id);
    CondicionesRetiro updateCondicionRetiro(Integer id, CondicionesRetiro entity);
    Boolean deleteById(Integer id);
    List<CondicionesRetiro> findByIdEvaluacion(Integer idEvaluacion);
}
