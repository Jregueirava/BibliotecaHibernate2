package dao;

import criteria.PrestamoCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import modelo.Prestamo;

import java.util.List;
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

    public List<Prestamo> recuperarTodos(){
        String jpql = "SELECT p FROM Prestamo p";
        TypedQuery<Prestamo> query = entityManager.
                createQuery(jpql, Prestamo.class);
        return query.getResultList();
    }

    public List<Prestamo> getPrestamoEstado(Prestamo.EstadoPrestamo estadoPrestamo){
        String jpql= "SELECT p FROM Prestamo p WHERE " + "p.estado =:estadoPrestamo";
        TypedQuery<Prestamo> query = entityManager.
                createQuery(jpql, Prestamo.class);
        query.setParameter("estadoPrestamo", estadoPrestamo);
        return query.getResultList();
    }

    public List<Prestamo> getPrestamosCriteria(PrestamoCriteria criteria){
        String jpql = "SELECT p FROM Prestamo p WHERE 1=1";
        if (criteria.isPresentEstadoPrestamo()){
            jpql+= " AND p.estado = :estadoPrestamo";
        }
        if (criteria.isPresentFechaInicio()){
            jpql+= " AND p.fechaInicio BETWEEN :fechaInicio AND :fechaFin";
        }
        TypedQuery<Prestamo> query = entityManager.createQuery(jpql, Prestamo.class);
        if (criteria.isPresentEstadoPrestamo()){
            query.setParameter("estadoPrestamo", criteria.getEstadoPrestamo());
        }
        if (criteria.isPresentFechaInicio()){
            query.setParameter("fechaInicio", criteria.getIniFechaInicio());
            query.setParameter("fechaFin", criteria.getFinFechaInicio());
        }
        return query.getResultList();
    }
}
