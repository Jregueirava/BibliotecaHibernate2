package modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "libro_categoria")
@IdClass(LibroCategoriaId.class )
public class LibroCategoria {
    @Id
    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;

    @Id
    @Column(name = "libro_id", nullable = false)
    private int libroId;

    @Id
    @Column(name = "categoria_id", nullable = false)
    private int categoriaId;

    @Column(name = "puntuacion", nullable = false)
    private int puntuacion = 0;


    //Relaciones

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", insertable = false, updatable = false)
    private Libro libro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false)
    private Categoria categoria;


    public LibroCategoria() {
    }

    public LibroCategoria(int usuarioId, Categoria categoria, Libro libro, Usuario usuario, int puntuacion, int categoriaId, int libroId) {
        this.usuarioId = usuarioId;
        this.categoria = categoria;
        this.libro = libro;
        this.usuario = usuario;
        this.puntuacion = puntuacion;
        this.categoriaId = categoriaId;
        this.libroId = libroId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getLibroId() {
        return libroId;
    }

    public void setLibroId(int libroId) {
        this.libroId = libroId;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    @Override
    public String toString() {
        return "LibroCategoria{" +
                "usuarioId=" + usuarioId +
                ", libroId=" + libroId +
                ", categoriaId=" + categoriaId +
                ", puntuacion=" + puntuacion +
                ", usuario=" + usuario +
                ", libro=" + libro +
                ", categoria=" + categoria +
                '}';
    }
}
