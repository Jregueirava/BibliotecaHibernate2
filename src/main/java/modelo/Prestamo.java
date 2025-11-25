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

    // Relación @OneToOne con Usuario - LAZY: carga el Usuario solo cuando lo accedes
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "ejemplar_id", nullable = false)
    private int ejemplarId;

    // Enum dentro de la clase (como hace el profesor)
    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, RETRASADO
    }

    // Constructor vacío - OBLIGATORIO
    public Prestamo() {
    }

    // Constructor con parámetros
    public Prestamo(int id, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaDevolucion,
                    EstadoPrestamo estado, Usuario usuario, int ejemplarId) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.usuario = usuario;
        this.ejemplarId = ejemplarId;
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

    public int getEjemplarId() {
        return ejemplarId;
    }

    public void setEjemplarId(int ejemplarId) {
        this.ejemplarId = ejemplarId;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", fechaDevolucion=" + fechaDevolucion +
                ", estado=" + estado +
                ", usuarioId=" + this.usuario +
                ", ejemplarId=" + ejemplarId +
                '}';
    }
}
