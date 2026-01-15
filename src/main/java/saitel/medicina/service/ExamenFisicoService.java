package saitel.medicina.service;

import java.util.List;

import saitel.medicina.entity.ExamenFisico;

public interface ExamenFisicoService {
    List<ExamenFisico> findByIdEvaluacion(Integer idEvaluacion);
    ExamenFisico guardar(ExamenFisico examen);
    java.util.List<ExamenFisico> findAll();
    java.util.Optional<ExamenFisico> findById(Integer id);
    ExamenFisico updateExamenFisico(Integer id, ExamenFisico examen);
    Boolean deleteById(Integer id);
}   
