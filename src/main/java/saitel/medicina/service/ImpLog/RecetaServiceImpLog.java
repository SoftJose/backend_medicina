package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saitel.medicina.entity.Evaluacion;
import saitel.medicina.entity.RecetasEnviada;
import saitel.medicina.repository.RecetaRepository;
import saitel.medicina.service.RecetaService;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecetaServiceImpLog implements RecetaService {
    private final RecetaRepository recetaRepository;

    @PersistenceContext
    private EntityManager em;

        @Override
    @Transactional
    public RecetasEnviada guardar(RecetasEnviada receta) {
        if (receta == null) {
            throw new IllegalArgumentException("La receta no puede ser nula");
        }

        if (receta.getIdEvaluacion() == null) {
            throw new IllegalArgumentException("La evaluación no puede ser nula");
        }

        Evaluacion evaluacion = receta.getIdEvaluacion();

        int anioActual = LocalDate.now().getYear();

        String ultimaReceta = recetaRepository.obtenerUltimaRecetaPorAnio(anioActual);

        long numero = 1;
        if (ultimaReceta != null && !ultimaReceta.isBlank()) {
            try {
                String soloNumero = ultimaReceta.replace(anioActual + "-", "").trim();
                numero = Long.parseLong(soloNumero) + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("El número de receta previo no tiene un formato válido: " + ultimaReceta);
            }
        }

        receta.setNumeroReceta(String.format("N°-%d-%04d", anioActual, numero));
        try {
            String nombreDoctor = (String) em.createNativeQuery(
                            "SELECT primer_nombre || ' ' || primer_apellido " +
                                    "FROM medicina.f_vta_empleado " +
                                    "WHERE id_rol = 45 AND estado = true " +
                                    "LIMIT 1")
                    .getSingleResult();

            receta.setDoctorA("Dra. " + nombreDoctor);
        } catch (NoResultException e) {
            throw new RuntimeException("No se encontró un doctor activo en el sistema");
        }
        Integer idEvaluacion = evaluacion.getId();
        try {
            String diagnostico = (String) em.createNativeQuery(
                            "SELECT descripcion " +
                                    "FROM medicina.tbl_diagnostico " +
                                    "WHERE id_evaluacion = :idEvaluacion " +
                                    "ORDER BY id_diagnostico DESC LIMIT 1")
                    .setParameter("idEvaluacion", idEvaluacion)
                    .getSingleResult();

            receta.setDiagnostico(diagnostico);
        } catch (NoResultException e) {
            receta.setDiagnostico("Sin diagnóstico registrado");
        }

        receta.setFecha(LocalDate.now());

        return recetaRepository.save(receta);
    }



    @Override
    @Transactional(readOnly = true)
    public List<RecetasEnviada> findAll() {
        return recetaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RecetasEnviada> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return recetaRepository.findById(id);
    }

    @Override
    @Transactional
    public RecetasEnviada actualizar(Integer id, RecetasEnviada recetasEnviada) {
        if (id == null || recetasEnviada == null) {
            throw new IllegalArgumentException("El ID y la receta no pueden ser nulos");
        }

        if (!recetaRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la receta con ID: " + id);
        }

        recetasEnviada.setId(id);
        return recetaRepository.save(recetasEnviada);
    }

    @Override
    @Transactional
    public Boolean delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            Optional<RecetasEnviada> recetaOpt = recetaRepository.findById(id);
            if (recetaOpt.isEmpty()) {
                return false;
            }
            RecetasEnviada receta = recetaOpt.get();
            if (receta.isImpresa()) {
                // No se puede eliminar si ya fue impresa
                return false;
            }
            recetaRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la receta con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<RecetasEnviada> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return recetaRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}