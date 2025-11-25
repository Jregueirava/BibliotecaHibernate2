# Guía: Mapeo de Entidades y Operaciones CRUD con Hibernate

## Índice
1. [Conceptos básicos](#conceptos-básicos)
2. [Estructura del proyecto](#estructura-del-proyecto)
3. [Pasos para cada entidad](#pasos-para-cada-entidad)
4. [Ejemplos detallados](#ejemplos-detallados)
5. [Checklist de trabajo](#checklist-de-trabajo)

---

## Conceptos básicos

### ¿Qué es una Entidad JPA?
Una **entidad** es una clase Java que representa una tabla de la base de datos. Cada instancia de la entidad representa una fila de esa tabla.

### ¿Qué son las anotaciones?
Las **anotaciones** son metadatos que le indican a Hibernate cómo mapear la clase Java a la tabla de la base de datos.

**Anotaciones principales:**
- `@Entity` - Marca la clase como una entidad JPA
- `@Table(name = "nombre_tabla")` - Especifica el nombre de la tabla en la BD
- `@Id` - Marca el atributo como clave primaria
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` - Indica que el ID es auto-incremental
- `@Column(name = "nombre_columna")` - Mapea un atributo a una columna específica

### ¿Qué es un DAO?
Un **DAO (Data Access Object)** es un patrón de diseño que encapsula todas las operaciones de acceso a datos. Separa la lógica de negocio de la lógica de acceso a datos.

**Estructura del patrón:**
- **Interfaz DAO:** Define el contrato con las operaciones CRUD
- **Clase Implementadora:** Proporciona la lógica concreta usando EntityManager

**Operaciones CRUD básicas:**
- **C**reate (Crear) - `persist()`
- **R**ead (Leer) - `find()`
- **U**pdate (Actualizar) - `merge()`
- **D**elete (Eliminar) - `remove()`

### ¿Qué es el EntityManager?
El **EntityManager** es la interfaz principal que permite interactuar con el contexto de persistencia. Es el gestor de tus entidades y permite realizar operaciones con la base de datos.

**Estados del ciclo de vida de una entidad:**
- **Transient** - El objeto solo existe en memoria, EntityManager no lo conoce
- **Managed** - EntityManager rastrea el objeto y sincroniza los cambios
- **Detached** - El objeto existe en BD pero EntityManager ya no rastrea las actualizaciones
- **Removed** - El objeto está marcado para eliminación en la próxima sincronización

**Gestión de transacciones:**
Las transacciones controlan cuándo y cómo se guardan los datos en la base de datos, asegurando que las operaciones sean atómicas (todo o nada).

### JPA vs Hibernate

| Aspecto | JPA | Hibernate |
|---------|-----|-----------|
| **Naturaleza** | Especificación estándar | Implementación concreta |
| **Portabilidad** | Portable entre proveedores | Específico de Hibernate |
| **Lenguaje de consultas** | JPQL | JPQL + HQL propio |
| **Autonomía** | Requiere implementación | Funciona independientemente |

**Importante:** JPA es la especificación (el "qué"), Hibernate es la implementación (el "cómo"). En este proyecto usamos anotaciones de JPA (`jakarta.persistence.*`) y Hibernate como proveedor.

---

## Estructura del proyecto

Tu proyecto ya tiene esta estructura:

```
src/main/java/
├── modelo/
│   └── Usuario.java         (Ya existe - ejemplo a seguir)
├── dao/
│   ├── UsuarioDAO.java      (Interfaz)
│   └── UsuarioDAOHib.java   (Implementación con Hibernate)
└── App.java                 (Clase principal)
```

**Para cada entidad nueva debes crear:**
1. Una clase de entidad en el paquete `modelo/`
2. Una interfaz DAO en el paquete `dao/`
3. Una implementación del DAO en el paquete `dao/`

---

## Pasos para cada entidad

### Paso 1: Crear la clase de entidad

**Importación necesaria:**
```java
import jakarta.persistence.*;
```

Siguiendo el ejemplo de `Usuario.java`, debes:

1. Crear una clase en el paquete `modelo`
2. Importar `jakarta.persistence.*`
3. Añadir la anotación `@Entity` a la clase
4. Añadir la anotación `@Table(name = "nombre_tabla")`
5. Crear atributos privados para cada columna
6. Anotar el ID con `@Id` y `@GeneratedValue`
7. Anotar cada atributo con `@Column(name = "nombre_columna")`
8. Crear constructores (uno vacío OBLIGATORIO y uno con parámetros)
9. Crear getters y setters para todos los atributos
10. Sobrescribir el método `toString()`

### Paso 2: Crear la interfaz DAO

Siguiendo el ejemplo de `UsuarioDAO.java`:

1. Crear una interfaz en el paquete `dao`
2. Declarar los métodos CRUD:
   - `boolean crear[Entidad]([Entidad] entidad)` - Devuelve true si se crea correctamente
   - `Optional<[Entidad]> buscarPorId(int id)` - Devuelve Optional con la entidad o vacío
   - `[Entidad] actualizar[Entidad]([Entidad] entidad)` - Devuelve la entidad actualizada
   - `boolean eliminar[Entidad]([Entidad] entidad)` - Devuelve true si se elimina correctamente

### Paso 3: Implementar el DAO con Hibernate

Siguiendo el ejemplo de `UsuarioDAOHib.java`:

1. Crear una clase que implemente la interfaz DAO
2. Añadir un atributo `EntityManager`
3. Crear un constructor que reciba el `EntityManager`
4. Implementar cada método CRUD usando el `EntityManager`

**Patrón de implementación:**

**Para CREAR:**
```java
EntityTransaction tran = entityManager.getTransaction();
try {
    tran.begin();
    entityManager.persist(entidad);
    tran.commit();
    return true;
} catch (Exception e) {
    if (tran.isActive()) {
        tran.rollback();
    }
    throw new RuntimeException("Error al crear...");
}
```

**Para BUSCAR:**
```java
Entidad e = entityManager.find(Entidad.class, id);
return Optional.ofNullable(e);
```

**Para ACTUALIZAR:**
```java
EntityTransaction tran = entityManager.getTransaction();
try {
    tran.begin();
    Entidad actualizada = entityManager.merge(entidad);
    tran.commit();
    return actualizada;
} catch (Exception e) {
    if (tran.isActive()) {
        tran.rollback();
    }
    throw new RuntimeException("Error al actualizar...");
}
```

**Para ELIMINAR:**
```java
EntityTransaction tran = entityManager.getTransaction();
try {
    tran.begin();
    Entidad encontrada = entityManager.find(Entidad.class, entidad.getId());
    if (encontrada != null) {
        entityManager.remove(encontrada);
        tran.commit();
        return true;
    }
    return false;
} catch (Exception e) {
    if (tran.isActive()) {
        tran.rollback();
    }
    throw new RuntimeException("Error al eliminar...");
}
```

---

## Ejemplos detallados

### Ejemplo 1: Entidad Autor

#### 1.1. Clase Autor.java

```java
package modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "autor")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "nacionalidad", length = 50)
    private String nacionalidad;

    // Constructor vacío - OBLIGATORIO en JPA
    public Autor() {
    }

    // Constructor parametrizado - Recomendado para facilitar la instanciación
    public Autor(String nombre, String nacionalidad) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    @Override
    public String toString() {
        return "Autor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", nacionalidad='" + nacionalidad + '\'' +
                '}';
    }
}
```

#### 1.2. Interfaz AutorDAO.java

```java
package dao;

import modelo.Autor;
import java.util.Optional;

public interface AutorDAO {

    boolean crearAutor(Autor a);  // TU estilo: variable corta
    Optional<Autor> buscarPorId(int id);
    Autor actualizarAutor(Autor a);  // TU estilo: variable corta
    boolean eliminarAutor(Autor a);  // TU estilo: variable corta
}
```

#### 1.3. Implementación AutorDAOHib.java

```java
package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Autor;
import java.util.Optional;

public class AutorDAOHib implements AutorDAO {

    private EntityManager entityManager;

    public AutorDAOHib(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean crearAutor(Autor a) {  // TU estilo: Autor a
        EntityTransaction tran = entityManager.getTransaction();  // TU estilo: tran
        try {
            tran.begin();
            entityManager.persist(a);
            tran.commit();
            return true;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
            }
            throw new RuntimeException("Error al crear autor: " + e.getMessage());
        }
    }

    @Override
    public Optional<Autor> buscarPorId(int id) {
        Autor a = entityManager.find(Autor.class, id);  // TU estilo: Autor a
        return Optional.ofNullable(a);
    }

    @Override
    public Autor actualizarAutor(Autor a) {  // TU estilo: Autor a
        EntityTransaction tran = entityManager.getTransaction();  // TU estilo: tran
        try {
            tran.begin();
            Autor autorActualizado = entityManager.merge(a);
            tran.commit();
            return autorActualizado;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
            }
            throw new RuntimeException("Error al actualizar autor: " + e.getMessage());
        }
    }

    @Override
    public boolean eliminarAutor(Autor a) {  // TU estilo: Autor a
        EntityTransaction tran = entityManager.getTransaction();  // TU estilo: tran
        try {
            tran.begin();
            Autor autorEnc = entityManager.find(Autor.class, a.getId());  // TU estilo: autorEnc
            if (autorEnc != null) {
                entityManager.remove(autorEnc);
                tran.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
            }
            throw new RuntimeException("Error al eliminar autor: " + e.getMessage());
        }
    }
}
```

---

### Ejemplo 2: Entidad Libro (con claves foráneas)

#### IMPORTANTE: Claves foráneas como Integer

Según las instrucciones, **debes anotar las claves foráneas como si fuesen atributos simples tipo entero**. Esto significa que en lugar de crear relaciones `@ManyToOne` o `@OneToMany`, simplemente defines los IDs como campos `Integer`.

```java
package modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "isbn", unique = true, nullable = false, length = 20)
    private String isbn;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @Column(name = "paginas")
    private Integer paginas;

    @Column(name = "editorial", length = 100)
    private String editorial;

    // Claves foráneas como atributos simples tipo Integer
    @Column(name = "autor_id")
    private Integer autorId;

    @Column(name = "categoria_id")
    private Integer categoriaId;

    // Constructores, getters, setters y toString()...
}
```

---

### Ejemplo 3: Entidad con ENUM (Ejemplar)

Para manejar tipos ENUM en MySQL:

```java
package modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    // Para el ENUM, usamos @Enumerated con STRING
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEjemplar estado;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "libro_id", nullable = false)
    private Integer libroId;

    // Constructor por defecto inicializa el estado
    public Ejemplar() {
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    // Getters, setters y toString()...
}

