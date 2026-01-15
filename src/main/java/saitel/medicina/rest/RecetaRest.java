package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.RecetasEnviada;
import saitel.medicina.reportes.RecetaReporte;
import saitel.medicina.service.RecetaService;
import saitel.medicina.dto.DocumentoBase64Dto;
import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RecetaRest {

    private final RecetaService recetaService;
    private final RecetaReporte recetaReporte;
    
    @PostMapping("/guardar")
    public ResponseEntity<RecetasEnviada> guardar(@Validated @RequestBody RecetasEnviada receta) {
        RecetasEnviada nueva = recetaService.guardar(receta);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/")
    public ResponseEntity<List<RecetasEnviada>> listarTodos() {
        List<RecetasEnviada> lista = recetaService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetasEnviada> obtenerPorId(@PathVariable Integer id) {
        return recetaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<RecetasEnviada> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody RecetasEnviada receta) {

        return recetaService.findById(id)
                .map(existing -> {
                    receta.setId(id);
                    RecetasEnviada actualizada = recetaService.actualizar(id, receta);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return recetaService.findById(id)
                .map(existing -> {
                    recetaService.delete(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
    
    @GetMapping("/pdf/{idEvaluacion}")
    public ResponseEntity<DocumentoBase64Dto> descargarReceta(@PathVariable Integer idEvaluacion) {
        try {
            DocumentoBase64Dto dto = recetaReporte.generarReceta(idEvaluacion);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}