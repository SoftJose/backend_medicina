package saitel.medicina.service;

import saitel.medicina.entity.Inmunizaciones;

import java.util.List;
import java.util.Optional;

public interface InmunizacionService {
    List<Inmunizaciones> findByIdEmpleado(Integer idEmpleado);
    Inmunizaciones save(Inmunizaciones inmunizacion);
    List<Inmunizaciones> findAll();
    Optional<Inmunizaciones> findById(Integer id);
    Inmunizaciones updateInmunizacion(Integer id, Inmunizaciones inmunizacion);
    Boolean deleteById(Integer id);
    List<Inmunizaciones> findByEvaluacionId(Integer idEvaluacion);
}
