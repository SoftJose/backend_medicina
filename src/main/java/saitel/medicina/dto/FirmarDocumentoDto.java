package saitel.medicina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmarDocumentoDto {
    private byte[] documento;
    private String clave;
    private String etiqueta;
    private int idEmpleado;
    private int qrDimension;
    private String nombreDocumento;
    private boolean ubicarFirma;
    private int pagina;
    private float cordX;
    private float cordY;
    private int orden;
}
