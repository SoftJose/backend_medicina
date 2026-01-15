package saitel.medicina.service;

import org.springframework.stereotype.Component;
import saitel.medicina.entity.ActividadesExtraLaborale;

import java.util.List;
import java.util.Optional;

@Component
public interface ActividadesExtraService {
    List<ActividadesExtraLaborale> findByIdEvaluacion(Integer idEvaluacion);
    ActividadesExtraLaborale guardarActividad(ActividadesExtraLaborale actividadesExtraLaborales);
    List<ActividadesExtraLaborale> findAll();
    Optional<ActividadesExtraLaborale> findById(Integer id);
    ActividadesExtraLaborale updateActividad(Integer id, ActividadesExtraLaborale actividadesExtraLaborales);
    Boolean deleteById(Integer id);
}
