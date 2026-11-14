-- =====================================================================
-- SGMA - Sistema de Gestión y Mantención de Activos Andina
-- Compatible con H2 (MODE=PostgreSQL) y con PostgreSQL
-- =====================================================================

-- ---------------------------- TABLAS ---------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
                                        id        BIGSERIAL    PRIMARY KEY,
                                        nombre    VARCHAR(100) NOT NULL,
    correo    VARCHAR(120) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    rol       VARCHAR(20)  NOT NULL
    CHECK (rol IN ('SUPERVISOR', 'TECNICO', 'PRODUCCION', 'BODEGA', 'GERENCIA'))
    );

CREATE TABLE IF NOT EXISTS activos (
                                       id              BIGSERIAL    PRIMARY KEY,
                                       codigo_interno  VARCHAR(20)  NOT NULL UNIQUE,
    nombre          VARCHAR(120) NOT NULL,
    apodo_planta    VARCHAR(80),
    tipo_equipo     VARCHAR(60)  NOT NULL,
    marca           VARCHAR(60)  NOT NULL,
    modelo          VARCHAR(60)  NOT NULL,
    ubicacion       VARCHAR(100) NOT NULL,
    criticidad      VARCHAR(10)  NOT NULL
    CHECK (criticidad IN ('CRITICA', 'ALTA', 'MEDIA', 'BAJA')),
    estado          VARCHAR(30)  NOT NULL DEFAULT 'Operativo'
    CHECK (estado IN ('Operativo', 'En Mantención', 'Detenido', 'Con anomalía reportada'))
    );

CREATE TABLE IF NOT EXISTS anomalias (
                                         id             BIGSERIAL     PRIMARY KEY,
                                         codigo         VARCHAR(20)   NOT NULL UNIQUE,
    activo_id      BIGINT        NOT NULL REFERENCES activos (id),
    reportado_por  VARCHAR(120)  NOT NULL,
    sintoma        VARCHAR(60)   NOT NULL,
    descripcion    VARCHAR(1000) NOT NULL,
    foto_url       VARCHAR(255)  NOT NULL,
    fecha_reporte  TIMESTAMP     NOT NULL DEFAULT NOW(),
    estado         VARCHAR(30)   NOT NULL DEFAULT 'Reportada'
    );

CREATE INDEX IF NOT EXISTS idx_anomalias_activo_fecha
    ON anomalias (activo_id, fecha_reporte DESC);

-- ------------------------- DATOS SEMILLA -----------------------------
-- Usuarios (contraseña en texto plano solo para la demo del Hito 1)
INSERT INTO usuarios (nombre, correo, password, rol) VALUES
                                                         ('Joan Vásquez',      'j.vasquez@andina.cl',    '1234', 'SUPERVISOR'),
                                                         ('Nicolas Pavlich',   'n.pavlich@andina.cl',    '1234', 'TECNICO'),
                                                         ('Andrés Valenzuela', 'a.valenzuela@andina.cl', '1234', 'PRODUCCION'),
                                                         ('Benjamín Jara',     'b.jara@andina.cl',       '1234', 'TECNICO'),
                                                         ('Usuario Bodega',    'bodega@andina.cl',       '1234', 'BODEGA'),
                                                         ('Usuario Gerencia',  'gerencia@andina.cl',     '1234', 'GERENCIA')
    ON CONFLICT DO NOTHING;

-- Activos industriales
INSERT INTO activos
(codigo_interno, nombre, apodo_planta, tipo_equipo, marca, modelo, ubicacion, criticidad, estado)
VALUES
    ('EXT-01', 'Extrusora Principal Línea 1',  'La Bestia Roja',     'Extrusora',            'KraussMaffei',   'KM-300',      'Nave A - Línea 1',     'CRITICA', 'Operativo'),
    ('INY-04', 'Inyectora Hidráulica 4',       'Inyectora Rápida',   'Inyectora',            'Engel',          'Victory 120', 'Nave A - Sector B',    'ALTA',    'Operativo'),
    ('CAL-02', 'Caldera de Vapor Industrial',  'Caldera Sur',        'Caldera',              'Cleaver-Brooks', 'CB-200',      'Sala de Calderas',     'CRITICA', 'En Mantención'),
    ('CMP-01', 'Compresor de Aire Tornillo',   'Compresor Central',  'Compresor',            'Atlas Copco',    'GA-55',       'Nave B - Servicios',   'MEDIA',   'Operativo'),
    ('CNT-03', 'Cinta Transportadora Empaque', 'Cinta Final',        'Cinta Transportadora', 'Dorner',         '2200 Series', 'Nave C - Despacho',    'BAJA',    'Detenido'),
    ('MOL-07', 'Molde Botella PET 500ml',      'Molde 500',          'Moldes y Matrices',    'Husky',          'H-PET',       'Taller de Matricería', 'ALTA',    'Operativo')
    ON CONFLICT DO NOTHING;