package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.TiposEnfermedadFamiliar;
import saitel.medicina.service.TiposEnfermedadesFamiliaresService;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-enfermedades-familiares")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoEnfermedadFamiliarRest {

    private final TiposEnfermedadesFamiliaresService tiposEnfermedadesFamiliaresService;

    @GetMapping("/")
    public ResponseEntity<List<TiposEnfermedadFamiliar>> listarTodos() {
        List<TiposEnfermedadFamiliar> lista = tiposEnfermedadesFamiliaresService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TiposEnfermedadFamiliar> obtenerPorId(@PathVariable Integer id) {
        TiposEnfermedadFamiliar encontrado = tiposEnfermedadesFamiliaresService.findById(id);
        if (encontrado != null) {
            return ResponseEntity.ok(encontrado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<TiposEnfermedadFamiliar> guardar(
            @Validated @RequestBody TiposEnfermedadFamiliar tiposEnfermedadFamiliar) {

        TiposEnfermedadFamiliar nuevo = tiposEnfermedadesFamiliaresService.save(tiposEnfermedadFamiliar);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/actualizar{id}")
    public ResponseEntity<TiposEnfermedadFamiliar> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody TiposEnfermedadFamiliar tiposEnfermedadFamiliar) {

        TiposEnfermedadFamiliar existente = tiposEnfermedadesFamiliaresService.findById(id);
        if (existente != null) {
            tiposEnfermedadFamiliar.setId(id);
            TiposEnfermedadFamiliar actualizado = tiposEnfermedadesFamiliaresService.save(tiposEnfermedadFamiliar);
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        TiposEnfermedadFamiliar existente = tiposEnfermedadesFamiliaresService.findById(id);
        if (existente != null) {
            tiposEnfermedadesFamiliaresService.deleteById(id);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }
}
