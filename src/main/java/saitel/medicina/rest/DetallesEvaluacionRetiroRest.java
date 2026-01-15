package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.DetalleEvaluacionRetiro;
import saitel.medicina.service.DetalleEvaluacionService;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-evaluacion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DetallesEvaluacionRetiroRest {

    private final DetalleEvaluacionService detalleEvaluacionService;

    @PostMapping("/guardar")
    public ResponseEntity<DetalleEvaluacionRetiro> guardar(
             @RequestBody DetalleEvaluacionRetiro detalle) {
        DetalleEvaluacionRetiro nuevo = detalleEvaluacionService.guardar(detalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<DetalleEvaluacionRetiro>> listar() {
        List<DetalleEvaluacionRetiro> detalles = detalleEvaluacionService.ListarDetallesEvaluacion();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleEvaluacionRetiro> obtenerPorId(@PathVariable Integer id) {
        return detalleEvaluacionService.ListarIdDatellesEvaluacion(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<DetalleEvaluacionRetiro> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody DetalleEvaluacionRetiro detalleActualizado) {

        return detalleEvaluacionService.ListarIdDatellesEvaluacion(id)
                .map(existing -> {
                    detalleActualizado.setId(id); 
                    DetalleEvaluacionRetiro actualizado =
                            detalleEvaluacionService.actualizar(id, detalleActualizado);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return detalleEvaluacionService.ListarIdDatellesEvaluacion(id)
                .map(existing -> {
                    detalleEvaluacionService.eliminar(existing.getId()); 
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }
}
