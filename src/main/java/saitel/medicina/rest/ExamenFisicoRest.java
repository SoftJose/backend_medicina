package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.ExamenFisico;
import saitel.medicina.service.ExamenFisicoService;

import java.util.List;

@RestController
@RequestMapping("/api/examen-fisico")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExamenFisicoRest {

    private final ExamenFisicoService service;

    @PostMapping("/guardar")
    public ResponseEntity<ExamenFisico> guardar(@RequestBody ExamenFisico examen) {
        ExamenFisico nuevo = service.guardar(examen);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<ExamenFisico>> listarTodos() {
        List<ExamenFisico> lista = service.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamenFisico> obtenerPorId(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ExamenFisico> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody ExamenFisico examen) {

        return service.findById(id)
                .map(existing -> {
                    examen.setId(id);
                    ExamenFisico actualizado = service.updateExamenFisico(id, examen);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return service.findById(id)
                .map(existing -> {
                    service.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}