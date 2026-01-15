package saitel.medicina.service.ImpLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import saitel.medicina.dto.DocumentoApiDto;
import saitel.medicina.filter.HeaderFilter;
import saitel.medicina.service.ApiConsumoService;

import java.util.Map;
import java.util.Optional;

@Service
public class DocumentoConsultaService {

    @Autowired
    private ApiConsumoService apiConsumoService;

    @Value("${api.documento.info}")
    private String apiDocumentoInfoPattern; 
    // http://.../documento/info/{base}/{catalogo}/{tabla}/{idTabla}/{campoTabla}

    public Optional<DocumentoApiDto> consultar(String base,
                                               String catalogo,
                                               String tabla,
                                               Long idTabla,
                                               String campoTabla) {
        try {
            String url = apiDocumentoInfoPattern
                    .replace("{base}", base)
                    .replace("{catalogo}", catalogo)
                    .replace("{tabla}", tabla)
                    .replace("{idTabla}", String.valueOf(idTabla))
                    .replace("{campoTabla}", campoTabla);

            Map<String, String> headers = HeaderFilter.getHeaders();
            String usuario = headers.get("usuario");
            String sesion = headers.get("sesion");
            String app = headers.get("app");

            var resp = apiConsumoService.get(
                    url,
                    new ParameterizedTypeReference<DocumentoApiDto>() {},
                    usuario, sesion, app
            );

            return Optional.ofNullable(resp.getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}