package saitel.medicina.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import saitel.medicina.repository.SucursalProjection;
import saitel.medicina.service.SucursalService;

@Controller
@RequiredArgsConstructor
public class SucursalQuery {
private final SucursalService sucursalService;

    @QueryMapping
    public List<SucursalProjection> sucursales() {
        return sucursalService.obtenerSucursales();
    }
}
