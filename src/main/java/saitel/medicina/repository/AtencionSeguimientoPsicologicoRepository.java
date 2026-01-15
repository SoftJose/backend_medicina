package saitel.medicina.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.AtencionSeguimientoPsicologico;
import saitel.medicina.entity.HistoriaClinicaPsicologica;

@Repository
public interface AtencionSeguimientoPsicologicoRepository extends JpaRepository<AtencionSeguimientoPsicologico, Integer> {
	List<AtencionSeguimientoPsicologico> findByHistoriaClinicaPsicologica(HistoriaClinicaPsicologica historiaClinicaPsicologica);
	List<AtencionSeguimientoPsicologico> findByHistoriaClinicaPsicologicaIn(List<HistoriaClinicaPsicologica> historia);
}
