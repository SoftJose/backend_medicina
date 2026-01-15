package saitel.medicina.service;
import saitel.medicina.entity.CitaMedica;
import java.util.List;
import java.util.Optional;

public interface CitaMedicaService {
    List<CitaMedica> listaCitaMedicas();
    Optional<CitaMedica> findById(Integer id);
    CitaMedica save(String sucursal, String departamento, String param, CitaMedica citaMedica);
    CitaMedica update(Integer id, CitaMedica citaMedica);
    Boolean delete(Integer id);
}
