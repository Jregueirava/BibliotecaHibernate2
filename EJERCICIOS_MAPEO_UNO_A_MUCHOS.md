# Ejercicios Mapeo Uno a Muchos (1:N)

## Introducción

En este documento se explican los ejercicios 3, 4 y 5 de mapeo Uno a Muchos, adaptados a tu estilo de código y nomenclatura.

### Conceptos Clave

**Relación Uno a Muchos (1:N)**: Un registro en una tabla puede estar relacionado con varios registros en otra tabla.

- **Lado "Uno" (One)**: Usa la anotación `@OneToMany`
- **Lado "Muchos" (Many)**: Usa la anotación `@ManyToOne` (este es el lado dueño de la relación)

**Bidireccional vs Unidireccional**:
- **Unidireccional**: Solo una entidad conoce la relación
- **Bidireccional**: Ambas entidades conocen la relación

---

## Ejercicio 1: Autor - Libro (Unidireccional)

### Descripción
Implementar la relación uno a muchos entre `Autor` y `Libro` de forma **unidireccional**. Un autor puede tener muchos libros, pero desde `Libro` solo conocemos su autor (no navegamos desde Autor hacia sus libros).

### Estado Actual vs Estado Objetivo

**Estado Actual** (Sin relación JPA):
```java
// En Libro.java
@Column (name = "autor_id")
private int autor_id;
```

**Estado Objetivo** (Relación ManyToOne unidireccional):
```java
// En Libro.java - Solo en Libro, NO en Autor
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "autor_id")
private Autor autor;
```

### Cambios Necesarios

#### 1. Modificar la clase `Libro.java`

**Cambiar el atributo**:
```java
// ANTES:
@Column (name = "autor_id")
private int autor_id;

// DESPUÉS:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "autor_id")
private Autor autor;
```

**Explicación**:
- `@ManyToOne`: Muchos libros pueden tener el mismo autor
- `fetch = FetchType.LAZY`: Carga perezosa del autor (solo se carga cuando se accede)
- `@JoinColumn(name = "autor_id")`: Define que la columna `autor_id` en la tabla `libro` es la clave foránea

**Modificar el constructor** (después del ejercicio 1):
```java
// ANTES:
public Libro(int id, String isbn, String titulo, LocalDate fecha_publicacion,
             int paginas, String editorial, int autor_id, int categoria_id) {
    this.id = id;
    this.isbn = isbn;
    this.titulo = titulo;
    this.fecha_publicacion = fecha_publicacion;
    this.paginas = paginas;
    this.editorial = editorial;
    this.autor_id = autor_id;
    this.categoria_id = categoria_id;
}

// DESPUÉS (ejercicio 1):
public Libro(int id, String isbn, String titulo, LocalDate fecha_publicacion,
             int paginas, String editorial, int categoria_id, Autor autor) {
    this.id = id;
    this.isbn = isbn;
    this.titulo = titulo;
    this.fecha_publicacion = fecha_publicacion;
    this.paginas = paginas;
    this.editorial = editorial;
    this.categoria_id = categoria_id;  // ← Todavía int
    this.autor = autor;                 // ← Ya es objeto
}
```

**Modificar getters y setters**:
```java
// ELIMINAR:
public int getAutor_id() {
    return autor_id;
}

public void setAutor_id(int autor_id) {
    this.autor_id = autor_id;
}

// AÑADIR:
public Autor getAutor() {
    return autor;
}

public void setAutor(Autor autor) {
    this.autor = autor;
}
```

**Modificar el `toString()`** (después del ejercicio 1, aún con `categoria_id` como int):
```java
@Override
public String toString() {
    return "Libro{" +
            "id=" + id +
            ", isbn='" + isbn + '\'' +
            ", titulo='" + titulo + '\'' +
            ", fecha_publicacion=" + fecha_publicacion +
            ", paginas=" + paginas +
            ", editorial='" + editorial + '\'' +
            ", autorId=" + this.autor +         // ← Objeto Autor (llama a autor.toString())
            ", categoriaId=" + categoria_id +   // ← Todavía int (se cambiará en ejercicio 2)
            '}';
}
```

**Nota**: Después del ejercicio 1, Categoria todavía es `int categoria_id`. Se cambiará en el ejercicio 2.

#### 2. La clase `Autor.java` NO se modifica

Como la relación es **unidireccional**, `Autor` **NO tiene** una lista de libros. Solo `Libro` conoce a su autor.

#### 3. Probar en `App.java`

