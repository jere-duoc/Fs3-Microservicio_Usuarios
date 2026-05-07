USE usuarios;

-- Eliminar tablas en orden inverso para evitar conflictos de llaves foráneas
DROP TABLE IF EXISTS solicitudes_arco;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
   id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
   nombre_rol VARCHAR(50) NOT NULL
);

CREATE TABLE usuarios (
   id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
   run VARCHAR(255) NOT NULL,
   digito_verificador VARCHAR(255) NOT NULL,
   run_hash VARCHAR(255) UNIQUE, -- Para validación de RUT único con datos encriptados
   primer_nombre VARCHAR(50),
   segundo_nombre VARCHAR(50),
   primer_apellido VARCHAR(50),
   segundo_apellido VARCHAR(50),
   fecha_nacimiento DATE,
   genero VARCHAR(20),
   correo_electronico VARCHAR(255),
   contrasena  VARCHAR(255),
   telefono_celular VARCHAR(255),
   acepto_terminos BOOLEAN DEFAULT FALSE,
   oposicion BOOLEAN DEFAULT FALSE, -- Derecho de Oposición (Ley ARCO - 21.719)
   activo BOOLEAN DEFAULT TRUE,      -- Para Derecho de Cancelación (Borrado Lógico)
   id_rol BIGINT,
   FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE solicitudes_arco (
    id_solicitud BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT,
    tipo_derecho ENUM('ACCESO', 'RECTIFICACION', 'CANCELACION', 'OPOSICION'),
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalles TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Inserción de roles necesarios para el Frontend
INSERT INTO roles (id_rol, nombre_rol) VALUES 
(1, 'Médico'),
(2, 'TENS'),
(3, 'Asist. Médico'),
(4, 'Administrativo'),
(5, 'Externo');
