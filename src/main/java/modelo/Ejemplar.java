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

    @Column (name = "libro_id", nullable = false)
    private int libro_id;

    public enum EstadoEjemplar {DISPONIBLE, PRESTADO, MANTENIMIENTO}

    public Ejemplar() {

    }

    public Ejemplar(int id, String codigo, EstadoEjemplar estado, String ubicacion, int libro_id) {
        this.id = id;
        this.codigo = codigo;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.libro_id = libro_id;
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

    public int getLibro_id() {
        return libro_id;
    }

    public void setLibro_id(int libro_id) {
        this.libro_id = libro_id;
    }

    @Override
    public String toString() {
        return "Ejemplar{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", estado=" + estado +
                ", ubicacion='" + ubicacion + '\'' +
                ", libro_id=" + libro_id +
                '}';
    }
}
