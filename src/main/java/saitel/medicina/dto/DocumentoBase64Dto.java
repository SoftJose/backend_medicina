package saitel.medicina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentoBase64Dto {

    @JsonProperty("recurso")
    private String base64;

    public DocumentoBase64Dto() {}

    public DocumentoBase64Dto(String base64) {
        this.base64 = base64;
    }
}
