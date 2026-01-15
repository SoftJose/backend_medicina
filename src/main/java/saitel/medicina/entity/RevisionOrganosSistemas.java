package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tbl_revision_organos_sistemas")
public class RevisionOrganosSistemas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revision", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @ColumnDefault("false")
    @Column(name = "piel_anexos")
    private Boolean pielAnexos;

    @ColumnDefault("false")
    @Column(name = "organos_sentidos")
    private Boolean organosSentidos;

    @ColumnDefault("false")
    @Column(name = "respiratorio")
    private Boolean respiratorio;

    @ColumnDefault("false")
    @Column(name = "cardio_vascular")
    private Boolean cardioVascular;

    @ColumnDefault("false")
    @Column(name = "digestivo")
    private Boolean digestivo;

    @ColumnDefault("false")
    @Column(name = "genito_urinario")
    private Boolean genitoUrinario;

    @ColumnDefault("false")
    @Column(name = "musculo_esqueletico")
    private Boolean musculoEsqueletico;

    @ColumnDefault("false")
    @Column(name = "endocrino")
    private Boolean endocrino;

    @ColumnDefault("false")
    @Column(name = "hemo_linfatico")
    private Boolean hemoLinfatico;

    @ColumnDefault("false")
    @Column(name = "nervioso")
    private Boolean nervioso;

    @Column(name = "descripcion", length = Integer.MAX_VALUE)
    private String descripcion;

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

}