```java
System.out.println("==== Ejercicio 1: Autor - Libro Unidireccional ====");

Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
if(libroOptional.isPresent()){
    Libro libro = libroOptional.get();
    System.out.println("====LIBRO ENCONTRADO====");
    System.out.println(libro);

    // Acceder al autor del libro
    System.out.println("Autor del libro: " + libro.getAutor().getNombre());
} else {
    System.out.println("====LIBRO NO ENCONTRADO====");
}
```

**Salida esperada:**
```
====LIBRO ENCONTRADO====
Libro{..., autorId=1, autorNombre='Robert C. Martin', ...}
Autor del libro: Robert C. Martin
```

---

## Ejercicio 2: Categoria - Libro (Unidireccional)

### Descripción
Implementar la relación uno a muchos entre `Categoria` y `Libro` de forma **unidireccional**. Una categoría puede tener muchos libros, pero desde `Libro` solo conocemos su categoría.

### Estado Actual vs Estado Objetivo

**Estado Actual** (Sin relación JPA):
```java
// En Libro.java
@Column (name = "categoria_id")
private int categoria_id;
```

**Estado Objetivo** (Relación ManyToOne unidireccional):
```java
// En Libro.java - Solo en Libro, NO en Categoria
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;
```

### Cambios Necesarios

#### 1. Modificar la clase `Libro.java`

**Cambiar el atributo**:
```java
// ANTES:
@Column (name = "categoria_id")
private int categoria_id;

// DESPUÉS:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;
```

**Actualizar el constructor** (después del ejercicio 2, ahora ambos son objetos):
```java
// DESPUÉS (ejercicio 2):
public Libro(int id, String isbn, String titulo, LocalDate fecha_publicacion,
             int paginas, String editorial, Categoria categoria, Autor autor) {
    this.id = id;
    this.isbn = isbn;
    this.titulo = titulo;
    this.fecha_publicacion = fecha_publicacion;
    this.paginas = paginas;
    this.editorial = editorial;
    this.categoria = categoria;  // ← Ahora es objeto
    this.autor = autor;           // ← Ya era objeto
}
```

**Modificar getters y setters**:
```java
// ELIMINAR:
public int getCategoria_id() {
    return categoria_id;
}

public void setCategoria_id(int categoria_id) {
    this.categoria_id = categoria_id;
}

// AÑADIR:
public Categoria getCategoria() {
    return categoria;
}

public void setCategoria(Categoria categoria) {
    this.categoria = categoria;
}
```

**Modificar el `toString()`** (después del ejercicio 2, con ambos como objetos):
```java
@Override
public String toString() {
    return "Libro{" +
            "id=" + id +
            ", isbn='" + isbn + '\'' +
            ", titulo='" + titulo + '\'' +
            ", fecha_publicacion=" + fecha_publicacion +
            ", paginas=" + paginas +
            ", editorial='" + editorial + '\'' +
            ", autorId=" + this.autor +       // ← Objeto Autor (llama a autor.toString())
            ", categoriaId=" + this.categoria + // ← Objeto Categoria (llama a categoria.toString())
            '}';
}
```

#### 2. La clase `Categoria.java` NO se modifica

Como la relación es **unidireccional**, `Categoria` **NO tiene** una lista de libros.

#### 3. Probar en `App.java`

```java
System.out.println("\n==== Ejercicio 2: Categoria - Libro Unidireccional ====");

Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
if(libroOptional.isPresent()){
    Libro libro = libroOptional.get();
    System.out.println("====LIBRO ENCONTRADO====");
    System.out.println(libro);

    // Acceder a la categoría del libro
    System.out.println("Categoría del libro: " + libro.getCategoria().getNombre());
} else {
    System.out.println("====LIBRO NO ENCONTRADO====");
}
```

**Salida esperada:**
```
====LIBRO ENCONTRADO====
Libro{..., categoriaId=1, categoriaNombre='Programación', ...}
Categoría del libro: Programación
```

---

## Ejercicio 3: Usuario - Prestamo (Bidireccional)

### Descripción
Implementar la relación uno a muchos entre `Usuario` y `Prestamo` de forma bidireccional. Un usuario puede tener muchos préstamos, pero cada préstamo pertenece a un único usuario.

### Estado Actual vs Estado Objetivo

**Estado Actual** (Relación OneToOne):
```java
// En Usuario.java
@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
private Prestamo prestamo;

// En Prestamo.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

**Estado Objetivo** (Relación OneToMany/ManyToOne):
```java
// En Usuario.java - Lado "Uno"
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Prestamo> prestamos = new ArrayList<>();

// En Prestamo.java - Lado "Muchos" (Lado dueño)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

### Cambios Necesarios

#### 1. Modificar la clase `Usuario.java`

