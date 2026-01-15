package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.Diagnostico;
import saitel.medicina.service.DiagnosticoService;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostico")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DiagnosticoRest {

    private final DiagnosticoService diagnosticoService;

    @PostMapping("/guardar")
    public ResponseEntity<Diagnostico> guardar(@RequestBody Diagnostico diagnostico) {
        Diagnostico nuevo = diagnosticoService.guardar(diagnostico);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<Diagnostico>> listarTodos() {
        List<Diagnostico> lista = diagnosticoService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diagnostico> obtenerPorId(@PathVariable Integer id) {
        return diagnosticoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Diagnostico> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody Diagnostico diagnostico) {

        return diagnosticoService.findById(id)
                .map(existing -> {
                    diagnostico.setId(id);
                    Diagnostico actualizado = diagnosticoService.updateDiagnostico(id, diagnostico);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return diagnosticoService.findById(id)
                .map(existing -> {
                    diagnosticoService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}