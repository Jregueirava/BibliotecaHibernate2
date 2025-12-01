# Ejercicio 1: Mapeo Muchos a Muchos (N:N) - Favoritos de Usuarios

## Contexto del ejercicio

El objetivo es crear una relación **muchos a muchos** entre `Usuario` y `Libro`, donde:
- Un usuario puede tener muchos libros favoritos
- Un libro puede ser favorito de muchos usuarios

Esta relación se implementa mediante una **tabla intermedia** llamada `favoritos` que contendrá las claves foráneas de ambas tablas.

---

## Paso 1: Crear la tabla `favoritos` en la base de datos

Primero necesitas ejecutar este script SQL en tu base de datos para crear la tabla intermedia:

```sql
CREATE TABLE favoritos (
    usuario_id INT NOT NULL,
    libro_id INT NOT NULL,
    PRIMARY KEY (usuario_id, libro_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (libro_id) REFERENCES libro(id) ON DELETE CASCADE
);

-- Datos de ejemplo: Usuario 1 marca varios libros como favoritos
INSERT INTO favoritos (usuario_id, libro_id) VALUES (1, 1);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (1, 3);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (1, 5);

-- Usuario 2 marca favoritos
INSERT INTO favoritos (usuario_id, libro_id) VALUES (2, 2);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (2, 4);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (2, 6);

-- Usuario 3 marca favoritos (incluye algún libro repetido con usuario 1)
INSERT INTO favoritos (usuario_id, libro_id) VALUES (3, 1);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (3, 7);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (3, 9);

-- Usuario 4 marca favoritos
INSERT INTO favoritos (usuario_id, libro_id) VALUES (4, 10);
INSERT INTO favoritos (usuario_id, libro_id) VALUES (4, 8);
```

**Nota importante:** Esta tabla NO necesita una clase de entidad propia en Java. Hibernate la manejará automáticamente gracias a la anotación `@ManyToMany`.

---

## Paso 2: Modificar la clase `Usuario.java`

Ahora tienes que añadir la relación muchos a muchos en tu clase `Usuario`. Aquí están los cambios que necesitas hacer:

### 2.1. Importar List y ArrayList

```java
import java.util.List;
import java.util.ArrayList;
```

### 2.2. Añadir el atributo `librosFavoritos`

Después del atributo `prestamo` (alrededor de la línea 38), añade:

```java
@ManyToMany
@JoinTable(
    name = "favoritos",
    joinColumns = @JoinColumn(name = "usuario_id"),
    inverseJoinColumns = @JoinColumn(name = "libro_id")
)
private List<Libro> librosFavoritos;
```

**Explicación de las anotaciones:**
- `@ManyToMany`: Define la relación muchos a muchos
- `@JoinTable`: Especifica la tabla intermedia que almacena las relaciones
  - `name = "favoritos"`: Nombre de la tabla intermedia
  - `joinColumns`: Define la FK de la tabla actual (Usuario → usuario_id)
  - `inverseJoinColumns`: Define la FK de la tabla relacionada (Libro → libro_id)

### 2.3. Inicializar la lista en los constructores

**En el constructor vacío** (línea 47):
```java
public Usuario() {
    librosFavoritos = new ArrayList<>();
}
```

**En el constructor parametrizado** (línea 40):
```java
public Usuario(String dni, String nombre, String apellidos, String email) {
    this.dni = dni;
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.email = email;
    librosFavoritos = new ArrayList<>();
}
```

### 2.4. Añadir getters y setters

Al final de la clase, antes del `toString()`, añade:

```java
public List<Libro> getLibrosFavoritos() {
    return librosFavoritos;
}

public void setLibrosFavoritos(List<Libro> librosFavoritos) {
    this.librosFavoritos = librosFavoritos;
}
```

### 2.5. Añadir métodos helper (opcional pero recomendado)

Estos métodos facilitan añadir y eliminar favoritos de forma más natural:

```java
public void addFavorito(Libro libro) {
    this.librosFavoritos.add(libro);
}

public void removeFavorito(Libro libro) {
    this.librosFavoritos.remove(libro);
}
```

### 2.6. Actualizar el `toString()`

