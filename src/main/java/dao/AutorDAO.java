package dao;

import modelo.Autor;

import java.util.Optional;

public interface AutorDAO {

    boolean crearAutor(Autor a);
    Optional<Autor> buscarPorId(int id);
    Autor actualizarAutor(Autor a);
    boolean eliminarAutor(Autor a);

}
