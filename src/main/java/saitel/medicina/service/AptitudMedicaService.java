package saitel.medicina.service;

import saitel.medicina.entity.AptitudLaboral;

import java.util.List;
import java.util.Optional;

public interface AptitudMedicaService {
    Optional<AptitudLaboral> findByIdEvaluacion(Integer idEvaluacion);
    AptitudLaboral guardar(AptitudLaboral aptitudMedica);
    List<AptitudLaboral> findAll();
    Optional<AptitudLaboral> findById(Integer id);
    AptitudLaboral updateAptitud(Integer id, AptitudLaboral aptitudMedica);
    Boolean deleteById(Integer id);
}