// Enum para el estado del ejemplar
enum EstadoEjemplar {
    DISPONIBLE,
    PRESTADO,
    MANTENIMIENTO
}
```

---

## Tipos de datos Java para columnas SQL

| Tipo SQL | Tipo Java |
|----------|-----------|
| INT | int o Integer |
| VARCHAR | String |
| DATE | LocalDate (sin @Temporal) |
| DATETIME/TIMESTAMP | LocalDateTime (sin @Temporal) |
| ENUM | Enum (con @Enumerated) |

**Notas sobre tipos:**
- **Diferencia entre `int` e `Integer`:**
  - `int` - tipo primitivo, no puede ser null
  - `Integer` - tipo objeto, puede ser null (útil para columnas opcionales)

- **LocalDate y LocalDateTime:** NO necesitas usar `@Temporal` con estos tipos. Solo anótales con `@Column`

---

## Checklist de trabajo

### Para cada entidad (Autor, Categoria, Libro, Ejemplar, Prestamo):

#### Entidad
- [ ] Crear clase en `modelo/`
- [ ] Añadir `@Entity` y `@Table(name = "...")`
- [ ] Declarar atributos privados
- [ ] Anotar ID con `@Id` y `@GeneratedValue`
- [ ] Anotar cada columna con `@Column`
- [ ] Manejar ENUMs con `@Enumerated(EnumType.STRING)`
- [ ] Anotar claves foráneas como `Integer` con `@Column`
- [ ] Crear constructor vacío
- [ ] Crear constructor con parámetros
- [ ] Crear getters y setters
- [ ] Sobrescribir `toString()`

#### Interfaz DAO
- [ ] Crear interfaz en `dao/`
- [ ] Declarar método `crear[Entidad]`
- [ ] Declarar método `buscarPorId`
- [ ] Declarar método `actualizar[Entidad]`
- [ ] Declarar método `eliminar[Entidad]`

#### Implementación DAO
- [ ] Crear clase que implemente la interfaz
- [ ] Añadir atributo `EntityManager`
- [ ] Crear constructor con `EntityManager`
- [ ] Implementar método crear (con `persist`)
- [ ] Implementar método buscar (con `find`)
- [ ] Implementar método actualizar (con `merge`)
- [ ] Implementar método eliminar (con `remove`)
- [ ] Manejar transacciones correctamente
- [ ] Manejar errores con try-catch

---

## Resumen de las 5 entidades a implementar

### 1. Autor
- **Atributos:** id, nombre, nacionalidad
- **Sin claves foráneas**

### 2. Categoria
- **Atributos:** id, nombre, descripcion
- **Sin claves foráneas**

### 3. Libro
- **Atributos:** id, isbn, titulo, fechaPublicacion, paginas, editorial
- **Claves foráneas como Integer:** autorId, categoriaId

### 4. Ejemplar
- **Atributos:** id, codigo, estado (ENUM), ubicacion
- **Claves foráneas como Integer:** libroId

### 5. Prestamo
- **Atributos:** id, fechaInicio, fechaFin, fechaDevolucion, estado (ENUM)
- **Claves foráneas como Integer:** usuarioId, ejemplarId

---

## Notas importantes

1. **Constructor vacío OBLIGATORIO:** El constructor sin argumentos es obligatorio en todas las entidades JPA. Hibernate no puede crear instancias sin él.

2. **Getters y Setters altamente recomendables:** Aunque técnicamente no son obligatorios en JPA, Hibernate y otras implementaciones los requieren para funcionar correctamente.

3. **Transacciones:** Todas las operaciones que modifican datos (crear, actualizar, eliminar) deben estar dentro de una transacción.

4. **Rollback:** Si ocurre un error, debes hacer rollback de la transacción para mantener la consistencia de datos.

5. **Optional:** Se usa `Optional<Entidad>` en el método buscar para manejar elegantemente el caso de que no se encuentre la entidad.

6. **Claves foráneas simples:** En esta actividad NO usas `@ManyToOne`, `@OneToMany`, etc. Solo defines las claves foráneas como campos `Integer` normales con `@Column`.

7. **EntityManager:** Es proporcionado desde fuera del DAO (inyección de dependencias), no lo creas dentro del DAO.

8. **@Enumerated(EnumType.STRING):** Usa siempre STRING para enums porque es seguro, legible y evita errores al cambiar el orden de los valores.

9. **Estrategias de generación de IDs:** En este ejercicio usamos `GenerationType.IDENTITY` que usa auto_increment de MySQL/MariaDB. Otras opciones son:
   - `AUTO` - Hibernate elige automáticamente
   - `SEQUENCE` - Usa secuencias (Oracle, PostgreSQL)
   - `TABLE` - Usa una tabla auxiliar para generar IDs

---

## IMPORTANTE: Tu estilo vs. el estilo del profesor

### 📝 Análisis de tu código actual:

Has desarrollado tu propio estilo de programación que es **válido y funcional**, pero difiere del profesor en algunos aspectos. Aquí está la comparación:

#### 1. **Nombres de métodos en los DAOs**

**TU ESTILO (el que ya usas):**
```java
boolean crearAutor(Autor a);
Autor actualizarAutor(Autor a);
boolean eliminarAutor(Autor a);  // ← Recibe objeto completo
```

**ESTILO DEL PROFESOR:**
```java
boolean crear(Autor autor);
Autor actualizar(Autor autor);
boolean eliminar(int id);  // ← OJO: recibe solo ID, no el objeto completo
```

**¿Qué hacer?**
- **Opción 1:** Mantén tu estilo (es válido, funciona igual de bien)
- **Opción 2:** Cambia al estilo del profesor para que coincida exactamente con su solución

**RECOMENDACIÓN:** Si el profesor va a comparar tu código con su solución, considera cambiar al menos el método `eliminar()` para que reciba `int id` en lugar del objeto completo.

#### 2. **Nombres de variables**

**TU ESTILO:**
```java
Autor a              // Variable corta
Usuario u
EntityTransaction tran       // "tran" en lugar de "tx"
Usuario usuarioEnc           // Nombres descriptivos en español
Autor autorActualizado
```

**ESTILO DEL PROFESOR:**
```java
Autor autor          // Variable con nombre completo
Categoria categoria
EntityTransaction tx          // "tx" abreviado
Autor autorEncontrado        // Nombres en español pero más largos
Autor actualizado
```

**Conclusión:** Ambos estilos son válidos. Tu estilo es más conciso, el del profesor es más explícito.

#### 3. **Implementación del método eliminar**

**TU IMPLEMENTACIÓN ACTUAL (recibe objeto):**
```java
@Override
public boolean eliminarAutor(Autor a) {
    EntityTransaction tran = entityManager.getTransaction();
    try {
        tran.begin();
        Autor autorEnc = entityManager.find(Autor.class, a.getId());
        if (autorEnc != null) {
            entityManager.remove(autorEnc);
            tran.commit();
            return true;
        }
        return false;
    } catch (Exception e) {
        if (tran.isActive()) {
            tran.rollback();
            return false;
        }
        throw new RuntimeException("Error al eliminar autor" + e);
    }
}
```

**IMPLEMENTACIÓN DEL PROFESOR (recibe ID):**
```java
@Override
public boolean eliminar(int id) {
    EntityTransaction tx = entityManager.getTransaction();
    try {
        tx.begin();
        Autor autor = entityManager.find(Autor.class, id);
        if (autor != null) {
            entityManager.remove(autor);
            tx.commit();
            return true;
        }
    } catch (Exception e) {
        if (tx.isActive()) tx.rollback();
    }
    return false;
}
```

**Diferencias clave:**
- Tu método: `eliminarAutor(Autor a)` - recibe objeto, extrae el ID
- Profesor: `eliminar(int id)` - recibe directamente el ID
- Ambos funcionan, pero la del profesor es más simple

#### 4. **Ubicación de los ENUMs**

**Ambos estilos funcionan:**
- **Opción 1:** ENUM dentro de la clase de la entidad (como hace el profesor)
- **Opción 2:** ENUM en archivo separado (también válido)

**ENUM dentro de la clase:**
```java
@Entity
@Table(name = "ejemplar")
public class Ejemplar {
    // ... atributos

    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    // Enum dentro de la clase
    public enum EstadoEjemplar {
        DISPONIBLE, PRESTADO, MANTENIMIENTO
    }

