# Relación Uno a Uno (@OneToOne) en Hibernate

## 📚 Índice
1. [Introducción](#introducción)
2. [¿Qué cambió en el código?](#qué-cambió-en-el-código)
3. [Conceptos clave](#conceptos-clave)
4. [LAZY vs EAGER - La diferencia crucial](#lazy-vs-eager---la-diferencia-crucial)
5. [Navegación Unidireccional vs Bidireccional](#navegación-unidireccional-vs-bidireccional)
6. [Ejemplo práctico en BibliotecaHibernate](#ejemplo-práctico-en-bibliotecahibernate)

---

## Introducción

La relación **Uno a Uno (1:1)** indica que **para cada registro de una tabla, solo puede haber un registro correspondiente en la otra tabla**.

### ¿Qué teníamos antes?

Hasta ahora, en el ejercicio de "Mapeo de entidades y atributos con Hibernate", teníamos las relaciones entre tablas representadas como **simples números enteros** (foreign keys). Por ejemplo, en `Prestamo` teníamos:

```java
@Column(name = "usuario_id", nullable = false)
private int usuarioId;  // Solo guardábamos el ID del usuario
```

Para obtener los datos del usuario necesitábamos:
1. Buscar el préstamo
2. Extraer el `usuarioId`
3. Hacer otra consulta manual para buscar el usuario

### ¿Qué tenemos ahora?

**Ahora**, con las relaciones `@OneToOne` de Hibernate, podemos hacer esto directamente:

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;  // ¡Guardamos el objeto Usuario completo!
```

Y acceder al usuario así:
```java
Prestamo prestamo = prestamoDAO.buscarPorId(1);
System.out.println(prestamo.getUsuario().getNombre());  // ¡Directo!
```

---

## ¿Qué cambió en el código?

### ANTES (con int - Versión del ejercicio 5)
```java
@Entity
@Table(name = "prestamo")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;  // ❌ Solo el ID

    @Column(name = "ejemplar_id", nullable = false)
    private int ejemplarId;  // ❌ Solo el ID

    // Constructor
    public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin,
                    LocalDate fechaDevolucion, EstadoPrestamo estado,
                    int usuarioId, int ejemplarId) {
        this.usuarioId = usuarioId;
        this.ejemplarId = ejemplarId;
    }

    // Getter
    public int getUsuarioId() {
        return usuarioId;
    }
}
```

### DESPUÉS (con relación @OneToOne - Versión actual)
```java
@Entity
@Table(name = "prestamo")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)  // ✅ Relación
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;  // ✅ Objeto completo

    @Column(name = "ejemplar_id", nullable = false)
    private int ejemplarId;  // Todavía como int

    // Constructor
    public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin,
                    LocalDate fechaDevolucion, EstadoPrestamo estado,
                    Usuario usuario, int ejemplarId) {
        this.usuario = usuario;  // ✅ Ahora recibe el objeto
        this.ejemplarId = ejemplarId;
    }

    // Getter
    public Usuario getUsuario() {  // ✅ Retorna el objeto
        return usuario;
    }
}
```

### 📋 Resumen de cambios realizados:

| Aspecto | ANTES | DESPUÉS |
|---------|-------|---------|
| **Tipo del atributo** | `int usuarioId` | `Usuario usuario` |
| **Anotación** | `@Column(name = "usuario_id")` | `@OneToOne + @JoinColumn(name = "usuario_id")` |
| **Constructor** | Recibe `int usuarioId` | Recibe `Usuario usuario` |
| **Getter** | `int getUsuarioId()` | `Usuario getUsuario()` |
| **Setter** | `void setUsuarioId(int id)` | `void setUsuario(Usuario u)` |
| **toString()** | Muestra `usuarioId=1` | Muestra `usuario=Usuario{...}` |

---

## Conceptos clave

### 🔗 @OneToOne

**Definición**: Anotación que establece una relación **Uno a Uno** entre dos entidades.

```java
@OneToOne
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

**¿Qué hace?**
- Le dice a Hibernate: "Este atributo `usuario` no es un campo normal, es una **relación con otra entidad**"
- Hibernate gestionará automáticamente las consultas para cargar el objeto relacionado

**Importante**:
- Por defecto, `@OneToOne` usa `FetchType.EAGER` (carga inmediata)
- Si quieres carga perezosa, debes especificar: `@OneToOne(fetch = FetchType.LAZY)`

### 📦 @JoinColumn

**Definición**: Define cuál es la **columna de clave foránea** en la base de datos.

```java
@JoinColumn(name = "usuario_id", nullable = false)
```

**Parámetros importantes:**
- `name`: El nombre de la columna en la base de datos que contiene la FK (`usuario_id`)
- `nullable`: Si puede ser NULL o no (false = obligatorio)
- `referencedColumnName`: La columna referenciada en la otra tabla (por defecto es `id`)

**¿Dónde va?**
Va en el **lado que tiene la clave foránea** en la base de datos. En nuestro caso:
- La tabla `prestamo` tiene la columna `usuario_id` (FK)
- Por lo tanto, `@JoinColumn` va en la clase `Prestamo`

---

## LAZY vs EAGER - La diferencia crucial

Esta es **LA PARTE MÁS IMPORTANTE** de la clase. Determina **CUÁNDO** Hibernate carga los datos relacionados.

### 🚀 EAGER (Ansioso/Inmediato/Impaciente)

**Significado**: "Carga TODO de inmediato, aunque no lo necesites"

```java
@OneToOne(fetch = FetchType.EAGER)  // Por defecto en @OneToOne
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

#### ¿Qué pasa internamente?

Cuando ejecutas:
```java
Prestamo prestamo = prestamoDAO.buscarPorId(1);
```

Hibernate hace **DOS consultas SQL inmediatamente**:

```sql
-- Consulta 1: Busca el préstamo
SELECT * FROM prestamo WHERE id = 1;

-- Consulta 2: Busca el usuario relacionado (AUTOMÁTICAMENTE)
SELECT * FROM usuario WHERE id = ?;
```

#### Salida en consola con EAGER:

```bash
Antes de acceder al usuario:
Usuario class: class modelo.Usuario  ← ¡Ya es el objeto real!
Prestamo{id=1, fechaInicio=2024-01-10, fechaFin=2024-01-24,
         fechaDevolucion=2024-01-23, estado=DEVUELTO,
         usuario=Usuario{id=1, dni='12345678A', nombre='Ana', ...},
         ejemplarId=3}
Después de acceder al usuario:
Usuario ID: 1
```

#### Características:

**Ventajas:**
- ✅ Datos disponibles inmediatamente
- ✅ No hay riesgo de `LazyInitializationException`
- ✅ Puedes cerrar la sesión y seguir accediendo a los datos

**Desventajas:**
- ❌ Carga datos que **puede que no necesites** (desperdicio)
- ❌ Más lento al inicio
- ❌ Mayor consumo de memoria

**Cuándo usar:**
- Cuando **SIEMPRE** necesitas los datos relacionados
- Ejemplo: Si cada vez que muestras un préstamo, SIEMPRE muestras el usuario

---

### 🐌 LAZY (Perezoso/Diferido/Vago)

**Significado**: "No cargues nada hasta que yo te lo pida expresamente"

```java
@OneToOne(fetch = FetchType.LAZY)  // ¡Hay que especificarlo!
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

#### ¿Qué pasa internamente?

Cuando ejecutas:
```java
Prestamo prestamo = prestamoDAO.buscarPorId(1);
```

Hibernate hace **SOLO UNA consulta**:

```sql
-- Consulta 1: Busca el préstamo
SELECT * FROM prestamo WHERE id = 1;
-- ¡NO busca el usuario todavía!
```

En lugar del usuario real, Hibernate crea un **PROXY** (objeto temporal/placeholder).

#### ¿Qué es un PROXY?

Un proxy es un **objeto "falso"** que Hibernate crea para **representar** al objeto real sin cargarlo todavía.

```java
Usuario usuario = prestamo.getUsuario();
// En este momento, usuario NO es un Usuario real
// Es un HibernateProxy$L6zHaBR0 (un "placeholder")
```

**Cuando accedes a algún método del proxy**, Hibernate hace la consulta SQL:

```java
String nombre = prestamo.getUsuario().getNombre();  // ← ¡AQUÍ se dispara la consulta SQL!
```

#### Salida en consola con LAZY:

```bash
Antes de acceder al usuario:
Usuario class: class modelo.Usuario$HibernateProxy$L6zHaBR0  ← ¡Es un PROXY!

# En el momento que ACCEDES a getUsuario().getId()...
Hibernate:
    select
        u1_0.id,
        u1_0.apellidos,
        u1_0.dni,
        u1_0.email,
        u1_0.fecha_nacimiento,
        u1_0.fecha_registro,
        u1_0.nombre,
        u1_0.telefono
    from
        usuario u1_0
    where
        u1_0.id=?  ← ¡AHORA hace la consulta!

Prestamo{id=1, fechaInicio=2024-01-10, ..., usuario=Usuario{...}, ...}
Después de acceder al usuario:
Usuario ID: 1
```

#### Características:

**Ventajas:**
- ✅ Más eficiente: Solo carga lo que necesitas
- ✅ Menor carga inicial
- ✅ Menos consumo de memoria

**Desventajas:**
- ❌ Puede dar `LazyInitializationException` si la sesión está cerrada
- ❌ Necesitas tener cuidado cuándo accedes a los datos

**Cuándo usar:**
- Cuando **NO SIEMPRE** necesitas los datos relacionados
- Es la opción **recomendada** en la mayoría de casos
- Ejemplo: A veces solo necesitas ver la fecha del préstamo, sin necesitar los datos del usuario

---

### 📊 Comparación LAZY vs EAGER

| Aspecto | EAGER | LAZY |
|---------|-------|------|
| **¿Cuándo carga los datos?** | Inmediatamente al cargar la entidad principal | Solo cuando accedes al atributo explícitamente |
| **Consultas SQL iniciales** | Todas de golpe (JOIN) | Solo la entidad principal |
| **Rendimiento inicial** | Más lento | Más rápido |
| **Uso de memoria** | Mayor (carga todo) | Menor (carga solo lo necesario) |
| **Tipo de objeto** | Objeto real desde el inicio | Proxy hasta que se accede |
| **Riesgo de error** | Bajo | `LazyInitializationException` si sesión cerrada |
| **Por defecto en @OneToOne** | ✅ Sí | No (hay que especificarlo) |
| **Recomendación general** | Cuando SIEMPRE necesitas los datos | Cuando NO SIEMPRE necesitas los datos ⭐ |

---

## Navegación Unidireccional vs Bidireccional

### 🔄 Navegación Unidireccional

**Definición**: Solo **una entidad** conoce a la otra.

En nuestro caso actual, `Prestamo` conoce a `Usuario`, pero `Usuario` NO conoce a `Prestamo`:

```java
// En Prestamo.java
@OneToOne
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;  // ✅ Prestamo conoce Usuario

// En Usuario.java
// ❌ NO hay ningún atributo que referencie a Prestamo
```

**Puedes hacer:**
```java
Prestamo p = prestamoDAO.buscarPorId(1);
Usuario u = p.getUsuario();  // ✅ Funciona
```

**NO puedes hacer:**
```java
Usuario u = usuarioDAO.buscarPorId(1);
Prestamo p = u.getPrestamo();  // ❌ Error: Usuario no tiene getPrestamo()
```

**Características:**
- ✅ Más simple
- ✅ Menos código
- ✅ Más fácil de mantener
- ✅ Recomendado por defecto

---

### 🔃 Navegación Bidireccional

**Definición**: **Ambas entidades** se conocen entre sí.

Si quisiéramos hacer bidireccional nuestro ejemplo:

```java
// En Prestamo.java
@OneToOne
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;  // Prestamo conoce Usuario

// En Usuario.java
@OneToOne(mappedBy = "usuario")  // ← mappedBy indica el lado inverso
private Prestamo prestamo;  // Usuario conoce Prestamo
```

**Ahora puedes hacer:**
```java
// Desde Prestamo
Prestamo p = prestamoDAO.buscarPorId(1);
Usuario u = p.getUsuario();  // ✅ Funciona

// Desde Usuario
Usuario u = usuarioDAO.buscarPorId(1);
Prestamo p = u.getPrestamo();  // ✅ También funciona
```

**Características:**
- ✅ Puedes navegar desde ambos lados
- ❌ Más complejo
- ❌ Debes sincronizar ambos lados manualmente
- ⚠️ Solo usar cuando realmente lo necesites

**Conceptos importantes:**
- `mappedBy`: Se usa en el **lado inverso** (el que NO tiene `@JoinColumn`)
- Indica el nombre del atributo en la otra clase que mapea la relación

---

## Ejemplo práctico en BibliotecaHibernate

### Cambios en Prestamo.java

```java
package modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    // ⭐ CAMBIO PRINCIPAL: De int usuarioId → Usuario usuario
    @OneToOne(fetch = FetchType.LAZY)  // LAZY para mejor rendimiento
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "ejemplar_id", nullable = false)
    private int ejemplarId;  // Todavía sin cambiar

    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, RETRASADO
    }

    // Constructor vacío - OBLIGATORIO
    public Prestamo() {
    }

    // Constructor con parámetros - ACTUALIZADO
    public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin,
                    LocalDate fechaDevolucion, EstadoPrestamo estado,
                    Usuario usuario, int ejemplarId) {  // ← Usuario, no int
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.usuario = usuario;  // ← Objeto, no ID
        this.ejemplarId = ejemplarId;
    }

    // Getters y Setters actualizados
    public Usuario getUsuario() {  // ← Retorna Usuario, no int
        return usuario;
    }

    public void setUsuario(Usuario usuario) {  // ← Recibe Usuario, no int
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", fechaDevolucion=" + fechaDevolucion +
                ", estado=" + estado +
                ", usuario=" + this.usuario +  // ← Imprime objeto completo
                ", ejemplarId=" + ejemplarId +
                '}';
    }
}
```

### Código de prueba en App.java

```java
public static void main(String[] args) {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    try(EntityManager em = Persistence
            .createEntityManagerFactory("biblioteca")
            .createEntityManager()){

        PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);

        Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
        if(prestamoOptional.isPresent()){
            System.out.println("====PRESTAMO ENCONTRADO====");
            System.out.println("Antes de acceder al prestamo:");

            // Comprobamos el tipo de clase (Proxy o real)
            System.out.println("Class: " + prestamoOptional.get().getUsuario().getClass());

            System.out.println(prestamoOptional.get());
            System.out.println("Después de acceder al prestamo:");

            // Al acceder a getId(), se dispara la consulta SQL si es LAZY
            System.out.println("Usuario ID: " + prestamoOptional.get().getUsuario().getId());
        }else {
            System.out.println("====PRESTAMO NO ENCONTRADO====");
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

### Salida esperada (con LAZY):

```bash
====PRESTAMO ENCONTRADO====
Antes de acceder al prestamo:
Class: class modelo.Usuario$HibernateProxy$L6zHaBR0  ← Es un PROXY

Hibernate:  ← Consulta SQL para cargar Usuario
    select
        u1_0.id,
        u1_0.apellidos,
        u1_0.dni,
        u1_0.email,
        u1_0.fecha_nacimiento,
        u1_0.fecha_registro,
        u1_0.nombre,
        u1_0.telefono
    from
        usuario u1_0
    where
        u1_0.id=?

Prestamo{id=1, fechaInicio=2024-01-10, fechaFin=2024-01-24,
         fechaDevolucion=2024-01-23, estado=DEVUELTO,
         usuario=Usuario{id=1, dni='12345678A', nombre='Ana',
                        apellidos='García López', ...},
         ejemplarId=3}
Después de acceder al prestamo:
Usuario ID: 1
```

---

## 🎯 Puntos clave para recordar

1. **@OneToOne**: Relación uno a uno entre entidades
2. **@JoinColumn**: Define la columna de clave foránea (va donde está la FK)
3. **FetchType.LAZY**: Carga los datos solo cuando los accedes (recomendado)
4. **FetchType.EAGER**: Carga los datos inmediatamente (por defecto en @OneToOne)
5. **Proxy**: Objeto temporal que Hibernate crea con LAZY hasta que accedes a los datos
6. **Unidireccional**: Solo una entidad conoce a la otra (más simple, recomendado)
7. **Bidireccional**: Ambas entidades se conocen (más complejo, usar solo si necesario)
8. **mappedBy**: Se usa en el lado inverso de una relación bidireccional

---

## ⚠️ Errores comunes

### 1. LazyInitializationException

**Error:**
```
org.hibernate.LazyInitializationException: could not initialize proxy - no Session
```

**Causa**: Intentas acceder a un objeto LAZY después de cerrar la sesión.

**Solución**: Accede a los datos ANTES de cerrar el EntityManager, o usa EAGER.

### 2. Olvidar actualizar el constructor

Cuando cambias de `int usuarioId` a `Usuario usuario`, debes actualizar:
- Constructor
- Getters/Setters
- toString()

### 3. Usar @Column en lugar de @JoinColumn

```java
// ❌ INCORRECTO
@OneToOne
@Column(name = "usuario_id")  // ¡Error! Debe ser @JoinColumn
private Usuario usuario;

// ✅ CORRECTO
@OneToOne
@JoinColumn(name = "usuario_id")
private Usuario usuario;
```

---

## 📚 Próximos pasos

En las próximas clases verás:
- **@ManyToOne**: Muchos a Uno (ej: Muchos libros → Un autor)
- **@OneToMany**: Uno a Muchos (ej: Un autor → Muchos libros)
- **@ManyToMany**: Muchos a Muchos (ej: Estudiantes ↔ Cursos)
