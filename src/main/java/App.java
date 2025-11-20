import dao.UsuarioDAO;
import dao.UsuarioDAOHib;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import modelo.Usuario;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Optional;

public class App {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try(EntityManager em = Persistence
                .createEntityManagerFactory("biblioteca")
                .createEntityManager()){
            UsuarioDAO usuarioDAO = new UsuarioDAOHib(em);
            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);

            if(usuarioOptional.isPresent()){
                System.out.println("====USUARIO ENCONTRADO====");
                System.out.println(usuarioOptional.get());
            }else {
                System.out.println("====USUARIO NO ENCONTRADO====");
            }
            System.out.println("Programa de prueba finalizado");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