Como tienes una lista de libros favoritos, puedes modificar el toString para mostrar la cantidad:

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
            ", cantidadFavoritos=" + this.librosFavoritos.size() +
            '}';
}
```

**Importante:** Evita imprimir directamente `librosFavoritos` en el toString para evitar bucles infinitos.

---

## Paso 3: Modificar la clase `Libro.java`

Ahora necesitas añadir el lado inverso de la relación en `Libro`.

### 3.1. Importar List y ArrayList

```java
import java.util.List;
import java.util.ArrayList;
```

### 3.2. Añadir el atributo `usuariosFavoritos`

Como tu clase `Libro` actualmente tiene atributos simples (`autor_id`, `categoria_id`) y no relaciones @ManyToOne, añade después del atributo `categoria_id`:

```java
@ManyToMany(mappedBy = "librosFavoritos")
private List<Usuario> usuariosFavoritos;
```

**Explicación:**
- `@ManyToMany(mappedBy = "librosFavoritos")`: Indica que esta es la relación inversa
- `mappedBy`: Hace referencia al nombre del atributo en la clase `Usuario` que contiene la configuración principal de la relación

### 3.3. Inicializar la lista en los constructores

**En el constructor vacío:**
```java
public Libro() {
    usuariosFavoritos = new ArrayList<>();
}
```

**En el constructor parametrizado** (actualiza el existente):
```java
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
    usuariosFavoritos = new ArrayList<>();
}
```

### 3.4. Añadir getters y setters

```java
public List<Usuario> getUsuariosFavoritos() {
    return usuariosFavoritos;
}

public void setUsuariosFavoritos(List<Usuario> usuariosFavoritos) {
    this.usuariosFavoritos = usuariosFavoritos;
}
```

### 3.5. Actualizar el `toString()` (opcional)

Puedes añadir la cantidad de usuarios que tienen este libro como favorito:

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
            ", autor_id=" + autor_id +
            ", categoria_id=" + categoria_id +
            ", cantidadUsuariosFavoritos=" + this.usuariosFavoritos.size() +
            '}';
}
```

---

## Paso 4: Probar la relación en `App.java`

Ahora vamos a crear pruebas en tu clase `App` para verificar que la relación funciona correctamente.

### 4.1. Test 1: Obtener los libros favoritos de un usuario

```java
System.out.println("==== Test 1: Libros Favoritos de Usuario ====");
Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
if (usuarioOptional.isPresent()) {
    Usuario usuario = usuarioOptional.get();
    System.out.println("Usuario encontrado: " + usuario.getNombre());
    System.out.println("Cantidad de favoritos: " + usuario.getLibrosFavoritos().size());

    System.out.println("==== Libros Favoritos del Usuario ====");
    for (Libro libro : usuario.getLibrosFavoritos()) {
        System.out.println("- " + libro.getTitulo() + " (ISBN: " + libro.getIsbn() + ")");
    }
} else {
    System.out.println("Usuario no encontrado");
}
```

### 4.2. Test 2: Obtener los usuarios que tienen un libro como favorito

```java
System.out.println("\n==== Test 2: Usuarios que tienen el libro como favorito ====");
Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
if (libroOptional.isPresent()) {
    Libro libro = libroOptional.get();
    System.out.println("Libro encontrado: " + libro.getTitulo());
    System.out.println("Cantidad de usuarios que lo tienen como favorito: "
                       + libro.getUsuariosFavoritos().size());

    System.out.println("==== Usuarios con este libro favorito ====");
    for (Usuario usuario : libro.getUsuariosFavoritos()) {
        System.out.println("- " + usuario.getNombre() + " " + usuario.getApellidos()
                           + " (DNI: " + usuario.getDni() + ")");
    }
} else {
    System.out.println("Libro no encontrado");
}
```

### 4.3. Ejemplo completo en App.java

