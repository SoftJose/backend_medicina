package saitel.medicina.service;


public interface SesionService {
    boolean verificarSesion(String usuario, String sesion, String app);
}
