package saitel.medicina.service;

import org.springframework.stereotype.Component;
import saitel.medicina.entity.AtencionSeguimientoPsicologico;

import java.util.List;

@Component
public interface AtencionSeguimientoService {
    java.util.List<AtencionSeguimientoPsicologico> obtenerPorHistoriaClinicaPsicologica(saitel.medicina.entity.HistoriaClinicaPsicologica historiaClinicaPsicologica);
    List<AtencionSeguimientoPsicologico> obtenerAtencionSeguimiento();
    AtencionSeguimientoPsicologico guardarAtencionSeguimiento(AtencionSeguimientoPsicologico atencionSeguimientoPsicologico);
    Boolean eliminarAtencionSeguimiento(Integer id);
    AtencionSeguimientoPsicologico obtenerAtencionSeguimientoPorId(Integer id);
    AtencionSeguimientoPsicologico actualizarAtencionSeguimiento(Integer id, AtencionSeguimientoPsicologico atencionSeguimientoPsicologico);
    List<AtencionSeguimientoPsicologico> obtenerPorHistoriasClinicas(List<saitel.medicina.entity.HistoriaClinicaPsicologica> historias);
}
