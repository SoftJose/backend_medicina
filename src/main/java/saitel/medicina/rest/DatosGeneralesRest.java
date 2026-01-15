package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.DatosGeneralesCertificados;
import saitel.medicina.service.DatosGeneralesService;

import java.util.List;

@RestController
@RequestMapping("/api/datos-generales")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DatosGeneralesRest {

    private final DatosGeneralesService service;

    @PostMapping("/guardar")
    public ResponseEntity<DatosGeneralesCertificados> guardar(@Validated @RequestBody DatosGeneralesCertificados entity) {
        DatosGeneralesCertificados nuevo = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<DatosGeneralesCertificados>> listarTodos() {
        List<DatosGeneralesCertificados> lista = service.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosGeneralesCertificados> obtenerPorId(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<DatosGeneralesCertificados> actualizar(@PathVariable Integer id, @Validated @RequestBody DatosGeneralesCertificados entity) {
        DatosGeneralesCertificados actualizado = service.updateDatosGenerales(id, entity);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (service.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
