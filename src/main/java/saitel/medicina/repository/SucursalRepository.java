package saitel.medicina.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import saitel.medicina.entity.Sucursal;

@Repository
public interface SucursalRepository extends JpaRepository <Sucursal, Long>{
    List <Sucursal> findAllByEstadoTrueAndEliminadoFalseOrderByIdSucursalAsc();
}
    