package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.FichaSocial;

import java.util.Optional;

@Repository
public interface FichaSocialRepository extends JpaRepository<FichaSocial,Integer> {

    Optional<FichaSocial> findByIdEmpleado(Integer id);
    boolean existsByIdEmpleado(Integer idEmpleado);
}
