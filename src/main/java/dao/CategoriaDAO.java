package dao;

import modelo.Categoria;

import java.util.Optional;

public interface CategoriaDAO {

    boolean crearCategoria(Categoria c);
    Optional<Categoria> buscarPorId(int id);
    boolean actualizarCategoria(Categoria c);
    boolean eliminarCategoria(Categoria c);

}
