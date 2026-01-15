package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.RevisionOrganosSistemas;
import saitel.medicina.service.RevisionOrganosSistemasService;

import java.util.List;

@RestController
@RequestMapping("/api/revision-organos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RevisionOrganosSistemasRest {

    private final RevisionOrganosSistemasService service;

    @PostMapping("/guardar")
    public ResponseEntity<RevisionOrganosSistemas> guardar(@RequestBody RevisionOrganosSistemas revisionOrganosSistemas) {
        RevisionOrganosSistemas nueva = service.guardarRevision(revisionOrganosSistemas);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/")
    public ResponseEntity<List<RevisionOrganosSistemas>> listarTodos() {
        List<RevisionOrganosSistemas> lista = service.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RevisionOrganosSistemas> obtenerPorId(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<RevisionOrganosSistemas> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody RevisionOrganosSistemas revisionOrganosSistemas) {

        return service.findById(id)
                .map(existing -> {
                    revisionOrganosSistemas.setId(id);
                    RevisionOrganosSistemas actualizada = service.updateRevision(id, revisionOrganosSistemas);
                    return ResponseEntity.ok(actualizada);
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