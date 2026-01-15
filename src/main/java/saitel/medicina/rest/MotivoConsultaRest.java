package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.MotivoConsulta;
import saitel.medicina.service.MotivoConsultaService;

import java.util.List;

@RestController
@RequestMapping("/api/motivo-consulta")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MotivoConsultaRest {

    private final MotivoConsultaService service;

    @PostMapping("/guardar")
    public ResponseEntity<MotivoConsulta> guardar(@Validated @RequestBody MotivoConsulta entity) {
        MotivoConsulta nuevo = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<MotivoConsulta>> listarTodos() {
        List<MotivoConsulta> lista = service.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotivoConsulta> obtenerPorId(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<MotivoConsulta> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody MotivoConsulta motivo) {

        return service.findById(id)
                .map(existing -> {
                    motivo.setId(id);
                    MotivoConsulta actualizado = service.save(motivo);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return service.findById(id)
                .map(existing -> {
                    service.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}