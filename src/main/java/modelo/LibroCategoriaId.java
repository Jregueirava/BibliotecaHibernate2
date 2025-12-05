package modelo;

import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.Objects;

public class LibroCategoriaId implements Serializable {

    private int usuarioId;
    @Column(name = "libro_id")
    private int libroId;
    @Column(name = "categoria_id")
    private int categoriaId;

    public LibroCategoriaId() {
    }

    public LibroCategoriaId(int usuarioId, int libroId, int categoriaId) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.categoriaId = categoriaId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public int getLibroId() {
        return libroId;
    }

    public void setLibroId(int libroId) {
        this.libroId = libroId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibroCategoriaId that = (LibroCategoriaId) o;
        return usuarioId == that.usuarioId &&
                libroId == that.libroId &&
                categoriaId == that.categoriaId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, libroId, categoriaId);
    }

}