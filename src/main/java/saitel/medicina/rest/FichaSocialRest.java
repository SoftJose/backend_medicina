package saitel.medicina.rest;

import saitel.medicina.dto.DocumentoBase64Dto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.FichaSocial;
import saitel.medicina.reportes.FichaSocialReporte;
import saitel.medicina.service.FichaSocialService;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.entity.DatosEmpleados;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ficha-social")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FichaSocialRest {

    private final FichaSocialService fichaSocialService;
    private final DatosEmpleadoService datosEmpleadoService;
    private final FichaSocialReporte fichaSocialReporte;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarFichaSocial(
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String departamento,
            @RequestParam String param,
            @RequestBody FichaSocial fichaSocial) {

        if (sucursal != null && sucursal.isBlank()) sucursal = null;
        if (departamento != null && departamento.isBlank()) departamento = null;

        try {
            FichaSocial nuevaFicha = fichaSocialService.guardarFichaSocial(sucursal, departamento, param, fichaSocial);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFicha);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al guardar la ficha social."+ e));
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<FichaSocial>> listarFichas(@RequestBody(required = false) FichaSocial filtro) {
        List<FichaSocial> fichas = fichaSocialService.obtenerTodos(filtro != null ? filtro : new FichaSocial());
        return ResponseEntity.ok(fichas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaSocial> obtenerFichaPorId(@PathVariable Integer id) {
        return fichaSocialService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<FichaSocial> actualizarFichaSocial(
            @PathVariable Integer id,
            @Validated @RequestBody FichaSocial fichaActualizada) {

        return fichaSocialService.obtenerPorId(id)
                .map(existing -> {
                    fichaActualizada.setId(id); 
                    FichaSocial actualizada = fichaSocialService.actualizarFichaSocial(id, fichaActualizada);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminarFichaSocial(@PathVariable Integer id) {
        return fichaSocialService.obtenerPorId(id)
                .map(existing -> {
                    fichaSocialService.eliminarFichaSocial(id);
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/pdf/empleado/{idEmpleado}")
    public ResponseEntity<DocumentoBase64Dto> generarReporteFichaSocialPorEmpleado(@PathVariable Integer idEmpleado) {
        try {
            FichaSocial ficha = fichaSocialService.findByIdEmpleado(idEmpleado)
                .orElseThrow(() -> new EntityNotFoundException("Ficha social no encontrada para el empleado"));
            DatosEmpleados empleado = datosEmpleadoService.obtenerPorId(idEmpleado)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));
            DocumentoBase64Dto pdfDto = fichaSocialReporte.generarPdf(empleado, ficha);
            return ResponseEntity.ok(pdfDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
