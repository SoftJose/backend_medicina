package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.Religion;
import saitel.medicina.service.ReligionService;

import java.util.List;

@RestController
@RequestMapping("/api/religiones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReligionRest {

    private final ReligionService religionService;

    @PostMapping("/guardar")
    public ResponseEntity<Religion> guardarReligion(@Validated @RequestBody Religion religion) {
        Religion nuevaReligion = religionService.guardarReligion(religion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReligion);
    }

    @GetMapping("/")
    public ResponseEntity<List<Religion>> listarReligiones() {
        List<Religion> religiones = religionService.obtenerTodas();
        return ResponseEntity.ok(religiones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Religion> obtenerPorId(@PathVariable Integer id) {
        return religionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("actualizar/{id}")
    public ResponseEntity<Religion> actualizarReligion(
            @PathVariable Integer id,
            @Validated @RequestBody Religion religionActualizada) {

        return religionService.obtenerPorId(id)
                .map(existing -> {
                    religionActualizada.setId(id);
                    Religion actualizada = religionService.actualizarReligion(id, religionActualizada);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Boolean> eliminarReligion(@PathVariable Integer id) {
        return religionService.obtenerPorId(id)
                .map(existing -> {
                    religionService.eliminarReligion(existing.getId());
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }
}
