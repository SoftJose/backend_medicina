package saitel.medicina.service.ImpLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saitel.medicina.entity.OrientacionSexual;
import saitel.medicina.repository.OrientacionSexualRepository;
import saitel.medicina.service.OrientacionSexualService;

import java.util.List;
import java.util.Optional;

@Service
public class OrientacionSexualServiceImpLog implements OrientacionSexualService {

    @Autowired
    private OrientacionSexualRepository orientacionSexualRepository;

    @Override
    @Transactional
    public List<OrientacionSexual> findAll() {
        return orientacionSexualRepository.findAll();
    }

    @Override
    @Transactional
    public OrientacionSexual findById(Integer id) {
         if (id == null) {
        throw new IllegalArgumentException("El ID no puede ser nulo");
    }
        Optional<OrientacionSexual> opt = orientacionSexualRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    @Transactional
    public OrientacionSexual save(OrientacionSexual orientacionSexual) {
        if (orientacionSexual == null) {
        throw new IllegalArgumentException("El objeto OrientacionSexual no puede ser nulo");
    }
        return orientacionSexualRepository.save(orientacionSexual);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del motivo no puede ser nulo");
        }

        try {
            if (!orientacionSexualRepository.existsById(id)) {
                return false;
            }
            orientacionSexualRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el motivo de consulta con ID: " + id, e);
        }
    }
}
