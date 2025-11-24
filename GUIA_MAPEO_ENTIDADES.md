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

    boolean crearAutor(Autor autor);
    Optional<Autor> buscarPorId(int id);
    Autor actualizarAutor(Autor autor);
    boolean eliminarAutor(Autor autor);
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
    public boolean crearAutor(Autor autor) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            entityManager.persist(autor);
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
        Autor autor = entityManager.find(Autor.class, id);
        return Optional.ofNullable(autor);
    }

    @Override
    public Autor actualizarAutor(Autor autor) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Autor autorActualizado = entityManager.merge(autor);
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
    public boolean eliminarAutor(Autor autor) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Autor autorEncontrado = entityManager.find(Autor.class, autor.getId());
            if (autorEncontrado != null) {
                entityManager.remove(autorEncontrado);
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

## Ejemplo de uso en App.java

```java
// Crear EntityManager
EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
EntityManager em = emf.createEntityManager();

// Crear DAOs
AutorDAO autorDAO = new AutorDAOHib(em);

// Crear un autor
Autor autor = new Autor("Gabriel García Márquez", "Colombiana");
boolean creado = autorDAO.crearAutor(autor);

// Buscar por ID
Optional<Autor> autorEncontrado = autorDAO.buscarPorId(1);
if (autorEncontrado.isPresent()) {
    System.out.println(autorEncontrado.get());
}

// Actualizar
if (autorEncontrado.isPresent()) {
    Autor a = autorEncontrado.get();
    a.setNacionalidad("Colombia");
    autorDAO.actualizarAutor(a);
}

// Eliminar
if (autorEncontrado.isPresent()) {
    autorDAO.eliminarAutor(autorEncontrado.get());
}

// Cerrar
em.close();
emf.close();
```

---

## Conclusión

Para cada una de las 5 entidades (Autor, Categoria, Libro, Ejemplar, Prestamo):

1. Crea la clase de entidad con todas sus anotaciones
2. Crea la interfaz DAO con los 4 métodos CRUD
3. Implementa la interfaz usando EntityManager
4. Sigue el patrón de Usuario que ya existe en tu proyecto

El proyecto Usuario ya te sirve como plantilla completa. Simplemente replica la estructura cambiando los nombres y atributos según cada tabla.

---

## Recursos adicionales

Para profundizar en los conceptos, consulta los apuntes del profesor en:
- **Repositorio:** https://github.com/IARFLOW/AD_UD3
- **Teoría JPA:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/JPA_Java_Persistence_API.md`
- **Mapeo de Entidades:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/Mapeo_Entidades_Hibernate.md.md`
- **Mapeo de Atributos:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/Mapeo_Atributos_Hibernate.md`
- **Implementación DAO:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/Ejemplo_Implementacion_DAO.md`
- **EntityManager:** `Teoria_UD3_MAPEO_OBXECTO_RELACIONAL_(ORM)/Hibernate/EntityManager.md`