**Cambio en el atributo**:
```java
// ANTES:
@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
private Prestamo prestamo;

// DESPUÉS:
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Prestamo> prestamos = new ArrayList<>();
```

**IMPORTANTE sobre `= new ArrayList<>()`**:
- Es **MUY RECOMENDABLE** inicializar la lista con `= new ArrayList<>()`
- Si no lo inicializas, la lista será `null` y te dará `NullPointerException` al hacer `prestamos.size()` o `addPrestamo()`
- Aunque en los apuntes del profesor a veces no aparece, **siempre deberías inicializarla** en la práctica real
- Mira el ejemplo unidireccional del profesor: ahí sí la inicializa con `= new ArrayList<>()`

**Explicación de las anotaciones**:
- `@OneToMany`: Define la relación uno a muchos
- `mappedBy = "usuario"`: Indica que el lado dueño es el atributo `usuario` en la clase `Prestamo`
- `cascade = CascadeType.ALL`: Las operaciones (persist, merge, remove, etc.) se propagan a los préstamos
- `orphanRemoval = true`: Si eliminas un préstamo de la lista, se elimina también de la base de datos

**Modificar los getters y setters**:
```java
// Getter
public List<Prestamo> getPrestamos() {
    return prestamos;
}

// Setter
public void setPrestamos(List<Prestamo> prestamos) {
    this.prestamos = prestamos;
}
```

**Añadir métodos helper** (según el enunciado del ejercicio 3.2):
```java
// Métodos para agregar y eliminar préstamos
public void agregarPrestamo(Prestamo prestamo) {
    prestamos.add(prestamo);
}

public void eliminarPrestamo(Prestamo prestamo) {
    prestamos.remove(prestamo);
}
```

**Modificar el método `toString()`** (para mostrar la cantidad de préstamos):
```java
@Override
public String toString() {
    return "Usuario{" +
            "id=" + id +
            ", dni='" + dni + '\'' +
            ", nombre='" + nombre + '\'' +
            ", apellidos='" + apellidos + '\'' +
            ", email='" + email + '\'' +
            ", telefono='" + telefono + '\'' +
            ", fecha_nacimiento=" + fecha_nacimiento +
            ", fecha_registro=" + fecha_registro +
            ", cantidadPrestamos=" + prestamos.size() +
            '}';
}
```

**ADVERTENCIA**: Ten cuidado con el `toString()` en relaciones bidireccionales. No llames a `prestamos.toString()` directamente porque causaría un bucle infinito (Usuario -> Prestamo -> Usuario -> ...).

#### 2. Modificar la clase `Prestamo.java`

**Cambio en la anotación**:
```java
// ANTES:
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;

// DESPUÉS:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

**Explicación**:
- `@ManyToOne`: Define que muchos préstamos pueden pertenecer a un usuario
- `fetch = FetchType.LAZY`: Carga perezosa del usuario
- `@JoinColumn(name = "usuario_id")`: Define la clave foránea en la tabla `prestamo`

**Modificar el método `toString()`** (para mostrar el ID y nombre del usuario):
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
            ", ejemplarId=" + this.ejemplar +
            '}';
}
```

**IMPORTANTE**: Esta forma funciona correctamente siempre que en `Usuario.toString()` uses `prestamos.size()` y **NO** imprimas la lista completa de préstamos. Si usas `.size()`, no hay bucle infinito.

#### 3. Probar en `App.java`

**Opción A: Verificación básica** (solo con `buscarPorId()` para verificar que el mapeo funciona):

```java
System.out.println("==== Ejercicio 3: Usuario - Prestamo Bidireccional ====");

Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
if(usuarioOptional.isPresent()){
    System.out.println("====USUARIO ENCONTRADO====");
    System.out.println(usuarioOptional.get());
} else {
    System.out.println("====USUARIO NO ENCONTRADO====");
}

Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
if(prestamoOptional.isPresent()){
    System.out.println("====PRESTAMO ENCONTRADO====");
    System.out.println(prestamoOptional.get());
} else {
    System.out.println("====PRESTAMO NO ENCONTRADO====");
}
```

**¿Qué verifica esto?**
- El usuario debe mostrar `cantidadPrestamos` en su `toString()`
- El préstamo debe mostrar el usuario completo en su `toString()`
- Si ambos funcionan correctamente, la relación bidireccional está implementada

---

**Opción B: Ejercicio 3.4 completo** (añadir préstamo y verificar cascade):

El enunciado 3.4 dice: *"Na clase App realiza operacións de engadir algún prestamo mais o usuario co que estas traballando. Actualiza unicamente o usuario mediante o DAO e comproba que o novo prestamo insertouse automaticamente."*

