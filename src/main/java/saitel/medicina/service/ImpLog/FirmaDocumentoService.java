
    package saitel.medicina.service.ImpLog;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.core.ParameterizedTypeReference;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.server.ResponseStatusException;
    import saitel.medicina.dto.FirmarDocumentoDto;
    import saitel.medicina.dto.DocumentoBase64Dto;
    import saitel.medicina.filter.HeaderFilter;
import saitel.medicina.service.ApiConsumoService;

import java.util.Map;

    @Service
    public class FirmaDocumentoService {

    @Autowired
    private ApiConsumoService apiConsumoService;

    @Value("${api.documento.firmar}")
    private String apiDocumentoFirmar;

    public DocumentoBase64Dto firmarDocumento(byte[] documento, String clave, int idEmpleado, String etiquetaFirma) {
    DocumentoBase64Dto notificacionFirmada = new DocumentoBase64Dto("");

    if (documento != null && documento.length > 0) {
        FirmarDocumentoDto firmarDocumentoDto = new FirmarDocumentoDto();
        firmarDocumentoDto.setClave(clave);
        firmarDocumentoDto.setIdEmpleado(idEmpleado);
        firmarDocumentoDto.setDocumento(documento);
        firmarDocumentoDto.setQrDimension(25);
        firmarDocumentoDto.setNombreDocumento("documento_digital.pdf");
        firmarDocumentoDto.setUbicarFirma(false);
        firmarDocumentoDto.setPagina(0);
        firmarDocumentoDto.setCordX(0f);
        firmarDocumentoDto.setCordY(0f);
        firmarDocumentoDto.setOrden(0);
        firmarDocumentoDto.setEtiqueta(etiquetaFirma);

        Map<String, String> headers = HeaderFilter.getHeaders();
        String usuario = headers.get("usuario");
        String sesion = headers.get("sesion");
        String app = headers.get("app");

        System.out.println("Headers enviados: usuario=" + usuario + ", sesion=" + sesion + ", app=" + app);

       System.out.println("Payload JSON enviado para firma: empleadoID=" + firmarDocumentoDto.getIdEmpleado() + ", etiqueta=" + firmarDocumentoDto.getEtiqueta());


        ResponseEntity<DocumentoBase64Dto> solicitudFirmada = null;
        try {
            solicitudFirmada = apiConsumoService.put(
                apiDocumentoFirmar,
                firmarDocumentoDto,
                new ParameterizedTypeReference<DocumentoBase64Dto>() {},
                usuario, sesion, app
            );
            System.out.println("Respuesta API de firma: " + solicitudFirmada);
        } catch (ResponseStatusException e) {
            System.out.println("Error al llamar a API de firma: " + e.getMessage());
            throw e;
        }

        if (solicitudFirmada.getStatusCode().is2xxSuccessful() && solicitudFirmada.getBody() != null) {
            notificacionFirmada = solicitudFirmada.getBody();
        }
    }

    return notificacionFirmada;
}
}