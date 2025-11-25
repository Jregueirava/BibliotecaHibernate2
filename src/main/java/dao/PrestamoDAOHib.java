package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Prestamo;

import java.util.Optional;

public class PrestamoDAOHib implements PrestamoDAO {

    private EntityManager entityManager;

    public PrestamoDAOHib(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean crearPrestamo(Prestamo p) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            entityManager.persist(p);
            tran.commit();
            return true;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al crear prestamo: " + e);
        }
    }

    @Override
    public Optional<Prestamo> buscarPorId(int id) {
        Prestamo p = entityManager.find(Prestamo.class, id);
        return Optional.ofNullable(p);
    }

    @Override
    public Prestamo actualizarPrestamo(Prestamo p) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Prestamo prestamoActualizado = entityManager.merge(p);
            tran.commit();
            return prestamoActualizado;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return p;
            }
            throw new RuntimeException("Error al actualizar prestamo: " + e);
        }
    }

    @Override
    public boolean eliminarPrestamo(Prestamo p) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Prestamo prestamoEnc = entityManager.find(Prestamo.class, p.getId());
            if (prestamoEnc != null) {
                entityManager.remove(prestamoEnc);
                tran.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al eliminar prestamo: " + e);
        }
    }
}
