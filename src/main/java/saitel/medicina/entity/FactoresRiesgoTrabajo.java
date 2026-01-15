package saitel.medicina.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_factores_riesgo_trabajo")
public class FactoresRiesgoTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factores_riesgo_trabajo", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Evaluacion idEvaluacion;

    @Column(name = "puesto_trabajo", length = Integer.MAX_VALUE)
    private String puestoTrabajo;

    @Column(name = "actividades", length = Integer.MAX_VALUE)
    private String actividades;

    @Column(name = "riesgo_temp_alta")
    private Boolean riesgoTempAlta;

    @Column(name = "riesgo_temp_baja")
    private Boolean riesgoTempBaja;

    @Column(name = "riesgo_radiacion_ionizante")
    private Boolean riesgoRadiacionIonizante;

    @Column(name = "riesgo_radiacion_no_ionizante")
    private Boolean riesgoRadiacionNoIonizante;

    @Column(name = "riesgo_ruido")
    private Boolean riesgoRuido;

    @Column(name = "riesgo_vibracion")
    private Boolean riesgoVibracion;

    @Column(name = "riesgo_iluminacion")
    private Boolean riesgoIluminacion;

    @Column(name = "riesgo_ventilacion")
    private Boolean riesgoVentilacion;

    @Column(name = "riesgo_fluido_electrico")
    private Boolean riesgoFluidoElectrico;

    @Column(name = "riesgo_fisico_otros", length = Integer.MAX_VALUE)
    private String riesgoFisicoOtros;

    @Column(name = "riesgo_atrapamiento_maquinas")
    private Boolean riesgoAtrapamientoMaquinas;

    @Column(name = "riesgo_atrapamiento_superficies")
    private Boolean riesgoAtrapamientoSuperficies;

    @Column(name = "riesgo_atrapamiento_objetos")
    private Boolean riesgoAtrapamientoObjetos;

    @Column(name = "riesgo_caida_objetos")
    private Boolean riesgoCaidaObjetos;

    @Column(name = "riesgo_caida_mismo_nivel")
    private Boolean riesgoCaidaMismoNivel;

    @Column(name = "riesgo_caida_diferente_nivel")
    private Boolean riesgoCaidaDiferenteNivel;

    @Column(name = "riesgo_contacto_superficies_trabajo")
    private Boolean riesgoContactoSuperficiesTrabajo;

    @Column(name = "riesgo_contacto_partes_fluido")
    private Boolean riesgoContactoPartesFluido;

    @Column(name = "riesgo_proyeccion_particulas")
    private Boolean riesgoProyeccionParticulas;

    @Column(name = "riesgo_proyeccion_fluido")
    private Boolean riesgoProyeccionFluido;

    @Column(name = "riesgo_pinchazos")
    private Boolean riesgoPinchazos;

    @Column(name = "riesgo_cortes")
    private Boolean riesgoCortes;

    @Column(name = "riesgo_atropellamiento_vehiculos")
    private Boolean riesgoAtropellamientoVehiculos;

    @Column(name = "riesgo_choque_vehicular")
    private Boolean riesgoChoqueVehicular;

    @Column(name = "riesgo_mecanico_otros", length = Integer.MAX_VALUE)
    private String riesgoMecanicoOtros;

    @Column(name = "riesgo_solidos")
    private Boolean riesgoSolidos;

    @Column(name = "riesgo_polvos")
    private Boolean riesgoPolvos;

    @Column(name = "riesgo_humos")
    private Boolean riesgoHumos;

    @Column(name = "riesgo_liquidos")
    private Boolean riesgoLiquidos;

    @Column(name = "riesgo_vapores")
    private Boolean riesgoVapores;

    @Column(name = "riesgo_aerosoles")
    private Boolean riesgoAerosoles;

    @Column(name = "riesgo_neblinas")
    private Boolean riesgoNeblinas;

    @Column(name = "riesgo_gases")
    private Boolean riesgoGases;

    @Column(name = "riesgo_quimico_otros", length = Integer.MAX_VALUE)
    private String riesgoQuimicoOtros;

    @Column(name = "riesgo_virus")
    private Boolean riesgoVirus;

    @Column(name = "riesgo_hongos")
    private Boolean riesgoHongos;

    @Column(name = "riesgo_bacterias")
    private Boolean riesgoBacterias;

    @Column(name = "riesgo_parasitos")
    private Boolean riesgoParasitos;

    @Column(name = "riesgo_exposicion_animales")
    private Boolean riesgoExposicionAnimales;

    @Column(name = "riesgo_exposicion_vector")
    private Boolean riesgoExposicionVector;

    @Column(name = "riesgo_biologico_otros", length = Integer.MAX_VALUE)
    private String riesgoBiologicoOtros;

    @Column(name = "riesgo_manejo_cargas")
    private Boolean riesgoManejoCargas;

    @Column(name = "riesgo_movimientos_repetitivos")
    private Boolean riesgoMovimientosRepetitivos;

    @Column(name = "riesgo_posturas_forzadas")
    private Boolean riesgoPosturasForzadas;

    @Column(name = "riesgo_trabajo_pvd")
    private Boolean riesgoTrabajoPvd;

    @Column(name = "riesgo_ergonomico_otros", length = Integer.MAX_VALUE)
    private String riesgoErgonomicoOtros;

    @Column(name = "riesgo_monotonia")
    private Boolean riesgoMonotonia;

    @Column(name = "riesgo_cantidad_tarea")
    private Boolean riesgoCantidadTarea;

    @Column(name = "riesgo_responsabilidad")
    private Boolean riesgoResponsabilidad;

    @Column(name = "riesgo_alta_exigencia")
    private Boolean riesgoAltaExigencia;

    @Column(name = "riesgo_supervision_autoridad")
    private Boolean riesgoSupervisionAutoridad;

        @Column(name = "riesgo_conflicto_rol")
    private Boolean riesgoConflictoRol;
    
    @Column(name = "riesgo_definicion_rol")
    private Boolean riesgoDefinicionRol;

    @Column(name = "riesgo_falta_autonomia")
    private Boolean riesgoFaltaAutonomia;

    @Column(name = "riesgo_inversion_trabajo")
    private Boolean riesgoInversionTrabajo;

    @Column(name = "riesgo_turnos")
    private Boolean riesgoTurnos;

    @Column(name = "riesgo_relaciones_interpersonales")
    private Boolean riesgoRelacionesInterpersonales;

    @Column(name = "riesgo_inestabilidad_laboral")
    private Boolean riesgoInestabilidadLaboral;

    @Column(name = "riesgo_psicosocial_otros", length = Integer.MAX_VALUE)
    private String riesgoPsicosocialOtros;

    @Column(name = "medidas_preventivas", length = Integer.MAX_VALUE)
    private String medidasPreventivas;

    @Column(name = "factor_riesgo_retiro", length = Integer.MAX_VALUE)
    private String factorRiesgoRetiro;

}