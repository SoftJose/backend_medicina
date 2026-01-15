package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.TipoEvaluacion;
import saitel.medicina.service.TipoEvaluacionService;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-evaluaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoEvaluacionRest {

    private final TipoEvaluacionService tipoEvaluacionService;

    @GetMapping("/")
    public ResponseEntity<List<TipoEvaluacion>> listarTodos() {
        List<TipoEvaluacion> tipos = tipoEvaluacionService.findAll();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoEvaluacion> obtenerPorId(@PathVariable Integer id) {
        return tipoEvaluacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/guardar")
    public ResponseEntity<TipoEvaluacion> guardar(@Validated @RequestBody TipoEvaluacion tipoEvaluacion) {
        TipoEvaluacion nuevo = tipoEvaluacionService.save(tipoEvaluacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<TipoEvaluacion> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody TipoEvaluacion tipoEvaluacion) {

        return tipoEvaluacionService.findById(id)
                .map(existing -> {
                    tipoEvaluacion.setId(id);
                    TipoEvaluacion actualizado = tipoEvaluacionService.save(tipoEvaluacion);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return tipoEvaluacionService.findById(id)
                .map(existing -> {
                    tipoEvaluacionService.deleteById(id);
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}