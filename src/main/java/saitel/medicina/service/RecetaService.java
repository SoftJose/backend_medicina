package saitel.medicina.service;

import saitel.medicina.entity.RecetasEnviada;

import java.util.List;
import java.util.Optional;

public interface RecetaService {
 List<RecetasEnviada> findByIdEvaluacion(Integer idEvaluacion);
 RecetasEnviada guardar(RecetasEnviada receta);
 List<RecetasEnviada> findAll();
 Optional<RecetasEnviada> findById(Integer id);
 RecetasEnviada actualizar(Integer id, RecetasEnviada recetasEnviada);
 Boolean delete(Integer id);
}
