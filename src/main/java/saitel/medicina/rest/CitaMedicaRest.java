package saitel.medicina.rest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import saitel.medicina.dto.DocumentoBase64Dto;
import saitel.medicina.entity.CitaMedica;
import saitel.medicina.filter.HeaderFilter;
import saitel.medicina.reportes.ReportesExcel.ReporteCitas;
import saitel.medicina.service.CitaMedicaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cita-medica")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CitaMedicaRest {

    private final CitaMedicaService citaMedicaService;
    private final ReporteCitas reporteCitas;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String departamento,
            @RequestParam String param,
            @RequestBody CitaMedica citaMedica) {

        try {
            Map<String, String> headers = HeaderFilter.getHeaders();
            String usuario = headers.get("usuario");
            citaMedica.setAliasUsuario(usuario);
            System.out.println("Usuario: " + usuario);
            CitaMedica nueva = citaMedicaService.save(sucursal, departamento, param, citaMedica);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al guardar la cita médica."));
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<CitaMedica>> listarTodos() {
        List<CitaMedica> lista = citaMedicaService.listaCitaMedicas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaMedica> obtenerPorId(@PathVariable Integer id) {
        return citaMedicaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody CitaMedica citaMedica) {

        try {
            Map<String, String> headers = HeaderFilter.getHeaders();
            String usuario = headers.get("usuario");
            citaMedica.setAliasUsuario(usuario);
            CitaMedica actualizada = citaMedicaService.update(id, citaMedica);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al actualizar la cita médica"));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return citaMedicaService.findById(id)
                .map(existing -> {
                    citaMedicaService.delete(id);
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

    @GetMapping("/reporte/excel")
    public DocumentoBase64Dto generarReporteCitas(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
    @RequestParam String departamento) {

    if (fechaInicio.isAfter(fechaFin)) {
        throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
    }

    return reporteCitas.generarReporteCitas(fechaInicio, fechaFin, departamento);
}
}
