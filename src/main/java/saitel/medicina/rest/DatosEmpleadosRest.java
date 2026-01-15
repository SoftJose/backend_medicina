package saitel.medicina.rest;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import saitel.medicina.entity.AtencionSeguimientoPsicologico;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.FichaSocial;
import saitel.medicina.entity.HistoriaClinicaPsicologica;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.service.FichaSocialService;
import saitel.medicina.service.HistorialClinicoPsicologicoService;  
import saitel.medicina.service.AtencionSeguimientoService;

@RestController
@RequestMapping("/api/datos-empleado")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DatosEmpleadosRest {

    private final DatosEmpleadoService datosEmpleadoService;
    private final FichaSocialService fichaSocialService;
    private final HistorialClinicoPsicologicoService historiaClinicaPsicologicaService;
    private final AtencionSeguimientoService atencionSeguimientoService;

    @GetMapping("/")
    public ResponseEntity<List<DatosEmpleados>> listarTodos() {
        List<DatosEmpleados> lista = datosEmpleadoService.obtenerTodos();
        return ResponseEntity.ok(lista);
    }
    @GetMapping("/detallePsicologia/{idEmpleado}")
    public ResponseEntity<?> obtenerDetallePaciente(@PathVariable Integer idEmpleado) {
    DatosEmpleados datos = datosEmpleadoService.obtenerPorId(idEmpleado).orElse(null);
        FichaSocial ficha = fichaSocialService.findByIdEmpleado(idEmpleado).orElse(null);
        List<HistoriaClinicaPsicologica> historia = historiaClinicaPsicologicaService.findByIdEmpleado(idEmpleado);
    List<AtencionSeguimientoPsicologico> seguimientos = atencionSeguimientoService.obtenerPorHistoriasClinicas(historia);
    Map<String, Object> detalle = Map.of(
        "datosEmpleado", datos,
        "fichaSocial", ficha,
        "historiaClinica", historia,
        "seguimientoPsicologico", seguimientos
    );
    return ResponseEntity.ok(detalle);
    }
    
}
