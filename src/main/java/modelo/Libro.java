package modelo;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "isbn", unique = true, nullable = false, length = 20)
    private String isbn;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "fecha_publicacion")
    private LocalDate fecha_publicacion;

    @Column(name = "paginas")
    private int paginas;

    @Column(name = "editorial", length = 100)
    private String editorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToMany(mappedBy = "librosFavoritos")
    private List<Usuario> listaUsuariosFavoritos = new ArrayList<>();

    public Libro() {
        this.listaUsuariosFavoritos = new ArrayList<>();


    }

    public Libro(int id, String isbn, String titulo, LocalDate fecha_publicacion, int paginas, String editorial, Autor autor, Categoria categoria) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.fecha_publicacion = fecha_publicacion;
        this.paginas = paginas;
        this.editorial = editorial;
        this.autor = autor;
        this.categoria = categoria;
        this.listaUsuariosFavoritos = new ArrayList<>();
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

    public List<Usuario> getUsuariosFavoritos() {
        return listaUsuariosFavoritos;
    }

    public void setUsuariosFavoritos(List<Usuario> usuariosFavoritos) {
        this.listaUsuariosFavoritos = usuariosFavoritos;
    }

    public void addUsuario(Usuario usuario){
        this.listaUsuariosFavoritos.add(usuario);
    }

    public void removeUsuario(Usuario usuario){
        this.listaUsuariosFavoritos.remove(usuario);
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
                ", usuariosFavoritos=" + this.listaUsuariosFavoritos.size() +
                '}';
    }
}
