    package saitel.medicina.service;

    import saitel.medicina.entity.Evaluacion;

    import java.util.List;
    import java.util.Optional;

    public interface EvaluacionService {
        
        List<Evaluacion> findAll();
        Optional<Evaluacion> findById(Integer id);
        Evaluacion save(String sucursal, String departamento, String param, Evaluacion evaluacion);
        Boolean deleteById(Integer id);
        Evaluacion update(Integer id, Evaluacion evaluacion);
        List<Evaluacion> findByEmpleadoId(Integer idEmpleado);
        Evaluacion firmarEmpleado(Integer id);
        
    }
