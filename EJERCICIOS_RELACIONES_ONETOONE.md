# Ejercicios: Relaciones @OneToOne en Hibernate

## Introducción

Este documento es una **guía práctica paso a paso** para completar los 3 ejercicios de relaciones `@OneToOne` que mandó el profesor. Está completamente adaptado a tu estilo de código y te explicará no solo el QUÉ sino también el POR QUÉ de cada cambio.

**💡 Referencia teórica:** Si necesitas repasar conceptos teóricos sobre `@OneToOne`, `LAZY` vs `EAGER`, o relaciones bidireccionales, consulta el documento [RELACION_UNO_A_UNO.md](RELACION_UNO_A_UNO.md).

---

## Estado Actual del Proyecto

Antes de empezar, este es el estado de las relaciones en tu proyecto:

- ✅ **Prestamo → Usuario**: Ya implementado con `@OneToOne(fetch = FetchType.LAZY)`
- ❌ **Prestamo → Ejemplar**: Todavía como `int ejemplarId` (sin relación)
- ❌ **Ejemplar → Libro**: Todavía como `int libro_id` (sin relación)
- ❌ **Relación bidireccional Ejemplar ↔ Prestamo**: No existe

---

## Al Final de Estos Ejercicios

Tendrás este esquema de relaciones completo:

```
       Usuario
          ↑
          | @OneToOne (unidireccional) - LAZY
       Prestamo
          ↑ ↓
          | @OneToOne (bidireccional) - LAZY
       Ejemplar
          ↑
          | @OneToOne (unidireccional) - LAZY
         Libro
```

**Resultado:**
- ✅ Prestamo → Usuario (ya existe)
- ✅ Prestamo → Ejemplar (Ejercicio 1)
- ✅ Ejemplar → Libro (Ejercicio 2)
- ✅ Ejemplar ↔ Prestamo bidireccional (Ejercicio 3)

---

## Pre-requisitos

Antes de empezar, asegúrate de que:

1. ✅ Docker está corriendo con la base de datos MariaDB
2. ✅ La base de datos `biblioteca` tiene datos de prueba en las tablas
3. ✅ Tu proyecto compila sin errores
4. ✅ `persistence.xml` está correctamente configurado
5. ✅ Has leído el documento teórico RELACION_UNO_A_UNO.md

---

# EJERCICIO 1: Prestamo → Ejemplar

## 🎯 Objetivo

Cambiar el atributo `int ejemplarId` por una **relación `@OneToOne`** con la entidad `Ejemplar`:
- Reemplazar `int ejemplarId` → `Ejemplar ejemplar`
- Probar con **FETCH EAGER**
- Probar con **FETCH LAZY** (recomendado)
- Entender las diferencias entre ambos

---

## 📋 Código ANTES (Estado Actual)

Así está actualmente tu clase `Prestamo.java`:

```java
// Prestamo.java - LÍNEAS 32-33
@Column(name = "ejemplar_id", nullable = false)
private int ejemplarId;  // ❌ Solo guardamos el ID

// Constructor - LÍNEA 46
public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin,
                LocalDate fechaDevolucion, EstadoPrestamo estado,
                Usuario usuario, int ejemplarId) {  // ← int
    // ...
    this.ejemplarId = ejemplarId;
}

// Getters/Setters - LÍNEAS 105-111
public int getEjemplarId() {
    return ejemplarId;
}

public void setEjemplarId(int ejemplarId) {
    this.ejemplarId = ejemplarId;
}

// toString() - LÍNEA 122
", ejemplarId=" + ejemplarId +
```

**Problema con este enfoque:**
- Solo tienes el ID, no el objeto completo
- Para obtener los datos del ejemplar, necesitarías hacer otra consulta manualmente
- No aprovechas el poder de Hibernate para gestionar relaciones

---

## PARTE A: Implementación con EAGER

### Paso 1: Añadir Import

Al principio de `Prestamo.java`, añade:

```java
import modelo.Ejemplar;
```

Tu sección de imports quedará así:

```java
package modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import modelo.Ejemplar;  // ← NUEVO
```

### Paso 2: Cambiar el Atributo

**ANTES:**
```java
@Column(name = "ejemplar_id", nullable = false)
private int ejemplarId;
```

**DESPUÉS:**
```java
// Relación @OneToOne con Ejemplar - EAGER: carga inmediatamente
@OneToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "ejemplar_id", nullable = false)
private Ejemplar ejemplar;  // ← Objeto completo, no ID
```

**💡 Explicación:**
- `@OneToOne`: Define una relación uno a uno
- `fetch = FetchType.EAGER`: Hibernate cargará el ejemplar INMEDIATAMENTE al cargar el préstamo
- `@JoinColumn(name = "ejemplar_id")`: La columna `ejemplar_id` en la tabla `prestamo` es la clave foránea

### Paso 3: Actualizar el Constructor

**ANTES:**
```java
public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin,
                LocalDate fechaDevolucion, EstadoPrestamo estado,
                Usuario usuario, int ejemplarId) {
    this.id = id;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.fechaDevolucion = fechaDevolucion;
    this.estado = estado;
    this.usuario = usuario;
    this.ejemplarId = ejemplarId;  // ← int
}
```

**DESPUÉS:**
```java
public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin,
                LocalDate fechaDevolucion, EstadoPrestamo estado,
                Usuario usuario, Ejemplar ejemplar) {  // ← Objeto Ejemplar
    this.id = id;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.fechaDevolucion = fechaDevolucion;
    this.estado = estado;
    this.usuario = usuario;
    this.ejemplar = ejemplar;  // ← Objeto, no ID
}
```

### Paso 4: Actualizar Getters y Setters

**ANTES:**
```java
public int getEjemplarId() {
    return ejemplarId;
}

public void setEjemplarId(int ejemplarId) {
    this.ejemplarId = ejemplarId;
}
```

**DESPUÉS:**
```java
public Ejemplar getEjemplar() {
    return ejemplar;
}

public void setEjemplar(Ejemplar ejemplar) {
    this.ejemplar = ejemplar;
}
```

### Paso 5: Actualizar toString()

