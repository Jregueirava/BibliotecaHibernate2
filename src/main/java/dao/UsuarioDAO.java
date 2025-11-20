package dao;

import modelo.Usuario;

import java.util.Optional;

public interface UsuarioDAO {

    boolean crearUsuario(Usuario u);
    Optional<Usuario> buscarPorId(int id);
    Usuario actualizarUsuario(Usuario u);
    boolean eliminarUsuario(Usuario u);

}
