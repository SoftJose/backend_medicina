package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.Antecedentes;
import saitel.medicina.service.AntecedentesService;

import java.util.List;

@RestController
@RequestMapping("/api/antecedentes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AntecedentesRest {

    private final AntecedentesService antecedentesService;

    @PostMapping("/guardar")
    public ResponseEntity<Antecedentes> guardarAntecedentes(
            @RequestBody Antecedentes antecedentes) {
        Antecedentes nuevoAntecedente = antecedentesService.guardarAntecedentes(antecedentes);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAntecedente);
    }

    @GetMapping("/")
    public ResponseEntity<List<Antecedentes>> listarAntecedentes() {
        List<Antecedentes> antecedentes = antecedentesService.findAll();
        return ResponseEntity.ok(antecedentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Antecedentes> obtenerPorId(@PathVariable Integer id) {
        return antecedentesService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Antecedentes> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody Antecedentes antecedenteActualizado) {

        return antecedentesService.findById(id)
                .map(existing -> {
                    antecedenteActualizado.setId(id);
                    Antecedentes actualizado = antecedentesService.updateAntecedente(id, antecedenteActualizado);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return antecedentesService.findById(id)
                .map(existing -> {
                    antecedentesService.deleteById(existing.getId());
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }
}
