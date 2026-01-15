package saitel.medicina.service;

import org.springframework.stereotype.Component;
import saitel.medicina.entity.TipoInmunizacion;

import java.util.List;
import java.util.Optional;

@Component
public interface TipoInmunizacionService {
    List<TipoInmunizacion> listarTodos();
    Optional<TipoInmunizacion> obtenerPorId(Integer id);
    TipoInmunizacion guardar(TipoInmunizacion tipoInmunizacion);
    Boolean eliminar(Integer id);
    TipoInmunizacion actualizar(Integer id, TipoInmunizacion tipoInmunizacion);
}
