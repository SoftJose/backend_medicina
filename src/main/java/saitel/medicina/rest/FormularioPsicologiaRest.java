package saitel.medicina.rest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import saitel.medicina.entity.FichaSocial;
import saitel.medicina.service.AtencionSeguimientoService;
import saitel.medicina.service.FichaSocialService;
import saitel.medicina.service.HistorialClinicoPsicologicoService;

@RestController
@RequestMapping("/api/formulario-psicologico")
public class FormularioPsicologiaRest {

    private final FichaSocialService fichaSocialService;
    private final HistorialClinicoPsicologicoService historialClinicoPsicologicoService;
    private final AtencionSeguimientoService atencionSeguimientoService;

    public FormularioPsicologiaRest(FichaSocialService fichaSocialService,
                                HistorialClinicoPsicologicoService historialClinicoPsicologicoService,
                                AtencionSeguimientoService atencionSeguimientoService) {
        this.fichaSocialService = fichaSocialService;
        this.historialClinicoPsicologicoService = historialClinicoPsicologicoService;
        this.atencionSeguimientoService = atencionSeguimientoService;
    }

    @GetMapping("/{idEmpleado}/detalle")
    public ResponseEntity<Map<String, Object>> getDetallePaciente(@PathVariable Integer idEmpleado) {
        Map<String, Object> detalle = new LinkedHashMap<>();
        Optional<FichaSocial> fichaSocial = fichaSocialService.findByIdEmpleado(idEmpleado);
        detalle.put("fichaSocial", fichaSocial.orElse(null));
        // Obtener todas las historias clínicas psicológicas del empleado
        var historias = historialClinicoPsicologicoService.filtrar(idEmpleado, null, null);
        detalle.put("historiasClinicasPsicologicas", historias);
        // Identificar la última historia clínica (por fechaAtencion)
        var ultimaHistoria = historias.stream()
            .max((h1, h2) -> h1.getFechaAtencion().compareTo(h2.getFechaAtencion()))
            .orElse(null);
        detalle.put("ultimaHistoriaClinicaPsicologica", ultimaHistoria);
        // Todas las atenciones/seguimientos asociadas a la última historia
        Object atencionesUltimaHistoria = null;
        if (ultimaHistoria != null) {
            atencionesUltimaHistoria = atencionSeguimientoService.obtenerPorHistoriaClinicaPsicologica(ultimaHistoria);
        }
        detalle.put("atencionesUltimaHistoriaClinica", atencionesUltimaHistoria);
        // Última atención/seguimiento asociada a la última historia
        Object ultimoSeguimiento = null;
        if (atencionesUltimaHistoria != null && atencionesUltimaHistoria instanceof java.util.List && !((java.util.List<?>)atencionesUltimaHistoria).isEmpty()) {
            var lista = (java.util.List<?>)atencionesUltimaHistoria;
            ultimoSeguimiento = lista.stream()
                .max((a1, a2) -> {
                    try {
                        var m = a1.getClass().getMethod("getFechaAtencion");
                        java.time.LocalDate fecha1 = (java.time.LocalDate) m.invoke(a1);
                        java.time.LocalDate fecha2 = (java.time.LocalDate) m.invoke(a2);
                        return fecha1.compareTo(fecha2);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .orElse(null);
        }
        detalle.put("ultimoSeguimiento", ultimoSeguimiento);

        return ResponseEntity.ok(detalle);
    }
}
