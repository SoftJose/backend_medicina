package saitel.medicina.service;

import saitel.medicina.entity.TiposEnfermedadFamiliar;

import java.util.List;

public interface TiposEnfermedadesFamiliaresService {
    List<TiposEnfermedadFamiliar> findAll();
    TiposEnfermedadFamiliar findById(Integer id);
    TiposEnfermedadFamiliar save(TiposEnfermedadFamiliar tiposEnfermedadFamiliar);
    Boolean deleteById(Integer id);
    TiposEnfermedadFamiliar update(Integer id, TiposEnfermedadFamiliar tiposEnfermedadFamiliar);

}
