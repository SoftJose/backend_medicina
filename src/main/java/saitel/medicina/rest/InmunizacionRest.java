package saitel.medicina.rest;

import saitel.medicina.dto.DocumentoBase64Dto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.FichaSocial;
import saitel.medicina.entity.Inmunizaciones;
import saitel.medicina.service.InmunizacionService;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.service.FichaSocialService;
import java.util.List;

@RestController
@RequestMapping("/api/inmunizaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InmunizacionRest {

    private final InmunizacionService inmunizacionService;
    private final DatosEmpleadoService datosEmpleadoService;
    private final FichaSocialService fichaSocialService;
    private final saitel.medicina.reportes.HCU083InmunizacionesReporte hcu083Reporte;
   
    @PostMapping("/guardar")
    public ResponseEntity<Inmunizaciones> guardar(@RequestBody Inmunizaciones inmunizacion) {
        Inmunizaciones nueva = inmunizacionService.save(inmunizacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/")
    public ResponseEntity<List<Inmunizaciones>> listarTodos() {
        List<Inmunizaciones> lista = inmunizacionService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inmunizaciones> obtenerPorId(@PathVariable Integer id) {
        return inmunizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Inmunizaciones> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody Inmunizaciones inmunizacion) {

        return inmunizacionService.findById(id)
                .map(existing -> {
                    inmunizacion.setId(id);
                    Inmunizaciones actualizada = inmunizacionService.updateInmunizacion(id, inmunizacion);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return inmunizacionService.findById(id)
                .map(existing -> {
                    inmunizacionService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

     @GetMapping("/pdf/{idEmpleado}")
    public ResponseEntity<DocumentoBase64Dto> descargarInmunizaciones(@PathVariable Integer idEmpleado) {
        try {
            DatosEmpleados empleado = datosEmpleadoService.obtenerPorId(idEmpleado)
                .orElse(null);
            if (empleado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            FichaSocial ficha = fichaSocialService.findByIdEmpleado(idEmpleado)
                .orElse(null);
            if (ficha == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            List<Inmunizaciones> inmunizaciones = inmunizacionService.findByIdEmpleado(idEmpleado);
            DocumentoBase64Dto pdfDto = hcu083Reporte.generarPdf(empleado, ficha, inmunizaciones);
            return ResponseEntity.ok(pdfDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}