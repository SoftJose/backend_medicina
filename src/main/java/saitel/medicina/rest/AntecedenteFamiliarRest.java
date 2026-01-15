package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.AntecedentesFamiliares;
import saitel.medicina.service.AntecedenteFamiliarService;

import java.util.List;

@RestController
@RequestMapping("/api/antecedentes-familiares")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AntecedenteFamiliarRest {

    private final AntecedenteFamiliarService antecedentesFamiliaresService;

    @PostMapping("/guardar")
    public ResponseEntity<AntecedentesFamiliares> guardarAntecedente(
            @Validated @RequestBody AntecedentesFamiliares antecedentesFamiliares) {
        AntecedentesFamiliares nuevoAntecedente = antecedentesFamiliaresService.save(antecedentesFamiliares);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAntecedente);
    }

    @GetMapping("/")
    public ResponseEntity<List<AntecedentesFamiliares>> listarAntecedentes() {
        List<AntecedentesFamiliares> antecedentesFamiliares = antecedentesFamiliaresService.findAll();
        return ResponseEntity.ok(antecedentesFamiliares);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AntecedentesFamiliares> obtenerPorId(@PathVariable Integer id) {
        return antecedentesFamiliaresService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<AntecedentesFamiliares> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody AntecedentesFamiliares antecedenteActualizado) {

        return antecedentesFamiliaresService.findById(id)
                .map(existing -> {
                    antecedenteActualizado.setId(id);
                    AntecedentesFamiliares actualizado = antecedentesFamiliaresService
                            .updateAntecedente(id, antecedenteActualizado);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return antecedentesFamiliaresService.findById(id)
                .map(existing -> {
                    antecedentesFamiliaresService.deleteById(existing.getId());
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }

}