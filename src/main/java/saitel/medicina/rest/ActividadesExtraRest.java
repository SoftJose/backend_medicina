package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.ActividadesExtraLaborale;
import saitel.medicina.service.ActividadesExtraService;

import java.util.List;

@RestController
@RequestMapping("/api/actividades-extras")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ActividadesExtraRest {

    private final ActividadesExtraService actividadesExtraService;

    @PostMapping("/guardar")
    public ResponseEntity<ActividadesExtraLaborale> guardarActividad(
            @Validated @RequestBody ActividadesExtraLaborale actividad) {
        ActividadesExtraLaborale nuevaActividad = actividadesExtraService.guardarActividad(actividad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaActividad);
    }

    @GetMapping("/")
    public ResponseEntity<List<ActividadesExtraLaborale>> listarActividades() {
        List<ActividadesExtraLaborale> actividades = actividadesExtraService.findAll();
        return ResponseEntity.ok(actividades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadesExtraLaborale> obtenerPorId(@PathVariable Integer id) {
        return actividadesExtraService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("actualizar/{id}")
    public ResponseEntity<ActividadesExtraLaborale> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody ActividadesExtraLaborale actividadActualizada) {

        return actividadesExtraService.findById(id)
                .map(existing -> {
                    actividadActualizada.setId(id);
                    ActividadesExtraLaborale actualizada = actividadesExtraService.updateActividad(id, actividadActualizada);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return actividadesExtraService.findById(id)
                .map(existing -> {
                    actividadesExtraService.deleteById(existing.getId());
                    return new ResponseEntity<>(true, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND));
    }
}
