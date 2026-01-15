package saitel.medicina.service;

import saitel.medicina.entity.MotivoConsulta;

import java.util.List;
import java.util.Optional;

public interface MotivoConsultaService {
    MotivoConsulta save(MotivoConsulta motivoConsulta);
    List<MotivoConsulta> findAll();
    Optional<MotivoConsulta> findById(Integer idMotivo);
    MotivoConsulta updateMotivoConsulta(Integer idMotivo, MotivoConsulta motivoConsulta);
    Boolean deleteById(Integer idMotivo);
    List<MotivoConsulta> findByIdEvaluacion(Integer idEvaluacion);
}
