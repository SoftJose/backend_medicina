package saitel.medicina.entity;

import java.math.BigDecimal;
import java.sql.Date;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Immutable
@Table(name = "f_vta_empleado", schema = "medicina")
@Subselect("SELECT * FROM f_vta_empleado")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatosEmpleados {

    @Id
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @Column(name = "sucursal")
    private String sucursal;

    @Column(name = "cedula")
    private String cedula;

    @Column(name = "primer_apellido")
    private String apellido;

    @Column(name = "primer_nombre")
    private String nombre;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "tipo_sangre")
    private String tipoSangre;

    @Column(name = "etnia")
    private String etnia;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "direccion")
    private String direccion;
    
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "discapacidad")
    private String discapacidad;

    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fecha_nac")
    private Date fechaNacimiento;

    @Column(name = "lugar_nacimiento")
    private String lugarNacimiento;

    @Column(name = "dis_porcentaje")
    private String disPorcentaje;

    @Column(name = "txt_tipo_ident")
    private String tipoIdentificacion;

    @Column(name = "nacionalidad")
    private String nacionalidad;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "movil")
    private String movil;

    @Column(name = "sueldo")
    private BigDecimal sueldo;

    @Column(name = "txtdatos_academico")
    private String DatosAcademico;

    @Column(name = "alias")
    private String alias;

    @Column(name = "email")
    private String email;

    @Column(name = "txt_estado_civil")
    private String EstadoCivil;

    @Column(name = "padre_dir_trabajo")
    private String padreDirTrabajo;

    @Column(name = "padre_telefono")
    private String padreTelefono;

    @Column(name = "madre_nombre")
    private String madreNombre;

    @Column(name = "madre_apellido")
    private String madreApellido;

    @Column(name = "madre_dir_trabajo")
    private String madreDirTrabajo;

    @Column(name = "madre_telefono")
    private String madreTelefono;

    @Column(name = "coyg_nombre")
    private String conyugeNombre;

    @Column(name = "coyg_apellido")
    private String conyugeApellido;

    @Column(name = "coyg_dir_trabajo")
    private String conyugeDirTrabajo;

    @Column(name = "coyg_telefono")
    private String conyugeTelefono;

    @Column(name = "email_personal")
    private String emailPersonal;

    @Column(name = "fecha_salida")
    private Date fechaSalida;

    @Column(name = "referencias")
    private String referencias;

    // Métodos de utilidad para extraer nombres/ apellidos
    @Transient
    @JsonProperty("primerNombre")
    public String getPrimerNombre() {
        if (nombre != null && nombre.contains(" ")) {
            return nombre.split(" ")[0];
        }
        return nombre;
    }

    @Transient
    @JsonProperty("segundoNombre")
    public String getSegundoNombre() {
        if (nombre != null && nombre.contains(" ")) {
            String[] partes = nombre.split(" ");
            return partes.length > 1 ? partes[1] : "";
        }
        return "";
    }

    @Transient
    @JsonProperty("primerApellido")
    public String getPrimerApellido() {
        if (apellido != null && apellido.contains(" ")) {
            return apellido.split(" ")[0];
        }
        return apellido;
    }

    @Transient
    @JsonProperty("segundoApellido")
    public String getSegundoApellido() {
        if (apellido != null && apellido.contains(" ")) {
            String[] partes = apellido.split(" ");
            return partes.length > 1 ? partes[1] : "";
        }
        return "";
    }

    @Transient
    public String generarNumeroHistoriaClinica() {
        String primerNombre = getPrimerNombre();
        String segundoNombre = getSegundoNombre();
        String primerApellido = getPrimerApellido();
        String segundoApellido = getSegundoApellido();

        String parte1 = primerApellido != null && primerApellido.length() >= 2
                ? primerApellido.substring(0, 2).toUpperCase()
                : (primerApellido != null ? primerApellido.toUpperCase() : "");

        String parte2 = segundoApellido != null && segundoApellido.length() >= 2
                ? segundoApellido.substring(0, 2).toUpperCase()
                : (segundoApellido != null ? segundoApellido.toUpperCase() : "");

        String parte3 = primerNombre != null && primerNombre.length() >= 2
                ? primerNombre.substring(0, 2).toUpperCase()
                : (primerNombre != null ? primerNombre.toUpperCase() : "");

        String parte4 = segundoNombre != null && segundoNombre.length() >= 2
                ? segundoNombre.substring(0, 2).toUpperCase()
                : (segundoNombre != null ? segundoNombre.toUpperCase() : "");

        String parte5 = cedula != null && cedula.length() >= 2
                ? cedula.substring(cedula.length() - 2)
                : "00";

        return parte1 + parte2 + parte3 + parte4 + parte5;
    }

}