**ANTES:**
```java
@Override
public String toString() {
    return "Prestamo{" +
            "id=" + id +
            ", fechaInicio=" + fechaInicio +
            ", fechaFin=" + fechaFin +
            ", fechaDevolucion=" + fechaDevolucion +
            ", estado=" + estado +
            ", usuarioId=" + this.usuario +
            ", ejemplarId=" + ejemplarId +  // ← int
            '}';
}
```

**DESPUÉS:**
```java
@Override
public String toString() {
    return "Prestamo{" +
            "id=" + id +
            ", fechaInicio=" + fechaInicio +
            ", fechaFin=" + fechaFin +
            ", fechaDevolucion=" + fechaDevolucion +
            ", estado=" + estado +
            ", usuario=" + this.usuario +
            ", ejemplar=" + this.ejemplar +  // ← Objeto completo
            '}';
}
```

**💡 Nota:** Cambié también "usuarioId=" a "usuario=" para ser consistente.

---

### 🧪 Código de Prueba en App.java (EAGER)

Añade este código en tu `App.java` (dentro del bloque try):

```java
System.out.println("\n=== EJERCICIO 1: Prestamo → Ejemplar (EAGER) ===");

PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);

if (prestamoOptional.isPresent()) {
    Prestamo p = prestamoOptional.get();
    System.out.println("Antes de acceder al ejemplar:");
    System.out.println("Ejemplar class: " + p.getEjemplar().getClass());
    System.out.println(p);
    System.out.println("Después de acceder al ejemplar:");
    System.out.println("Código ejemplar: " + p.getEjemplar().getCodigo());
} else {
    System.out.println("====PRESTAMO NO ENCONTRADO====");
}
```

### 📊 Salida Esperada con EAGER

```bash
=== EJERCICIO 1: Prestamo → Ejemplar (EAGER) ===
Antes de acceder al ejemplar:
Ejemplar class: class modelo.Ejemplar  ← Objeto real, NO proxy
Prestamo{id=1, fechaInicio=2024-01-10, fechaFin=2024-01-24,
         fechaDevolucion=2024-01-23, estado=DEVUELTO,
         usuario=Usuario{...},
         ejemplar=Ejemplar{id=3, codigo='EJ-003', estado=DISPONIBLE, ...}}
Después de acceder al ejemplar:
Código ejemplar: EJ-003
```

### 💡 Observaciones EAGER

**¿Qué pasó internamente?**

Hibernate hizo **UNA sola consulta SQL con JOIN**:

```sql
SELECT p.*, u.*, e.*
FROM prestamo p
LEFT JOIN usuario u ON p.usuario_id = u.id
LEFT JOIN ejemplar e ON p.ejemplar_id = e.id
WHERE p.id = 1;
```

**Características:**
- ✅ **Una sola consulta** (más eficiente en número de queries)
- ✅ **Objeto real** disponible inmediatamente (no es proxy)
- ✅ **No hay riesgo** de `LazyInitializationException`
- ❌ **Carga datos** aunque no los necesites
- ❌ **Más lenta** si tienes muchas relaciones (carga todo de golpe)

---

## PARTE B: Implementación con LAZY (Recomendado)

### Cambio Único

Para cambiar a LAZY, solo necesitas modificar **UNA línea**:

```java
// Relación @OneToOne con Ejemplar - LAZY: carga solo cuando lo accedes
@OneToOne(fetch = FetchType.LAZY)  // ← Solo cambia esto
@JoinColumn(name = "ejemplar_id", nullable = false)
private Ejemplar ejemplar;
```

**Todo lo demás queda igual** (constructor, getters, setters, toString).

### 🧪 Código de Prueba (LAZY)

El código de prueba es el mismo que con EAGER. Añade esto en `App.java`:

```java
System.out.println("\n=== EJERCICIO 1: Prestamo → Ejemplar (LAZY) ===");

PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);

if (prestamoOptional.isPresent()) {
    Prestamo p = prestamoOptional.get();
    System.out.println("Antes de acceder al ejemplar:");
    System.out.println("Ejemplar class: " + p.getEjemplar().getClass());
    System.out.println(p);
    System.out.println("Después de acceder al ejemplar:");
    System.out.println("Código ejemplar: " + p.getEjemplar().getCodigo());
} else {
    System.out.println("====PRESTAMO NO ENCONTRADO====");
}
```

### 📊 Salida Esperada con LAZY

```bash
=== EJERCICIO 1: Prestamo → Ejemplar (LAZY) ===
Antes de acceder al ejemplar:
Ejemplar class: class modelo.Ejemplar$HibernateProxy$xyz  ← ¡Es un PROXY!

Hibernate:
    select
        e1_0.id,
        e1_0.codigo,
        e1_0.estado,
        e1_0.libro_id,
        e1_0.ubicacion
    from
        ejemplar e1_0
    where
        e1_0.id=?  ← Consulta SQL disparada AHORA

Prestamo{id=1, fechaInicio=2024-01-10, fechaFin=2024-01-24,
         fechaDevolucion=2024-01-23, estado=DEVUELTO,
         usuario=Usuario{...},
         ejemplar=Ejemplar{id=3, codigo='EJ-003', estado=DISPONIBLE, ...}}
Después de acceder al ejemplar:
Código ejemplar: EJ-003
```

### 💡 Observaciones LAZY

**¿Qué pasó internamente?**

Hibernate hizo **DOS consultas SQL separadas**:

1. **Primera consulta** (al buscar el préstamo):
```sql
SELECT p.*
FROM prestamo p
WHERE p.id = 1;
```

2. **Segunda consulta** (cuando accedes a `p.getEjemplar().getClass()` o `.getCodigo()`):
```sql
SELECT e.*
FROM ejemplar e
WHERE e.id = ?;
```

**Características:**
- ✅ **Más eficiente** si no siempre necesitas el ejemplar
- ✅ **Carga inicial más rápida** (solo carga el préstamo)
- ✅ **Menos memoria** consumida inicialmente
- ⚠️ **Proxy** hasta que accedes al objeto (Hibernate crea un objeto temporal)
- ❌ **Riesgo** de `LazyInitializationException` si la sesión está cerrada

---

## 📊 Comparativa EAGER vs LAZY (Ejercicio 1)

