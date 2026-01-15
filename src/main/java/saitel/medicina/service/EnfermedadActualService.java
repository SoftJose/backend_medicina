package saitel.medicina.service;

import java.util.List;

import saitel.medicina.entity.EnfermedadActual;

public interface EnfermedadActualService {
    List<EnfermedadActual> findByIdEvaluacion(Integer idEvaluacion);
    EnfermedadActual guardar(EnfermedadActual enfermedadActual);
    java.util.List<EnfermedadActual> findAll();
    java.util.Optional<EnfermedadActual> findById(Integer id);
    EnfermedadActual updateEnfermedadActual(Integer id, EnfermedadActual enfermedadActual);
    Boolean deleteById(Integer id);
}
