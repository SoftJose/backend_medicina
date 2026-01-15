package saitel.medicina.service;

import saitel.medicina.entity.DatosGeneralesCertificados;
import java.util.List;
import java.util.Optional;

public interface DatosGeneralesService {
    DatosGeneralesCertificados save(DatosGeneralesCertificados datosGenerales);
    List<DatosGeneralesCertificados> findAll();
    Optional<DatosGeneralesCertificados> findById(Integer id);
    DatosGeneralesCertificados updateDatosGenerales(Integer id, DatosGeneralesCertificados datosGenerales);
    Boolean deleteById(Integer id);
    List<DatosGeneralesCertificados> findByEvaluacion(Integer idEvaluacion);
}
