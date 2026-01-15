package saitel.medicina.entity;

import lombok.Getter;

@Getter
public enum DepartamentoTipo {
 P("Psicologo"),
    M("Medico");

    private final String nombreDB;

    DepartamentoTipo(String nombreDB) {
        this.nombreDB = nombreDB;
    }

    public static DepartamentoTipo fromCode(String code) {
        if (code == null) throw new IllegalArgumentException("El departamento no puede ser nulo");
        return switch (code.toUpperCase()) {
            case "P" -> P;
            case "M" -> M;
            default -> throw new IllegalArgumentException("Departamento inválido: " + code);
        };
    }
}
