package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.EnfermedadActual;
import saitel.medicina.service.EnfermedadActualService;

import java.util.List;

@RestController
@RequestMapping("/api/enfermedad-actual")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EnfermedadActualRest {

    private final EnfermedadActualService enfermedadActualService;

    @PostMapping("/guardar")
    public ResponseEntity<EnfermedadActual> guardar(@RequestBody EnfermedadActual enfermedadActual) {
        EnfermedadActual nueva = enfermedadActualService.guardar(enfermedadActual);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/")
    public ResponseEntity<List<EnfermedadActual>> listarTodos() {
        List<EnfermedadActual> lista = enfermedadActualService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnfermedadActual> obtenerPorId(@PathVariable Integer id) {
        return enfermedadActualService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EnfermedadActual> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody EnfermedadActual enfermedadActual) {

        return enfermedadActualService.findById(id)
                .map(existing -> {
                    enfermedadActual.setId(id);
                    EnfermedadActual actualizada = enfermedadActualService.updateEnfermedadActual(id, enfermedadActual);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return enfermedadActualService.findById(id)
                .map(existing -> {
                    enfermedadActualService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}