| Aspecto | EAGER | LAZY |
|---------|-------|------|
| **Consultas SQL** | 1 con JOIN | 2 separadas |
| **Tipo de objeto** | Ejemplar real | EjemplarProxy |
| **Cuándo carga** | Inmediatamente | Al acceder al objeto |
| **Velocidad inicial** | Más lenta | Más rápida |
| **Uso de memoria** | Mayor | Menor |
| **Riesgo de error** | Ninguno | LazyInitializationException |
| **Recomendado cuando** | SIEMPRE necesitas el ejemplar | NO SIEMPRE lo necesitas |

**💡 Recomendación:** Usa `LAZY` como valor por defecto. Solo usa `EAGER` si sabes que **siempre** necesitarás acceder al ejemplar relacionado.

---

## ✅ Resumen Ejercicio 1

Has aprendido:
- ✅ Cómo cambiar de `int` a objeto relacionado con `@OneToOne`
- ✅ La diferencia entre `EAGER` y `LAZY`
- ✅ Cómo Hibernate crea proxies con LAZY
- ✅ Actualizar constructor, getters/setters y toString()

**Archivos modificados:**
- `src/main/java/modelo/Prestamo.java`
- `src/main/java/App.java` (código de prueba)

---

# EJERCICIO 2: Ejemplar → Libro

## 🎯 Objetivo

Cambiar el atributo `int libro_id` por una **relación `@OneToOne`** con la entidad `Libro`:
- Reemplazar `int libro_id` → `Libro libro`
- Decidir nomenclatura: `getLibro_id()` vs `getLibro()`
- Probar con **FETCH EAGER**
- Probar con **FETCH LAZY** (recomendado)

---

## 📋 Código ANTES (Estado Actual)

Así está actualmente tu clase `Ejemplar.java`:

```java
// Ejemplar.java
@Column(name = "libro_id", nullable = false)
private int libro_id;  // ❌ snake_case con int

// Constructor
public Ejemplar(int id, String codigo, EstadoEjemplar estado,
                String ubicacion, int libro_id) {
    // ...
    this.libro_id = libro_id;
}

// Getters/Setters - Nota el snake_case
public int getLibro_id() {
    return libro_id;
}

public void setLibro_id(int libro_id) {
    this.libro_id = libro_id;
}

// toString()
", libro_id=" + libro_id +
```

---

## ⚠️ IMPORTANTE: Decisión sobre Nomenclatura

Tu código actual usa **snake_case** para `libro_id`. Al cambiar a relación `@OneToOne`, tienes **2 opciones**:

### OPCIÓN 1: Mantener nombre con guión bajo

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "libro_id", nullable = false)
private Libro libro_id;  // ← Mantiene underscore

public Libro getLibro_id() { return libro_id; }
public void setLibro_id(Libro libro_id) { this.libro_id = libro_id; }
```

**Pros:**
- ✅ Consistente con tu estilo actual de `fecha_nacimiento`, `fecha_registro`

**Contras:**
- ❌ Confuso: `libro_id` sugiere un `int`, no un objeto `Libro`
- ❌ Menos legible: `ejemplar.getLibro_id().getTitulo()` vs `ejemplar.getLibro().getTitulo()`
- ❌ No es el estándar Java

---

### OPCIÓN 2: Nombre estándar (RECOMENDADO)

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "libro_id", nullable = false)
private Libro libro;  // ← Nombre limpio, sin underscore

public Libro getLibro() { return libro; }
public void setLibro(Libro libro) { this.libro = libro; }
```

**Pros:**
- ✅ Más claro: `libro` es un objeto `Libro`
- ✅ Estándar Java para objetos relacionados
- ✅ Más legible: `ejemplar.getLibro().getTitulo()`
- ✅ Consistente con `Prestamo.usuario` y `Prestamo.ejemplar`

**Contras:**
- ⚠️ Rompe consistencia con snake_case de atributos de fecha

---

### 💡 Recomendación Final

**Usa OPCIÓN 2** (nombre estándar `libro`).

**Razón:** Cuando trabajas con objetos relacionados en Hibernate/JPA, es mejor seguir la convención estándar Java. El snake_case es útil para campos simples de fecha (`fecha_nacimiento`), pero para relaciones es más claro usar nombres sin underscore.

**Nota:** En este documento te mostraré la implementación con **OPCIÓN 2**, pero si decides usar OPCIÓN 1, solo cambia:
- `libro` → `libro_id`
- `getLibro()` → `getLibro_id()`
- `setLibro()` → `setLibro_id()`

---

## Implementación con LAZY (Recomendada)

### Paso 1: Añadir Import

Al principio de `Ejemplar.java`, añade:

```java
import modelo.Libro;
```

### Paso 2: Cambiar el Atributo

**ANTES:**
```java
@Column(name = "libro_id", nullable = false)
private int libro_id;
```

**DESPUÉS (OPCIÓN 2 - Recomendada):**
```java
// Relación @OneToOne con Libro - LAZY: carga el Libro solo cuando lo accedes
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "libro_id", nullable = false)
private Libro libro;  // ← Objeto completo
```

### Paso 3: Actualizar el Constructor

**ANTES:**
```java
public Ejemplar(int id, String codigo, EstadoEjemplar estado,
                String ubicacion, int libro_id) {
    this.id = id;
    this.codigo = codigo;
    this.estado = estado;
    this.ubicacion = ubicacion;
    this.libro_id = libro_id;  // ← int
}
```

**DESPUÉS:**
```java
public Ejemplar(int id, String codigo, EstadoEjemplar estado,
                String ubicacion, Libro libro) {  // ← Objeto Libro
    this.id = id;
    this.codigo = codigo;
    this.estado = estado;
    this.ubicacion = ubicacion;
    this.libro = libro;  // ← Objeto, no ID
}
```

### Paso 4: Actualizar Getters y Setters

**ANTES:**
```java
public int getLibro_id() {
    return libro_id;
}

public void setLibro_id(int libro_id) {
    this.libro_id = libro_id;
}
```

**DESPUÉS:**
```java
public Libro getLibro() {
    return libro;
}

public void setLibro(Libro libro) {
    this.libro = libro;
}
```

### Paso 5: Actualizar toString()

**ANTES:**
```java
@Override
public String toString() {
    return "Ejemplar{" +
            "id=" + id +
            ", codigo='" + codigo + '\'' +
            ", estado=" + estado +
            ", ubicacion='" + ubicacion + '\'' +
            ", libro_id=" + libro_id +  // ← int
            '}';
}
```