```java
System.out.println("\n==== Ejercicio 3.4: Añadir préstamo a usuario ====");

// 1. Buscar el usuario con el que trabajar
Optional<Usuario> usuarioParaActualizar = usuarioDAO.buscarPorId(1);
if(usuarioParaActualizar.isPresent()){
    Usuario usuario = usuarioParaActualizar.get();
    System.out.println("Usuario ANTES de añadir préstamo:");
    System.out.println(usuario);  // Debe mostrar cantidadPrestamos=1

    // 2. Buscar un ejemplar disponible para el nuevo préstamo
    Optional<Ejemplar> ejemplarParaPrestamo = ejemplarDAO.buscarPorId(5);
    if(ejemplarParaPrestamo.isPresent()){
        // 3. Crear un nuevo préstamo
        Prestamo nuevoPrestamo = new Prestamo();
        nuevoPrestamo.setFechaInicio(java.time.LocalDate.now());
        nuevoPrestamo.setFechaFin(java.time.LocalDate.now().plusDays(15));
        nuevoPrestamo.setEstado(Prestamo.EstadoPrestamo.ACTIVO);
        nuevoPrestamo.setUsuario(usuario);
        nuevoPrestamo.setEjemplar(ejemplarParaPrestamo.get());

        // 4. Añadir el préstamo al usuario usando el método helper
        usuario.agregarPrestamo(nuevoPrestamo);

        // 5. Actualizar SOLO el usuario (el préstamo se insertará automáticamente por cascade)
        usuario = usuarioDAO.actualizarUsuario(usuario);  // actualizar() del DAO ya gestiona la transacción

        System.out.println("\nUsuario DESPUÉS de añadir préstamo:");
        // 6. Volver a buscar el usuario para ver los cambios
        Optional<Usuario> usuarioActualizado = usuarioDAO.buscarPorId(1);
        if(usuarioActualizado.isPresent()){
            System.out.println(usuarioActualizado.get());  // Debe mostrar cantidadPrestamos=2
            System.out.println("\n✅ Préstamo insertado automáticamente gracias a cascade!");
        }
    } else {
        System.out.println("Ejemplar no encontrado para crear préstamo");
    }
} else {
    System.out.println("Usuario no encontrado");
}
```

**Explicación del código 3.4:**

1. **Buscar el usuario** (id=1) que tiene 1 préstamo según la base de datos inicial
2. **Buscar un ejemplar disponible** (id=5) para asociar al nuevo préstamo
3. **Crear un nuevo préstamo** con:
   - Fecha inicio: hoy
   - Fecha fin: dentro de 15 días
   - Estado: ACTIVO
   - Asociado al usuario y al ejemplar
4. **Usar `agregarPrestamo()`** para añadir el préstamo a la lista del usuario
5. **Actualizar SOLO el usuario** con `actualizarUsuario()`
   - Gracias a `cascade = CascadeType.ALL`, el nuevo préstamo se inserta automáticamente
6. **Verificar** que el usuario ahora tiene 2 préstamos (antes tenía 1)

**Salida esperada:**
```
Usuario ANTES de añadir préstamo:
Usuario{..., cantidadPrestamos=1}

Usuario DESPUÉS de añadir préstamo:
Usuario{..., cantidadPrestamos=2}

✅ Préstamo insertado automáticamente gracias a cascade!
```

**Conceptos clave que demuestra este ejercicio:**
- `cascade = CascadeType.ALL`: Al actualizar el usuario, también se guardan sus préstamos nuevos
- `orphanRemoval = true`: Si eliminaras un préstamo de la lista, se borraría de la BD
- Relación bidireccional: El usuario conoce sus préstamos, y cada préstamo conoce su usuario

---

## Ejercicio 4: Libro - Ejemplar (Bidireccional)

### Descripción
Implementar la relación uno a muchos entre `Libro` y `Ejemplar` de forma bidireccional. Un libro puede tener muchos ejemplares, pero cada ejemplar pertenece a un único libro.

### Estado Actual vs Estado Objetivo

**Estado Actual** (después de ejercicios 1 y 2):
```java
// En Libro.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "autor_id")
private Autor autor;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;

// En Ejemplar.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn (name = "libro_id", nullable = false)
private Libro libro;
```

**Estado Objetivo** (ejercicio 4):
```java
// En Libro.java - Lado "Uno" - Añadir la lista de ejemplares
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "autor_id")
private Autor autor;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;

@OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Ejemplar> ejemplares = new ArrayList<>();  // ← NUEVO

// En Ejemplar.java - Lado "Muchos" (Lado dueño)
@ManyToOne(fetch = FetchType.LAZY)  // ← Cambiar de @OneToOne a @ManyToOne
@JoinColumn(name = "libro_id", nullable = false)
private Libro libro;
```

