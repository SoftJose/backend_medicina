package saitel.medicina.rest;

import saitel.medicina.dto.DocumentoBase64Dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import saitel.medicina.entity.AtencionSeguimientoPsicologico;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.HistoriaClinicaPsicologica;
import saitel.medicina.service.AtencionSeguimientoService;
import saitel.medicina.service.DatosEmpleadoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/seguimiento-psicologico")
public class AtencionSeguimientoPsicologicoRest {

    private final AtencionSeguimientoService seguimientoService;
    private final DatosEmpleadoService datosEmpleadoService;

    // Listar seguimientos por historia
    @GetMapping("/historia/{historiaId}")
    public List<AtencionSeguimientoPsicologico> listarPorHistoria(@PathVariable Integer historiaId) {
        HistoriaClinicaPsicologica historia = new HistoriaClinicaPsicologica();
        historia.setId(historiaId);
        return seguimientoService.obtenerPorHistoriaClinicaPsicologica(historia);
    }

    @GetMapping ("/")
    public List<AtencionSeguimientoPsicologico> listarTodos() {
        return seguimientoService.obtenerAtencionSeguimiento();
    }

    @PostMapping ("/guardar")
   public ResponseEntity<?> guardar(@RequestBody AtencionSeguimientoPsicologico seguimiento) {
    try {
        seguimiento.setFechaRegistro(LocalDateTime.now());
        AtencionSeguimientoPsicologico nuevo = seguimientoService.guardarAtencionSeguimiento(seguimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    } catch (IllegalArgumentException | EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ocurrió un error al guardar el seguimiento psicológico." + e));
    }
   }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarSeguimiento(@PathVariable Integer id, @RequestBody AtencionSeguimientoPsicologico seguimiento) {
        try {
            AtencionSeguimientoPsicologico actualizado = seguimientoService.actualizarAtencionSeguimiento(id, seguimiento);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al actualizar el seguimiento psicológico." + e));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarSeguimiento(@PathVariable Integer id) {
        try {
            Boolean eliminado = seguimientoService.eliminarAtencionSeguimiento(id);
            if (eliminado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontró el seguimiento psicológico con el ID proporcionado."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al eliminar el seguimiento psicológico." + e));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        try {
            AtencionSeguimientoPsicologico seguimiento = seguimientoService.obtenerAtencionSeguimientoPorId(id);
            return ResponseEntity.ok(seguimiento);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al obtener el seguimiento psicológico." + e));
        }
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<DocumentoBase64Dto> descargarPdf(@PathVariable Integer id) {
        try {
            AtencionSeguimientoPsicologico seguimiento = seguimientoService.obtenerAtencionSeguimientoPorId(id);
            if (seguimiento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            HistoriaClinicaPsicologica historia = seguimiento.getHistoriaClinicaPsicologica();
            DatosEmpleados empleado = null;
            if (historia != null && historia.getIdEmpleado() != null) {
                empleado = datosEmpleadoService.obtenerPorId(historia.getIdEmpleado()).orElse(null);
            }
            saitel.medicina.reportes.AtencionSeguimientoReporte reporte = new saitel.medicina.reportes.AtencionSeguimientoReporte();
            java.util.List<AtencionSeguimientoPsicologico> lista = java.util.Collections.singletonList(seguimiento);
            DocumentoBase64Dto pdfDto = reporte.generarPdf(empleado, lista);
            return ResponseEntity.ok(pdfDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/pdf/historia/{idHistoria}")
    public ResponseEntity<DocumentoBase64Dto> descargarPdfPorHistoria(@PathVariable Integer idHistoria) {
        try {
            HistoriaClinicaPsicologica historia = new HistoriaClinicaPsicologica();
            historia.setId(idHistoria);
            List<AtencionSeguimientoPsicologico> seguimientos = seguimientoService.obtenerPorHistoriaClinicaPsicologica(historia);
            if (seguimientos == null || seguimientos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            DatosEmpleados empleado = null;
            if (seguimientos.get(0).getHistoriaClinicaPsicologica() != null && seguimientos.get(0).getHistoriaClinicaPsicologica().getIdEmpleado() != null) {
                empleado = datosEmpleadoService.obtenerPorId(seguimientos.get(0).getHistoriaClinicaPsicologica().getIdEmpleado()).orElse(null);
            }
            saitel.medicina.reportes.AtencionSeguimientoReporte reporte = new saitel.medicina.reportes.AtencionSeguimientoReporte();
            DocumentoBase64Dto pdfDto = reporte.generarPdf(empleado, seguimientos);
            return ResponseEntity.ok(pdfDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

