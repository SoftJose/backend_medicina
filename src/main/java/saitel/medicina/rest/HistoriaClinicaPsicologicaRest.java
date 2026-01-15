package saitel.medicina.rest;

import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.dto.DocumentoBase64Dto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.HistoriaClinicaPsicologica;
import saitel.medicina.service.HistorialClinicoPsicologicoService;
import saitel.medicina.repository.HistorialClinicoPsicologicoRepository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/historia-clinica-psicologica")
@RequiredArgsConstructor
public class HistoriaClinicaPsicologicaRest {

    private final HistorialClinicoPsicologicoService historialClinicoPsicologicoService;
    private final saitel.medicina.service.DatosEmpleadoService datosEmpleadoService;
    private final saitel.medicina.reportes.HistoriaClinicaPsicologicaReporte historiaClinicaPsicologicaReporte;
    private final HistorialClinicoPsicologicoRepository historiaClinicaPsicologicaRepository;
    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String departamento,
            @RequestParam String param,
            @RequestBody HistoriaClinicaPsicologica historiaClinicaPsicologica) {

        try {
            historiaClinicaPsicologica.setFechaRegistro(LocalDateTime.now());
            HistoriaClinicaPsicologica nueva = historialClinicoPsicologicoService.guardarHistorialClinicoPsicologico(sucursal, departamento, param, historiaClinicaPsicologica);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);

        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al guardar la historia clinica psicologica."+e));
        }
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<HistoriaClinicaPsicologica>> getPorEmpleado(@PathVariable Integer idEmpleado) {
        return ResponseEntity.ok(historialClinicoPsicologicoService.filtrar(idEmpleado, null, null));
    }

    // Filtrar por empleado y rango de años
    @GetMapping("/filtrar")
    public ResponseEntity<List<HistoriaClinicaPsicologica>> filtrar(
            @RequestParam Integer idEmpleado,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin) {
        return ResponseEntity.ok(historialClinicoPsicologicoService.filtrar(idEmpleado, anioInicio, anioFin));
    }

    @GetMapping("/{idHistoria}")
    public ResponseEntity<HistoriaClinicaPsicologica> getPorId(@PathVariable Integer idHistoria) {
        HistoriaClinicaPsicologica historia = historialClinicoPsicologicoService.obtenerHistorialClinicoPsicologicoPorId(idHistoria);
        if (historia != null) {
            return ResponseEntity.ok(historia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{idHistoria}")
    public ResponseEntity<HistoriaClinicaPsicologica> actualizar(
            @PathVariable Integer idHistoria,
            @RequestBody HistoriaClinicaPsicologica historia) {
        return ResponseEntity.ok(historialClinicoPsicologicoService.actualizarHistorialClinicoPsicologico(idHistoria, historia));
    }

    @DeleteMapping("/{idHistoria}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idHistoria) {
        historialClinicoPsicologicoService.eliminarHistorialClinicoPsicologico(idHistoria);
        return ResponseEntity.noContent().build();
    }


    // PDF de una historia específica en base64
    @GetMapping("/pdf/{idHistoria}")
    public ResponseEntity<DocumentoBase64Dto> descargarPdfPorId(@PathVariable Integer idHistoria) {
        try {
            HistoriaClinicaPsicologica historia = historialClinicoPsicologicoService.obtenerHistorialClinicoPsicologicoPorId(idHistoria);
            if (historia == null) return ResponseEntity.notFound().build();
            DatosEmpleados empleado = historia.getIdEmpleado() != null ? datosEmpleadoService.obtenerPorId(historia.getIdEmpleado()).orElse(null) : null;
            java.util.List<HistoriaClinicaPsicologica> lista = java.util.Collections.singletonList(historia);
            DocumentoBase64Dto pdfDto = historiaClinicaPsicologicaReporte.generarPdf(empleado, lista);
            return ResponseEntity.ok(pdfDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PDF de todas las historias de un empleado (una hoja por historia) en base64
    @GetMapping("/pdf/empleado/{idEmpleado}")
    public ResponseEntity<DocumentoBase64Dto> descargarPdfPorEmpleado(@PathVariable Integer idEmpleado) {
        try {
            java.util.List<HistoriaClinicaPsicologica> historias = historialClinicoPsicologicoService.filtrar(idEmpleado, null, null);
            if (historias == null || historias.isEmpty()) return ResponseEntity.notFound().build();
            DatosEmpleados empleado = datosEmpleadoService.obtenerPorId(idEmpleado).orElse(null);
            DocumentoBase64Dto pdfDto = historiaClinicaPsicologicaReporte.generarPdf(empleado, historias);
            return ResponseEntity.ok(pdfDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/filtrar/detalle")
    public org.springframework.data.domain.Page<HistoriaClinicaPsicologica> filtrarHistorias(
    @RequestParam(required = false) String sucursal,
    @RequestParam(required = false) String departamento,
    @RequestParam(required = false) Integer idEmpleado,
    @RequestParam(required = false) LocalDate fechaInicio,
    @RequestParam(required = false) LocalDate fechaFin,
    Pageable pageable) {
    if (fechaInicio == null || fechaFin == null) {
        fechaInicio = LocalDate.now().withDayOfMonth(1).withMonth(1);
        fechaFin = LocalDate.now().withDayOfMonth(31).withMonth(12);
    }
    return historiaClinicaPsicologicaRepository.findHistoriaClinicaPsicologica(sucursal, departamento, idEmpleado, fechaInicio, fechaFin, pageable);
}
}

