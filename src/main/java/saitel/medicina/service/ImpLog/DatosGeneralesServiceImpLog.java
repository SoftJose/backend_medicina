package saitel.medicina.service.ImpLog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saitel.medicina.entity.DatosGeneralesCertificados;
import saitel.medicina.repository.DatosGeneralesRepository;
import saitel.medicina.service.DatosGeneralesService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatosGeneralesServiceImpLog implements DatosGeneralesService {
    private final DatosGeneralesRepository repository;

    @Override
    public DatosGeneralesCertificados save(DatosGeneralesCertificados datosGenerales) {
        if (datosGenerales == null) {
        throw new IllegalArgumentException("El objeto datosGenerales no puede ser nulo");
    }
        return repository.save(datosGenerales);
    }

    @Override
    public List<DatosGeneralesCertificados> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<DatosGeneralesCertificados> findById(Integer id) {
        if (id == null) {
        throw new IllegalArgumentException("El id no puede ser nulo");
    }
        return repository.findById(id);
    }

    @Override
    public DatosGeneralesCertificados updateDatosGenerales(Integer id, DatosGeneralesCertificados datosGenerales) {
        if (id == null) {
        throw new IllegalArgumentException("El id no puede ser nulo");
    }
        return repository.findById(id)
                .map(existing -> {
                    datosGenerales.setId(id);
                    return repository.save(datosGenerales);
                })
                .orElse(null);
    }

    @Override
    public Boolean deleteById(Integer id) {
        if (id == null) {
        throw new IllegalArgumentException("El id no puede ser nulo");
    }
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<DatosGeneralesCertificados> findByEvaluacion(Integer idEvaluacion) {
        if (idEvaluacion == null) {
        throw new IllegalArgumentException("El idEvaluacion no puede ser nulo");
    }
        return repository.findByEvaluacion_Id(idEvaluacion);
    }
}
