package saitel.medicina.service.ImpLog;

import java.util.List;

import org.springframework.stereotype.Service;
import saitel.medicina.repository.SucursalProjection;
import saitel.medicina.repository.SucursalRepository;
import saitel.medicina.service.SucursalService;

@Service
public class SucursalServiceImpLog implements SucursalService {
private final SucursalRepository sucursalRepository;
public SucursalServiceImpLog (SucursalRepository sucursalRepository){
    this. sucursalRepository=sucursalRepository;
}
@Override
public List<SucursalProjection> obtenerSucursales() {
        return sucursalRepository
            .findAllByEstadoTrueAndEliminadoFalseOrderByIdSucursalAsc();
}

}
