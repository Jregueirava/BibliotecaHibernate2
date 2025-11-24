package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Usuario;

import java.util.Optional;

public class UsuarioDAOHib implements UsuarioDAO{

    private EntityManager entityManager;
    public UsuarioDAOHib(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean crearUsuario(Usuario u) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            entityManager.persist(u);
            tran.commit();
            return true;
        }catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al crear usuario" + e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        Usuario u = entityManager.find(Usuario.class, id);
        return Optional.ofNullable(u);
    }

    @Override
    public Usuario actualizarUsuario(Usuario u) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Usuario usuarioActualizado = entityManager.merge(u);
            tran.commit();
            return usuarioActualizado;
        }catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return u;
            }
            throw new RuntimeException("Error al modificar usuario" + e);
        }
    }

    @Override
    public boolean eliminarUsuario(Usuario u) {
        EntityTransaction tran = entityManager.getTransaction();
        try {
            tran.begin();
            Usuario usuarioEnc = entityManager.find(Usuario.class, u.getId());
            if(usuarioEnc != null){
                entityManager.remove(usuarioEnc);
                tran.commit();
                return true;
            }
            return false;
        }catch (Exception e) {
            if (tran.isActive()) {
                tran.rollback();
                return false;
            }
            throw new RuntimeException("Error al borrar usuario" + e);
        }
    }
}
