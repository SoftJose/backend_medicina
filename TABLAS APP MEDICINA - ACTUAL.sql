CREATE TABLE medicina.tbl_religion (
    id_religion SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

--DEPARTAMENTO MEDICO Y PSICOLOGICO
CREATE TABLE medicina.tbl_cita_medica (
    id_cita SERIAL PRIMARY KEY,
    id_empleado INT NOT NULL,            
    alias_usuario VARCHAR(25) NOT NULL, -- Alias del profesional logueado
    tipo_profesional VARCHAR(20) CHECK (tipo_profesional IN ('MEDICO', 'PSICOLOGO')) NOT NULL,
    motivo_consulta TEXT NOT NULL,
    departamento VARCHAR(50), -- Médico o Psicológico
    fecha_cita DATE NOT NULL,
    hora_cita TIME NOT NULL,
    observacion TEXT,
    notificacion_enviada BOOLEAN DEFAULT FALSE,
    estado VARCHAR(20) CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'REALIZADA')) DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

 -- DEPARTAMENTO PSICOLOGICO--
CREATE TABLE medicina.tbl_ficha_social (
    id_ficha_social SERIAL PRIMARY KEY,
    id_empleado INT NOT NULL,
    id_religion INT not null,
    fecha DATE NOT NULL DEFAULT now(),
    contactos_emergencia JSONB, --[{ "parentesco": "", "nombre": "",  "telefono": "" }]        
    genograma TEXT,                 
    numero_historia_clinica VARCHAR(50) UNIQUE NOT null
    FOREIGN KEY (id_religion) REFERENCES medicina.tbl_religion(id_religion)
);

CREATE TABLE medicina.tbl_historia_clinica_psicologica (
    id_historia SERIAL PRIMARY KEY,
    id_empleado INT NOT NULL,              
    fecha_atencion DATE NOT NULL DEFAULT CURRENT_DATE,   -- Fecha de la sesión clínica
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Fecha de ingreso al sistema
    motivo_consulta TEXT,
    observacion_general TEXT,-- Examen mental
    aspectos_motivo TEXT,
    historial_situacion TEXT,
    intentos_previos TEXT,
    redes_apoyo JSONB,-- [{ "nombre": "", "parentesco": "" }]
    aspectos_familia JSONB,-- [{ "nombre": "", "parentesco": "", "edad": "", "escolaridad": "", "ocupacion": "", "relacion": "" }]
    observaciones_familia TEXT,
    familiograma TEXT,
    area_personal JSONB,-- { "sueno": "", "alimentacion": "", "ejercicio": "", "pasatiempos": "" }
    historia_academica TEXT,
    area_academica TEXT,
    historial_laboral TEXT,
    antecedentes_personales TEXT,
    relaciones_pareja TEXT,
    conductas_riesgo TEXT,
    impresion_diagnostica TEXT, -- DSM V / CIE-10
    reactivos_aplicados JSONB,-- [{ "nombre": "", "resultados": "", "fecha": "" }]
    plan_tratamiento TEXT
);


CREATE TABLE medicina.tbl_atencion_seguimiento_psicologico (
    id_seguimiento SERIAL PRIMARY KEY,
    id_historia INT NOT NULL,  
    tipo_atencion VARCHAR(20) CHECK (tipo_atencion IN ('ONLINE', 'PRESENCIAL')) NOT NULL,
    psicopatologia TEXT NOT NULL,
    sesion VARCHAR(50) NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    consumo_as TEXT,
    fecha_atencion DATE NOT NULL DEFAULT CURRENT_DATE,
    hora_atencion TIME NOT NULL DEFAULT CURRENT_TIME,
    riesgo_social TEXT,
    otros TEXT,
    temas_tratados TEXT,
    reactivos_aplicados JSONB,  -- [{ "nombre": "", "tipo": "", "resultados": "", "fecha": "" }]
    resultados TEXT,
    herramientas_enfoques TEXT,
    avances TEXT,
    seguimiento_social TEXT,
    familiar_contactado JSONB,  -- { "contactado": true/false, "nombre": "", "parentesco": "", "telefono": "", "relato": "" }
    verificadores_seguimiento TEXT,
    firma_psicologo BOOLEAN DEFAULT FALSE,
    firma_empleado BOOLEAN DEFAULT FALSE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    
    CONSTRAINT fk_historia FOREIGN KEY (id_historia) REFERENCES medicina.tbl_historia_clinica_psicologica(id_historia)
    ON DELETE CASCADE
);


