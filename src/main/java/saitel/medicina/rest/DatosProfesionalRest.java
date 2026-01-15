package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import saitel.medicina.dto.FirmaRequestDto;
import saitel.medicina.entity.DatosProfesional;
import saitel.medicina.service.DatosProfesionalService;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/datos-profesional")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DatosProfesionalRest {

    private final DatosProfesionalService datosProfesionalService;

    @PostMapping("/guardar")
    public ResponseEntity<DatosProfesional> guardar(@RequestBody DatosProfesional datosProfesional) {
        DatosProfesional nuevo = datosProfesionalService.guardar(datosProfesional);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/")
    public ResponseEntity<List<DatosProfesional>> listarTodos() {
        List<DatosProfesional> lista = datosProfesionalService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosProfesional> obtenerPorId(@PathVariable Integer id) {
        return datosProfesionalService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<DatosProfesional> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody DatosProfesional datosProfesional) {

        return datosProfesionalService.findById(id)
                .map(existing -> {
                    datosProfesional.setId(id);
                    DatosProfesional actualizado = datosProfesionalService.updateDatosProfesional(id, datosProfesional);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return datosProfesionalService.findById(id)
                .map(existing -> {
                    datosProfesionalService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

   @PutMapping("/firmar/{id}")
        public ResponseEntity<DatosProfesional> firmarProfesional(
        @PathVariable Integer id,
        @RequestBody FirmaRequestDto request) {

    try {
        byte[] pdfBytes = Base64.getDecoder().decode(request.getPdfBase64().replaceAll("\\s+", ""));

        DatosProfesional actualizado = datosProfesionalService.firmarProfesional(id, request.getClave(), pdfBytes);
        return ResponseEntity.ok(actualizado);

    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
}