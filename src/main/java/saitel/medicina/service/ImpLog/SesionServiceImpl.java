package saitel.medicina.service.ImpLog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import saitel.medicina.service.SesionService;

@Service
public class SesionServiceImpl implements SesionService {

    @Value("${uri.autenticar}")
    private String uriAutenticar;

    @Override
    public boolean verificarSesion( String alias, String sesion, String app){
		if (uriAutenticar == null) {
        throw new IllegalArgumentException("La URI de autenticación no puede ser nula");
    }
    if (alias == null || sesion == null || app == null) {
        throw new IllegalArgumentException("Alias, sesion y app no pueden ser nulos");
    }
        String uri = uriAutenticar;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("usuario", alias);
        httpHeaders.set("sesion", sesion);
        httpHeaders.set("app", app);

        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, boolean.class);
        boolean response = responseEntity.getBody() != null ? responseEntity.getBody(): responseEntity.getBody() ;
        return response;
        // if (!response) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Sesion caducada. Ha iniciado sesion en otro dispositivo.");}
    }
}
