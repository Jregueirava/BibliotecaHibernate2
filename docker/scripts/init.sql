
DROP DATABASE IF EXISTS biblioteca;

-- Creación da base de datos
CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

-- Táboa de usuarios
CREATE TABLE usuario (
    id int PRIMARY KEY AUTO_INCREMENT,
    dni VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    fecha_nacimiento DATE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Táboa de autores
CREATE TABLE autor (
    id int PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(50)
);

-- Táboa de categorías
CREATE TABLE categoria (
    id int PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255)
);

-- Táboa de libros
CREATE TABLE libro (
    id int PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    fecha_publicacion DATE,
    paginas INT,
    editorial VARCHAR(100),

    -- Claves foráneas como enteiros (sen relacións JPA aínda)
    autor_id int,
    categoria_id int,

    FOREIGN KEY (autor_id) REFERENCES autor(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

-- Táboa de exemplares
CREATE TABLE ejemplar (
    id int PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    estado ENUM('DISPONIBLE', 'PRESTADO', 'MANTENIMIENTO') DEFAULT 'DISPONIBLE',
    ubicacion VARCHAR(100),

    -- Clave foránea a libro
    libro_id int NOT NULL,
    FOREIGN KEY (libro_id) REFERENCES libro(id)
);

-- Táboa de préstamos
CREATE TABLE prestamo (
    id int PRIMARY KEY AUTO_INCREMENT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    fecha_devolucion DATE,
    estado ENUM('ACTIVO', 'DEVUELTO', 'RETRASADO') DEFAULT 'ACTIVO',

    -- Claves foráneas como enteiros
    usuario_id int NOT NULL,
    ejemplar_id int NOT NULL,

    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (ejemplar_id) REFERENCES ejemplar(id)
);

-- Inserción de datos

-- Usuarios
INSERT INTO usuario (dni, nombre, apellidos, email, telefono, fecha_nacimiento) VALUES
('12345678A', 'Ana', 'García López', 'ana.garcia@email.com', '611111111', '1990-05-15'),
('87654321B', 'Carlos', 'Martínez Ruiz', 'carlos.martinez@email.com', '622222222', '1985-08-22'),
('11223344C', 'Laura', 'Díaz Fernández', 'laura.diaz@email.com', '633333333', '1992-12-10');

-- Autores
INSERT INTO autor (nombre, nacionalidad) VALUES
('Robert C. Martin', 'Estados Unidos'),
('Martin Fowler', 'Reino Unido'),
('Eric Gamma', 'Alemania'),
('Kent Beck', 'Estados Unidos'),
('Joshua Bloch', 'Estados Unidos'),
('Cay S. Horstmann', 'Alemania');

-- Categorías
INSERT INTO categoria (nombre, descripcion) VALUES
('Programación', 'Libros sobre lenguajes de programación y desarrollo software'),
('Arquitectura Software', 'Patrones y arquitecturas de software'),
('Java', 'Tecnologías específicas de Java'),
('Bases de Datos', 'Diseño y administración de bases de datos'),
('Metodologías Ágiles', 'Scrum, XP y otras metodologías ágiles');

-- Libros de informática EN CASTELÁN
INSERT INTO libro (isbn, titulo, fecha_publicacion, paginas, editorial, autor_id, categoria_id) VALUES
('978-0132350884', 'Código Limpio: Manual de artesanía software ágil', '2008-08-01', 464, 'Prentice Hall', 1, 1),
('978-0201633610', 'Patrones de Diseño: Elementos de software orientado a objetos reutilizable', '1994-10-31', 395, 'Addison-Wesley', 3, 2),
('978-0134685991', 'Java Efectivo', '2018-01-06', 416, 'Addison-Wesley', 5, 3),
('978-0321125217', 'Diseño Orientado al Dominio: Abordando la complejidad en el corazón del software', '2003-08-30', 560, 'Addison-Wesley', 2, 2),
('978-0135957059', 'El Programador Pragmático: Tu viaje hacia la maestría', '2019-09-13', 352, 'Addison-Wesley', 1, 1),
('978-1119806911', 'Java Core Volumen I: Fundamentos', '2021-09-14', 928, 'Pearson', 6, 3),
('978-0134494166', 'Arquitectura Limpia: Guía del artesano para la estructura y diseño del software', '2017-09-20', 432, 'Prentice Hall', 1, 2),
('978-0321278654', 'Refactorización: Mejorando el diseño de código existente', '1999-01-01', 464, 'Addison-Wesley', 2, 1),
('978-0137081073', 'Desarrollo Guiado por Pruebas: Con ejemplos', '2002-11-18', 240, 'Addison-Wesley', 4, 5),
('978-0596007126', 'Head First Patrones de Diseño', '2004-10-25', 694, 'OReilly Media', 3, 2);

-- Ejemplares
INSERT INTO ejemplar (codigo, estado, ubicacion, libro_id) VALUES
('EJ-001', 'DISPONIBLE', 'Estantería A-1', 1),
('EJ-002', 'DISPONIBLE', 'Estantería A-1', 1),
('EJ-003', 'PRESTADO', 'Estantería B-2', 2),
('EJ-004', 'DISPONIBLE', 'Estantería B-2', 2),
('EJ-005', 'DISPONIBLE', 'Estantería C-3', 3),
('EJ-006', 'MANTENIMIENTO', 'Taller', 4),
('EJ-007', 'DISPONIBLE', 'Estantería D-4', 5),
('EJ-008', 'DISPONIBLE', 'Estantería E-5', 6);

-- Préstamos
INSERT INTO prestamo (fecha_inicio, fecha_fin, fecha_devolucion, estado, usuario_id, ejemplar_id) VALUES
('2024-01-10', '2024-01-24', '2024-01-23', 'DEVUELTO', 1, 3),
('2024-02-01', '2024-02-15', NULL, 'ACTIVO', 2, 6),
('2024-01-15', '2024-01-29', '2024-02-01', 'RETRASADO', 3, 1);