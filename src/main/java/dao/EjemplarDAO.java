package dao;

import modelo.Ejemplar;

import java.util.Optional;

public interface EjemplarDAO {

    boolean crearEjemplar(Ejemplar e);
    Optional<Ejemplar> buscarPorId(int id);
    Ejemplar actualizarEjemplar(Ejemplar e);
    boolean eliminarEjemplar(Ejemplar e);

}
