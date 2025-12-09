package dao;

import criteria.PrestamoCriteria;
import modelo.Prestamo;

import java.util.List;
import java.util.Optional;

public interface PrestamoDAO {

    boolean crearPrestamo(Prestamo p);
    Optional<Prestamo> buscarPorId(int id);
    Prestamo actualizarPrestamo(Prestamo p);
    boolean eliminarPrestamo(Prestamo p);
    public List<Prestamo> recuperarTodos();
    public List<Prestamo> getPrestamoEstado(Prestamo.EstadoPrestamo estadoPrestamo);
    public List<Prestamo> getPrestamosCriteria(PrestamoCriteria criteria);
}