### Cambios Necesarios

#### 1. Modificar la clase `Libro.java`

**IMPORTANTE**: Los ejercicios 1 y 2 deben estar completados antes. Es decir, `Libro` ya debe tener `Autor` y `Categoria` como objetos.

**Añadir la lista de ejemplares** (después de los atributos autor y categoria):
```java
@OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Ejemplar> ejemplares = new ArrayList<>();
```

**RECORDATORIO**: Inicializa siempre con `= new ArrayList<>()` para evitar `NullPointerException`.

**El constructor NO cambia** (ya se modificó en ejercicios 1 y 2):
```java
// Ya está así después de los ejercicios 1 y 2:
public Libro(int id, String isbn, String titulo, LocalDate fecha_publicacion,
             int paginas, String editorial, Categoria categoria, Autor autor) {
    this.id = id;
    this.isbn = isbn;
    this.titulo = titulo;
    this.fecha_publicacion = fecha_publicacion;
    this.paginas = paginas;
    this.editorial = editorial;
    this.categoria = categoria;
    this.autor = autor;
}
```

**Añadir getters/setters para ejemplares**:
```java
public List<Ejemplar> getEjemplares() {
    return ejemplares;
}

public void setEjemplares(List<Ejemplar> ejemplares) {
    this.ejemplares = ejemplares;
}

// Métodos para agregar y eliminar ejemplares
public void agregarEjemplar(Ejemplar ejemplar) {
    ejemplares.add(ejemplar);
}

public void eliminarEjemplar(Ejemplar ejemplar) {
    ejemplares.remove(ejemplar);
}
```

**Los getters/setters de autor y categoria NO cambian** (ya se modificaron en ejercicios 1 y 2).

**Modificar el `toString()`**:
```java
@Override
public String toString() {
    return "Libro{" +
            "id=" + id +
            ", isbn='" + isbn + '\'' +
            ", titulo='" + titulo + '\'' +
            ", fecha_publicacion=" + fecha_publicacion +
            ", paginas=" + paginas +
            ", editorial='" + editorial + '\'' +
            ", autorId=" + this.autor +
            ", categoriaId=" + this.categoria +
            ", cantidadEjemplares=" + ejemplares.size() +
            '}';
}
```

**IMPORTANTE**: Usamos `ejemplares.size()` para mostrar la cantidad sin imprimir la lista completa, evitando así bucles infinitos.

#### 2. Modificar la clase `Ejemplar.java`

**Cambiar la anotación**:
```java
// ANTES:
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn (name = "libro_id", nullable = false)
private Libro libro;

// DESPUÉS:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "libro_id", nullable = false)
private Libro libro;
```

**Modificar el `toString()`**:
```java
@Override
public String toString() {
    return "Ejemplar{" +
            "id=" + id +
            ", codigo='" + codigo + '\'' +
            ", estado=" + estado +
            ", ubicacion='" + ubicacion + '\'' +
            ", libroId=" + this.libro +
            ", prestamoId=" + (prestamo != null ? prestamo.getId() : "null") +
            '}';
}
```

**NOTA**: Usamos `this.libro` porque `Libro.toString()` usará `ejemplares.size()`, no imprimirá la lista completa, así que no hay bucle infinito.

#### 3. Probar en `App.java`

**Forma simple** (solo con `buscarPorId()` para verificar que funciona):

```java
System.out.println("==== Ejercicio 4: Libro - Ejemplar Bidireccional ====");

Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
if(libroOptional.isPresent()){
    System.out.println("====LIBRO ENCONTRADO====");
    System.out.println(libroOptional.get());
} else {
    System.out.println("====LIBRO NO ENCONTRADO====");
}

Optional<Ejemplar> ejemplarOptional = ejemplarDAO.buscarPorId(1);
if(ejemplarOptional.isPresent()){
    System.out.println("====EJEMPLAR ENCONTRADO====");
    System.out.println(ejemplarOptional.get());
} else {
    System.out.println("====EJEMPLAR NO ENCONTRADO====");
}
```

**¿Qué verifica esto?**
- El libro debe mostrar `cantidadEjemplares`, `autorId`, `autorNombre`, `categoriaId`, `categoriaNombre` en su `toString()`
- El ejemplar debe mostrar `libroId` y `libroTitulo` en su `toString()`
- Si ambos funcionan correctamente, la relación bidireccional está implementada

---

