# 📚 Mapeo de Relaciones con Hibernate - Sistema Biblioteca

**Fecha**: 2 de diciembre de 2025
**Proyecto**: BibliotecaHibernate
**Objetivo**: Implementar y comprender mapeos de relaciones One-to-Many y Many-to-Many con JPA/Hibernate

---

## 📋 Índice

1. [Resumen de la Sesión](#resumen-de-la-sesión)
2. [Mapeo One-to-Many (1:N)](#mapeo-one-to-many-1n)
3. [Mapeo Many-to-Many (N:N)](#mapeo-many-to-many-nn)
4. [Corrección de Errores](#corrección-de-errores)
5. [Conceptos Clave Aprendidos](#conceptos-clave-aprendidos)

---

## 🎯 Resumen de la Sesión

Durante esta sesión se implementaron y corrigieron varios ejercicios relacionados con el mapeo de relaciones en Hibernate:

### Ejercicios Completados

**Ejercicio 3**: Mapeo One-to-Many unidireccional entre Autor y Libros
**Ejercicio 4**: Mapeo One-to-Many unidireccional entre Categoria y Libros
**Ejercicio 5**: Mapeo One-to-Many bidireccional entre Usuario y Prestamo (con métodos auxiliares)
**Ejercicio 6**: Mapeo One-to-Many unidireccional entre Libro y Ejemplar
**Ejercicio 7**: Mapeo One-to-Many unidireccional entre Prestamo y Ejemplar
**Ejercicio 1 (N:N)**: Mapeo Many-to-Many bidireccional entre Usuario y Libro (tabla favoritos)

---

## 🔗 Mapeo One-to-Many (1:N)

### Concepto

Una relación **1:N** indica que un registro de una tabla puede estar relacionado con múltiples registros de otra tabla, pero cada registro de la segunda tabla pertenece a un solo registro de la primera.

### Implementaciones Realizadas

#### 1. Usuario - Prestamo (Bidireccional)

Esta fue la implementación más compleja, ya que requirió sincronización en ambos lados de la relación.

**Lado Propietario: Prestamo**
```java
@Entity
@Table(name = "prestamo")
public class Prestamo {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Getters y setters...
}
```

**Lado Inverso: Usuario**
```java
@Entity
@Table(name = "usuario")
public class Usuario {
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestamo> prestamos = new ArrayList<>();

    // Métodos auxiliares para mantener la sincronización
    public void agregarPrestamo(Prestamo p) {
        prestamos.add(p);
        p.setUsuario(this);  // Sincroniza el otro lado
    }

    public void eliminarPrestamo(Prestamo p) {
        prestamos.remove(p);
        p.setUsuario(null);  // Sincroniza el otro lado
    }
}
```

**Modificación del toString() en Usuario**
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
            ", cantidadPrestamos=" + prestamos.size() +  // ← Nueva línea
            ", cantidadLibros=" + this.librosFavoritos.size() +
            '}';
}
```

#### 2. Autor - Libro (Unidireccional)

**Lado Propietario: Libro**
```java
@Entity
@Table(name = "libro")
public class Libro {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Autor autor;
}
```

**Nota**: Autor no tiene referencia a la lista de libros (unidireccional).

#### 3. Categoria - Libro (Unidireccional)

**Lado Propietario: Libro**
```java
@Entity
@Table(name = "libro")
public class Libro {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
```

#### 4. Libro - Ejemplar (Unidireccional)

**Lado Propietario: Ejemplar**
```java
@Entity
@Table(name = "ejemplar")
public class Ejemplar {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id")
    private Libro libro;
}
```

#### 5. Prestamo - Ejemplar (Unidireccional)

**Lado Propietario: Prestamo**
```java
@Entity
@Table(name = "prestamo")
public class Prestamo {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejemplar_id")
    private Ejemplar ejemplar;
}
```

---

## 🔄 Mapeo Many-to-Many (N:N)

### Concepto

Una relación **N:N** indica que varios registros de una tabla pueden estar relacionados con varios registros de otra tabla. Esta relación requiere una **tabla intermedia** para almacenar las asociaciones.

### Implementación: Usuario - Libro (Favoritos)

#### Tabla Intermedia en Base de Datos

**Archivo**: `docker/scripts/init.sql`

```sql
CREATE TABLE favoritos (
    usuario_id int NOT NULL,
    libro_id int NOT NULL,
    PRIMARY KEY (usuario_id, libro_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (libro_id) REFERENCES libro(id)
);

-- Datos de prueba
INSERT INTO favoritos (usuario_id, libro_id) VALUES (1, 3);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (1, 1);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (2, 4);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (2, 5);
```

**Características**:
- Clave primaria compuesta: `(usuario_id, libro_id)`
- Dos claves foráneas que referencian a `usuario` y `libro`
- Evita duplicados gracias a la PRIMARY KEY

#### Lado Propietario: Usuario

**Archivo**: `src/main/java/modelo/Usuario.java`

```java
@Entity
@Table(name = "usuario")
public class Usuario {
    // ... otros atributos ...

    @ManyToMany
    @JoinTable(
        name = "favoritos",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "libro_id")
    )
    private List<Libro> librosFavoritos;

    public Usuario() {
        prestamos = new ArrayList<>();
        librosFavoritos = new ArrayList<>();  // ← Importante inicializar
    }

    public Usuario(String dni, String nombre, String apellidos, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        prestamos = new ArrayList<>();
        this.librosFavoritos = new ArrayList<>();  // ← Importante inicializar
    }

    // Métodos para gestionar favoritos
    public void addLibro(Libro libro) {
        this.librosFavoritos.add(libro);
    }

    public void removeLibro(Libro libro) {
        this.librosFavoritos.remove(libro);
    }

    public List<Libro> getLibrosFavoritos() {
        return librosFavoritos;
    }

    public void setLibrosFavoritos(List<Libro> librosFavoritos) {
        this.librosFavoritos = librosFavoritos;
    }
}
```

**Anotaciones clave**:
- `@ManyToMany`: Define la relación muchos a muchos
- `@JoinTable`: Define la tabla intermedia y sus columnas
- `joinColumns`: Columna que referencia a esta entidad (Usuario)
- `inverseJoinColumns`: Columna que referencia a la otra entidad (Libro)

#### Lado Inverso: Libro

**Archivo**: `src/main/java/modelo/Libro.java`

```java
@Entity
@Table(name = "libro")
public class Libro {
    // ... otros atributos ...

    @ManyToMany(mappedBy = "librosFavoritos")
    private List<Usuario> listaUsuariosFavoritos = new ArrayList<>();

    public Libro() {
        this.listaUsuariosFavoritos = new ArrayList<>();  // ← Inicializar
    }

    public Libro(int id, String isbn, String titulo, LocalDate fecha_publicacion,
                 int paginas, String editorial, Autor autor, Categoria categoria) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.fecha_publicacion = fecha_publicacion;
        this.paginas = paginas;
        this.editorial = editorial;
        this.autor = autor;
        this.categoria = categoria;
        this.listaUsuariosFavoritos = new ArrayList<>();  // ← Inicializar
    }

    // Métodos para consultar usuarios
    public void addUsuario(Usuario usuario) {
        this.listaUsuariosFavoritos.add(usuario);
    }

    public void removeUsuario(Usuario usuario) {
        this.listaUsuariosFavoritos.remove(usuario);
    }

    public List<Usuario> getUsuariosFavoritos() {
        return listaUsuariosFavoritos;
    }

    public void setUsuariosFavoritos(List<Usuario> usuariosFavoritos) {
        this.listaUsuariosFavoritos = usuariosFavoritos;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", fecha_publicacion=" + fecha_publicacion +
                ", paginas=" + paginas +
                ", editorial='" + editorial + '\'' +
                ", autor=" + autor.getId() +
                ", categoria=" + categoria.getId() +
                ", usuariosFavoritos=" + this.listaUsuariosFavoritos.size() +  // ← Nueva línea
                '}';
    }
}
```

**Anotación clave**:
- `mappedBy = "librosFavoritos"`: Indica que esta es la parte inversa de la relación. El atributo `librosFavoritos` en Usuario es el propietario.

#### Uso en App.java

**Archivo**: `src/main/java/App.java`

```java
// 1. Consultar un Libro y sus Usuarios favoritos
Optional<Libro> optLibro = libroDAO.buscarPorId(1);
if(optLibro.isPresent()){
    Libro l = optLibro.get();
    System.out.println(l);
    for (Usuario u: l.getUsuariosFavoritos()){
        System.out.println("\t USUARIO");
        System.out.println(u);
    }
}

// 2. Consultar un Usuario y sus Libros favoritos
Optional<Usuario> optusuario = usuarioDAO.buscarPorId(1);
if(optusuario.isPresent()){
    Usuario u = optusuario.get();
    System.out.println(u);
    for (Libro l: u.getLibrosFavoritos()){
        System.out.println("\t LIBRO");
        System.out.println(l);
    }
}

// 3. Crear un nuevo Libro y añadirlo como favorito a un Usuario
Optional<Autor> optAutor = autorDAO.buscarPorId(1);
Optional<Categoria> optCategoria = categoriaDAO.buscarPorId(1);
if(optAutor.isPresent() && optCategoria.isPresent()){
    Libro libro1 = new Libro(-1, "978-0132350889", "Nuevo Libro",
            LocalDate.now(), 100, "Editorial F.Wirtz",
            optAutor.get(), optCategoria.get());
    libro1 = libroDAO.actualizarLibro(libro1);  // Primero guardar el libro

    if(optusuario.isPresent()){
        Usuario u = optusuario.get();
        u.addLibro(libro1);  // ← Modificar el lado propietario (Usuario)
        usuarioDAO.actualizarUsuario(u);  // ← Guardar cambios
    }
}

// 4. Crear un nuevo Usuario y añadirle un Libro favorito
if(optLibro.isPresent()){
    Libro libro2 = optLibro.get();
    Usuario u2 = new Usuario("12345679T", "Pepe",
                             "Perez", "pepeperez2@gmail.com");
    u2 = usuarioDAO.actualizarUsuario(u2);  // Primero guardar el usuario
    u2.addLibro(libro2);  // ← Modificar el lado propietario (Usuario)
    u2 = usuarioDAO.actualizarUsuario(u2);  // ← Guardar cambios
}
```

---

## 🔧 Corrección de Errores

### Error 1: @Column en lugar de @JoinColumn

**Problema Original** (línea 33 en `Libro.java`):
```java
@ManyToOne(fetch = FetchType.LAZY)
@Column(name = "autor_id")  // ❌ INCORRECTO
private Autor autor;
```

**Mensaje de Error**:
```
org.hibernate.AnnotationException: Property 'modelo.Libro.autor' is a '@ManyToOne'
association and may not use '@Column' to specify column mappings
(use '@JoinColumn' instead)
```

**Solución**:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "autor_id")  // ✅ CORRECTO
private Autor autor;
```

**Explicación**:
- `@Column`: Se usa para mapear **atributos simples** (String, int, LocalDate, etc.)
- `@JoinColumn`: Se usa para mapear **relaciones entre entidades** (@ManyToOne, @OneToOne, etc.)

### Error 2: Modificación del lado inverso en @ManyToMany

**Problema Original** (líneas 63-70 en `App.java`):
```java
if(optLibro.isPresent()){
    Libro libro2 = optLibro.get();
    Usuario u2 = new Usuario("12345678T", "Pepe",
                             "Perez", "pepeperez@gmail.com");
    u2 = usuarioDAO.actualizarUsuario(u2);
    libro2.addUsuario(u2);  // ❌ Modificar Libro (lado inverso)
    libro2 = libroDAO.actualizarLibro(libro2);  // ❌ NO se guarda en BD
}
```

**Síntoma**:
- No se insertaba el registro en la tabla `favoritos`
- No había errores en consola
- El código parecía ejecutarse correctamente

**Causa**:
En una relación bidireccional `@ManyToMany`:
- **Usuario** tiene `@JoinTable` → es el **lado propietario** ✅
- **Libro** tiene `mappedBy` → es el **lado inverso** ❌

**Hibernate SOLO sincroniza cambios cuando modificas el lado propietario**.

**Solución**:
```java
if(optLibro.isPresent()){
    Libro libro2 = optLibro.get();
    Usuario u2 = new Usuario("12345679T", "Pepe",
                             "Perez", "pepeperez2@gmail.com");
    u2 = usuarioDAO.actualizarUsuario(u2);
    u2.addLibro(libro2);  // ✅ Modificar Usuario (lado propietario)
    u2 = usuarioDAO.actualizarUsuario(u2);  // ✅ SÍ se guarda en BD
}
```

**Explicación**:
- Modificar `usuario.addLibro(libro)` → ✅ Se guarda en la tabla `favoritos`
- Modificar `libro.addUsuario(usuario)` → ❌ NO se guarda (lado inverso con `mappedBy`)

---

## 💡 Conceptos Clave Aprendidos

### 1. Relaciones Unidireccionales vs Bidireccionales

**Unidireccional**: Solo una entidad conoce la relación
```java
// Libro conoce a Autor, pero Autor no conoce sus libros
@Entity
public class Libro {
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;
}
```

**Bidireccional**: Ambas entidades conocen la relación
```java
// Usuario conoce sus libros favoritos
@Entity
public class Usuario {
    @ManyToMany
    @JoinTable(...)
    private List<Libro> librosFavoritos;
}

// Libro conoce qué usuarios lo tienen como favorito
@Entity
public class Libro {
    @ManyToMany(mappedBy = "librosFavoritos")
    private List<Usuario> usuariosFavoritos;
}
```

### 2. Lado Propietario vs Lado Inverso

**Lado Propietario**:
- Define la tabla intermedia con `@JoinTable`
- Controla las operaciones de persistencia
- Los cambios aquí SE GUARDAN en la base de datos

**Lado Inverso**:
- Usa `mappedBy` para referenciar al propietario
- Solo para consultas y navegación
- Los cambios aquí NO SE GUARDAN en la base de datos

### 3. Cascade y OrphanRemoval

```java
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Prestamo> prestamos;
```

**CascadeType.ALL**:
- Propaga todas las operaciones (persist, merge, remove, etc.)
- Si guardas/borras un Usuario, también se guardan/borran sus Prestamos

**orphanRemoval = true**:
- Si eliminas un Prestamo de la lista, se borra de la BD automáticamente
- Solo tiene sentido en relaciones donde el "hijo" no puede existir sin el "padre"

### 4. FetchType.LAZY vs EAGER

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "autor_id")
private Autor autor;
```

**LAZY (por defecto en @ManyToOne)**:
- No carga la entidad relacionada hasta que se accede a ella
- Mejora el rendimiento inicial
- Puede causar excepciones si se accede fuera de la sesión de Hibernate

**EAGER**:
- Carga la entidad relacionada inmediatamente
- Más simple de usar
- Puede causar problemas de rendimiento con muchas relaciones

### 5. Inicialización de Listas

**Siempre inicializar colecciones en constructores**:
```java
public Usuario() {
    prestamos = new ArrayList<>();
    librosFavoritos = new ArrayList<>();  // ← MUY IMPORTANTE
}
```

**Por qué es importante**:
- Evita `NullPointerException`
- Hibernate puede reemplazar la lista con su propia implementación (PersistentBag)
- Buena práctica para evitar errores

### 6. Métodos Auxiliares para Sincronización

En relaciones bidireccionales, es recomendable crear métodos que sincronicen ambos lados:

```java
// En Usuario
public void agregarPrestamo(Prestamo p) {
    prestamos.add(p);
    p.setUsuario(this);  // Sincroniza el otro lado
}

public void eliminarPrestamo(Prestamo p) {
    prestamos.remove(p);
    p.setUsuario(null);  // Sincroniza el otro lado
}
```

**Ventajas**:
- Mantiene la consistencia
- Evita errores de sincronización
- Facilita el uso correcto de la relación

---

## 📊 Diagrama de Relaciones Implementadas

```
┌─────────────┐
│   Autor     │
└─────────────┘
       │
       │ 1:N (unidireccional)
       │
       ▼
┌─────────────┐        1:N (unidireccional)        ┌──────────────┐
│  Categoria  │◄────────────────────────────────────│   Libro      │
└─────────────┘                                     └──────────────┘
                                                           │
                                                           │ 1:N (unidireccional)
                                                           │
                                                           ▼
                                                    ┌──────────────┐
                                                    │  Ejemplar    │
                                                    └──────────────┘
                                                           │
                                                           │ N:1 (unidireccional)
                                                           │
                                                           ▼
┌─────────────┐        1:N (bidireccional)        ┌──────────────┐
│  Usuario    │◄──────────────────────────────────│  Prestamo    │
└─────────────┘                                    └──────────────┘
       ▲
       │
       │ N:N (bidireccional - tabla favoritos)
       │
       ▼
┌─────────────┐
│   Libro     │
└─────────────┘
```

---

## ✅ Checklist de Verificación

- [x] Ejercicio 3: Relación Autor-Libro implementada
- [x] Ejercicio 4: Relación Categoria-Libro implementada
- [x] Ejercicio 5: Relación Usuario-Prestamo bidireccional implementada
- [x] Ejercicio 6: Relación Libro-Ejemplar implementada
- [x] Ejercicio 7: Relación Prestamo-Ejemplar implementada
- [x] Ejercicio 1 (N:N): Tabla favoritos creada en init.sql
- [x] Ejercicio 1 (N:N): Entidad Usuario mapeada con @ManyToMany
- [x] Ejercicio 1 (N:N): Entidad Libro mapeada con mappedBy
- [x] Error @Column corregido a @JoinColumn
- [x] Error lado inverso N:N corregido
- [x] Métodos toString() actualizados
- [x] Constructores con inicialización de listas
- [x] Métodos auxiliares add/remove implementados
- [x] Pruebas en App.java funcionando correctamente

---

## 🎓 Conclusión

Durante esta sesión se implementaron con éxito todos los mapeos de relaciones del sistema de biblioteca:

1. **Relaciones 1:N Unidireccionales**: Autor-Libro, Categoria-Libro, Libro-Ejemplar, Prestamo-Ejemplar
2. **Relaciones 1:N Bidireccionales**: Usuario-Prestamo (con sincronización)
3. **Relaciones N:N Bidireccionales**: Usuario-Libro (tabla favoritos)

Se aprendió la diferencia crítica entre:
- `@Column` vs `@JoinColumn`
- Lado propietario vs lado inverso
- Modificaciones que se persisten vs modificaciones que no se persisten

**Regla de oro para @ManyToMany bidireccional**:
> Siempre modifica el lado propietario (el que tiene @JoinTable) para que los cambios se persistan en la base de datos.

---

**Documento generado**: 2 de diciembre de 2025
**Commit de referencia**: `a5a237e`
**Archivos modificados**:
- `docker/scripts/init.sql`
- `src/main/java/App.java`
- `src/main/java/modelo/Libro.java`
- `src/main/java/modelo/Usuario.java`
