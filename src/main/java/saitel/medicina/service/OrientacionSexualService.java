package saitel.medicina.service;

import saitel.medicina.entity.OrientacionSexual;
import java.util.List;

public interface OrientacionSexualService {
    List<OrientacionSexual> findAll();
    OrientacionSexual findById(Integer id);
    OrientacionSexual save(OrientacionSexual orientacionSexual);
    Boolean deleteById(Integer id);
}
