package saitel.medicina.service;

import org.springframework.stereotype.Component;
import saitel.medicina.entity.Religion;

import java.util.List;
import java.util.Optional;

@Component
public interface ReligionService {
    List<Religion> obtenerTodas();
    Optional<Religion> obtenerPorId(Integer id);
    Religion guardarReligion(Religion religion);
    Boolean eliminarReligion(Integer id);
    Religion actualizarReligion(Integer id,Religion religion);
}
