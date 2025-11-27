package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Ejemplar;

import java.util.Optional;

public class EjemplarDAOHib implements EjemplarDAO{

    private EntityManager entityManager;
    public EjemplarDAOHib(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public boolean crearEjemplar(Ejemplar e) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            entityManager.persist(e);
            tran.commit();
            return true;
        } catch (Exception ex) {
            if (tran.isActive()) {
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al crear Ejemplar" + ex);
        }
    }

    @Override
    public Optional<Ejemplar> buscarPorId(int id) {
        Ejemplar e = entityManager.find(Ejemplar.class, id);
        return Optional.ofNullable(e);
    }

    @Override
    public Ejemplar actualizarEjemplar(Ejemplar e) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Ejemplar ejemplarActualizado = entityManager.merge(e);
            tran.commit();
            return ejemplarActualizado;
        } catch (Exception ex){
            if (tran.isActive()) {
                tran.rollback();
                return e;
            }
            throw new RuntimeException("Error al actualizar ejemplar" + ex);
        }
    }

    @Override
    public boolean eliminarEjemplar(Ejemplar e) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Ejemplar ejemplarEnc = entityManager.find(Ejemplar.class, e.getId());
            if (ejemplarEnc != null){
                entityManager.remove(ejemplarEnc);
                tran.commit();
                return true;
            }
            return false;
        } catch (Exception ex) {
            if (tran.isActive()){
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al eliminar ejemplar" + ex);
        }
    }
}