--DEPARTAMENTO MEDICO--
Create table medicina.tbl_tipo_evaluacion(
    id_tipo_evaluacion SERIAL PRIMARY KEY,
    nombre_evaluacion varchar(150) not null,
    fecha DATE NOT NULL DEFAULT now()
); 

CREATE TABLE medicina.tbl_orientacion_sexual (
	id_orientacion_sexual SERIAL primary KEY,
	id_evaluacion int4 NULL,
	nombre varchar NOT NULL,
);

CREATE TABLE medicina.tbl_evaluacion (
    id_evaluacion SERIAL PRIMARY KEY,
    id_tipo_evaluacion INT NOT NULL,
    id_ficha_social INT not null,
    id_orientacion_sexual int4 NULL,
    alias_usuario VARCHAR(50),
    id_empleado INT not null,
    fecha DATE NOT NULL DEFAULT now(),
    resultado TEXT null,
    lateralidad VARCHAR null,
    observaciones TEXT,
    firma_empleado BOOLEAN DEFAULT false,
    Evaluacion_completa BOOLEAN DEFAULT false,
    fecha_ultimo_dia_laboral DATE,
    fecha_reingreso DATE,
    total_dias INT,
    causa_salida TEXT,
    actividades_puesto text,
    puesto_Ciuo varchar(25) NULL
    
    FOREIGN KEY (id_tipo_evaluacion) REFERENCES medicina.tbl_tipo_evaluacion(id_tipo_evaluacion),
    FOREIGN KEY (id_ficha_social) REFERENCES medicina.tbl_ficha_social (id_ficha_social),
    CONSTRAINT tbl_evaluacion_id_orientacion_sexual__fkey FOREIGN KEY (id_orientacion_sexual) REFERENCES medicina.tbl_orientacion_sexual(id_orientacion_sexual)
    
);

