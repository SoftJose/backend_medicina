package saitel.medicina.service;

import saitel.medicina.entity.FactoresRiesgoTrabajo;

import java.util.List;
import java.util.Optional;

public interface FactoresRTService {
    List<FactoresRiesgoTrabajo> findByIdEvaluacion(Integer idEvaluacion);
    FactoresRiesgoTrabajo guardar(FactoresRiesgoTrabajo factoresRiesgoTrabajo);
    List<FactoresRiesgoTrabajo> findAll();
    Optional<FactoresRiesgoTrabajo> findById(Integer id);
    FactoresRiesgoTrabajo updateFactores(Integer id, FactoresRiesgoTrabajo factoresRiesgoTrabajo);
    Boolean deleteById(Integer id);
}
