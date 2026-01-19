package saitel.medicina.service;

import java.util.List;

import saitel.medicina.repository.SucursalProjection;

public interface SucursalService {
    List<SucursalProjection> obtenerSucursales();
}