Aquí tienes un ejemplo de cómo quedaría la clase `App` con las pruebas:

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
            LibroDAO libroDAO = new LibroDAOHib(em);

            // Test 1: Libros favoritos de un usuario
            System.out.println("==== Test 1: Libros Favoritos de Usuario ====");
            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                System.out.println("Usuario encontrado: " + usuario.getNombre());
                System.out.println("Cantidad de favoritos: " + usuario.getLibrosFavoritos().size());

                System.out.println("==== Libros Favoritos del Usuario ====");
                for (Libro libro : usuario.getLibrosFavoritos()) {
                    System.out.println("- " + libro.getTitulo() + " (ISBN: " + libro.getIsbn() + ")");
                }
            } else {
                System.out.println("Usuario no encontrado");
            }

            // Test 2: Usuarios que tienen un libro como favorito
            System.out.println("\n==== Test 2: Usuarios que tienen el libro como favorito ====");
            Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
            if (libroOptional.isPresent()) {
                Libro libro = libroOptional.get();
                System.out.println("Libro encontrado: " + libro.getTitulo());
                System.out.println("Cantidad de usuarios que lo tienen como favorito: "
                                   + libro.getUsuariosFavoritos().size());

                System.out.println("==== Usuarios con este libro favorito ====");
                for (Usuario usuario : libro.getUsuariosFavoritos()) {
                    System.out.println("- " + usuario.getNombre() + " " + usuario.getApellidos()
                                       + " (DNI: " + usuario.getDni() + ")");
                }
            } else {
                System.out.println("Libro no encontrado");
            }

            System.out.println("\nPrograma de prueba finalizado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

## Conceptos clave del Mapeo Muchos a Muchos

### 1. Tabla intermedia
- La tabla `favoritos` es una **tabla de unión** (join table)
- Contiene dos claves foráneas: `usuario_id` y `libro_id`
- La clave primaria es compuesta (ambas columnas juntas)
- **No necesita una clase de entidad en Java**

### 2. Anotación @ManyToMany
- Se coloca en ambos lados de la relación
- Un lado tiene `@JoinTable` (lado propietario - Usuario)
- El otro lado tiene `mappedBy` (lado inverso - Libro)

### 3. Lado propietario vs lado inverso
- **Lado propietario (Usuario)**: Define la tabla intermedia con `@JoinTable`
- **Lado inverso (Libro)**: Usa `mappedBy` para referenciar el atributo del lado propietario

### 4. Fetch Type por defecto
- `@ManyToMany` usa `FetchType.LAZY` por defecto
- Los libros favoritos solo se cargan cuando accedes a `getLibrosFavoritos()`
- Los usuarios favoritos solo se cargan cuando accedes a `getUsuariosFavoritos()`

---

## Diferencias con tu código actual

### En Usuario.java:
- **Cambio principal:** Añadir relación @ManyToMany con Libro
- **Mantienes:** Tu relación @OneToOne con Prestamo
- **Nota:** En la solución del profesor hay una relación @OneToMany entre Usuario y Prestamo, pero tú tienes @OneToOne. Mantén tu implementación actual.

### En Libro.java:
- **Cambio principal:** Añadir relación @ManyToMany con Usuario
- **Mantienes:** Tus atributos `autor_id` y `categoria_id` (sin relaciones @ManyToOne)
- **Nota:** La solución del profesor usa relaciones @ManyToOne, pero tú usas IDs directos. Mantén tu implementación.

---

## Resumen de archivos a modificar

1. **Script SQL:** Ejecutar en la base de datos para crear tabla `favoritos`
2. **Usuario.java:** Añadir atributo `librosFavoritos` con @ManyToMany
3. **Libro.java:** Añadir atributo `usuariosFavoritos` con @ManyToMany(mappedBy)
4. **App.java:** Añadir pruebas para verificar la relación

---

## Posibles errores y soluciones

### Error 1: LazyInitializationException
**Problema:** Intentas acceder a `librosFavoritos` fuera de la sesión de Hibernate

**Solución:** Asegúrate de acceder a las listas dentro del bloque try-with-resources donde está el EntityManager

### Error 2: StackOverflowError en toString()
**Problema:** Imprimes directamente las listas completas en toString()

**Solución:** Usa `.size()` para mostrar solo la cantidad, no la lista completa

### Error 3: Tabla favoritos no existe
**Problema:** No ejecutaste el script SQL

**Solución:** Ejecuta el script SQL del Paso 1 en tu base de datos

---

## Verificación final

Una vez hayas implementado todo, deberías poder:
1. ✓ Obtener todos los libros favoritos de un usuario
2. ✓ Obtener todos los usuarios que tienen un libro como favorito
3. ✓ Ver la cantidad de favoritos en el toString() de Usuario
4. ✓ Ver la cantidad de usuarios favoritos en el toString() de Libro

---

**¡Listo!** Siguiendo estos pasos habrás completado el ejercicio de Mapeo Muchos a Muchos manteniendo tu estilo de código y nomenclatura.
