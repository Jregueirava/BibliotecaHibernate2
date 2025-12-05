package modelo;

import jakarta.persistence.*;

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

    @Column(name = "apellidos",  nullable = false, length = 100)
    private String apellidos;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fecha_nacimiento;

    @Column(name = "fecha_registro")
    private LocalDateTime fecha_registro;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestamo> prestamos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "libro_id")
    )

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibroCategoria> valoracionesLibros = new ArrayList<>();

    private List<Libro> librosFavoritos;

    public Usuario() {
        prestamos = new ArrayList<>();
        librosFavoritos = new ArrayList<>();
        valoracionesLibros = new ArrayList<>();
    }

    public Usuario(String dni, String nombre, String apellidos, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        prestamos = new ArrayList<>();
        this.librosFavoritos = new ArrayList<>();
    }

    public void agregarPrestamo(Prestamo p) {
        prestamos.add(p);
    }

    public void eliminarPrestamo(Prestamo p) {
        prestamos.remove(p);
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

    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(LocalDate fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public LocalDateTime getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDateTime fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    public List<Libro> getLibrosFavoritos() {
        return librosFavoritos;
    }

    public void setLibrosFavoritos(List<Libro> librosFavoritos) {
        this.librosFavoritos = librosFavoritos;
    }

    public void addFavorito(Libro libro){
        this.librosFavoritos.add(libro);
    }

    public void removeFavorito(Libro libro){
        this.librosFavoritos.remove(libro);
    }
    public void addLibro(Libro libro){
        this.librosFavoritos.add(libro);
    }

    public void removeLibro(Libro libro) {
        this.librosFavoritos.remove(libro);
    }

    public List<LibroCategoria> getValoracionesLibros() {
        return valoracionesLibros;
    }

    public void setValoracionesLibros(List<LibroCategoria> valoracionesLibros) {
        this.valoracionesLibros = valoracionesLibros;
    }

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
                ", cantidadLibros=" + this.librosFavoritos.size() +
                '}';
    }
}