## Ejercicio 5: Prestamo - Ejemplar (Unidireccional)

### Descripción
Según la solución del profesor, la relación entre `Prestamo` y `Ejemplar` es **unidireccional desde Prestamo hacia Ejemplar**. Un préstamo tiene un ejemplar, pero el ejemplar no mantiene una referencia directa a todos sus préstamos en esta configuración.

### Estado Actual vs Estado Objetivo

**Estado Actual**:
```java
// En Prestamo.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ejemplar_id", nullable = false)
private Ejemplar ejemplar;

// En Ejemplar.java
@OneToOne(mappedBy = "ejemplar", cascade = CascadeType.ALL)
private Prestamo prestamo;
```

**Estado Objetivo** (Unidireccional):
```java
// En Prestamo.java - Lado dueño
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ejemplar_id", nullable = false)
private Ejemplar ejemplar;

// En Ejemplar.java - Sin referencia a Prestamo
// (Eliminar el atributo prestamo)
```

### Cambios Necesarios

#### 1. Modificar la clase `Prestamo.java`

**Cambiar la anotación**:
```java
// ANTES:
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ejemplar_id", nullable = false)
private Ejemplar ejemplar;

// DESPUÉS:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ejemplar_id", nullable = false)
private Ejemplar ejemplar;
```

El getter, setter y `toString()` permanecen igual (ya modificados en el ejercicio 3).

#### 2. Modificar la clase `Ejemplar.java`

**Eliminar la referencia bidireccional**:
```java
// ELIMINAR ESTO:
@OneToOne(mappedBy = "ejemplar", cascade = CascadeType.ALL)
private Prestamo prestamo;
```

**Eliminar getter/setter de prestamo**:
```java
// ELIMINAR:
public Prestamo getPrestamo() {
    return prestamo;
}

public void setPrestamo(Prestamo prestamo) {
    this.prestamo = prestamo;
}
```

**Modificar el `toString()`**:
```java
@Override
public String toString() {
    return "Ejemplar{" +
            "id=" + id +
            ", codigo='" + codigo + '\'' +
            ", estado=" + estado +
            ", ubicacion='" + ubicacion + '\'' +
            ", libroId=" + (libro != null ? libro.getId() : "null") +
            ", libroTitulo='" + (libro != null ? libro.getTitulo() : "null") + '\'' +
            '}';
}
```

**NOTA**: Ya no hay referencia a `prestamo` en el `toString()`.

---

## Resumen de las Relaciones

| Relación                | Tipo           | Lado Dueño   | Anotaciones                                                         |
|-------------------------|----------------|--------------|---------------------------------------------------------------------|
| **Autor - Libro**       | Unidireccional | **Libro**    | `@ManyToOne` en Libro                                               |
| **Categoria - Libro**   | Unidireccional | **Libro**    | `@ManyToOne` en Libro                                               |
| **Usuario - Prestamo**  | Bidireccional  | **Prestamo** | `@ManyToOne` en Prestamo + `@OneToMany(mappedBy)` en Usuario       |
| **Libro - Ejemplar**    | Bidireccional  | **Ejemplar** | `@ManyToOne` en Ejemplar + `@OneToMany(mappedBy)` en Libro         |
| **Prestamo - Ejemplar** | Unidireccional | **Prestamo** | `@ManyToOne` en Prestamo                                            |

---

## Preguntas Frecuentes y Dudas Importantes

### ❓ 1. ¿Es necesario poner `= new ArrayList<>()` al inicializar la lista?

**SÍ, es MUY RECOMENDABLE.**

```java
// SIN inicializar (MAL)
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Prestamo> prestamos;  // ← prestamos es NULL

// CON inicializar (BIEN)
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Prestamo> prestamos = new ArrayList<>();  // ← prestamos es una lista vacía
```

**¿Por qué?**

Si no lo inicializas y haces esto:
```java
Usuario usuario = new Usuario();
usuario.getPrestamos().size();  // ← NullPointerException! 💥
usuario.agregarPrestamo(prestamo);   // ← NullPointerException! 💥
```

**Observación**: Aunque en los apuntes del profesor a veces no aparece explícitamente, en el ejemplo unidireccional **sí lo inicializa**. En la práctica real, **siempre deberías inicializarlo**.

---

### ❓ 2. ¿Cómo evitar bucles infinitos en `toString()` con relaciones bidireccionales?

**La solución del profesor (la que usamos):**

