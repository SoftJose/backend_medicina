package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.DatosProfesional;
import saitel.medicina.repository.DatosProfesionalRepository;
import saitel.medicina.service.DatosProfesionalService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatosProfesionalServiceImpLog implements DatosProfesionalService {
    
    private final DatosProfesionalRepository repository;
    private final FirmaDocumentoService firmaDocumentoService;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public DatosProfesional guardar(DatosProfesional datosProfesional) {
        if (datosProfesional == null) {
            throw new IllegalArgumentException("Los datos profesionales no pueden ser nulos");
        }

        try {
            String nombresApellidos = (String) em.createNativeQuery(
                "SELECT primer_nombre || ' ' || primer_apellido " +
                "FROM medicina.f_vta_empleado " +
                "WHERE id_rol = 45 AND estado = true " +
                "LIMIT 1")
                .getSingleResult();
            nombresApellidos = "Dra. " + nombresApellidos;
            datosProfesional.setNombresApellidos(nombresApellidos);

            Integer idProfesional = (Integer) em.createNativeQuery(
                "SELECT id_empleado " +
                "FROM medicina.f_vta_empleado " +
                "WHERE id_rol = 45 AND estado = true " +
                "LIMIT 1")
                .getSingleResult();
            datosProfesional.setIdProfesional(idProfesional);

        } catch (NoResultException e) {
            datosProfesional.setNombresApellidos("Dra. No Registrada");
        }

        datosProfesional.setFecha(LocalDate.now());
        datosProfesional.setHora(LocalTime.now());
        
        return repository.save(datosProfesional);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatosProfesional> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DatosProfesional> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return repository.findById(id);
    }

    @Override
    @Transactional
    public DatosProfesional updateDatosProfesional(Integer id, DatosProfesional datosProfesional) {
        if (id == null || datosProfesional == null) {
            throw new IllegalArgumentException("El ID y los datos profesionales no pueden ser nulos");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se encontraron los datos profesionales con ID: " + id);
        }

        datosProfesional.setId(id);
        return repository.save(datosProfesional);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            repository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar los datos profesionales con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<DatosProfesional> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return repository.findByIdEvaluacion_Id(idEvaluacion);
        }

  @Override
public DatosProfesional firmarProfesional(Integer id, String claveFirma, byte[] pdfDocumento) {
    if (id == null) {
        throw new IllegalArgumentException("El ID no puede ser nulo");
    }
    DatosProfesional profesional = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
    firmaDocumentoService.firmarDocumento(
        pdfDocumento,
        claveFirma,
        profesional.getId(),
        "FIRMA PROFESIONAL"
    );

    profesional.setFirmaSello(true);

    return repository.save(profesional);
}
}