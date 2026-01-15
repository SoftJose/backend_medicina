package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.AtencionSeguimientoPsicologico;
import saitel.medicina.entity.HistoriaClinicaPsicologica;
import saitel.medicina.repository.AtencionSeguimientoPsicologicoRepository;
import saitel.medicina.service.AtencionSeguimientoService;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AtencionSeguimientoPsicologicoServiceImpLog implements AtencionSeguimientoService {

    private final AtencionSeguimientoPsicologicoRepository atencionSeguimientoRepository;
    @Override
    @Transactional(readOnly = true)
    public List<AtencionSeguimientoPsicologico> obtenerPorHistoriaClinicaPsicologica(saitel.medicina.entity.HistoriaClinicaPsicologica historiaClinicaPsicologica) {
        return atencionSeguimientoRepository.findByHistoriaClinicaPsicologica(historiaClinicaPsicologica);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtencionSeguimientoPsicologico> obtenerAtencionSeguimiento() {
        return atencionSeguimientoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public AtencionSeguimientoPsicologico obtenerAtencionSeguimientoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return atencionSeguimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atención y seguimiento psicológico no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public AtencionSeguimientoPsicologico guardarAtencionSeguimiento(AtencionSeguimientoPsicologico atencionSeguimiento) {
        if (atencionSeguimiento == null) {
            throw new IllegalArgumentException("La atención y seguimiento psicológico no puede ser nulo");
        }
        return atencionSeguimientoRepository.save(atencionSeguimiento);
    }

    @Override
    @Transactional
    public AtencionSeguimientoPsicologico actualizarAtencionSeguimiento(Integer id, AtencionSeguimientoPsicologico atencionSeguimiento) {
        if (id == null || atencionSeguimiento == null) {
            throw new IllegalArgumentException("El ID y la atención y seguimiento psicológico no pueden ser nulos");
        }
        if (!atencionSeguimientoRepository.existsById(id)) {
            throw new EntityNotFoundException("Atención y seguimiento psicológico no encontrado con ID: " + id);
        }

        atencionSeguimiento.setId(id);
        return atencionSeguimientoRepository.save(atencionSeguimiento);
    }

    @Override
    @Transactional
    public Boolean eliminarAtencionSeguimiento(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!atencionSeguimientoRepository.existsById(id)) {
                return false;
            }
            atencionSeguimientoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la atención y seguimiento psicológico con ID: " + id, e);
        }
    }
   @Override
@Transactional(readOnly = true)
public List<AtencionSeguimientoPsicologico> obtenerPorHistoriasClinicas(List<HistoriaClinicaPsicologica> historias) {
    if (historias == null) {
        throw new IllegalArgumentException("La lista de historias clínicas no puede ser nula");
    }
    if (historias.isEmpty()) {
        return java.util.Collections.emptyList();
    }
    return atencionSeguimientoRepository.findByHistoriaClinicaPsicologicaIn(historias);
}
}