**DESPUÉS:**
```java
@Override
public String toString() {
    return "Ejemplar{" +
            "id=" + id +
            ", codigo='" + codigo + '\'' +
            ", estado=" + estado +
            ", ubicacion='" + ubicacion + '\'' +
            ", libro=" + this.libro +  // ← Objeto completo
            '}';
}
```

---

### 🧪 Código de Prueba en App.java (LAZY)

Añade este código en tu `App.java`:

```java
System.out.println("\n=== EJERCICIO 2: Ejemplar → Libro (LAZY) ===");

EjemplarDAO ejemplarDAO = new EjemplarDAOHib(em);
Optional<Ejemplar> ejemplarOptional = ejemplarDAO.buscarPorId(1);

if (ejemplarOptional.isPresent()) {
    Ejemplar e = ejemplarOptional.get();
    System.out.println("Antes de acceder al libro:");
    System.out.println("Libro class: " + e.getLibro().getClass());
    System.out.println(e);
    System.out.println("Después de acceder al libro:");
    System.out.println("Título libro: " + e.getLibro().getTitulo());
} else {
    System.out.println("====EJEMPLAR NO ENCONTRADO====");
}
```

### 📊 Salida Esperada con LAZY

```bash
=== EJERCICIO 2: Ejemplar → Libro (LAZY) ===
Antes de acceder al libro:
Libro class: class modelo.Libro$HibernateProxy$abc  ← ¡Proxy!

Hibernate:
    select
        l1_0.id,
        l1_0.isbn,
        l1_0.titulo,
        l1_0.fecha_publicacion,
        l1_0.paginas,
        l1_0.editorial,
        l1_0.autor_id,
        l1_0.categoria_id
    from
        libro l1_0
    where
        l1_0.id=?  ← Consulta SQL disparada al acceder

Ejemplar{id=1, codigo='EJ-001', estado=DISPONIBLE, ubicacion='Estantería A1',
         libro=Libro{id=1, isbn='978-84-9804-654-0', titulo='Cien años de soledad', ...}}
Después de acceder al libro:
Título libro: Cien años de soledad
```

### 💡 Observaciones

**Con LAZY:**
- Proxy hasta que accedes al libro
- Dos consultas SQL separadas
- Más eficiente si no siempre necesitas los datos del libro

**Si usaras EAGER:**
- Objeto real inmediatamente
- Una sola consulta con JOIN
- Más simple pero menos eficiente

---

## ✅ Resumen Ejercicio 2

Has aprendido:
- ✅ Cómo adaptar snake_case a objetos relacionados
- ✅ La importancia de nomenclatura clara (getLibro vs getLibro_id)
- ✅ Aplicar el mismo patrón del Ejercicio 1 a otra entidad

**Archivos modificados:**
- `src/main/java/modelo/Ejemplar.java`
- `src/main/java/App.java` (código de prueba)

---

# EJERCICIO 3: Relación Bidireccional Ejemplar ↔ Prestamo

## 🎯 Objetivo

Hacer que la relación entre `Prestamo` y `Ejemplar` sea **bidireccional**:
- Hasta ahora: Prestamo conoce a Ejemplar (`prestamo.getEjemplar()`)
- Ahora: Ejemplar TAMBIÉN conocerá a Prestamo (`ejemplar.getPrestamo()`)
- Aprender sobre `mappedBy` y roles owner/inverse
- **CUIDADO:** Evitar bucles infinitos en `toString()`

---

## 📋 Estado ANTES (Unidireccional)

**Actualmente:**

```java
// Prestamo.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ejemplar_id", nullable = false)
private Ejemplar ejemplar;  // ✅ Prestamo conoce Ejemplar

// Ejemplar.java
// ❌ NO hay ningún atributo que referencie a Prestamo
```

**Esto significa:**
```java
// ✅ FUNCIONA:
Prestamo p = prestamoDAO.buscarPorId(1).get();
Ejemplar e = p.getEjemplar();  // ✅ OK

// ❌ NO FUNCIONA:
Ejemplar e = ejemplarDAO.buscarPorId(1).get();
Prestamo p = e.getPrestamo();  // ❌ Error: método no existe
```

---

## ¿Por Qué Bidireccional?

**Ventajas:**
- Puedes navegar en ambas direcciones
- Más flexible para consultas complejas
- Modela mejor relaciones del mundo real

**Ejemplo práctico:**
```java
// Encontrar todos los préstamos de un ejemplar específico
Ejemplar e = ejemplarDAO.buscarPorId(5).get();
Prestamo p = e.getPrestamo();  // ← AHORA FUNCIONARÁ
Usuario u = p.getUsuario();
System.out.println("Este ejemplar está prestado a: " + u.getNombre());
```

---

## Conceptos Clave

### Owner vs Inverse

En una relación bidireccional, **uno de los lados es el "owner"** (dueño) y el otro es el **"inverse"** (inverso):

**Owner (Dueño):**
- Es el lado que tiene la **clave foránea** en la base de datos
- Usa `@JoinColumn`
- En nuestro caso: **Prestamo** (tiene `ejemplar_id` en su tabla)

**Inverse (Inverso):**
- Es el lado que NO tiene la clave foránea
- Usa `mappedBy`
- En nuestro caso: **Ejemplar** (NO tiene `prestamo_id` en su tabla)

**Diagrama:**
```
TABLA prestamo           TABLA ejemplar
+--------------+         +-------------+
| id           |         | id          |
| fecha_inicio |         | codigo      |
| ejemplar_id  | ----→   | libro_id    |
+--------------+         +-------------+
     ↑ Owner                 ↑ Inverse
    (tiene FK)            (NO tiene FK)
```

---

## Implementación

### ⚠️ Prestamo.java - NO CAMBIA NADA

**Importante:** En `Prestamo.java` NO necesitas hacer ningún cambio. Ya tiene la anotación correcta como owner:

```java
// Prestamo.java - QUEDA IGUAL
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ejemplar_id", nullable = false)  // ← Owner
private Ejemplar ejemplar;
```

### ✅ Ejemplar.java - AÑADIR Relación Inversa

En `Ejemplar.java`, añade el nuevo atributo **DESPUÉS** de `libro`:

