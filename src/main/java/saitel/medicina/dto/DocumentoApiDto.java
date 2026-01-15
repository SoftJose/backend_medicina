package saitel.medicina.dto;

import lombok.Data;

@Data
public class DocumentoApiDto {
    private String tabla;
    private Long idTabla;
    private String campoTabla;
    private byte[] documento;
    private String base;
    private String catalogo;
    private String nombreDocumento;
}
