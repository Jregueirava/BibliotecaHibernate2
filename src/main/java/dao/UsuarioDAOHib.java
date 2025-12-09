package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import modelo.Usuario;

import java.util.List;
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

    public Optional<Usuario> findByDni(String dniPar){
        String jpql = "SELECT u FROM Usuario u WHERE u.dni = :dniPar";
        TypedQuery<Usuario> query = entityManager.createQuery(jpql, Usuario.class);
        query.setParameter("dniPar", dniPar);
        Usuario usr = query.getSingleResult();
        Optional<Usuario> optUsr = Optional.of(query.getSingleResult());
        return optUsr;
    }

    public List<Object[]> favoritosPorUsario(){
        String jpql = "SELECT u, count(l) FROM Usuario u LEFT JOIN u.librosFavoritos l GROUP BY u";
        TypedQuery<Object[]> query =  entityManager.createQuery(jpql, Object[].class);
        return query.getResultList();
    }
}