```java
// Ejemplar.java

@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "libro_id", nullable = false)
private Libro libro;

// AÑADIR este atributo:
// Relación bidireccional - mappedBy indica que Prestamo es el owner
@OneToOne(mappedBy = "ejemplar", fetch = FetchType.LAZY)
private Prestamo prestamo;  // ← NUEVO
```

**💡 Explicación de `mappedBy`:**

```java
@OneToOne(mappedBy = "ejemplar", ...)
```

- `mappedBy`: "Esta relación YA está mapeada en la otra entidad"
- `"ejemplar"`: Nombre del atributo en `Prestamo.java` que mapea esta relación
- Le dice a Hibernate: "No crees otra FK, usa la que ya existe en Prestamo"

### Añadir Getters y Setters

Añade estos métodos en `Ejemplar.java`:

```java
public Prestamo getPrestamo() {
    return prestamo;
}

public void setPrestamo(Prestamo prestamo) {
    this.prestamo = prestamo;
}
```

---

## ⚠️ CUIDADO: toString() y Bucles Infinitos

### El Problema

Si dejas los `toString()` como están, tendrás un **StackOverflowError**:

```java
// Ejemplar.java (MAL):
@Override
public String toString() {
    return "Ejemplar{..., prestamo=" + prestamo + "}";
    //                                   ↑ Llama prestamo.toString()
}

// Prestamo.java (MAL):
@Override
public String toString() {
    return "Prestamo{..., ejemplar=" + ejemplar + "}";
    //                                   ↑ Llama ejemplar.toString()
}
```

**Resultado:**
```
Ejemplar.toString()
  → llama Prestamo.toString()
    → llama Ejemplar.toString()
      → llama Prestamo.toString()
        → llama Ejemplar.toString()
          → ...∞ StackOverflowError
```

### La Solución

En relaciones bidireccionales, **uno de los lados debe mostrar solo el ID**, no el objeto completo.

**Decisión:** `Ejemplar` mostrará solo el ID de `Prestamo`.

**Modificar `Ejemplar.java` toString():**

```java
@Override
public String toString() {
    return "Ejemplar{" +
            "id=" + id +
            ", codigo='" + codigo + '\'' +
            ", estado=" + estado +
            ", ubicacion='" + ubicacion + '\'' +
            ", libro=" + this.libro +  // ← OK, unidireccional
            ", prestamoId=" + (prestamo != null ? prestamo.getId() : "null") +
            // ↑ Solo ID, NO objeto completo
            '}';
}
```

**💡 Nota:**
- `libro` sigue mostrando el objeto completo (es unidireccional)
- `prestamoId` solo muestra el ID (es bidireccional)
- Verificamos `!= null` porque puede no haber préstamo asociado

**Prestamo.java toString() queda igual:**

```java
// Prestamo.java - NO CAMBIAR
@Override
public String toString() {
    return "Prestamo{" +
            "id=" + id +
            // ...
            ", ejemplar=" + this.ejemplar +  // ← Muestra objeto completo, OK
            '}';
}
```

---

### 🧪 Código de Prueba en App.java

Añade este código completo que prueba ambas direcciones:

```java
System.out.println("\n=== EJERCICIO 3: Bidireccional Ejemplar ↔ Prestamo ===");

// Prueba 1: Prestamo → Ejemplar (ya funcionaba antes)
System.out.println("\n✅ Prueba 1: Prestamo → Ejemplar");
PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
Optional<Prestamo> prestamoOpt = prestamoDAO.buscarPorId(1);
if (prestamoOpt.isPresent()) {
    Prestamo p = prestamoOpt.get();
    System.out.println("Código del ejemplar: " + p.getEjemplar().getCodigo());
} else {
    System.out.println("Prestamo no encontrado");
}

// Prueba 2: Ejemplar → Prestamo (AHORA funciona)
System.out.println("\n✅ Prueba 2: Ejemplar → Prestamo");
EjemplarDAO ejemplarDAO = new EjemplarDAOHib(em);
Optional<Ejemplar> ejemplarOpt = ejemplarDAO.buscarPorId(1);
if (ejemplarOpt.isPresent()) {
    Ejemplar e = ejemplarOpt.get();
    if (e.getPrestamo() != null) {
        System.out.println("Este ejemplar está en el préstamo ID: " + e.getPrestamo().getId());
        System.out.println("Usuario que lo tiene: " + e.getPrestamo().getUsuario().getNombre());
    } else {
        System.out.println("Este ejemplar no tiene préstamo activo");
    }
} else {
    System.out.println("Ejemplar no encontrado");
}

// Prueba 3: Verificar que toString() NO causa StackOverflowError
System.out.println("\n✅ Prueba 3: ToString sin error");
if (ejemplarOpt.isPresent()) {
    System.out.println(ejemplarOpt.get());  // ← No debe dar error
    System.out.println("¡toString() funciona sin bucle infinito!");
}
```

### 📊 Salida Esperada

```bash
=== EJERCICIO 3: Bidireccional Ejemplar ↔ Prestamo ===

✅ Prueba 1: Prestamo → Ejemplar
Código del ejemplar: EJ-001

✅ Prueba 2: Ejemplar → Prestamo
Hibernate:
    select
        p1_0.id,
        p1_0.estado,
        p1_0.fecha_devolucion,
        p1_0.fecha_fin,
        p1_0.fecha_inicio,
        p1_0.usuario_id
    from
        prestamo p1_0
    where
        p1_0.ejemplar_id=?  ← Consulta del prestamo asociado

Este ejemplar está en el préstamo ID: 1
Usuario que lo tiene: Ana

✅ Prueba 3: ToString sin error
Ejemplar{id=1, codigo='EJ-001', estado=DISPONIBLE, ubicacion='Estantería A1',
         libro=Libro{...}, prestamoId=1}
¡toString() funciona sin bucle infinito!
```

---

## 📊 Diagrama de la Relación Bidireccional

```
Prestamo.java:
┌─────────────────────────────────────┐
│ @OneToOne(fetch = FetchType.LAZY)   │  ← Owner
│ @JoinColumn(name="ejemplar_id")     │  (tiene @JoinColumn)
│ private Ejemplar ejemplar;          │
└──────────────┬──────────────────────┘
               │
               │ Referencias mutuas
               │
Ejemplar.java: ↓
┌─────────────────────────────────────┐
│ @OneToOne(mappedBy="ejemplar", ...) │  ← Inverse
│ private Prestamo prestamo;          │  (tiene mappedBy)
└─────────────────────────────────────┘

Base de datos:
prestamo.ejemplar_id ──→ ejemplar.id
(FK en tabla prestamo)
```

