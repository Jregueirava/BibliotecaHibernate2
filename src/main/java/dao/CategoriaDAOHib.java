package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
        return Optional.empty();
    }

    @Override
    public boolean actualizarCategoria(Categoria c) {
        return false;
    }

    @Override
    public boolean eliminarCategoria(Categoria c) {
        return false;
    }
}
