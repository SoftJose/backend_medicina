package saitel.medicina.service.ImpLog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.ActividadesExtraLaborale;
import saitel.medicina.repository.ActividadesExtraRepository;
import saitel.medicina.service.ActividadesExtraService;
import org.springframework.dao.EmptyResultDataAccessException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActividadesExtraServiceImpLog implements ActividadesExtraService {

    private final ActividadesExtraRepository actividadesExtraRepository;

    /**
         * @param actividad
         * @return todos los datos guardados
         */
    @Override
    public ActividadesExtraLaborale guardarActividad(ActividadesExtraLaborale actividad) {
        if (actividad == null) throw new IllegalArgumentException("La actividad extra no puede ser nula");
        return actividadesExtraRepository.save(actividad);
    }

    /**
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActividadesExtraLaborale> findAll() {
        return actividadesExtraRepository.findAll();
    }

    @Override
    public Optional<ActividadesExtraLaborale> findById(Integer id) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");
        return Optional.ofNullable(actividadesExtraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Actividad extra no encontrada con ID: " + id)));
    }

    @Override
    public ActividadesExtraLaborale updateActividad(Integer id, ActividadesExtraLaborale actividad) {
        if (id == null || actividad == null)
            throw new IllegalArgumentException("El ID y la actividad extra no pueden ser nulos");

        findById(id); 

        actividad.setId(id);
        return actividadesExtraRepository.save(actividad);
    }

    @Override
    public Boolean deleteById(Integer id) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");

        try {
            actividadesExtraRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la actividad extra con ID: " + id, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadesExtraLaborale> findByIdEvaluacion(Integer idEvaluacion) {
        if (idEvaluacion == null) {
            throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
        }
        return actividadesExtraRepository.findByIdEvaluacion_Id(idEvaluacion);
    }
}