package saitel.medicina.service;

import saitel.medicina.entity.Antecedentes;

import java.util.List;
import java.util.Optional;

public interface AntecedentesService {
    List<Antecedentes> findByIdEvaluacion(Integer idEvaluacion);
    Antecedentes guardarAntecedentes(Antecedentes antecedentes);
    List<Antecedentes> findAll();
    Optional<Antecedentes> findById(Integer id);
    Antecedentes updateAntecedente(Integer id, Antecedentes antecedentes);
    Boolean deleteById(Integer id);
}
