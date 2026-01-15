package saitel.medicina.service.ImpLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import saitel.medicina.dto.DocumentoApiDto;
import saitel.medicina.filter.HeaderFilter;
import saitel.medicina.service.ApiConsumoService;
import saitel.medicina.util.ExtensionUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

@Service
public class DocumentoService {

    @Autowired
    private ApiConsumoService apiConsumoService;

    @Value("${api.documento.ingresar}")
    private String apiDocumentoIngresar;

    public void guardarDocumento(String tabla,
                                 Long idTabla,
                                 String campoTabla,
                                 String documento,     // Base64
                                 String base,
                                 String catalogo,
                                 String extension,
                                 String nombreDocumento) {

        if (documento != null) {
            if (!documento.isBlank()) {
                DocumentoApiDto documentoApiDto = new DocumentoApiDto();
                documentoApiDto.setTabla(tabla);
                documentoApiDto.setIdTabla(idTabla);
                documentoApiDto.setCampoTabla(campoTabla);
                documentoApiDto.setDocumento(Base64.getDecoder().decode(documento));
                documentoApiDto.setBase(base);
                documentoApiDto.setCatalogo(catalogo);
                documentoApiDto.setNombreDocumento(
                        nombreDocumento
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                                + "." + ExtensionUtil.extension(extension)
                );

                Map<String, String> headers = HeaderFilter.getHeaders();
                String usuario = headers.get("usuario");
                String sesion = headers.get("sesion");
                String app = headers.get("app");

                try {
                    apiConsumoService.post(
                            apiDocumentoIngresar,
                            documentoApiDto,
                            new ParameterizedTypeReference<DocumentoApiDto>() {},
                            usuario, sesion, app
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
