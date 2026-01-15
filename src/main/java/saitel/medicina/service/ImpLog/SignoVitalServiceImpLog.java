package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.SignoVital;
import saitel.medicina.repository.SignoVitalRepository;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.service.SignoVitalService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignoVitalServiceImpLog implements SignoVitalService {
    
    private final SignoVitalRepository signoVitalRepository;
    private final DatosEmpleadoService datosEmpleadoService;

    @Override
    @Transactional
    public SignoVital guardarSignoVital(String sucursal, String departamento, String param, SignoVital signoVital) {
        if (signoVital == null) {
            throw new IllegalArgumentException("El signo vital no puede ser nulo");
        }

        if (param == null || param.isBlank()) {
            throw new IllegalArgumentException("Debe especificar un valor identificador (cédula, nombre o apellido).");
        }

        if (signoVital.getFechaSignos() == null) {
            signoVital.setFechaSignos(LocalDate.now());
        }
        if(signoVital.getHoraSignos() == null){
            signoVital.setFechaSignos(LocalDate.now());
        }

        List<DatosEmpleados> listaDato = datosEmpleadoService.buscarEmpleado(sucursal, departamento, param);

        if (listaDato.isEmpty()) {
            throw new EntityNotFoundException("No se encontró ningún empleado con esos datos.");
        }

        DatosEmpleados empleado = listaDato.get(0);
        signoVital.setIdEmpleado(empleado.getIdEmpleado());

        return signoVitalRepository.save(signoVital);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignoVital> findAll() {
        return signoVitalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SignoVital> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return signoVitalRepository.findById(id);
    }

    @Override
    @Transactional
    public SignoVital updateSignoVital(Integer id, SignoVital signoVital) {
        if (id == null || signoVital == null) {
            throw new IllegalArgumentException("El ID y el signo vital no pueden ser nulos");
        }

        if (!signoVitalRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el signo vital con ID: " + id);
        }

        signoVital.setId(id);
        return signoVitalRepository.save(signoVital);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!signoVitalRepository.existsById(id)) {
                return false;
            }
            signoVitalRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el signo vital con ID: " + id, e);
        }
    }

        @Override
        @Transactional(readOnly = true)
        public List<SignoVital> findByIdEvaluacion(Integer idEvaluacion) {
            if (idEvaluacion == null) {
                throw new IllegalArgumentException("El ID de evaluación no puede ser nulo");
            }
            return signoVitalRepository.findByIdEvaluacion_Id(idEvaluacion);
        }
}