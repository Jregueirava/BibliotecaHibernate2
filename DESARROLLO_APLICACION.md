# Desarrollo de la Aplicación Biblioteca con Hibernate

Este documento describe el desarrollo de la aplicación tras completar la configuración inicial del proyecto.

## Índice
1. [Resumen del Desarrollo](#resumen-del-desarrollo)
2. [Capa de Modelo - Entidad Usuario](#capa-de-modelo---entidad-usuario)
3. [Capa de Acceso a Datos - DAO](#capa-de-acceso-a-datos---dao)
4. [Aplicación Principal](#aplicación-principal)
5. [Patrones de Diseño Utilizados](#patrones-de-diseño-utilizados)
6. [Problemas Detectados](#problemas-detectados)
7. [Flujo de Ejecución](#flujo-de-ejecución)
8. [Próximos Pasos](#próximos-pasos)

---

## Resumen del Desarrollo

Tras establecer la configuración base del proyecto (Maven, Docker, Hibernate), se han implementado los siguientes componentes:

1. **Entidad Usuario** - Mapeo JPA de la tabla `usuario`
2. **Interfaz UsuarioDAO** - Contrato para operaciones CRUD
3. **Implementación UsuarioDAOHib** - Implementación con Hibernate
4. **Aplicación App** - Clase principal para probar la funcionalidad

La arquitectura sigue el patrón **DAO (Data Access Object)** para separar la lógica de acceso a datos del resto de la aplicación.

---

## Capa de Modelo - Entidad Usuario

### Archivo: `src/main/java/modelo/Usuario.java`

#### Descripción General

Clase que representa la entidad `Usuario` mapeada a la tabla `usuario` en la base de datos.

```java
@Entity
@Table(name = "usuario")
public class Usuario {
    // Campos y métodos
}
```

### Anotaciones JPA Utilizadas

#### 1. @Entity
```java
@Entity
```
- Marca la clase como una entidad JPA
- Hibernate gestionará el ciclo de vida de los objetos de esta clase

#### 2. @Table
```java
@Table(name = "usuario")
```
- Especifica el nombre de la tabla en la base de datos
- Si no se especifica, JPA usaría el nombre de la clase

#### 3. @Id y @GeneratedValue
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
```
- **@Id**: Marca el campo como clave primaria
- **@GeneratedValue**: Indica que el valor se genera automáticamente
- **GenerationType.IDENTITY**: La BD genera el valor (AUTO_INCREMENT en MariaDB)

#### 4. @Column
```java
@Column(name = "dni", unique = true, nullable = false, length = 20)
private String dni;
```

Atributos configurados:
- **name**: Nombre de la columna en la BD
- **unique**: Restricción de unicidad (para DNI y email)
- **nullable**: Si acepta valores NULL (false = NOT NULL)
- **length**: Longitud máxima del campo VARCHAR

### Campos de la Entidad

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | int | PK, AUTO_INCREMENT | Identificador único |
| dni | String | UNIQUE, NOT NULL, max 20 | DNI del usuario |
| nombre | String | NOT NULL, max 100 | Nombre |
| apellidos | String | NOT NULL, max 100 | Apellidos |
| email | String | UNIQUE, NOT NULL, max 100 | Correo electrónico |
| telefono | String | max 20 | Teléfono (opcional) |
| fecha_nacimiento | LocalDate | - | Fecha de nacimiento |
| fecha_registro | LocalDateTime | - | Fecha de registro |

### Tipos de Datos Java vs SQL

Hibernate mapea automáticamente:
- `int` ↔ `INT`
- `String` ↔ `VARCHAR`
- `LocalDate` ↔ `DATE`
- `LocalDateTime` ↔ `TIMESTAMP`

### Constructores

#### Constructor con Parámetros
```java
public Usuario(String dni, String nombre, String apellidos, String email) {
    this.dni = dni;
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.email = email;
}
```
- Facilita la creación de usuarios con los campos obligatorios
- No incluye `id` (se genera automáticamente)

#### Constructor Vacío
```java
public Usuario() {
}
```
- **OBLIGATORIO** para JPA/Hibernate
- Hibernate lo usa internamente para crear instancias mediante reflection

### Métodos

- **Getters y Setters**: Para todos los campos (patrón JavaBean)
- **toString()**: Representación en texto del objeto (útil para debugging)

### Errores Detectados

#### Error en línea 34:
```java
@Column(name = "fecha_reistro")  // Error tipográfico
private LocalDateTime fecha_registro;
```

**Problema**: El nombre de la columna en la anotación es `fecha_reistro` pero debería ser `fecha_registro`

**Impacto**:
- Hibernate intentará mapear a una columna `fecha_reistro` que NO existe en la BD
- Causará error en tiempo de ejecución al validar el esquema

**Solución**: Corregir la línea 34:
```java
@Column(name = "fecha_registro")
private LocalDateTime fecha_registro;
```

---

## Capa de Acceso a Datos - DAO

### ¿Qué es el Patrón DAO?

**DAO (Data Access Object)** es un patrón de diseño que:
- Separa la lógica de acceso a datos de la lógica de negocio
- Proporciona una interfaz abstracta para operaciones CRUD
- Facilita el cambio de implementación (ej: de Hibernate a JDBC)

### Interfaz: `dao/UsuarioDAO.java`

```java
public interface UsuarioDAO {
    boolean crearUsuario(Usuario u);
    Optional<Usuario> buscarPorId(int id);
    Usuario actualizarUsuario(Usuario u);
    boolean eliminarUsuario(Usuario u);
}
```

#### Operaciones Definidas

| Método | Retorno | Descripción |
|--------|---------|-------------|
| crearUsuario | boolean | Inserta un nuevo usuario. Retorna true si tiene éxito |
| buscarPorId | Optional<Usuario> | Busca por ID. Optional evita null |
| actualizarUsuario | Usuario | Actualiza un usuario existente |
| eliminarUsuario | boolean | Elimina un usuario. Retorna true si tiene éxito |

#### Uso de Optional

```java
Optional<Usuario> buscarPorId(int id);
```

**Ventajas de Optional**:
- Evita `NullPointerException`
- Hace explícito que el resultado puede no existir
- API moderna de Java 8+

**Uso**:
```java
Optional<Usuario> usuarioOpt = dao.buscarPorId(1);
if (usuarioOpt.isPresent()) {
    Usuario u = usuarioOpt.get();
    // trabajar con u
} else {
    // manejar caso no encontrado
}
```

---

## Implementación: `dao/UsuarioDAOHib.java`

### Estructura General

```java
public class UsuarioDAOHib implements UsuarioDAO {
    private EntityManager entityManager;

    public UsuarioDAOHib(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    // Implementación de métodos
}
```

### EntityManager

Es la interfaz principal de JPA para:
- Gestionar entidades (persist, merge, remove, find)
- Ejecutar consultas (JPQL, Criteria API)
- Gestionar transacciones

### Inyección por Constructor

```java
public UsuarioDAOHib(EntityManager entityManager) {
    this.entityManager = entityManager;
}
```

**Ventajas**:
- **Inversión de Dependencias**: No crea el EntityManager, lo recibe
- **Testeable**: Se puede inyectar un mock en pruebas
- **Flexibilidad**: El EntityManager se crea externamente

---

## Implementación de Operaciones CRUD

### 1. Crear Usuario (INSERT)

```java
@Override
public boolean crearUsuario(Usuario u) {
    EntityTransaction tran = entityManager.getTransaction();
    try {
        tran.begin();
        entityManager.persist(u);
        tran.commit();
        return true;
    } catch (Exception e) {
        if (tran.isActive()) {
            tran.rollback();
            return false;
        }
        throw new RuntimeException("Error al crear usuario" + e);
    }
}
```

#### Flujo de Ejecución:

1. **Obtener transacción**: `getTransaction()`
2. **Iniciar transacción**: `begin()`
3. **Persistir entidad**: `persist(u)` → Genera INSERT
4. **Confirmar cambios**: `commit()` → Ejecuta el SQL
5. **Manejo de errores**: Si falla, `rollback()`

#### Conceptos Clave:

- **persist()**: Marca el objeto como "managed" y programa un INSERT
- El INSERT real se ejecuta en el `commit()`
- El `id` se asigna automáticamente tras el INSERT

---

### 2. Buscar Usuario por ID (SELECT)

```java
@Override
public Optional<Usuario> buscarPorId(int id) {
    Usuario u = entityManager.find(Usuario.class, id);
    Optional<Usuario> usuarioORec = Optional.empty();
    return usuarioORec;
}
```

#### Problema Detectado:

Este método tiene un **BUG GRAVE**:

```java
Usuario u = entityManager.find(Usuario.class, id);  // Busca el usuario
Optional<Usuario> usuarioORec = Optional.empty();  // Crea Optional vacío
return usuarioORec;  // SIEMPRE retorna vacío, ignora 'u'
```

#### Implementación Correcta:

```java
@Override
public Optional<Usuario> buscarPorId(int id) {
    Usuario u = entityManager.find(Usuario.class, id);
    return Optional.ofNullable(u);
}
```

**Explicación**:
- `find()` retorna `null` si no encuentra el usuario
- `Optional.ofNullable(u)` crea:
  - `Optional.of(u)` si u != null
  - `Optional.empty()` si u == null

#### Operación find()

```java
Usuario u = entityManager.find(Usuario.class, id);
```

- Genera: `SELECT * FROM usuario WHERE id = ?`
- Retorna `null` si no existe
- **NO requiere transacción** (operación de lectura)

---

### 3. Actualizar Usuario (UPDATE)

```java
@Override
public Usuario actualizarUsuario(Usuario u) {
    EntityTransaction tran = entityManager.getTransaction();
    try {
        tran.begin();
        Usuario usuarioActualizado = entityManager.merge(u);
        tran.commit();
        return usuarioActualizado;
    } catch (Exception e) {
        if (tran.isActive()) {
            tran.rollback();
            return u;
        }
        throw new RuntimeException("Error al modificar usuario" + e);
    }
}
```

#### Operación merge()

- Sincroniza un objeto "detached" con la BD
- Si el usuario existe: Genera UPDATE
- Si no existe: Genera INSERT (comportamiento upsert)
- Retorna una copia "managed" del objeto

#### Diferencia persist() vs merge()

| Aspecto | persist() | merge() |
|---------|-----------|---------|
| Objeto | Debe ser nuevo | Puede ser existente o nuevo |
| Retorno | void | Objeto managed |
| Uso típico | INSERT | UPDATE |
| Estado inicial | Transient | Detached |

---

### 4. Eliminar Usuario (DELETE)

```java
@Override
public boolean eliminarUsuario(Usuario u) {
    EntityTransaction tran = entityManager.getTransaction();
    try {
        tran.begin();
        Usuario usuarioEnc = entityManager.find(Usuario.class, u.getId());
        if(usuarioEnc != null){
            entityManager.remove(usuarioEnc);
            tran.commit();
            return true;
        }
        return false;
    } catch (Exception e) {
        if (tran.isActive()) {
            tran.rollback();
            return false;
        }
        throw new RuntimeException("Error al borrar usuario" + e);
    }
}
```

#### Flujo:

1. **Buscar el usuario**: `find()` → Para obtener la entidad managed
2. **Verificar existencia**: `if(usuarioEnc != null)`
3. **Eliminar**: `remove()` → Genera DELETE
4. **Commit**: Ejecuta el DELETE

#### ¿Por qué buscar primero?

```java
Usuario usuarioEnc = entityManager.find(Usuario.class, u.getId());
```

**Razón**: `remove()` solo funciona con entidades en estado **managed**. Si el usuario viene de fuera (detached), debemos obtener la versión managed primero.

---

## Aplicación Principal

### Archivo: `App.java`

```java
public class App {
    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try(EntityManager em = Persistence
                .createEntityManagerFactory("biblioteca")
                .createEntityManager()){

            UsuarioDAO usuarioDAO = new UsuarioDAOHib(em);
            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);

            if(usuarioOptional.isPresent()){
                System.out.println("====USUARIO ENCONTRADO====");
                System.out.println(usuarioOptional.get());
            }else {
                System.out.println("====USUARIO NO ENCONTRADO====");
            }
            System.out.println("Programa de prueba finalizado");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Análisis del Código

#### 1. Configuración del Logger

```java
SLF4JBridgeHandler.removeHandlersForRootLogger();
SLF4JBridgeHandler.install();
```

**Propósito**:
- Redirige los logs de `java.util.logging` (JUL) a SLF4J
- Necesario porque algunas librerías usan JUL
- Unifica todos los logs bajo SLF4J

#### 2. Try-with-resources

```java
try(EntityManager em = Persistence
        .createEntityManagerFactory("biblioteca")
        .createEntityManager()){
    // código
}
```

**Ventajas**:
- Cierra automáticamente el EntityManager al salir del bloque
- Previene fugas de recursos
- Sintaxis moderna de Java 7+

#### 3. Creación del EntityManagerFactory

```java
Persistence.createEntityManagerFactory("biblioteca")
```

- Lee el archivo `persistence.xml`
- Busca la persistence-unit con nombre `"biblioteca"`
- Configura el pool de conexiones
- **Costoso**: Se debe crear UNA VEZ y reutilizar

#### 4. Creación del EntityManager

```java
.createEntityManager()
```

- Crea un EntityManager ligado al factory
- **Lightweight**: Se puede crear muchas veces
- No thread-safe: Un EntityManager por hilo

#### 5. Prueba del DAO

```java
UsuarioDAO usuarioDAO = new UsuarioDAOHib(em);
Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);

if(usuarioOptional.isPresent()){
    System.out.println("====USUARIO ENCONTRADO====");
    System.out.println(usuarioOptional.get());
}else {
    System.out.println("====USUARIO NO ENCONTRADO====");
}
```

**Flujo**:
1. Crea el DAO inyectando el EntityManager
2. Busca el usuario con ID = 1
3. Verifica si existe usando `isPresent()`
4. Imprime el resultado usando `toString()` del Usuario

---

## Patrones de Diseño Utilizados

### 1. DAO (Data Access Object)

**Propósito**: Encapsular el acceso a datos

**Beneficios**:
- Separación de responsabilidades
- Facilita testing (mock del DAO)
- Cambio de implementación sin afectar lógica de negocio

### 2. Dependency Injection (DI)

```java
public UsuarioDAOHib(EntityManager entityManager) {
    this.entityManager = entityManager;
}
```

**Beneficios**:
- Bajo acoplamiento
- Testeable
- Flexible

### 3. Optional Pattern

```java
Optional<Usuario> buscarPorId(int id);
```

**Beneficios**:
- Evita NullPointerException
- API expresiva
- Manejo explícito de ausencia de valor

### 4. Try-with-resources

```java
try(EntityManager em = ...) {
    // uso del recurso
} // cierre automático
```

**Beneficios**:
- Gestión automática de recursos
- Código más limpio
- Previene fugas de memoria

---

## Problemas Detectados

### 1. Error en Mapeo de fecha_registro

**Archivo**: `Usuario.java:34`

```java
@Column(name = "fecha_reistro")  // INCORRECTO
private LocalDateTime fecha_registro;
```

**Corrección**:
```java
@Column(name = "fecha_registro")  // CORRECTO
private LocalDateTime fecha_registro;
```

### 2. Bug en buscarPorId()

**Archivo**: `UsuarioDAOHib.java:34-37`

```java
@Override
public Optional<Usuario> buscarPorId(int id) {
    Usuario u = entityManager.find(Usuario.class, id);
    Optional<Usuario> usuarioORec = Optional.empty();
    return usuarioORec;  // SIEMPRE retorna vacío
}
```

**Corrección**:
```java
@Override
public Optional<Usuario> buscarPorId(int id) {
    Usuario u = entityManager.find(Usuario.class, id);
    return Optional.ofNullable(u);
}
```

### 3. Typo en mensaje de error

**Archivo**: `UsuarioDAOHib.java:53`

```java
throw new RuntimeException("Error al mpodificar usuario" + e);
```

**Corrección**:
```java
throw new RuntimeException("Error al modificar usuario: " + e);
```

### 4. Inconsistencia en persistence.xml

**Archivo**: `persistence.xml:11`

```xml
<class>com.biblioteca.model.Usuario</class>
```

**Problema**: El package real es `modelo`, no `model`

**Corrección**:
```xml
<class>modelo.Usuario</class>
```

---

## Flujo de Ejecución Completo

### Diagrama de Flujo

```
1. Iniciar Aplicación (App.main)
   ↓
2. Configurar SLF4J Bridge
   ↓
3. Crear EntityManagerFactory
   ├─ Leer persistence.xml
   ├─ Configurar conexión a MariaDB
   ├─ Validar esquema (hbm2ddl.auto=validate)
   └─ Inicializar pool de conexiones
   ↓
4. Crear EntityManager
   ↓
5. Crear UsuarioDAOHib
   ↓
6. Ejecutar buscarPorId(1)
   ├─ EntityManager.find()
   ├─ SQL: SELECT * FROM usuario WHERE id = 1
   └─ Mapear ResultSet a Usuario
   ↓
7. Verificar resultado (Optional)
   ├─ Si presente → Imprimir usuario
   └─ Si vacío → Imprimir "no encontrado"
   ↓
8. Cerrar EntityManager (automático)
   ↓
9. Finalizar aplicación
```

### SQL Generado

Si `hibernate.show_sql=true`, se verá:

```sql
SELECT
    u1_0.id,
    u1_0.apellidos,
    u1_0.dni,
    u1_0.email,
    u1_0.fecha_nacimiento,
    u1_0.fecha_registro,
    u1_0.nombre,
    u1_0.telefono
FROM usuario u1_0
WHERE u1_0.id=?
```

---

## Estados de una Entidad JPA

Una entidad puede estar en 4 estados:

### 1. Transient (Transitorio)
```java
Usuario u = new Usuario("12345", "Juan", "Pérez", "juan@email.com");
// No está gestionado por EntityManager
```

### 2. Managed (Gestionado)
```java
entityManager.persist(u);
// Ahora Hibernate rastrea los cambios
```

### 3. Detached (Desconectado)
```java
entityManager.close();
// El objeto sigue existiendo pero ya no está gestionado
```

### 4. Removed (Eliminado)
```java
entityManager.remove(u);
// Marcado para eliminación
```

---

## Próximos Pasos

### 1. Corregir los Bugs Identificados
- [ ] Corregir `fecha_reistro` → `fecha_registro` en Usuario.java
- [ ] Arreglar `buscarPorId()` en UsuarioDAOHib.java
- [ ] Corregir typo "mpodificar" → "modificar"
- [ ] Actualizar package en persistence.xml

### 2. Ampliar Funcionalidad de UsuarioDAO
- [ ] `List<Usuario> buscarTodos()` - Listar todos los usuarios
- [ ] `Optional<Usuario> buscarPorDni(String dni)` - Buscar por DNI
- [ ] `Optional<Usuario> buscarPorEmail(String email)` - Buscar por email
- [ ] `List<Usuario> buscarPorNombre(String nombre)` - Búsqueda parcial

### 3. Implementar Otras Entidades
- [ ] Crear entidad `Autor`
- [ ] Crear entidad `Categoria`
- [ ] Crear entidad `Libro`
- [ ] Crear entidad `Ejemplar`
- [ ] Crear entidad `Prestamo`

### 4. Implementar Relaciones JPA
- [ ] `@ManyToOne` entre Libro y Autor
- [ ] `@ManyToOne` entre Libro y Categoria
- [ ] `@OneToMany` entre Libro y Ejemplar
- [ ] `@ManyToOne` entre Prestamo y Usuario
- [ ] `@ManyToOne` entre Prestamo y Ejemplar

### 5. Crear DAOs para Otras Entidades
- [ ] AutorDAO + AutorDAOHib
- [ ] CategoriaDAO + CategoriaDAOHib
- [ ] LibroDAO + LibroDAOHib
- [ ] EjemplarDAO + EjemplarDAOHib
- [ ] PrestamoDAO + PrestamoDAOHib

### 6. Implementar Lógica de Negocio
- [ ] Servicio de préstamos
- [ ] Validación de disponibilidad
- [ ] Cálculo de fechas de devolución
- [ ] Gestión de retrasos
- [ ] Sistema de multas

### 7. Testing
- [ ] Tests unitarios para DAOs (con H2 in-memory)
- [ ] Tests de integración con MariaDB
- [ ] Tests de la capa de servicio

### 8. Mejoras de Arquitectura
- [ ] Crear capa de Servicio (Service Layer)
- [ ] Implementar manejo de excepciones personalizado
- [ ] Añadir logging estructurado
- [ ] Implementar patrón Repository

---

## Resumen de Archivos Creados

```
src/main/java/
├── modelo/
│   └── Usuario.java           # Entidad JPA para usuarios
├── dao/
│   ├── UsuarioDAO.java        # Interfaz DAO
│   └── UsuarioDAOHib.java     # Implementación con Hibernate
└── App.java                   # Aplicación principal de prueba
```

---

## Conclusiones

Se ha implementado exitosamente:

1. **Mapeo ORM** de la entidad Usuario usando anotaciones JPA
2. **Patrón DAO** para encapsular el acceso a datos
3. **Operaciones CRUD** básicas (Create, Read, Update, Delete)
4. **Aplicación de prueba** que demuestra el uso del DAO

**Logros**:
- Separación clara de responsabilidades
- Código testeable y mantenible
- Uso de patrones modernos de Java (Optional, try-with-resources)
- Integración completa con Hibernate

**Pendiente**:
- Corregir bugs identificados
- Ampliar funcionalidad
- Implementar el resto de entidades
- Desarrollar lógica de negocio completa

---

**Documento creado**: 2025-11-20
**Próximo documento**: Implementación de entidades relacionadas y relaciones JPA
