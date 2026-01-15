package saitel.medicina.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import saitel.medicina.entity.OrientacionSexual;
import saitel.medicina.service.OrientacionSexualService;
import java.util.List;

@RestController
@RequestMapping("/api/orientacion-sexual")
public class OrientacionSexualRest {

    @Autowired
    private OrientacionSexualService orientacionSexualService;

    @GetMapping("/")
    public List<OrientacionSexual> getAll() {
        return orientacionSexualService.findAll();
    }

    @GetMapping("/{id}")
    public OrientacionSexual getById(@PathVariable Integer id) {
        return orientacionSexualService.findById(id);
    }

    @PostMapping("/guardar")
    public OrientacionSexual create(@RequestBody OrientacionSexual orientacionSexual) {
        return orientacionSexualService.save(orientacionSexual);
    }

    @PutMapping("/actualizar/{id}")
    public OrientacionSexual update(@PathVariable Integer id, @RequestBody OrientacionSexual orientacionSexual) {
        orientacionSexual.setIdOrientacionSexual(id);
        return orientacionSexualService.save(orientacionSexual);
    }

    @DeleteMapping("/eliminar/{id}")
    public void delete(@PathVariable Integer id) {
        orientacionSexualService.deleteById(id);
    }
}
