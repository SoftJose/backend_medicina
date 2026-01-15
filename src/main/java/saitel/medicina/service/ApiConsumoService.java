package saitel.medicina.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public interface ApiConsumoService {
        
    public <T, R> ResponseEntity<R> get(String uri, ParameterizedTypeReference<R> responseType, String usuario, String sesion, String app, Object... uriVariables);
    
    public <T, R> ResponseEntity<R> put(String uri, T body, ParameterizedTypeReference<R> responseType, String usuario, String sesion, String app);

    public <T, R> ResponseEntity<R> post(String uri, T body, ParameterizedTypeReference<R> responseType, String usuario, String sesion, String app);

}
