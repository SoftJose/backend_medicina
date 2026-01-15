package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.AntecedentesTrabajo;
import saitel.medicina.service.AnTrabajoService;

import java.util.List;

@RestController
@RequestMapping("/api/antecedentes-trabajo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AntecedentesTrabajoRest {

    private final AnTrabajoService anTrabajoService;

    @PostMapping("/guardar")
    public ResponseEntity<AntecedentesTrabajo> guardar(
            @Validated @RequestBody AntecedentesTrabajo antecedentesTrabajo) {
        AntecedentesTrabajo nuevo = anTrabajoService.guardar(antecedentesTrabajo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<AntecedentesTrabajo>> listarAntecedentes() {
        List<AntecedentesTrabajo> lista = anTrabajoService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AntecedentesTrabajo> obtenerPorId(@PathVariable Integer id) {
        return anTrabajoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<AntecedentesTrabajo> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody AntecedentesTrabajo antecedentesTrabajo) {

        return anTrabajoService.findById(id)
                .map(existing -> {
                    antecedentesTrabajo.setId(id);
                    AntecedentesTrabajo actualizado = anTrabajoService.updateAntecedente(id, antecedentesTrabajo);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return anTrabajoService.findById(id)
                .map(existing -> {
                    anTrabajoService.deleteById(existing.getId());
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }
}
