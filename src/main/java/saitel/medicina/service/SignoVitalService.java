package saitel.medicina.service;

import saitel.medicina.entity.SignoVital;

import java.util.List;
import java.util.Optional;

public interface SignoVitalService {
    List<SignoVital> findByIdEvaluacion(Integer idEvaluacion);
    SignoVital guardarSignoVital(String sucursal, String departamento, String param,SignoVital signoVital);
    List<SignoVital> findAll();
    Optional<SignoVital> findById(Integer id);
    SignoVital updateSignoVital(Integer id, SignoVital signoVital);
    Boolean deleteById(Integer id);
}
