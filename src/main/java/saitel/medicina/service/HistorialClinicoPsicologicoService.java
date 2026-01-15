package saitel.medicina.service;

import org.springframework.stereotype.Component;
import saitel.medicina.entity.HistoriaClinicaPsicologica;

import java.util.List;

@Component
public interface HistorialClinicoPsicologicoService {
    List<HistoriaClinicaPsicologica> findByIdEmpleado(Integer idEmpleado);
    List<HistoriaClinicaPsicologica> obtenerHistorialClinicoPsicologico();
    HistoriaClinicaPsicologica guardarHistorialClinicoPsicologico(String sucursal, String departamento, String param,HistoriaClinicaPsicologica historiaClinicaPsicologica);
    Boolean eliminarHistorialClinicoPsicologico(Integer id);
    HistoriaClinicaPsicologica obtenerHistorialClinicoPsicologicoPorId(Integer id);
    HistoriaClinicaPsicologica actualizarHistorialClinicoPsicologico(Integer id, HistoriaClinicaPsicologica historiaClinicaPsicologica);
    List<HistoriaClinicaPsicologica> filtrar(Integer idEmpleado, Integer anioInicio, Integer anioFin);
}
