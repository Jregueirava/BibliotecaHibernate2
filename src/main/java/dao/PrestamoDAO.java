package dao;

import modelo.Prestamo;

import java.util.Optional;

public interface PrestamoDAO {

    boolean crearPrestamo(Prestamo p);
    Optional<Prestamo> buscarPorId(int id);
    Prestamo actualizarPrestamo(Prestamo p);
    boolean eliminarPrestamo(Prestamo p);

}
