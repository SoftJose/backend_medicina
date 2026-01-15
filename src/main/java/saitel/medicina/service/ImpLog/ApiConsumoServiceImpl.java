package saitel.medicina.service.ImpLog;

import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import saitel.medicina.service.ApiConsumoService;

@Service
public class ApiConsumoServiceImpl implements ApiConsumoService {

    private <T, R> ResponseEntity<R> sendRequest(String uri, 
                                HttpMethod method, 
                                T requestBody, 
                                 ParameterizedTypeReference<R> responseType, 
                                HttpHeaders headers, 
                                Object... uriVariables) {
                                    
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpEntity<T> httpEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<R> response = restTemplate.exchange(uri, method, httpEntity, responseType, uriVariables.length == 1 && uriVariables[0] instanceof Map ? ((Map<?, ?>) uriVariables[0]).values().toArray() : uriVariables);

            return response;
        } catch (Exception e) {
            if(e.getMessage().startsWith("I/O error on")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error de conexión a la API");
            }
            JSONObject jsonResponse = new JSONObject(e.getMessage().substring(e.getMessage().indexOf("{")));
            String message = jsonResponse.optString("message", "An error occurred");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private HttpHeaders buildHeaders(String usuario, String sesion, String app) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("usuario", usuario);
        headers.set("sesion", sesion);
        headers.set("app", app);
        return headers;
    }

    @Override
    public <T, R> ResponseEntity<R> get(String uri, ParameterizedTypeReference<R> responseType, String usuario, String sesion, String app,
            Object... uriVariables) {
        HttpHeaders headers = buildHeaders(usuario, sesion, app);
        return sendRequest(uri, HttpMethod.GET, null, responseType, headers, uriVariables);
    }

    @Override
    public <T, R> ResponseEntity<R> post(String uri, T body, ParameterizedTypeReference<R> responseType, String usuario, String sesion, String app){
        HttpHeaders headers = buildHeaders(usuario, sesion, app);
        return sendRequest(uri, HttpMethod.POST, body, responseType, headers, new Object[0]);
    }

    @Override
    public <T, R> ResponseEntity<R> put(String uri, T body, ParameterizedTypeReference<R> responseType, String usuario, String sesion, String app) {
        HttpHeaders headers = buildHeaders(usuario, sesion, app);
        return sendRequest(uri, HttpMethod.PUT, body, responseType, headers, new Object[0]);
    }
}

