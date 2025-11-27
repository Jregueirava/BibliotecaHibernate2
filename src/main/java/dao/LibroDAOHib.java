package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Libro;

import java.util.Optional;

public class LibroDAOHib implements LibroDAO {

    private EntityManager entityManager;
    public LibroDAOHib(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean crearLibro(Libro l) {
        EntityTransaction tran = entityManager.getTransaction();
        try{
            tran.begin();
            entityManager.persist(l);
            tran.commit();
            return true;
        } catch(Exception e){
            if(tran.isActive()){
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al crear libro" + e);
        }
    }

    @Override
    public Optional<Libro> buscarPorId(int id) {
        Libro l = entityManager.find(Libro.class, id);
        return Optional.ofNullable(l);
    }

    @Override
    public Libro actualizarLibro(Libro l) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Libro libroActualizado = entityManager.merge(l);
            tran.commit();
            return libroActualizado;
        } catch(Exception e){
            if(tran.isActive()){
                tran.rollback();
                return l;
            }
            throw new RuntimeException("Error al actualizar libro" + e);
        }
    }

    @Override
    public boolean eliminarLibro(Libro l) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Libro libroEnc = entityManager.find(Libro.class, l.getId());
            if(libroEnc != null){
                entityManager.remove(libroEnc);
                tran.commit();
                return true;
            }
            return false;
        } catch (Exception e){
            if(tran.isActive()){
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al eliminar libro" + e);
        }
    }
}
