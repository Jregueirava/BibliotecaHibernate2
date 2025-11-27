package dao;

import modelo.Libro;

import java.util.Optional;

public interface LibroDAO {

    boolean crearLibro(Libro l);
    Optional<Libro> buscarPorId(int id);
    Libro actualizarLibro(Libro l);
    boolean eliminarLibro(Libro l);


}