---

## ✅ Resumen Ejercicio 3

Has aprendido:
- ✅ Qué es una relación bidireccional
- ✅ Diferencia entre owner (con @JoinColumn) e inverse (con mappedBy)
- ✅ El significado de `mappedBy = "ejemplar"`
- ✅ **CRÍTICO:** Cómo evitar bucles infinitos en toString()
- ✅ Navegar en ambas direcciones: `prestamo.getEjemplar()` y `ejemplar.getPrestamo()`

**Archivos modificados:**
- `src/main/java/modelo/Ejemplar.java` (añadido atributo prestamo y modificado toString)
- `src/main/java/App.java` (código de prueba)

**Archivos NO modificados:**
- `src/main/java/modelo/Prestamo.java` (queda igual)

---

# Código Completo Final

## Prestamo.java Completo (Después de Ejercicio 1)

```java
package modelo;

import jakarta.persistence.*;
import modelo.Ejemplar;
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

    // Relación @OneToOne con Usuario - LAZY: carga el Usuario solo cuando lo accedes
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación @OneToOne con Ejemplar - LAZY: carga el Ejemplar solo cuando lo accedes
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private Ejemplar ejemplar;

    // Enum dentro de la clase
    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, RETRASADO
    }

    // Constructor vacío - OBLIGATORIO
    public Prestamo() {
    }

    // Constructor con parámetros
    public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaDevolucion,
                    EstadoPrestamo estado, Usuario usuario, Ejemplar ejemplar) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.usuario = usuario;
        this.ejemplar = ejemplar;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = ejemplar;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", fechaDevolucion=" + fechaDevolucion +
                ", estado=" + estado +
                ", usuario=" + this.usuario +
                ", ejemplar=" + this.ejemplar +
                '}';
    }
}
```

---

## Ejemplar.java Completo (Después de los 3 Ejercicios)

```java
package modelo;

import jakarta.persistence.*;
import modelo.Libro;
import modelo.Prestamo;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    // Relación @OneToOne con Libro - LAZY: carga el Libro solo cuando lo accedes
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    // Relación bidireccional - mappedBy indica que Prestamo es el owner
    @OneToOne(mappedBy = "ejemplar", fetch = FetchType.LAZY)
    private Prestamo prestamo;

    // Enum dentro de la clase
    public enum EstadoEjemplar {
        DISPONIBLE, PRESTADO, MANTENIMIENTO
    }

    // Constructor vacío - OBLIGATORIO
    public Ejemplar() {
    }

    // Constructor con parámetros
    public Ejemplar(int id, String codigo, EstadoEjemplar estado,
                    String ubicacion, Libro libro) {
        this.id = id;
        this.codigo = codigo;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.libro = libro;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    public void setEstado(EstadoEjemplar estado) {
        this.estado = estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    @Override
    public String toString() {
        return "Ejemplar{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", estado=" + estado +
                ", ubicacion='" + ubicacion + '\'' +
                ", libro=" + this.libro +
                ", prestamoId=" + (prestamo != null ? prestamo.getId() : "null") +
                '}';
    }
}
```

---

# Diagrama y Comparativa Final

## Diagrama de Relaciones Completo

```
Estado final de todas las relaciones @OneToOne:

           Usuario
              ↑
              | @OneToOne (unidireccional)
              | FetchType.LAZY
              | @JoinColumn(name="usuario_id")
           Prestamo
              ↑ ↓
              | @OneToOne (bidireccional)
              | FetchType.LAZY
              | Owner: @JoinColumn(name="ejemplar_id")
              | Inverse: mappedBy="ejemplar"
           Ejemplar
              ↑
              | @OneToOne (unidireccional)
              | FetchType.LAZY
              | @JoinColumn(name="libro_id")
             Libro
```

---

## Tabla Resumen de Todas las Relaciones

| Relación | Tipo | Owner | Inverse | Fetch | Anotaciones |
|----------|------|-------|---------|-------|-------------|
| **Prestamo → Usuario** | Unidireccional | Prestamo | - | LAZY | @JoinColumn |
| **Prestamo ↔ Ejemplar** | Bidireccional | Prestamo | Ejemplar | LAZY | @JoinColumn + mappedBy |
| **Ejemplar → Libro** | Unidireccional | Ejemplar | - | LAZY | @JoinColumn |

---

## Navegación Posible (Lo que FUNCIONA)

```java
// ✅ Desde Prestamo:
Prestamo p = prestamoDAO.buscarPorId(1).get();
Usuario u = p.getUsuario();      // ✅ Funciona (unidireccional)
Ejemplar e = p.getEjemplar();    // ✅ Funciona (bidireccional)

// ✅ Desde Ejemplar:
Ejemplar e = ejemplarDAO.buscarPorId(1).get();
Prestamo p = e.getPrestamo();    // ✅ Funciona (bidireccional)
Libro l = e.getLibro();          // ✅ Funciona (unidireccional)

// ✅ Navegación en cadena:
Prestamo p = prestamoDAO.buscarPorId(1).get();
Usuario u = p.getUsuario();
Ejemplar e = p.getEjemplar();
Libro l = e.getLibro();
System.out.println(u.getNombre() + " tiene prestado: " + l.getTitulo());
```

---

## Navegación NO Posible (Lo que NO FUNCIONA)

```java
// ❌ Desde Usuario (no tiene relación inversa):
Usuario u = usuarioDAO.buscarPorId(1).get();
Prestamo p = u.getPrestamo();    // ❌ Error: método no existe

// ❌ Desde Libro (no tiene relación inversa):
Libro l = libroDAO.buscarPorId(1).get();
Ejemplar e = l.getEjemplar();    // ❌ Error: método no existe
```

**💡 Nota:** Si necesitaras estas navegaciones, tendrías que hacer esas relaciones bidireccionales también.

---

# Errores Comunes y Soluciones

## Error 1: Olvidar Actualizar el Constructor

### Síntoma

