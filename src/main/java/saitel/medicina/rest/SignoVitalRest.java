package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.SignoVital;
import saitel.medicina.service.SignoVitalService;

import java.util.List;
@RestController
@RequestMapping("/api/signos-vitales")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SignoVitalRest {

    private final SignoVitalService signoVitalService;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String departamento,
            @RequestParam String param,
            @RequestBody SignoVital signoVital) {

        SignoVital nuevo = signoVitalService.guardarSignoVital(sucursal, departamento, param, signoVital);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<SignoVital>> listarTodos() {
        return ResponseEntity.ok(signoVitalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignoVital> obtenerPorId(@PathVariable Integer id) {
        return signoVitalService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<SignoVital> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody SignoVital signoVital) {

        SignoVital actualizado = signoVitalService.updateSignoVital(id, signoVital);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        Boolean eliminado = signoVitalService.deleteById(id);
        return ResponseEntity.ok(eliminado);
    }
}