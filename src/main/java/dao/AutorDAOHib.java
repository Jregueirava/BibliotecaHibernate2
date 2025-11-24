package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Autor;

import java.security.PrivateKey;
import java.util.Optional;

public class AutorDAOHib implements AutorDAO{

    private EntityManager entityManager;
    public AutorDAOHib(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public boolean crearAutor(Autor a) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            entityManager.persist(a);
            tran.commit();
            return true;
        }catch(Exception e){
            if (tran.isActive()){
                tran.rollback();;
                return false;
            }
            throw new RuntimeException("Error al crear Autor" + e);
        }

    }

    @Override
    public Optional<Autor> buscarPorId(int id) {
        Autor a = entityManager.find(Autor.class, id);
        return Optional.ofNullable(a);
    }

    @Override
    public Autor actualizarAutor(Autor a) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Autor autorActualizado = entityManager.merge(a);
            tran.commit();
            return autorActualizado;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return a;
            }
            throw new RuntimeException("Error al modificar autor" + e);
        }
    }

    @Override
    public boolean eliminarAutor(Autor a) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Autor autorEnc = entityManager.find(Autor.class, a.getId());
            if (autorEnc != null){
                entityManager.remove(autorEnc);
                tran.commit();
                return true;
            }
            return false;
        } catch (Exception e){
            if (tran.isActive()){
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al eliminar autor" + e);
        }

    }
}