```java
Prestamo p = new Prestamo(1, fecha1, fecha2, null, EstadoPrestamo.ACTIVO, u, 1);
// Error de compilación:
// no suitable constructor found for Prestamo(int,LocalDate,LocalDate,LocalDate,EstadoPrestamo,Usuario,int)
// constructor Prestamo.Prestamo(int,LocalDate,LocalDate,LocalDate,EstadoPrestamo,Usuario,Ejemplar) is not applicable
```

### Causa

El constructor ahora espera un objeto `Ejemplar`, pero estás pasando un `int`.

### Solución

```java
// ❌ INCORRECTO:
Prestamo p = new Prestamo(1, fecha1, fecha2, null, EstadoPrestamo.ACTIVO, u, 1);

// ✅ CORRECTO:
Ejemplar e = ejemplarDAO.buscarPorId(1).get();
Prestamo p = new Prestamo(1, fecha1, fecha2, null, EstadoPrestamo.ACTIVO, u, e);
```

---

## Error 2: LazyInitializationException

### Síntoma

```
org.hibernate.LazyInitializationException: could not initialize proxy [modelo.Ejemplar#1] - no Session
```

### Causa

Intentas acceder a un objeto `LAZY` **después de cerrar** el `EntityManager`.

### Ejemplo Problemático

```java
Prestamo p;
try (EntityManager em = Persistence.createEntityManagerFactory("biblioteca").createEntityManager()) {
    PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
    p = prestamoDAO.buscarPorId(1).get();
}  // ← EntityManager se cierra aquí

System.out.println(p.getEjemplar().getCodigo());  // ❌ Error!
```

### Solución

Accede a los datos `LAZY` **ANTES** de cerrar el EntityManager:

```java
try (EntityManager em = Persistence.createEntityManagerFactory("biblioteca").createEntityManager()) {
    PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
    Prestamo p = prestamoDAO.buscarPorId(1).get();

    // ✅ Acceder AQUÍ, dentro del try
    System.out.println(p.getEjemplar().getCodigo());
}
```

---

## Error 3: @Column en lugar de @JoinColumn

### Síntoma

Error en tiempo de ejecución, mapping incorrecto, o Hibernate intenta crear columnas incorrectas.

### Causa

```java
// ❌ INCORRECTO:
@OneToOne
@Column(name = "ejemplar_id")  // ¡Mal! Debe ser @JoinColumn
private Ejemplar ejemplar;
```

### Solución

```java
// ✅ CORRECTO:
@OneToOne
@JoinColumn(name = "ejemplar_id")  // ← @JoinColumn para relaciones
private Ejemplar ejemplar;
```

**💡 Regla:**
- `@Column` para atributos simples (int, String, LocalDate)
- `@JoinColumn` para relaciones (@OneToOne, @ManyToOne)

---

## Error 4: Dos @JoinColumn en Bidireccional

### Síntoma

Hibernate intenta crear dos claves foráneas, o el mapping es inconsistente.

### Causa

```java
// Prestamo.java
@OneToOne
@JoinColumn(name = "ejemplar_id")  // ← Owner (OK)
private Ejemplar ejemplar;

// Ejemplar.java
@OneToOne
@JoinColumn(name = "prestamo_id")  // ❌ MAL! Debería ser mappedBy
private Prestamo prestamo;
```

### Solución

En bidireccional, **UN SOLO LADO** tiene `@JoinColumn`, el otro usa `mappedBy`:

```java
// Prestamo.java (Owner - tiene @JoinColumn)
@OneToOne
@JoinColumn(name = "ejemplar_id")  // ✅
private Ejemplar ejemplar;

// Ejemplar.java (Inverse - tiene mappedBy)
@OneToOne(mappedBy = "ejemplar")  // ✅
private Prestamo prestamo;
```

**💡 Regla de oro:**
- `@JoinColumn` en el lado con FK en base de datos
- `mappedBy` en el otro lado

---

## Error 5: StackOverflowError en toString()

### Síntoma

```
java.lang.StackOverflowError
    at modelo.Ejemplar.toString()
    at modelo.Prestamo.toString()
    at modelo.Ejemplar.toString()
    at modelo.Prestamo.toString()
    ...
```

### Causa

En relación bidireccional, ambos `toString()` se llaman mutuamente:

```java
// Ejemplar.java (MAL):
", prestamo=" + prestamo  // ← Llama prestamo.toString()

// Prestamo.java (MAL):
", ejemplar=" + ejemplar  // ← Llama ejemplar.toString()
```

### Solución

**Uno de los lados** debe mostrar solo el ID:

```java
// Ejemplar.java (BIEN):
", prestamoId=" + (prestamo != null ? prestamo.getId() : "null")
// ↑ Solo ID, NO objeto

// Prestamo.java (BIEN):
", ejemplar=" + this.ejemplar
// ↑ Puede mostrar objeto completo
```

---

## Error 6: Olvidar Imports

### Síntoma

```
error: cannot find symbol
  symbol:   class Ejemplar
  location: class Prestamo
```

### Solución

Asegúrate de tener todos los imports:

```java
// En Prestamo.java:
import jakarta.persistence.*;
import modelo.Ejemplar;
import modelo.Usuario;
import java.time.LocalDate;

// En Ejemplar.java:
import jakarta.persistence.*;
import modelo.Libro;
import modelo.Prestamo;
```

---

# Tips de Debugging

## Tip 1: Ver Tipo de Clase (Proxy vs Real)

Para verificar si tienes un proxy o el objeto real:

```java
System.out.println(prestamo.getEjemplar().getClass().getName());

// Con EAGER:
// modelo.Ejemplar  ← Objeto real

// Con LAZY:
// modelo.Ejemplar$HibernateProxy$xyz  ← Proxy
```

---

## Tip 2: Ver Consultas SQL

En `persistence.xml`, activa esto para ver las consultas:

```xml
<property name="hibernate.show_sql" value="true"/>
<property name="hibernate.format_sql" value="true"/>
```

Así verás exactamente cuándo Hibernate hace queries:

```sql
Hibernate:
    select
        p1_0.id,
        p1_0.ejemplar_id,
        p1_0.fecha_inicio,
        ...
    from
        prestamo p1_0
    where
        p1_0.id=?
```

---

## Tip 3: Verificar Datos en Base de Datos

Usa queries SQL directas para verificar tus datos:

