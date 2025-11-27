import dao.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import modelo.*;
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
            CategoriaDAO categoriaDAO = new CategoriaDAOHib(em);
            LibroDAO libroDAO = new LibroDAOHib(em);
            PrestamoDAO prestamoDAO = new PrestamoDAOHib(em);
            EjemplarDAO ejemplarDAO = new EjemplarDAOHib(em);


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

            Optional<Categoria> categoriaOptinal = categoriaDAO.buscarPorId(3);
            if(categoriaOptinal.isPresent()){
                System.out.println("====CATEGORIA ENCONTRADO====");
                System.out.println(categoriaOptinal.get());
            } else {
                System.out.println("====CATEGORIA NO ENCONTRADO====");
            }

            Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
            if(libroOptional.isPresent()){
                System.out.println("====LIBRO ENCONTRADO====");
                System.out.println(libroOptional.get());
            } else {
                System.out.println("====LIBRO NO ENCONTRADO====");
            }

            System.out.println("==== Ejercicio 1 con LAZY ====");
            Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
            if(prestamoOptional.isPresent()){
                Prestamo p = prestamoOptional.get();
                System.out.println("====PRESTAMO ENCONTRADO====");
                System.out.println("Antes de acceder al prestamo:");
                System.out.println("Ejmplar class: " +  p.getEjemplar().getClass());
                System.out.println(p);
                System.out.println("Después de acceder al prestamo:");
                System.out.println("UCódigo ejemplar: " + p.getEjemplar().getCodigo());
                System.out.println("====Fin de Prestamo ENCONTRADO====");
            }else {
                System.out.println("====PRESTAMO NO ENCONTRADO====");
            }

            System.out.println("====== Prueba de Prestamos y Ejemplar Bidireccional ======");
            Optional<Ejemplar>  ejemplarOptional = ejemplarDAO.buscarPorId(1);
            if(ejemplarOptional.isPresent()){
                System.out.println("====EJEMPLAR ENCONTRADO====");
                System.out.println(ejemplarOptional.get());
            } else {
                System.out.println("====EJEMPLAR NO ENCONTRADO====");
            }


//            System.out.println("==== Ejercicio 2 con LAZY ====");
//            Optional<Ejemplar> ejemplarOptional = ejemplarDAO.buscarPorId(1);
//            if(ejemplarOptional.isPresent()){
//                Ejemplar e = ejemplarOptional.get();
//                System.out.println("====Ejemplar ENCONTRADO====");
//                System.out.println("Antes de acceder al libro:");
//                System.out.println("Libro class: " +  e.getLibro().getClass());
//                System.out.println(e);
//                System.out.println("Después de acceder al libro:");
//                System.out.println("Título libro: " +  e.getLibro().getTitulo());
//
//                System.out.println(ejemplarOptional.get());
//            } else {
//                System.out.println("====Ejemplar NO ENCONTRADO====");
//            }

//            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
//            if(usuarioOptional.isPresent()){
//                Usuario u = usuarioOptional.get();
//                System.out.println("====USUARIO ENCONTRADO====");
//                System.out.println(u);
//                System.out.println("====Fin de Usuario ENCONTRADO====");
//            }else {
//                System.out.println("====PRESTAMO NO ENCONTRADO====");
//            }


            System.out.println("Programa de prueba finalizado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
