package saitel.medicina.service;

import org.springframework.stereotype.Component;
import saitel.medicina.entity.FichaSocial;

import java.util.List;
import java.util.Optional;

@Component
public interface FichaSocialService {
    List<FichaSocial> obtenerTodos(FichaSocial fichaSocial);
    Optional<FichaSocial> obtenerPorId(Integer id);
    FichaSocial guardarFichaSocial(String sucursal,String departamento,String param,FichaSocial fichaSocial);
    FichaSocial actualizarFichaSocial(Integer id,FichaSocial fichaSocial);
    Boolean eliminarFichaSocial(Integer id);
    Optional<FichaSocial> findByIdEmpleado(Integer idEmpleado);
}
