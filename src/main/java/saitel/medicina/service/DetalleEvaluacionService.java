package saitel.medicina.service;


import saitel.medicina.entity.DetalleEvaluacionRetiro;

import java.util.List;
import java.util.Optional;

public interface DetalleEvaluacionService {
    List<DetalleEvaluacionRetiro>ListarDetallesEvaluacion();
    DetalleEvaluacionRetiro guardar(DetalleEvaluacionRetiro detalleEvaluacionRetiro);
    DetalleEvaluacionRetiro actualizar(Integer id, DetalleEvaluacionRetiro detalleEvaluacionRetiro);
    Boolean eliminar(Integer id);
    Optional<DetalleEvaluacionRetiro> ListarIdDatellesEvaluacion(Integer id);
    List<DetalleEvaluacionRetiro> findByIdEvaluacion(Integer idEvaluacion);
}
