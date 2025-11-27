package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Autor;
import modelo.Categoria;

import java.util.Optional;

public class CategoriaDAOHib implements CategoriaDAO{

    private EntityManager entityManager;
    public CategoriaDAOHib(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public boolean crearCategoria(Categoria c) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            entityManager.persist(c);
            tran.commit();
            return true;
        } catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al crear la categoria" + e);
        }
    }

    @Override
    public Optional<Categoria> buscarPorId(int id) {
        Categoria c = entityManager.find(Categoria.class, id);
        return Optional.ofNullable(c);
    }

    @Override
    public Categoria actualizarCategoria(Categoria c) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Categoria categoriaActualizada = entityManager.merge(c);
            tran.commit();
            return categoriaActualizada;
        } catch (Exception e){
            if (tran.isActive()) {
                tran.rollback();
                return c;
            }
            throw new RuntimeException("Error al actualizar la vategoría" + e);
        }
    }

    @Override
    public boolean eliminarCategoria(Categoria c) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Categoria categoriaEnc = entityManager.find(Categoria.class, c.getId());
            if (categoriaEnc != null){
                entityManager.remove(categoriaEnc);
                tran.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tran.isActive()){
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al eliminar la categoría" + e);
        }
    }
}
