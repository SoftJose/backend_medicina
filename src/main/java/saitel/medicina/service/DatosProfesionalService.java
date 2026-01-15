package saitel.medicina.service;
import java.util.*;
import saitel.medicina.entity.DatosProfesional;

public interface DatosProfesionalService {
    DatosProfesional guardar(DatosProfesional datosProfesional);
    List<DatosProfesional> findAll();
    Optional<DatosProfesional> findById(Integer id);
    DatosProfesional updateDatosProfesional(Integer id, DatosProfesional datosProfesional);
    Boolean deleteById(Integer id);
    List<DatosProfesional> findByIdEvaluacion(Integer idEvaluacion);
    DatosProfesional firmarProfesional(Integer id, String claveFirma , byte[] pdfDocumento);
}
