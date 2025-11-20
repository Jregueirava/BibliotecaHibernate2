# Configuración del Proyecto Biblioteca con Hibernate

Este documento describe toda la configuración realizada para trabajar con Hibernate en el proyecto de gestión de biblioteca.

## Índice
1. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
2. [Configuración de Maven](#configuración-de-maven)
3. [Configuración de Docker](#configuración-de-docker)
4. [Base de Datos](#base-de-datos)
5. [Configuración de Hibernate/JPA](#configuración-de-hibernatejpa)
6. [Configuración de Logs](#configuración-de-logs)
7. [Cómo Ejecutar el Proyecto](#cómo-ejecutar-el-proyecto)

---

## Arquitectura del Proyecto

El proyecto utiliza:
- **Java 21** como lenguaje de programación
- **Maven** como gestor de dependencias
- **Hibernate 6.3.1** como framework ORM (Object-Relational Mapping)
- **MariaDB 11** como sistema de base de datos
- **Docker** para la contenerización de servicios
- **phpMyAdmin** para la administración visual de la base de datos

---

## Configuración de Maven

### Archivo: `pom.xml`

El archivo `pom.xml` contiene las siguientes dependencias clave:

### 1. Hibernate Core
```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.3.1.Final</version>
</dependency>
```
- **Propósito**: Framework ORM que permite mapear objetos Java a tablas de base de datos
- **Versión**: 6.3.1.Final (versión moderna y estable)

### 2. Conector MariaDB
```xml
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>3.3.3</version>
</dependency>
```
- **Propósito**: Driver JDBC para conectar con bases de datos MariaDB/MySQL
- **Versión**: 3.3.3

### 3. Sistema de Logging (SLF4J)
```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.9</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jul-to-slf4j</artifactId>
    <version>2.0.9</version>
</dependency>
```
- **Propósito**: Sistema de logging unificado para eliminar warnings y controlar los logs de Hibernate
- **slf4j-simple**: Implementación simple de SLF4J
- **jul-to-slf4j**: Puente para redirigir logs de java.util.logging a SLF4J

---

## Configuración de Docker

### Archivo: `docker-compose.yml`

El proyecto utiliza Docker Compose para orquestar tres servicios:

### 1. Servicio MariaDB
```yaml
mariadb:
  image: mariadb:11
  container_name: mariadbHibernate
  environment:
    MYSQL_ROOT_PASSWORD: rootpassword
    MYSQL_DATABASE: biblioteca
    MYSQL_USER: testuser
    MYSQL_PASSWORD: testpass
  ports:
    - "3306:3306"
  volumes:
    - ./docker/scripts/init.sql:/docker-entrypoint-initdb.d/init.sql
```

**Características**:
- Base de datos MariaDB versión 11
- Usuario root: `root` / `rootpassword`
- Usuario de aplicación: `testuser` / `testpass`
- Base de datos: `biblioteca`
- Puerto expuesto: `3306`
- Script de inicialización automático: `init.sql`

### 2. Servicio phpMyAdmin
```yaml
phpmyadmin:
  image: phpmyadmin:5
  container_name: phpmyadminHibernate
  environment:
    PMA_HOST: mariadb
    PMA_USER: root
    PMA_PASSWORD: rootpassword
  ports:
    - "8081:80"
```

**Características**:
- Interfaz web para administrar MariaDB
- Accesible en: `http://localhost:8081`
- Conecta automáticamente con el servicio MariaDB

### 3. Servicio Java Application
```yaml
javaapp:
  build: .
  container_name: javaappHibernate
  depends_on:
    - mariadb
  environment:
    DB_URL: jdbc:mariadb://mariadb:3306/biblioteca
    DB_USER: testuser
    DB_PASS: testpass
```

**Características**:
- Construye la aplicación usando el Dockerfile
- Depende del servicio MariaDB (espera a que esté disponible)
- Variables de entorno para conexión a BD

### Archivo: `Dockerfile`

Utiliza construcción multi-etapa para optimizar el tamaño de la imagen:

```dockerfile
# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
```

**Ventajas**:
- Imagen final más ligera (Alpine Linux)
- Maven solo se usa en la compilación
- Separación de build y runtime

---

## Base de Datos

### Archivo: `docker/scripts/init.sql`

Este script se ejecuta automáticamente al iniciar el contenedor de MariaDB por primera vez.

### Modelo de Datos

El proyecto implementa un sistema de gestión de biblioteca con las siguientes entidades:

#### 1. Tabla `usuario`
```sql
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
```
**Usuarios registrados en la biblioteca**

#### 2. Tabla `autor`
```sql
CREATE TABLE autor (
    id int PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(50)
);
```
**Autores de los libros**

#### 3. Tabla `categoria`
```sql
CREATE TABLE categoria (
    id int PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255)
);
```
**Categorías de libros** (Programación, Arquitectura Software, Java, etc.)

#### 4. Tabla `libro`
```sql
CREATE TABLE libro (
    id int PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    fecha_publicacion DATE,
    paginas INT,
    editorial VARCHAR(100),
    autor_id int,
    categoria_id int,
    FOREIGN KEY (autor_id) REFERENCES autor(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);
```
**Libros disponibles en la biblioteca**

#### 5. Tabla `ejemplar`
```sql
CREATE TABLE ejemplar (
    id int PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    estado ENUM('DISPONIBLE', 'PRESTADO', 'MANTENIMIENTO') DEFAULT 'DISPONIBLE',
    ubicacion VARCHAR(100),
    libro_id int NOT NULL,
    FOREIGN KEY (libro_id) REFERENCES libro(id)
);
```
**Ejemplares físicos de cada libro**

#### 6. Tabla `prestamo`
```sql
CREATE TABLE prestamo (
    id int PRIMARY KEY AUTO_INCREMENT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    fecha_devolucion DATE,
    estado ENUM('ACTIVO', 'DEVUELTO', 'RETRASADO') DEFAULT 'ACTIVO',
    usuario_id int NOT NULL,
    ejemplar_id int NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (ejemplar_id) REFERENCES ejemplar(id)
);
```
**Préstamos de libros a usuarios**

### Relaciones

- **Libro** ← muchos a uno → **Autor**
- **Libro** ← muchos a uno → **Categoria**
- **Ejemplar** ← muchos a uno → **Libro**
- **Prestamo** ← muchos a uno → **Usuario**
- **Prestamo** ← muchos a uno → **Ejemplar**

### Datos de Prueba

El script incluye datos iniciales:
- 3 usuarios
- 6 autores (Robert C. Martin, Martin Fowler, etc.)
- 5 categorías
- 10 libros de programación
- 8 ejemplares
- 3 préstamos

---

## Configuración de Hibernate/JPA

### Archivo: `src/main/resources/META-INF/persistence.xml`

Este es el archivo central de configuración de JPA/Hibernate.

```xml
<persistence-unit name="biblioteca" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

    <!-- Clases del modelo -->
    <class>com.biblioteca.model.Usuario</class>

    <properties>
        <!-- Configuración JDBC -->
        <property name="javax.persistence.jdbc.driver" value="org.mariadb.jdbc.Driver"/>
        <property name="javax.persistence.jdbc.url" value="jdbc:mariadb://localhost:3306/biblioteca"/>
        <property name="javax.persistence.jdbc.user" value="testuser"/>
        <property name="javax.persistence.jdbc.password" value="testpass"/>

        <!-- Configuración de Hibernate -->
        <property name="hibernate.hbm2ddl.auto" value="validate"/>
        <property name="hibernate.show_sql" value="true"/>
        <property name="hibernate.format_sql" value="true"/>

        <!-- Optimización -->
        <property name="hibernate.connection.pool_size" value="10"/>
        <property name="hibernate.jdbc.batch_size" value="20"/>
    </properties>
</persistence-unit>
```

### Propiedades Importantes

#### 1. Unidad de Persistencia
- **name**: `biblioteca` - Nombre que se usa para obtener el EntityManagerFactory
- **transaction-type**: `RESOURCE_LOCAL` - Transacciones gestionadas localmente (no JTA)

#### 2. Provider
- **org.hibernate.jpa.HibernatePersistenceProvider** - Implementación de JPA por Hibernate

#### 3. Clases Entidad
- Cada clase que se mapea a una tabla debe declararse con `<class>`
- Actualmente: `com.biblioteca.model.Usuario`

#### 4. Configuración JDBC
- **driver**: Driver JDBC de MariaDB
- **url**: Conexión a `localhost:3306/biblioteca`
- **user/password**: Credenciales de la base de datos

#### 5. Hibernate DDL
- **hibernate.hbm2ddl.auto**: `validate`
  - `validate`: Solo valida que el esquema coincide con las entidades (NO modifica la BD)
  - Otras opciones: `create`, `create-drop`, `update`, `none`

#### 6. Debug SQL
- **hibernate.show_sql**: `true` - Muestra las consultas SQL en consola
- **hibernate.format_sql**: `true` - Formatea las consultas para mejor legibilidad

#### 7. Optimización
- **connection.pool_size**: `10` - Pool de 10 conexiones concurrentes
- **jdbc.batch_size**: `20` - Agrupa hasta 20 operaciones en un batch

---

## Configuración de Logs

### Archivo: `src/main/resources/META-INF/simplelogger.properties`

Configuración de SLF4J Simple Logger para reducir el ruido en los logs:

```properties
# Nivel global
org.slf4j.simpleLogger.defaultLogLevel=error

# Reducir logs detallados de Hibernate
org.slf4j.simpleLogger.log.org.hibernate=error
org.slf4j.simpleLogger.log.org.hibernate.engine=error
org.slf4j.simpleLogger.log.org.hibernate.jdbc=error
org.slf4j.simpleLogger.log.org.hibernate.SQL=off
```

**Configuración**:
- Nivel por defecto: `error` (solo errores)
- Logs de Hibernate internos: `error`
- SQL de Hibernate: `off` (desactivado, porque ya se controla con `show_sql`)

---

## Cómo Ejecutar el Proyecto

### 1. Iniciar los Contenedores Docker

```bash
docker-compose up -d
```

Esto iniciará:
- MariaDB en `localhost:3306`
- phpMyAdmin en `http://localhost:8081`
- La aplicación Java

### 2. Verificar que MariaDB está Funcionando

```bash
docker ps
```

Deberías ver tres contenedores:
- `mariadbHibernate`
- `phpmyadminHibernate`
- `javaappHibernate`

### 3. Acceder a phpMyAdmin

- URL: `http://localhost:8081`
- Usuario: `root`
- Contraseña: `rootpassword`

### 4. Compilar el Proyecto (sin Docker)

```bash
mvn clean compile
```

### 5. Ejecutar la Aplicación (sin Docker)

```bash
mvn exec:java -Dexec.mainClass="com.biblioteca.Main"
```

### 6. Detener los Contenedores

```bash
docker-compose down
```

Para eliminar también los volúmenes (datos de la BD):
```bash
docker-compose down -v
```

---

## Estructura del Proyecto

```
BibliotecaHibernate/
├── docker/
│   └── scripts/
│       └── init.sql              # Script de inicialización de BD
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── biblioteca/
│       │           └── model/
│       │               └── Usuario.java
│       └── resources/
│           └── META-INF/
│               ├── persistence.xml       # Configuración JPA/Hibernate
│               └── simplelogger.properties  # Configuración logs
├── docker-compose.yml            # Orquestación de servicios
├── Dockerfile                    # Imagen Docker de la aplicación
└── pom.xml                       # Dependencias Maven
```

---

## Próximos Pasos

1. **Crear entidades JPA** para todas las tablas:
   - `Usuario` (ya existe)
   - `Autor`
   - `Categoria`
   - `Libro`
   - `Ejemplar`
   - `Prestamo`

2. **Añadir las clases** al `persistence.xml`

3. **Implementar relaciones JPA**:
   - `@ManyToOne` para libro → autor, libro → categoría
   - `@OneToMany` para autor → libros, categoría → libros
   - etc.

4. **Crear DAOs (Data Access Objects)** para operaciones CRUD

5. **Implementar la lógica de negocio**:
   - Gestión de préstamos
   - Control de disponibilidad
   - Búsqueda de libros
   - etc.

---

## Notas Importantes

### Conexión desde la Aplicación Java

- **Desarrollo local** (fuera de Docker): usar `localhost:3306`
- **Dentro de Docker**: usar `mariadb:3306` (nombre del servicio)

### Credenciales de Base de Datos

- **Usuario root**: `root` / `rootpassword`
- **Usuario aplicación**: `testuser` / `testpass`
- **Base de datos**: `biblioteca`

### Reiniciar la Base de Datos

Si necesitas reiniciar la BD con datos limpios:

```bash
docker-compose down -v
docker-compose up -d
```

El flag `-v` elimina los volúmenes, lo que provoca que el script `init.sql` se ejecute nuevamente.

---

## Recursos Adicionales

- [Documentación de Hibernate](https://hibernate.org/orm/documentation/6.3/)
- [Especificación JPA](https://jakarta.ee/specifications/persistence/)
- [MariaDB Documentation](https://mariadb.com/kb/en/documentation/)
- [Docker Compose Reference](https://docs.docker.com/compose/)

---

**Documento creado**: 2025-11-20
**Versión de Hibernate**: 6.3.1.Final
**Versión de Java**: 21