```java
// En Prestamo.java
@Override
public String toString() {
    return "Prestamo{" +
            "id=" + id +
            ", fechaInicio=" + fechaInicio +
            ", fechaFin=" + fechaFin +
            ", fechaDevolucion=" + fechaDevolucion +
            ", estado=" + estado +
            ", usuarioId=" + this.usuario +      // ← Llama a usuario.toString()
            ", ejemplarId=" + this.ejemplar +    // ← Llama a ejemplar.toString()
            '}';
}
```

**¿Por qué NO hay bucle infinito aquí?**

Porque en el lado "Uno" de la relación (Usuario, Libro) usamos `.size()` en vez de imprimir la lista completa:

```java
// En Usuario.java - LA CLAVE ESTÁ AQUÍ ⚠️
@Override
public String toString() {
    return "Usuario{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", cantidadPrestamos=" + prestamos.size() +   // ← USA .size(), NO prestamos
            '}';
}
```

**Flujo sin bucle:**
```
1. Llama a prestamo.toString()
2. Encuentra "this.usuario" → llama a usuario.toString()
3. Usuario.toString() usa prestamos.size() (NO imprime la lista completa)
4. Devuelve "Usuario{..., cantidadPrestamos=2}"
5. FIN (sin recursividad) ✅
```

**❌ Lo que causaría bucle infinito:**

Si en `Usuario.toString()` hicieras:
```java
return "Usuario{..., prestamos=" + prestamos + "}";  // ← ESTO SÍ causaría bucle
```

**Regla de oro**: En el lado "Uno" de la relación bidireccional (`@OneToMany`), **usa `.size()` para mostrar la cantidad, NO imprimas la lista completa**.

---

### ❓ 3. ¿Debo usar nombres en inglés o castellano para los métodos?

**Puedes usar ambos**, pero sé consistente con tu estilo.

En tu proyecto ya usas nombres en castellano en los DAOs:
- `crearUsuario()`
- `actualizarLibro()`
- `eliminarPrestamo()`

Por lo tanto, es más consistente usar:
```java
public void agregarPrestamo(Prestamo prestamo) {
    prestamos.add(prestamo);
}

public void eliminarPrestamo(Prestamo prestamo) {
    prestamos.remove(prestamo);
}
```

**Nota**: Usamos `agregar` en vez de `añadir` para evitar problemas con la `ñ` en algunos entornos.

---

### ❓ 4. ¿Son necesarios los métodos `agregarPrestamo()` y `eliminarPrestamo()`?

**Depende de lo que necesites hacer:**

- **Si solo vas a hacer `buscarPorId()` para comprobar**: NO son estrictamente necesarios
- **Si vas a añadir/eliminar préstamos desde código**: SÍ son útiles y simplifican el código
- **Según el enunciado del ejercicio 3.2**: "Inclue métodos para engadir e eliminar prestamos en usuario" → **SÍ debes incluirlos**

Además, estos métodos hacen el código más legible:
```java
// Sin métodos helper (menos legible)
usuario.getPrestamos().add(prestamo);
usuario.getPrestamos().remove(prestamo);

// Con métodos helper (más legible)
usuario.agregarPrestamo(prestamo);
usuario.eliminarPrestamo(prestamo);
```

---

## Puntos Clave a Recordar

### 1. Lado Dueño de la Relación
El lado que tiene `@ManyToOne` (o `@JoinColumn` sin `mappedBy`) es el **lado dueño**. Este lado es responsable de mantener la clave foránea en la base de datos.

### 2. Cascade y OrphanRemoval
- `cascade = CascadeType.ALL`: Las operaciones en el padre se propagan a los hijos
- `orphanRemoval = true`: Si eliminas un hijo de la colección, se elimina de la base de datos

### 3. Cuidado con toString() en Relaciones Bidireccionales

**Siguiendo el enfoque del profesor:**

En el lado "Muchos" (`@ManyToOne`), puedes usar `this.usuario` o `this.libro`:
```java
// En Prestamo (lado "Muchos")
return "Prestamo{..., usuarioId=" + this.usuario + ", ejemplarId=" + this.ejemplar + "}";

// En Ejemplar (lado "Muchos")
return "Ejemplar{..., libroId=" + this.libro + "}";
```

En el lado "Uno" (`@OneToMany`), **DEBES usar `.size()`** para evitar bucles:
```java
// En Usuario (lado "Uno")
return "Usuario{..., cantidadPrestamos=" + prestamos.size() + "}";  // ✅ CORRECTO

// En Libro (lado "Uno")
return "Libro{..., cantidadEjemplares=" + ejemplares.size() + "}";  // ✅ CORRECTO
```

**❌ MAL** (causa bucle infinito):
```java
// En Usuario (lado "Uno")
return "Usuario{..., prestamos=" + prestamos + "}";  // ← Imprime la lista completa = BUCLE
```

