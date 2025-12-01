# Mapeo Un a Moitos

| Relación                | Tipo           | Lado dueño   | Anotación                                                    |
| ----------------------- | -------------- | ------------ | ------------------------------------------------------------ |
| **Autor - Libro**       | Unidireccional | **Libro**    | `@ManyToOne` en Libro                                        |
| **Categoria - Libro**   | Unidireccional | **Libro**    | `@ManyToOne` en Libro                                        |
| **Usuario - Prestamo**  | Bidireccional  | **Prestamo** | `@ManyToOne` en Prestamo + `@OneToMany(mappedBy)` en Usuario |
| **Libro - Ejemplar**    | Unidireccional | **Ejemplar** | `@ManyToOne` en Ejemplar                                     |
| **Prestamo - Ejemplar** | Unidireccional | **Prestamo** | `@ManyToOne` en Prestamo                                     |

```java
package model;

import jakarta.persistence.*;

@Entity
@Table(name = "autor")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "nacionalidad", length = 50)
    private String nacionalidad;

    public Autor(int id, String nombre, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    public Autor() {
    }

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

```java
package model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name="descripcion", length = 255)
    private String descripcion;

    public Categoria(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Categoria() {
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}


```

```java
package model;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    @Column(name="ubicacion", length = 100)
    private String ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="libro_id", nullable = false)
    private Libro libro;

    public enum EstadoEjemplar { DISPONIBLE, PRESTADO, MANTENIMIENTO }

    public Ejemplar(int id, String codigo, EstadoEjemplar estado, String ubicacion, Libro libro) {
        this.id = id;
        this.codigo = codigo;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.libro = libro;
    }

    public Ejemplar(){

    }

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

    @Override
    public String toString() {
        return "Ejemplar{" +
                "id=" + id +
                ", codigo='A" + codigo + '\'' +
                ", estado=" + estado +
                ", ubicacion='" + ubicacion + '\'' +
                ", libroId=" + this.libro +
                '}';
    }
}


```

```java
package model;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(name= "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @Column(name = "paginas")
    private int paginas;

    @Column(name="editorial", length = 100)
    private String editorial;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy por defecto
    @JoinColumn(name = "autor_id") // FK en tabla libro
    private Autor autor;;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id") // FK en tabla libro
    private Categoria categoria;

    public Libro(int id, String isbn, String titulo, LocalDate fechaPublicacion, int paginas, String editorial, Categoria categoria, Autor autor) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.fechaPublicacion = fechaPublicacion;
        this.paginas = paginas;
        this.editorial = editorial;
        this.categoria = categoria;
        this.autor = autor;
    }

    public Libro() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "\n Libro{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                ", paginas=" + paginas +
                ", editorial='" + editorial + '\'' +
                ", autorId=" + this.autor.getId() + ", autorNombre= " + this.autor.getNombre() +
                ", categoriaId=" + this.categoria +
                '}';
    }
}
```

```java
package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name="fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name="fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private EstadoPrestamo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ejemplar_id", nullable = false)
    private Ejemplar ejemplar;

    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, RETRASADO
    }

    public Prestamo() {}

    public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaDevolucion,
                    EstadoPrestamo estado, Usuario usuario, Ejemplar ejemplar) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.usuario =usuario;
        this.ejemplar = ejemplar;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

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
        return "\n Prestamo{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", fechaDevolucion=" + fechaDevolucion +
                ", estado=" + estado +
                ", usuarioId=" + this.usuario +
                ", ejemplarId=" + this.ejemplar +
                '}';
    }
}


```

```java
package model;


import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "dni", unique = true, nullable = false, length = 20)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestamo> prestamos = new ArrayList<>();

    // Constructores
    public Usuario() {}

    public Usuario(String dni, String nombre, String apellidos, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fecha) {
        this.fechaNacimiento = LocalDate.parse(fecha);
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Prestamo> getPrestamos() { return prestamos; }
    public void setPrestamos(List<Prestamo> prestamos) { this.prestamos = prestamos; }

    // Helper methods para mantener la relación
    public void addPrestamo(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    public void removePrestamo(Prestamo prestamo) {
        prestamos.remove(prestamo);
    }

    @Override
    public String toString() {
        return "\n Usuario{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", fechaRegistro=" + fechaRegistro +
                ", cantidadPrestamos= "+this.prestamos.size() +
                '}';
    }
}

```

```java
import DAO.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import model.Ejemplar;
import model.Libro;
import model.Prestamo;
import model.Usuario;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.Optional;

public class App {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        try(EntityManager em = Persistence.createEntityManagerFactory("biblioteca").createEntityManager()){

            LibroDAO libroDAO = new LibroDAOImpl(em);
            PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
            EjemplarDAO ejemplarDAO = new EjemplarDAOHib(em);
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(em);

            Optional<Libro> libro = libroDAO.buscarPorId(1);
            if (libro.isPresent()){// ← Verás
                System.out.println("====LIBRO CON CATEGORIA Y AUTOR=====");
                System.out.println(libro.get());

            } else {
                System.out.println("Libro no encontrado");
            }

            Optional<Prestamo> prestamo = prestamoDAO.buscarPorId(1);
            if (prestamo.isPresent()){
                System.out.println("====PRESTAMO CON USUARIO Y EJEMPLAR=====");
                System.out.println(prestamo.get());

            } else {
                System.out.println("Prestamo no encontrado");
            }

            Optional<Ejemplar> ejemplar = ejemplarDAO.buscarPorId(1);
            if (ejemplar.isPresent()){
                System.out.println("====EJEMPLAR CON LIBRO=====");

                System.out.println(ejemplar.get());

            } else {
                System.out.println("Ejemplar no encontrado");
            }

            Optional<Usuario> usuarioRecuperado = usuarioDAO.buscarPorId(1);
            if (usuarioRecuperado.isPresent()){
                System.out.println("====USUARIO CON PRESTAMOS=====");

                System.out.println(usuarioRecuperado.get());

            } else {
                System.out.println("Ejemplar no encontrado");
            }


        } catch (Exception e) {
            System.out.println("Error inicializando en EntityManager:" + e.getMessage());
        }
    }
}

```
