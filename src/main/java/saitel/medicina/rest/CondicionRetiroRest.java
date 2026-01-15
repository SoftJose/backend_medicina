package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.CondicionesRetiro;
import saitel.medicina.service.CondicionRetiroService;
import java.util.List;

@RestController
@RequestMapping("/api/condicion_retiro")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CondicionRetiroRest {

    private final CondicionRetiroService condicionesService;

    @PostMapping("/guardar")
    public ResponseEntity<CondicionesRetiro> guardar(@RequestBody CondicionesRetiro condiciones) {
        CondicionesRetiro nueva = condicionesService.save(condiciones);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/")
    public ResponseEntity<List<CondicionesRetiro>> listarTodas() {
        List<CondicionesRetiro> lista = condicionesService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CondicionesRetiro> obtenerPorId(@PathVariable Integer id) {
        return condicionesService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<CondicionesRetiro> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody CondicionesRetiro condiciones) {

        return condicionesService.findById(id)
                .map(existing -> {
                    condiciones.setId(id);
                    CondicionesRetiro actualizada = condicionesService.updateCondicionRetiro(id, condiciones);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return condicionesService.findById(id)
                .map(existing -> {
                    condicionesService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}