--DEPARTAMENTO MEDICO
CREATE TABLE medicina.tbl_tipo_inmunizacion (
    id_tipo_inmunizacion SERIAL PRIMARY KEY,
    nombre_inmunizacion VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE medicina.tbl_inmunizaciones (
    id_inmunizaciones SERIAL PRIMARY KEY,
    id_tipo_inmunizacion INT8 not null,
    id_tipo_evaluacion INT8 NOT NULL,
    id_empleado INT NOT NULL,
    dosis VARCHAR(20) NOT NULL,
    fecha DATE,
    lote VARCHAR(50),
    esquema_completo BOOLEAN,
    responsable_vacunacion VARCHAR(150),
    establecimiento_salud VARCHAR(150),
    observaciones TEXT,
    CONSTRAINT fk_tipo_evaluacion FOREIGN KEY (id_tipo_evaluacion) REFERENCES medicina.tbl_tipo_evaluacion(id_tipo_evaluacion),
    FOREIGN KEY (id_tipo_inmunizacion) REFERENCES medicina.tbl_tipo_inmunizacion(id_tipo_inmunizacion)
);

--COMPONENETES DE EVALUACIONES O  FORMULARIOS MEDICOS

CREATE TABLE medicina.tbl_motivo_consulta (
    id_motivo SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    motivo TEXT NOT NULL,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_antecedentes (
    id_antecedente SERIAL PRIMARY KEY,
    id_evaluacion int8 NOT NULL,
    hijos_vivos INT4 NULL,
    hijos_muertos INT4 NULL,
    usa_metodo_planificacion BOOL NULL,
    tipo_metodo_planificacion VARCHAR(100) NULL,
    -- ANTECEDENTES CLÍNICO-QUIRÚRGICOS
    descripcion_clinico_quirurgico text NULL,
    -- ANTECEDENTES GINECO-OBSTÉTRICOS
    menarquia_edad int4 NULL,
    ciclos_menstruales varchar(50) NULL,
    fecha_ultima_menstruacion date NULL,
    numero_gestas int4 NULL,
    numero_partos int4 NULL,
    numero_cesareas int4 NULL,
    numero_abortos int4 NULL,
    vida_sexual_activa bool NULL,
        -- EXÁMENES REALIZADOS
    examen_papanicolaou bool NULL,
    tiempo_papanicolaou_anios int4 NULL,
    resultado_papanicolaou varchar(255) NULL,

    examen_colposcopia bool NULL,
    tiempo_colposcopia_anios int4 NULL,
    resultado_colposcopia varchar(255) NULL,

    examen_mamografia bool NULL,
    tiempo_mamografia_anios int4 NULL,
    resultado_mamografia varchar(255) NULL,

    examen_eco_mamario bool NULL,
    tiempo_eco_mamario_anios int4 NULL,
    resultado_eco_mamario varchar(255) NULL,
    -- ANTECEDENTES REPRODUCTIVOS MASCULINOS
    
    examen_antigeno_prostatico bool NULL,
    tiempo_antigeno_prostatico_anios int4 NULL,
    resultado_antigeno_prostatico varchar(255) NULL,

    examen_eco_prostatico bool NULL,
    tiempo_eco_prostatico_anios int4 NULL,
    resultado_eco_prostatico varchar(255) NULL,
    -- HÁBITOS TÓXICOS
    consumo_tabaco bool NULL,
    tiempo_tabaco_meses int4 NULL,
    cantidad_tabaco varchar(100) NULL,
    ex_consumidor_tabaco bool NULL,
    abstinencia_tabaco_meses int4 NULL,

    consumo_alcohol bool NULL,
    tiempo_alcohol_meses int4 NULL,
    cantidad_alcohol varchar(100) NULL,
    ex_consumidor_alcohol bool NULL,
    abstinencia_alcohol_meses int4 NULL,

    consumo_otras_drogas bool NULL,
    tipo_otras_drogas varchar(100) NULL,
    tiempo_otras_drogas_meses INT4 null,
    cantidad_otras_drogas varchar(100) NULL,
    ex_consumidor_otras_drogas bool NULL,
    abstinencia_otras_drogas_meses int4 NULL,
    -- ESTILO DE VIDA
    actividad_fisica bool NULL,
    descripcion_actividad_fisica varchar(255) NULL,
    actividad_fisica_dias_semana int4 NULL,
    -- MEDICACIÓN HABITUAL
    medicacion_habitual bool NULL,
    descripcion_medicacion varchar(255) NULL,
    cantidad_medicacion_unidad varchar(100) NULL,
    -- INCIDENTES
    incidentes text NULL,
    -- CONTROL DE REGISTRO
    fecha_registro timestamp DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT tbl_antecedentes_id_evaluacion_fkey FOREIGN KEY (id_evaluacion)
        REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);

CREATE TABLE medicina.tbl_antecedentes_trabajo (
    id_antecedente_trabajo int4 PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    empresa TEXT,
    puesto_trabajo TEXT,
    actividades TEXT,
    tiempo_trabajo_meses INTEGER,
    riesgo_fisico BOOLEAN,
    riesgo_mecanico BOOLEAN,
    riesgo_quimico BOOLEAN,
    riesgo_biologico BOOLEAN,
    riesgo_ergonomico BOOLEAN,
    riesgo_psicosocial BOOLEAN,
    observaciones_empleo TEXT,
    accidente_descripcion TEXT,
    accidente_calificado BOOLEAN,
    accidente_especificar TEXT,
    accidente_fecha DATE,
    accidente_observaciones TEXT,
    enfermedad_descripcion TEXT,
    enfermedad_calificada BOOLEAN,
    enfermedad_especificar TEXT,
    enfermedad_fecha DATE,
    enfermedad_observaciones TEXT,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_tipos_enfermedad_familiar (
    id_tipo_enfermedad INT PRIMARY KEY,
    nombre TEXT NOT null
);
CREATE TABLE medicina.tbl_antecedentes_familiares (
    id_antecedente_familiar SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    id_tipo_enfermedad INT NOT NULL,
    descripcion TEXT NOT null,
FOREIGN KEY (id_tipo_enfermedad) REFERENCES medicina.tbl_tipos_enfermedad_familiar(id_tipo_enfermedad),
FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_factores_riesgo_trabajo (
    id_factores_riesgo_trabajo SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    puesto_trabajo TEXT,
    actividades TEXT,
    tiempo_trabajo_meses varchar(25),
    factor_riesgo_retiro TEXT,
    -- Físico
    riesgo_temp_alta BOOLEAN,
    riesgo_temp_baja BOOLEAN,
    riesgo_radiacion_ionizante BOOLEAN,
    riesgo_radiacion_no_ionizante BOOLEAN,
    riesgo_ruido BOOLEAN,
    riesgo_vibracion BOOLEAN,
    riesgo_iluminacion BOOLEAN,
    riesgo_ventilacion BOOLEAN,
    riesgo_fluido_electrico BOOLEAN,
    riesgo_fisico_otros TEXT,
    -- Mecánico
    riesgo_atrapamiento_maquinas BOOLEAN,
    riesgo_atrapamiento_superficies BOOLEAN,
    riesgo_atrapamiento_objetos BOOLEAN,
    riesgo_caida_objetos BOOLEAN,
    riesgo_caida_mismo_nivel BOOLEAN,
    riesgo_caida_diferente_nivel BOOLEAN,
    riesgo_contacto_superficies_trabajo BOOLEAN,
    riesgo_contacto_partes_fluido BOOLEAN,
    riesgo_proyeccion_particulas BOOLEAN,
    riesgo_proyeccion_fluido BOOLEAN,
    riesgo_pinchazos BOOLEAN,
    riesgo_cortes BOOLEAN,
    riesgo_atropellamiento_vehiculos BOOLEAN,
    riesgo_choque_vehicular BOOLEAN,
    riesgo_mecanico_otros TEXT,
    -- Químico
    riesgo_solidos BOOLEAN,
    riesgo_polvos BOOLEAN,
    riesgo_humos BOOLEAN,
    riesgo_liquidos BOOLEAN,
    riesgo_vapores BOOLEAN,
    riesgo_aerosoles BOOLEAN,
    riesgo_neblinas BOOLEAN,
    riesgo_gases BOOLEAN,
    riesgo_quimico_otros TEXT,
    -- Biológico
    riesgo_virus BOOLEAN,
    riesgo_hongos BOOLEAN,
    riesgo_bacterias BOOLEAN,
    riesgo_parasitos BOOLEAN,
    riesgo_exposicion_animales BOOLEAN,
    riesgo_exposicion_vector BOOLEAN,
    riesgo_biologico_otros TEXT,
    -- Ergonómico
    riesgo_manejo_cargas BOOLEAN,
    riesgo_movimientos_repetitivos BOOLEAN,
    riesgo_posturas_forzadas BOOLEAN,
    riesgo_trabajo_pvd BOOLEAN,
    riesgo_ergonomico_otros TEXT,
    -- Psicosocial
    riesgo_monotonia BOOLEAN,
    riesgo_cantidad_tarea BOOLEAN,
    riesgo_responsabilidad BOOLEAN,
    riesgo_alta_exigencia BOOLEAN,
    riesgo_supervision_autoridad BOOLEAN,
    riesgo_definicion_rol BOOLEAN,
    riesgo_conflicto_rol BOOLEAN,
    riesgo_falta_autonomia BOOLEAN,
    riesgo_inversion_trabajo BOOLEAN,
    riesgo_turnos BOOLEAN,
    riesgo_relaciones_interpersonales BOOLEAN,
    riesgo_inestabilidad_laboral BOOLEAN,
    riesgo_psicosocial_otros TEXT,
    -- Medidas preventivas
    medidas_preventivas TEXT,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);

--AGREGAR CAMPOS A tbl_signo vital

ALTER TABLE medicina.tbl_signo_vital  
ADD COLUMN temperatura NUMERIC(5,2),
ADD COLUMN frecuencia_respiratoria INT,
ADD COLUMN peso NUMERIC(5,2),
ADD COLUMN talla NUMERIC(5,2),
ADD COLUMN masa_corporal NUMERIC(5,2),
ADD COLUMN perimetro_abdominal NUMERIC(5,2),
ALTER TABLE medicina.tbl_signo_vital
ADD COLUMN id_evaluacion INT,
ADD CONSTRAINT fk_signo_evaluacion,
FOREIGN KEY (id_evaluacion)
REFERENCES medicina.tbl_evaluacion(id_evaluacion);

--COMPONENETES DE EVALUACIONES

CREATE TABLE medicina.tbl_actividades_extra_laborales (
    id_actividad serial PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    descripcion TEXT,
    fecha_registro DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_enfermedad_actual (
    id_enfermedad serial PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    descripcion TEXT,
    fecha_registro DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_revision_organos_sistemas (
    id_revision serial PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    piel_anexos BOOLEAN DEFAULT FALSE,
    organos_sentidos BOOLEAN DEFAULT FALSE,
    respiratorio BOOLEAN DEFAULT FALSE,
    cardio_vascular BOOLEAN DEFAULT FALSE,
    digestivo BOOLEAN DEFAULT FALSE,
    genito_urinario BOOLEAN DEFAULT FALSE,
    musculo_esqueletico BOOLEAN DEFAULT FALSE,
    endocrino BOOLEAN DEFAULT FALSE,
    hemo_linfatico BOOLEAN DEFAULT FALSE,
    nervioso BOOLEAN DEFAULT FALSE,
    descripcion TEXT,
    fecha_registro DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);

CREATE TABLE medicina.tbl_examen_fisico (
    id_examen_fisico SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    -- 1. Piel
    cicatrices BOOLEAN,
    tatuajes BOOLEAN,
    piel_faneras BOOLEAN,
    -- 2. Ojos
    parpados BOOLEAN,
    conjuntivas BOOLEAN,
    pupilas BOOLEAN,
    cornea BOOLEAN,
    motilidad_ocular BOOLEAN,
    -- 3. Oído
    conducto_auditivo_externo BOOLEAN,
    pabellon BOOLEAN,
    timpanos BOOLEAN,
    -- 4. Orofaringe
    labios BOOLEAN,
    lengua BOOLEAN,
    faringe BOOLEAN,
    amigdalas BOOLEAN,
    dentadura BOOLEAN,
    -- 5. Nariz
    tabique BOOLEAN,
    cornetes BOOLEAN,
    mucosas BOOLEAN,
    senos_paranasales BOOLEAN,
    -- 6. Cuello
    tiroides_masas BOOLEAN,
    movilidad_cuello BOOLEAN,
    -- 7. Tórax
    mamas BOOLEAN,
    corazon BOOLEAN,
    -- 8. Tórax (Respiratorio)
    pulmones BOOLEAN,
    parrilla_costal BOOLEAN,
    -- 9. Abdomen
    visceras BOOLEAN,
    pared_abdominal BOOLEAN,
    -- 10. Columna
    flexibilidad BOOLEAN,
    desviacion BOOLEAN,
    dolor BOOLEAN,
    -- 11. Pelvis
    pelvis BOOLEAN,
    genitales BOOLEAN,
    -- 12. Extremidades
    vascular BOOLEAN,
    miembros_superiores BOOLEAN,
    miembros_inferiores BOOLEAN,
    -- 13. Neurológico
    fuerza BOOLEAN,
    sensibilidad BOOLEAN,
    marcha BOOLEAN,
    reflejos BOOLEAN,

    observaciones TEXT,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);

CREATE TABLE medicina.tbl_examenes (
    id_examen SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    nombre_examen VARCHAR(100) NOT NULL,
    fecha_examen DATE NOT NULL,
    resultado TEXT,
    observaciones TEXT,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_diagnostico (
    id_diagnostico SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    descripcion TEXT NOT NULL,
    cie VARCHAR(10),
    es_presuntivo BOOLEAN DEFAULT FALSE,
    es_definitivo BOOLEAN DEFAULT false,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_aptitud_laboral (
    id_concepto_aptitud SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    resultado_aptitud VARCHAR(30) CHECK (
        resultado_aptitud IN ('APTO', 'APTO EN OBSERVACIÓN', 'APTO CON LIMITACIONES', 'NO APTO')
    ),
    detalle_observaciones TEXT,
    limitacion TEXT,
    reubicacion TEXT,
        FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion (id_evaluacion)
);
CREATE TABLE medicina.tbl_recomendaciones (
    id_recomendacion SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_recetas_enviadas (
    id_receta SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    numero_receta VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL,
    doctor_a VARCHAR(100) NOT NULL,
    diagnostico TEXT,
    receta TEXT,
    impresa BOOLEAN,
    indicaciones text NULL
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_datos_profesional (
    id_datos_profesional SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    id_profesional INT null,
    fecha DATE 	NULL,
    hora TIME NOT NULL,
    nombres_apellidos VARCHAR(150) NOT NULL,
    codigo_profesional VARCHAR(50),
    firma_sello Boolean DEFAULT false,
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);
CREATE TABLE medicina.tbl_condiciones_retiro (
    id_condicion_retiro SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    resultado_condicion VARCHAR(20) CHECK (
        resultado_condicion IN ('SATISFACTORIO', 'NO SATISFACTORIO')
    ),
    observaciones_retiro TEXT,
    
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion(id_evaluacion)
);

CREATE TABLE medicina.tbl_detalle_evaluacion_retiro (
    id_retiro SERIAL PRIMARY KEY,
    id_evaluacion INTEGER NOT NULL,
    se_realizo BOOLEAN DEFAULT false,
    observaciones TEXT,
    CONSTRAINT fk_evaluacion_retiro
    FOREIGN KEY (id_evaluacion)REFERENCES medicina.tbl_evaluacion (id_evaluacion)
);

CREATE TABLE medicina.tbl_datos_generales (
    id_datos_generales SERIAL PRIMARY KEY,
    id_evaluacion INT8 NOT NULL,
    fecha_emision DATE NOT NULL DEFAULT now(),
    tipo_evaluacion VARCHAR(20) NOT NULL CHECK (
        tipo_evaluacion IN ('INGRESO', 'PERIODICO', 'REINTEGRO', 'SALIDA')
    ),
    FOREIGN KEY (id_evaluacion) REFERENCES medicina.tbl_evaluacion (id_evaluacion)
);

--REPORTES--
CREATE TABLE medicina.tbl_reportes (
    id_reporte serial4 NOT NULL,
    id_usuario INT NOT NULL,
    fecha_generacion TIMESTAMP DEFAULT now(),
    detalles TEXT,
    CONSTRAINT tbl_reportes_pk PRIMARY KEY (id_reporte)
);


INSERT INTO medicina.tbl_tipos_enfermedad_familiar (id_tipo_enfermedad, nombre) VALUES
(1, 'Enfermedad Cardio-Vascular'),
(2, 'Enfermedad Metabólica'),
(3, 'Enfermedad Neurológica'),
(4, 'Enfermedad Oncológica'),
(5, 'Enfermedad Infecciosa'),
(6, 'Enfermedad Hereditaria / Congénita'),
(7, 'Discapacidades'),
(8, 'Otros')
ON CONFLICT (id_tipo_enfermedad) DO NOTHING;


--CREACIÓN DEL SERVER Y USER MAPPING

CREATE EXTENSION IF NOT EXISTS postgres_fdw;
CREATE SERVER svr_isp2
	FOREIGN DATA WRAPPER postgres_fdw
	OPTIONS (dbname 'db_isp', host '138.185.139.89', port '5432');

CREATE USER MAPPING FOR postgres
    SERVER svr_isp2
    OPTIONS (user 'postgres', password 'Gi%9875.-*5+$)(');

--		VISTAS Y TABLAS FORANEAS

CREATE FOREIGN TABLE medicina.f_vta_empleado (
    id_empleado integer OPTIONS (column_name 'id_empleado') NULL,
    sucursal TEXT OPTIONS (column_name 'txt_sucursal'),
    cedula TEXT OPTIONS (column_name 'dni'),
    primer_apellido TEXT OPTIONS (column_name 'apellido'),
    primer_nombre TEXT OPTIONS (column_name 'nombre'),
    sexo TEXT OPTIONS (column_name 'txt_sexo'),
    edad INTEGER OPTIONS (column_name 'edad'),
    tipo_sangre TEXT OPTIONS (column_name 'tipo_sangre'),
    etnia TEXT OPTIONS (column_name 'etnia'),
    cargo TEXT OPTIONS (column_name 'cargo'),
    fecha_ingreso DATE OPTIONS (column_name 'fecha_ingreso'),
    departamento TEXT OPTIONS (column_name 'departamento'),
    discapacidad TEXT OPTIONS (column_name 'discapacidad'),
    id_rol integer OPTIONS (column_name 'id_rol') null,
    rol TEXT OPTIONS (column_name 'rol'),
    estado BOOLEAN OPTIONS (column_name 'estado'),
    datos_academicos TEXT options (column_name 'instrucciones' ),
    alias TEXT options (column_name 'alias'),
    email TEXT OPTIONS (column_name 'email'),
    estado_civil text OPTIONS(column_name 'txt_estado_civil'),
    direccion text OPTIONS(column_name 'direccion'),
    fecha_salida DATE OPTIONS (column_name 'fecha_salida'),
    emailpersonal TEXT OPTIONS (column_name 'emailpersonal'),
    referencias TEXT OPTIONS (column_name 'referencias')

    )
SERVER svr_isp OPTIONS (schema_name 'public', table_name 'vta_empleado');

CREATE FOREIGN TABLE medicina.f_vta_sucursal (
    id_sucursal integer OPTIONS (column_name 'id_sucursal') NOT NULL,
    sucursal    varchar(60) OPTIONS (column_name 'sucursal'),
    estado      boolean OPTIONS (column_name 'estado'),
    eliminado   boolean OPTIONS (column_name 'eliminado')
)
SERVER svr_isp OPTIONS (schema_name 'public', table_name 'vta_sucursal');

