package saitel.medicina.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import saitel.medicina.entity.Sucursal;
import saitel.medicina.service.SucursalService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/sucursales")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SucursalRest {

    private final SucursalService sucursalService;

    @GetMapping("/")
    public ResponseEntity<List<Sucursal>> listarTodos() {
        List<Sucursal> sucursales = sucursalService.ObtenerSucursalesOrdenadasPorId();
        return ResponseEntity.ok(sucursales);
    }

}
