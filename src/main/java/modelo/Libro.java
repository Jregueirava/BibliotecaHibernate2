package modelo;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "isbn", unique = true, nullable = false, length = 20)
    private String isbn;

    @Column (name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column (name = "fecha_publicacion")
    private LocalDate fecha_publicacion;

    @Column (name = "paginas")
    private int paginas;

    @Column (name = "editorial", length = 100)
    private String editorial;

    @Column (name = "autor_id")
    private int autor_id;

   @Column (name = "categoria_id")
    private int categoria_id;

   public Libro() {

   }

    public Libro(int id, String isbn, String titulo, LocalDate fecha_publicacion, int paginas, String editorial, int autor_id, int categoria_id) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.fecha_publicacion = fecha_publicacion;
        this.paginas = paginas;
        this.editorial = editorial;
        this.autor_id = autor_id;
        this.categoria_id = categoria_id;
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

    public LocalDate getFecha_publicacion() {
        return fecha_publicacion;
    }

    public void setFecha_publicacion(LocalDate fecha_publicacion) {
        this.fecha_publicacion = fecha_publicacion;
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

    public int getAutor_id() {
        return autor_id;
    }

    public void setAutor_id(int autor_id) {
        this.autor_id = autor_id;
    }

    public int getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(int categoria_id) {
        this.categoria_id = categoria_id;
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
                ", autor_id=" + autor_id +
                ", categoria_id=" + categoria_id +
                '}';
    }
}
