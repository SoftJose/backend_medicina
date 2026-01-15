package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.Examenes;
import saitel.medicina.service.ExamenService;

import java.util.List;

@RestController
@RequestMapping("/api/examenes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExamenRest {

    private final ExamenService examenService;

    @PostMapping("/guardar")
    public ResponseEntity<Examenes> guardar(@RequestBody Examenes examen) {
        Examenes nuevo = examenService.guardar(examen);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<Examenes>> listarTodos() {
        List<Examenes> lista = examenService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Examenes> obtenerPorId(@PathVariable Integer id) {
        return examenService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Examenes> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody Examenes examen) {

        return examenService.findById(id)
                .map(existing -> {
                    examen.setId(id);
                    Examenes actualizado = examenService.updateExamen(id, examen);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return examenService.findById(id)
                .map(existing -> {
                    examenService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}