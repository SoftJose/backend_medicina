package saitel.medicina.service;

import saitel.medicina.entity.AntecedentesFamiliares;

import java.util.List;
import java.util.Optional;

public interface AntecedenteFamiliarService {
    List<AntecedentesFamiliares> findByIdEvaluacion(Integer idEvaluacion);
    List<AntecedentesFamiliares> findAll();
    Optional<AntecedentesFamiliares> findById(Integer id);
    AntecedentesFamiliares save(AntecedentesFamiliares antecedente);
    AntecedentesFamiliares updateAntecedente(Integer id, AntecedentesFamiliares antecedente);
    Boolean deleteById(Integer id);
}
