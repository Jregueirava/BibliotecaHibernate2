import dao.AutorDAO;
import dao.AutorDAOHib;
import dao.PrestamoDAO;
import dao.PrestamoDAOHib;
import dao.UsuarioDAO;
import dao.UsuarioDAOHib;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import modelo.Autor;
import modelo.Prestamo;
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
            AutorDAO autorDAO = new AutorDAOHib(em);
            PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);


            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
            if(usuarioOptional.isPresent()){
                System.out.println("====USUARIO ENCONTRADO====");
                System.out.println(usuarioOptional.get());
            }else {
                System.out.println("====USUARIO NO ENCONTRADO====");
            }

            Optional<Autor> autorOptional = autorDAO.buscarPorId(2);
            if(autorOptional.isPresent()){
                System.out.println("====AUTOR ENCONTRADO====");
                System.out.println(autorOptional.get());
            }else {
                System.out.println("====AUTOR NO ENCONTRADO====");
            }

            Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
            if(prestamoOptional.isPresent()){
                System.out.println("====PRESTAMO ENCONTRADO====");
                System.out.println("Antes de acceder al prestamo:");
                System.out.println("Class: " + prestamoOptional.get().getUsuario().getClass());
                System.out.println(prestamoOptional.get());
                System.out.println("Después de acceder al prestamo:");
                System.out.println("Usuario ID: " + prestamoOptional.get().getUsuario().getId());
            }else {
                System.out.println("====PRESTAMO NO ENCONTRADO====");
            }

            System.out.println("Programa de prueba finalizado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