    // ... resto del código
}
```

**Recomendación:** Sigue el estilo del profesor (ENUM dentro de la clase) para las entidades Ejemplar y Prestamo.

---

## La clase App.java - Explicación detallada

### ¿Qué es App.java?

`App.java` es tu **clase principal de pruebas**. Es donde:
1. Creas el `EntityManager`
2. Instancias los DAOs
3. Pruebas las operaciones CRUD de cada entidad

### Estructura básica de App.java

```java
import dao.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import modelo.*;
import org.slf4j.bridge.SLF4JBridgeHandler;
import java.util.Optional;

public class App {
    public static void main(String[] args) {
        // 1. Configuración de logs (reduce mensajes en consola)
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // 2. Crear EntityManager con try-with-resources
        //    Se cierra automáticamente al finalizar
        try (EntityManager em = Persistence
                .createEntityManagerFactory("biblioteca")  // ← nombre en persistence.xml
                .createEntityManager()) {

            // 3. Crear DAOs (uno por cada entidad)
            UsuarioDAO usuarioDAO = new UsuarioDAOHib(em);
            AutorDAO autorDAO = new AutorDAOHib(em);
            CategoriaDAO categoriaDAO = new CategoriaDAOHib(em);
            // ... etc

            // 4. Aquí van las pruebas CRUD

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

## Enfoque progresivo: Desarrollar App.java paso a paso

**NO necesitas comentar código durante el desarrollo.** Vas construyendo `App.java` progresivamente conforme completas cada entidad.

### 📅 PASO 1: Solo tienes Usuario y Autor (Estado actual de tu proyecto)

**App.java SIMPLE - Como el ejemplo del profesor:**

```java
import dao.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import modelo.*;
import org.slf4j.bridge.SLF4JBridgeHandler;
import java.util.Optional;

public class App {
    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try(EntityManager em = Persistence
                .createEntityManagerFactory("biblioteca")
                .createEntityManager()){

            // Crear DAOs
            UsuarioDAO usuarioDAO = new UsuarioDAOHib(em);
            AutorDAO autorDAO = new AutorDAOHib(em);

            // PRUEBA USUARIO (ya existe en tu código)
            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
            if(usuarioOptional.isPresent()){
                System.out.println("====USUARIO ENCONTRADO====");
                System.out.println(usuarioOptional.get());
            } else {
                System.out.println("====USUARIO NO ENCONTRADO====");
            }

            // PRUEBA AUTOR - Simple como la de Usuario
            Optional<Autor> autorOptional = autorDAO.buscarPorId(1);
            if(autorOptional.isPresent()){
                System.out.println("====AUTOR ENCONTRADO====");
                System.out.println(autorOptional.get());
            } else {
                System.out.println("====AUTOR NO ENCONTRADO====");
            }

            System.out.println("Programa de prueba finalizado");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Nota importante:** El ejercicio pide **implementar** los métodos CRUD en el DAO, pero **NO dice que tengas que probarlos todos en App.java**. La clase App.java es solo una prueba simple (como `buscarPorId()`) para demostrar que el DAO funciona.

### 📅 PASO 2: Acabas de completar Categoria

**Añades a App.java (solo la prueba simple):**

```java
// En la sección de DAOs:
CategoriaDAO categoriaDAO = new CategoriaDAOHib(em);  // ← NUEVO

// Añades después de las pruebas de Usuario y Autor:

// PRUEBA CATEGORIA - Simple como las anteriores
Optional<Categoria> categoriaOptional = categoriaDAO.buscarPorId(1);
if(categoriaOptional.isPresent()){
    System.out.println("====CATEGORIA ENCONTRADA====");
    System.out.println(categoriaOptional.get());
} else {
    System.out.println("====CATEGORIA NO ENCONTRADA====");
}
```

### 📅 PASO 3: Completar Libro (con claves foráneas)

**Añades a App.java:**

```java
// En la sección de DAOs:
LibroDAO libroDAO = new LibroDAOImpl(em);  // ← NUEVO

// PRUEBA LIBRO - Simple
Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
if(libroOptional.isPresent()){
    System.out.println("====LIBRO ENCONTRADO====");
    System.out.println(libroOptional.get());
} else {
    System.out.println("====LIBRO NO ENCONTRADO====");
}
```

**Nota:** Para que funcione esta prueba, necesitas tener datos en la base de datos. Libro tiene claves foráneas (`autor_id`, `categoria_id`), así que asegúrate de tener autores y categorías creados primero en la BD.

### 📅 PASO 4: Completar Ejemplar (con ENUM)

**Añades a App.java:**

```java
// En la sección de DAOs:
EjemplarDAO ejemplarDAO = new EjemplarDAOHib(em);  // ← NUEVO

// PRUEBA EJEMPLAR - Simple
Optional<Ejemplar> ejemplarOptional = ejemplarDAO.buscarPorId(1);
if(ejemplarOptional.isPresent()){
    System.out.println("====EJEMPLAR ENCONTRADO====");
    System.out.println(ejemplarOptional.get());
} else {
    System.out.println("====EJEMPLAR NO ENCONTRADO====");
}
```

### 📅 PASO 5: Completar Prestamo

**Añades a App.java:**

```java
// En la sección de DAOs:
PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);  // ← NUEVO

// PRUEBA PRESTAMO - Simple
Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
if(prestamoOptional.isPresent()){
    System.out.println("====PRESTAMO ENCONTRADO====");
    System.out.println(prestamoOptional.get());
} else {
    System.out.println("====PRESTAMO NO ENCONTRADO====");
}
```

---

## App.java COMPLETO - Versión final simple

Una vez que tengas **todas las entidades completadas** (Autor, Categoria, Libro, Ejemplar, Prestamo), tu `App.java` quedaría así:

```java
import dao.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import modelo.*;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Optional;

public class App {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try (EntityManager em = Persistence
                .createEntityManagerFactory("biblioteca")
                .createEntityManager()) {

            // Crear todos los DAOs
            UsuarioDAO usuarioDAO = new UsuarioDAOHib(em);
            AutorDAO autorDAO = new AutorDAOHib(em);
            CategoriaDAO categoriaDAO = new CategoriaDAOHib(em);
            LibroDAO libroDAO = new LibroDAOImpl(em);
            EjemplarDAO ejemplarDAO = new EjemplarDAOHib(em);
            PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);

            // Pruebas simples - Solo buscarPorId() como el ejemplo del profesor

            // USUARIO
            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
            if(usuarioOptional.isPresent()){
                System.out.println("====USUARIO ENCONTRADO====");
                System.out.println(usuarioOptional.get());
            } else {
                System.out.println("====USUARIO NO ENCONTRADO====");
            }

            // AUTOR
            Optional<Autor> autorOptional = autorDAO.buscarPorId(1);
            if(autorOptional.isPresent()){
                System.out.println("====AUTOR ENCONTRADO====");
                System.out.println(autorOptional.get());
            } else {
                System.out.println("====AUTOR NO ENCONTRADO====");
            }

            // CATEGORIA
            Optional<Categoria> categoriaOptional = categoriaDAO.buscarPorId(1);
            if(categoriaOptional.isPresent()){
                System.out.println("====CATEGORIA ENCONTRADA====");
                System.out.println(categoriaOptional.get());
            } else {
                System.out.println("====CATEGORIA NO ENCONTRADA====");
            }

            // LIBRO
            Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
            if(libroOptional.isPresent()){
                System.out.println("====LIBRO ENCONTRADO====");
                System.out.println(libroOptional.get());
            } else {
                System.out.println("====LIBRO NO ENCONTRADO====");
            }

            // EJEMPLAR
            Optional<Ejemplar> ejemplarOptional = ejemplarDAO.buscarPorId(1);
            if(ejemplarOptional.isPresent()){
                System.out.println("====EJEMPLAR ENCONTRADO====");
                System.out.println(ejemplarOptional.get());
            } else {
                System.out.println("====EJEMPLAR NO ENCONTRADO====");
            }

            // PRESTAMO
            Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
            if(prestamoOptional.isPresent()){
                System.out.println("====PRESTAMO ENCONTRADO====");
                System.out.println(prestamoOptional.get());
            } else {
                System.out.println("====PRESTAMO NO ENCONTRADO====");
            }

            System.out.println("\nPrograma de prueba finalizado");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Nota:** Este App.java solo hace **pruebas simples con `buscarPorId(1)`** de cada entidad, como el ejemplo del profesor. El ejercicio pide **implementar los métodos CRUD en los DAOs**, pero no requiere probarlos todos en App.java.

---

## 🎯 Cómo usar App.java durante el desarrollo

**Enfoque simple - Como el profesor:**

1. **Completas una entidad** (modelo + DAO + DAOImpl)
2. **Añades el DAO a App.java** en la sección de creación de DAOs
3. **Añades una prueba simple** `buscarPorId(1)` de esa entidad
4. **Ejecutas** el programa para verificar que funciona
5. **Repites** para la siguiente entidad

**Ventajas:**
- Código simple y claro, como el del profesor
- Fácil de ejecutar y probar
- No necesitas comentar/descomentar código
- Solo necesitas tener datos con ID=1 en la base de datos para probar

**¿Qué pasa si no tengo datos en la BD?**
- El programa mostrará "NO ENCONTRADO" para esas entidades
- Puedes insertar datos manualmente en la BD con MySQL Workbench/phpMyAdmin
- O puedes hacer una prueba temporal del método `crear()` en App.java para insertar datos

---

## Conclusión

### Pasos para completar el proyecto:

Para cada una de las 5 entidades (Autor, Categoria, Libro, Ejemplar, Prestamo):

1. **Crea la clase de entidad** en `modelo/` con todas sus anotaciones JPA
2. **Crea la interfaz DAO** en `dao/` con los 4 métodos CRUD:
   - `boolean crear(Entidad e)`
   - `Optional<Entidad> buscarPorId(int id)`
   - `Entidad actualizar(Entidad e)`
   - `boolean eliminar(int id)` ← **Recibe ID, no objeto completo**
3. **Implementa la interfaz** en `dao/` usando EntityManager
4. **Actualiza App.java** agregando el DAO y pruebas de la entidad
5. **Ejecuta y verifica** que todo funciona correctamente

### Recordatorios importantes:

✅ **Tu estilo de código es válido** - Variables cortas (`Autor a`, `Categoria cat`), métodos con nombre de entidad
✅ **Diferencia con el profesor** - Él usa `crear()`, `actualizar()`, `eliminar(int id)` sin nombre de entidad
✅ **Decisión sobre el método eliminar** - Puedes mantener `eliminarAutor(Autor a)` o cambiar a `eliminar(int id)` como el profesor
✅ **ENUMs dentro de la clase** de la entidad (EstadoEjemplar, EstadoPrestamo) - sigue al profesor en esto
✅ **Claves foráneas como Integer** con `@Column`, no `@ManyToOne`
✅ **Desarrollo progresivo de App.java** sin necesidad de comentar código
✅ **Constructor vacío obligatorio** en todas las entidades
✅ **Try-with-resources** para EntityManager en App.java
✅ **Usa `EntityTransaction tran`** (tu estilo) no `tx` (del profesor)

**Tu proyecto Usuario y Autor ya te sirven como plantilla completa.** Simplemente replica TU PROPIA estructura cambiando los nombres y atributos según cada tabla. Los ejemplos en esta guía usan tu estilo de variables y nombres de métodos.

---

## Recursos adicionales

Para profundizar en los conceptos, consulta los apuntes del profesor en:
- **Repositorio:** https://github.com/IARFLOW/AD_UD3
- **Teoría JPA:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/JPA_Java_Persistence_API.md`
- **Mapeo de Entidades:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/Mapeo_Entidades_Hibernate.md.md`
- **Mapeo de Atributos:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/Mapeo_Atributos_Hibernate.md`
- **Implementación DAO:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/Ejemplo_Implementacion_DAO.md`
- **EntityManager:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/EntityManager.md`
