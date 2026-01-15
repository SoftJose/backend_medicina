package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.FactoresRiesgoTrabajo;
import saitel.medicina.service.FactoresRTService;

import java.util.List;

@RestController
@RequestMapping("/api/factores-riesgo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FactoresRTRest {

    private final FactoresRTService factoresRTService;

    @PostMapping("/guardar")
    public ResponseEntity<FactoresRiesgoTrabajo> guardar(@RequestBody FactoresRiesgoTrabajo factores) {
        FactoresRiesgoTrabajo nuevo = factoresRTService.guardar(factores);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<FactoresRiesgoTrabajo>> listarTodos() {
        List<FactoresRiesgoTrabajo> lista = factoresRTService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactoresRiesgoTrabajo> obtenerPorId(@PathVariable Integer id) {
        return factoresRTService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<FactoresRiesgoTrabajo> actualizar(
            @PathVariable Integer id,
            @RequestBody FactoresRiesgoTrabajo factores) {

        return factoresRTService.findById(id)
                .map(existing -> {
                    factores.setId(id);
                    FactoresRiesgoTrabajo actualizado = factoresRTService.updateFactores(id, factores);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return factoresRTService.findById(id)
                .map(existing -> {
                    factoresRTService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}