package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.Recomendaciones;
import saitel.medicina.service.RecomendacionTratamientoService;

import java.util.List;

@RestController
@RequestMapping("/api/recomendaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RecomendacionTratamientoRest {

    private final RecomendacionTratamientoService recomendacionService;

    @PostMapping("/guardar")
    public ResponseEntity<Recomendaciones> guardar(@RequestBody Recomendaciones recomendacion) {
        Recomendaciones nueva = recomendacionService.guardar(recomendacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/")
    public ResponseEntity<List<Recomendaciones>> listarTodos() {
        List<Recomendaciones> lista = recomendacionService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recomendaciones> obtenerPorId(@PathVariable Integer id) {
        return recomendacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Recomendaciones> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody Recomendaciones recomendacion) {

        return recomendacionService.findById(id)
                .map(existing -> {
                    recomendacion.setId(id);
                    Recomendaciones actualizada = recomendacionService.updateRecomendacion(id, recomendacion);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return recomendacionService.findById(id)
                .map(existing -> {
                    recomendacionService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}