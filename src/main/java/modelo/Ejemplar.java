package modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column (name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column (name = "estado", nullable = false)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    @Column (name = "ubicacion", length = 100)
    private String ubicacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "libro_id", nullable = false)
    private Libro libro;

    @OneToOne(mappedBy = "ejemplar", cascade = CascadeType.ALL)
    private Prestamo prestamo;

    public enum EstadoEjemplar {DISPONIBLE, PRESTADO, MANTENIMIENTO}

    public Ejemplar() {

    }

    public Ejemplar(int id, String codigo, EstadoEjemplar estado, String ubicacion, Libro libro) {
        this.id = id;
        this.codigo = codigo;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.libro = libro;
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
                ", libro=" + libro +
                ", prestamosID=" + this.prestamo.getId() +
                '}';
    }
}
