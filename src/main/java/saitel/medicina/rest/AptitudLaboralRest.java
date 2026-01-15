package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.AptitudLaboral;
import saitel.medicina.service.AptitudMedicaService;

import java.util.List;

@RestController
@RequestMapping("/api/aptitud-medica")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AptitudLaboralRest {

    private final AptitudMedicaService aptitudMedicaService;

    @PostMapping("/guardar")
    public ResponseEntity<AptitudLaboral> guardar(@RequestBody AptitudLaboral aptitudMedica) {
        AptitudLaboral nuevo = aptitudMedicaService.guardar(aptitudMedica);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<AptitudLaboral>> listarAptitudes() {
        List<AptitudLaboral> lista = aptitudMedicaService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AptitudLaboral> obtenerPorId(@PathVariable Integer id) {
        return aptitudMedicaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<AptitudLaboral> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody AptitudLaboral aptitudMedica) {

        return aptitudMedicaService.findById(id)
                .map(existing -> {
                    aptitudMedica.setId(id);
                    AptitudLaboral actualizada = aptitudMedicaService.updateAptitud(id, aptitudMedica);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return aptitudMedicaService.findById(id)
                .map(existing -> {
                    aptitudMedicaService.deleteById(existing.getId());
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }
}