```sql
-- Ver los IDs de relaciones
SELECT id, usuario_id, ejemplar_id FROM prestamo WHERE id = 1;

-- Ver el JOIN entre prestamo y ejemplar
SELECT p.id, p.ejemplar_id, e.codigo, e.estado
FROM prestamo p
LEFT JOIN ejemplar e ON p.ejemplar_id = e.id
WHERE p.id = 1;

-- Verificar relación libro-ejemplar
SELECT e.id, e.codigo, l.titulo
FROM ejemplar e
LEFT JOIN libro l ON e.libro_id = l.id
WHERE e.id = 1;
```

---

## Tip 4: Usar Optional Correctamente

Siempre verifica si el Optional tiene valor:

```java
// ✅ BIEN:
Optional<Prestamo> opt = prestamoDAO.buscarPorId(1);
if (opt.isPresent()) {
    Prestamo p = opt.get();
    // usar p
} else {
    System.out.println("No encontrado");
}

// ❌ MAL:
Prestamo p = prestamoDAO.buscarPorId(1).get();  // Sin verificar!
```

---

## Tip 5: Logs de Hibernate

Para ver más detalles de lo que hace Hibernate, en `src/main/resources/simplelogger.properties`:

```properties
org.hibernate.level=DEBUG
org.hibernate.SQL=DEBUG
org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

Esto mostrará:
- Todas las queries SQL
- Los parámetros que se pasan
- Cuándo se crean los proxies

---

# Checklist de Verificación

## ✓ Ejercicio 1: Prestamo → Ejemplar

- [ ] Añadí `import modelo.Ejemplar;`
- [ ] Cambié `int ejemplarId` a `Ejemplar ejemplar`
- [ ] Usé `@OneToOne` y `@JoinColumn`
- [ ] Actualicé el constructor (recibe `Ejemplar`, no `int`)
- [ ] Actualicé getters: `getEjemplar()` en lugar de `getEjemplarId()`
- [ ] Actualicé setters: `setEjemplar(Ejemplar e)`
- [ ] Actualicé `toString()`: muestra `ejemplar` no `ejemplarId`
- [ ] Probé con `EAGER` - funciona correctamente
- [ ] Probé con `LAZY` - funciona correctamente
- [ ] Vi la diferencia en las consultas SQL (1 con EAGER, 2 con LAZY)
- [ ] Código de prueba en App.java ejecuta sin errores

---

## ✓ Ejercicio 2: Ejemplar → Libro

- [ ] Añadí `import modelo.Libro;`
- [ ] Decidí nomenclatura: `libro` (recomendado) o `libro_id`
- [ ] Cambié `int libro_id` a `Libro libro`
- [ ] Usé `@OneToOne` y `@JoinColumn`
- [ ] Actualicé el constructor (recibe `Libro`, no `int`)
- [ ] Actualicé getters: `getLibro()` en lugar de `getLibro_id()`
- [ ] Actualicé setters: `setLibro(Libro l)`
- [ ] Actualicé `toString()`: muestra `libro` no `libro_id`
- [ ] Probé con `EAGER` - funciona correctamente (opcional)
- [ ] Probé con `LAZY` - funciona correctamente
- [ ] Código de prueba en App.java ejecuta sin errores

---

## ✓ Ejercicio 3: Bidireccional Ejemplar ↔ Prestamo

- [ ] En `Prestamo.java` NO hice cambios (ya era owner)
- [ ] En `Ejemplar.java` añadí `import modelo.Prestamo;`
- [ ] Añadí atributo `@OneToOne(mappedBy="ejemplar")` en Ejemplar
- [ ] Añadí `private Prestamo prestamo;` en Ejemplar
- [ ] Añadí `getPrestamo()` y `setPrestamo()` en Ejemplar
- [ ] **CRÍTICO:** Modifiqué `toString()` de Ejemplar para mostrar solo ID
- [ ] Verifiqué que `toString()` usa: `prestamo.getId()` no `prestamo`
- [ ] Probé Prestamo → Ejemplar (sigue funcionando)
- [ ] Probé Ejemplar → Prestamo (AHORA funciona)
- [ ] Probé que `toString()` NO causa `StackOverflowError`
- [ ] Código de prueba en App.java ejecuta sin errores

---

## ✓ Verificación General

- [ ] Todas las consultas SQL se muestran en consola (`hibernate.show_sql=true`)
- [ ] No hay `LazyInitializationException` (accedo a LAZY dentro del try)
- [ ] Los proxies se cargan correctamente con LAZY
- [ ] EAGER carga datos inmediatamente (una sola consulta)
- [ ] La base de datos tiene datos de prueba en todas las tablas
- [ ] El proyecto compila sin errores
- [ ] Todas las pruebas en App.java pasan correctamente
- [ ] Entiendo la diferencia entre EAGER y LAZY
- [ ] Entiendo qué es owner vs inverse en bidireccional
- [ ] Entiendo por qué uso `mappedBy` en lugar de `@JoinColumn`

---

# Conclusión

¡Felicidades! Has completado los 3 ejercicios de relaciones `@OneToOne` en Hibernate. Ahora sabes:

✅ Cómo cambiar de `int` a objetos relacionados con `@OneToOne`
✅ La diferencia crucial entre `EAGER` y `LAZY` (y cuándo usar cada uno)
✅ Cómo funcionan los proxies de Hibernate
✅ Qué es una relación bidireccional y cómo implementarla
✅ El concepto de owner vs inverse (`@JoinColumn` vs `mappedBy`)
✅ **MUY IMPORTANTE:** Cómo evitar bucles infinitos en `toString()`
✅ Errores comunes y cómo solucionarlos

## Próximos Pasos

En las próximas clases verás:
- **@ManyToOne**: Muchos a Uno (ej: Muchos libros → Un autor)
- **@OneToMany**: Uno a Muchos (ej: Un autor → Muchos libros)
- **@ManyToMany**: Muchos a Muchos (ej: Estudiantes ↔ Cursos)

Estos seguirán patrones similares, pero con colecciones (`List`, `Set`) en lugar de objetos únicos.

---

**📚 Documentación relacionada:**
- [RELACION_UNO_A_UNO.md](RELACION_UNO_A_UNO.md) - Teoría detallada sobre @OneToOne