### 4. Métodos Helper
En relaciones bidireccionales con listas, es útil crear métodos para agregar y eliminar elementos:

```java
public void agregarPrestamo(Prestamo prestamo) {
    prestamos.add(prestamo);
}

public void eliminarPrestamo(Prestamo prestamo) {
    prestamos.remove(prestamo);
}
```

Estos métodos simplifican el código cuando necesitas agregar o eliminar elementos de la colección.

### 5. Fetch Type
- `FetchType.LAZY` (por defecto en `@ManyToOne` y `@OneToOne`): Carga perezosa, se carga solo cuando se accede
- `FetchType.EAGER`: Carga inmediata junto con la entidad principal

Para colecciones (`@OneToMany`, `@ManyToMany`), el valor por defecto es `LAZY`.

---

## Orden de Implementación Recomendado

1. **Ejercicio 1 y 2** (si no están completos): Convertir `autor_id` y `categoria_id` en objetos `Autor` y `Categoria` en la clase `Libro`
2. **Ejercicio 3**: Implementar Usuario - Prestamo bidireccional
3. **Ejercicio 4**: Implementar Libro - Ejemplar bidireccional
4. **Ejercicio 5**: Simplificar Prestamo - Ejemplar a unidireccional (eliminar la referencia en Ejemplar)

---

## Pruebas Finales en App.java

**Forma completa** (con todo junto):

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

            UsuarioDAO usuarioDAO = new UsuarioDAOHib(em);
            PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
            LibroDAO libroDAO = new LibroDAOHib(em);
            EjemplarDAO ejemplarDAO = new EjemplarDAOHib(em);

            // Ejercicio 3: Usuario - Prestamo Bidireccional
            System.out.println("==== Ejercicio 3: Usuario - Prestamo Bidireccional ====");
            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
            if(usuarioOptional.isPresent()){
                System.out.println("====USUARIO ENCONTRADO====");
                System.out.println(usuarioOptional.get());
            } else {
                System.out.println("====USUARIO NO ENCONTRADO====");
            }

            Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
            if(prestamoOptional.isPresent()){
                System.out.println("====PRESTAMO ENCONTRADO====");
                System.out.println(prestamoOptional.get());
            } else {
                System.out.println("====PRESTAMO NO ENCONTRADO====");
            }

            // Ejercicio 4: Libro - Ejemplar Bidireccional
            System.out.println("\n==== Ejercicio 4: Libro - Ejemplar Bidireccional ====");
            Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
            if(libroOptional.isPresent()){
                System.out.println("====LIBRO ENCONTRADO====");
                System.out.println(libroOptional.get());
            } else {
                System.out.println("====LIBRO NO ENCONTRADO====");
            }

            Optional<Ejemplar> ejemplarOptional = ejemplarDAO.buscarPorId(1);
            if(ejemplarOptional.isPresent()){
                System.out.println("====EJEMPLAR ENCONTRADO====");
                System.out.println(ejemplarOptional.get());
            } else {
                System.out.println("====EJEMPLAR NO ENCONTRADO====");
            }

            System.out.println("\nPrograma de prueba finalizado");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

**¿Qué debe salir en consola si todo está bien?**
- Usuario mostrará `cantidadPrestamos`
- Prestamo mostrará `usuarioId` y `usuarioNombre`
- Libro mostrará `cantidadEjemplares`, `autorId`, `autorNombre`, `categoriaId`, `categoriaNombre`
- Ejemplar mostrará `libroId` y `libroTitulo`

---

## Estructura Final de las Clases

### Usuario.java
- `List<Prestamo> prestamos` con `@OneToMany(mappedBy = "usuario")`
- Métodos `agregarPrestamo()` y `eliminarPrestamo()`
- `toString()` mostrando cantidad de préstamos

### Prestamo.java
- `Usuario usuario` con `@ManyToOne`
- `Ejemplar ejemplar` con `@ManyToOne`
- `toString()` mostrando ID y nombre del usuario

### Libro.java
- `Autor autor` con `@ManyToOne`
- `Categoria categoria` con `@ManyToOne`
- `List<Ejemplar> ejemplares` con `@OneToMany(mappedBy = "libro")`
- Métodos `agregarEjemplar()` y `eliminarEjemplar()`
- `toString()` mostrando cantidad de ejemplares

### Ejemplar.java
- `Libro libro` con `@ManyToOne`
- **Sin** referencia a `Prestamo` (relación unidireccional desde Prestamo)
- `toString()` mostrando ID y título del libro

---

¡Buena suerte con la implementación!
