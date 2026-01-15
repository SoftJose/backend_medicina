package saitel.medicina.service.ImpLog;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.FichaSocial;
import saitel.medicina.repository.FichaSocialRepository;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.service.FichaSocialService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FichaSocialServiceImpLog implements FichaSocialService {

    private final FichaSocialRepository fichaSocialRepository;
    private final DatosEmpleadoService datosEmpleadoService;

    @Override
    @Transactional(readOnly = true)
    public List<FichaSocial> obtenerTodos(FichaSocial fichaSocial) {
        return fichaSocialRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FichaSocial> obtenerPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return Optional.ofNullable(fichaSocialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ficha social no encontrada con ID: " + id)));
    }

    @Override
    @Transactional
    public FichaSocial guardarFichaSocial(String sucursal, String departamento, String param, FichaSocial fichaSocial) {
        if (fichaSocial == null) {
            throw new IllegalArgumentException("La ficha social no puede ser nula");
        }

        if (param == null || param.isBlank()) {
            throw new IllegalArgumentException("Debe especificar un valor identificador (cédula, nombre o apellido).");
        }

        if (fichaSocial.getFecha() == null) {
            fichaSocial.setFecha(LocalDate.now());
        }

        List<DatosEmpleados> listaDato = datosEmpleadoService.buscarEmpleado(sucursal, departamento, param);

        if (listaDato.isEmpty()) {
            throw new EntityNotFoundException("No se encontró ningún empleado con esos datos.");
        }

        DatosEmpleados empleado = listaDato.get(0);
        boolean existeFicha = fichaSocialRepository.existsByIdEmpleado(empleado.getIdEmpleado());
        if (existeFicha) {
            throw new IllegalArgumentException("El empleado ya tiene una ficha social registrada.");
        }

        String numeroH = generarNumeroHistoriaClinicaSiNoExiste(empleado.getIdEmpleado());
        fichaSocial.setIdEmpleado(empleado.getIdEmpleado());
        fichaSocial.setNumeroHistoriaClinica(numeroH);

        FichaSocial nuevaFicha = fichaSocialRepository.save(fichaSocial);
        return nuevaFicha;
    }



    @Override
    @Transactional
    public FichaSocial actualizarFichaSocial(Integer id, FichaSocial fichaSocial) {
        if (id == null || fichaSocial == null) {
            throw new IllegalArgumentException("El ID y la ficha social no pueden ser nulos");
        }

        fichaSocialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ficha social no encontrada con ID: " + id));

        fichaSocial.setId(id);
        return fichaSocialRepository.save(fichaSocial);
    }

    @Override
    @Transactional
    public Boolean eliminarFichaSocial(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        try {
            if (!fichaSocialRepository.existsById(id)) {
                return false;
            }
            fichaSocialRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la ficha social con ID: " + id, e);
        }
    }

    @Override
    public Optional<FichaSocial> findByIdEmpleado(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return Optional.ofNullable(fichaSocialRepository.findByIdEmpleado(id)
                .orElseThrow(() -> new EntityNotFoundException("Ficha social no encontrada con ID: " + id)));
    }

    public String generarNumeroHistoriaClinicaSiNoExiste(Integer idEmpleado) {
        Optional<FichaSocial> fichaExistente = fichaSocialRepository.findByIdEmpleado(idEmpleado);

        if (fichaExistente.isPresent()) {
            return fichaExistente.get().getNumeroHistoriaClinica();
        }

        Optional<DatosEmpleados> empleado = datosEmpleadoService.obtenerPorId(idEmpleado);
        if (empleado.isEmpty()) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + idEmpleado);
        }

        return empleado.get().generarNumeroHistoriaClinica();
    }



}