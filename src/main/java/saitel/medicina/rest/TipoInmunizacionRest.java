package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.TipoInmunizacion;
import saitel.medicina.service.TipoInmunizacionService;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-inmunizaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoInmunizacionRest {

    private final TipoInmunizacionService tipoInmunizacionService;

    @GetMapping("/")
    public ResponseEntity<List<TipoInmunizacion>> listarTodos() {
        List<TipoInmunizacion> tipos = tipoInmunizacionService.listarTodos();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoInmunizacion> obtenerPorId(@PathVariable Integer id) {
        return tipoInmunizacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/guardar")
    public ResponseEntity<TipoInmunizacion> guardar(@Validated @RequestBody TipoInmunizacion tipoInmunizacion) {
        TipoInmunizacion nuevo = tipoInmunizacionService.guardar(tipoInmunizacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<TipoInmunizacion> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody TipoInmunizacion tipoInmunizacion) {

        return tipoInmunizacionService.obtenerPorId(id)
                .map(existing -> {
                    tipoInmunizacion.setId(id);
                    TipoInmunizacion actualizado = tipoInmunizacionService.actualizar(id, tipoInmunizacion);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return tipoInmunizacionService.obtenerPorId(id)
                .map(existing -> {
                    tipoInmunizacionService.eliminar(id);
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}
