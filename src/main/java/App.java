import criteria.PrestamoCriteria;
import dao.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import modelo.*;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class App {

    public App() {
    }

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

            PrestamoCriteria prestamoCriteria = new PrestamoCriteria();
            prestamoCriteria.setEstadoPrestamo(Prestamo.EstadoPrestamo.ACTIVO);
            prestamoCriteria.setIniFechaInicio(LocalDate.of(2024, 1, 31));
            prestamoCriteria.setFinFechaInicio(LocalDate.of(2024, 2, 10));

            List<Prestamo> listPrestamo = prestamoDAO.
                    getPrestamosCriteria(prestamoCriteria);
            for(Prestamo p : listPrestamo){
                System.out.println(p);
            }


//            List<Object[]> listObj = usuarioDAO.favoritosPorUsario();
//            for(Object[] ob: listObj){
//                Usuario u = (Usuario) ob[0];
//                long total = (long) ob[1];
//                System.out.println("Usuario" + u.getId() + "nombre" + u.getNombre() + "librosFavoritos: " + total);
//            }
//            List<Prestamo> listaPrestamos = prestamoDAO.getPrestamoEstado(Prestamo.EstadoPrestamo.ACTIVO);
//            for(Prestamo p : listaPrestamos) {
//                System.out.println(p);
//            }
           //List<Prestamo> listaPrestamos = prestamoDAO.recuperarTodos();
//            for(Prestamo p : listaPrestamos){
//                System.out.println(p);
//            }
//            /Optional<Usuario> usr = usuarioDAO.findByDni("12345678A");
//            if(usr.isPresent())
//                System.out.println(usr.get());
//            else
//                System.out.println("Usuario no encontrado");

//            // Mapeo moitos a moitos (N:N) Para Libro
//            // Crea una nueva tabla denominada favoritos(usuario_id, libro_id), que contiene los libros favoritos de cada usuario (N:N). Mapea de manera correspondiente cada una de las entidades.
//            Optional<Libro> optLibro = libroDAO.buscarPorId(1);
//            if(optLibro.isPresent()){
//                Libro l = optLibro.get();
//                System.out.println((l));
//                for (Usuario u: l.getUsuariosFavoritos()){
//                    System.out.println("\t USUARIO");
//                    System.out.println(u);
//                }
//            }
//
//            // Mapeo moitos a moitos (N:N) Para Usuario
//            Optional<Usuario> optusuario = usuarioDAO.buscarPorId(1);
//            if(optusuario.isPresent()){
//                Usuario u = optusuario.get();
//                System.out.println((u));
//                for (Libro l: u.getLibrosFavoritos()){
//                    System.out.println("\t LIBRO");
//                    System.out.println(u);
//                }
//            }
//
//            Optional<Autor> optAutor = autorDAO.buscarPorId(1);
//            Optional<Categoria> optCategoria = categoriaDAO.buscarPorId(1);
//            if(optAutor.isPresent() && optCategoria.isPresent()){
//                Libro libro1 = new Libro(-1, "978-0132350889", "Nuevo Libro",
//                        LocalDate .now(), 100, "Editorial F.Wirtz",
//                        optAutor.get(), optCategoria.get());
//                libro1 = libroDAO.actualizarLibro(libro1);
//                if(optusuario.isPresent()){
//                    Usuario u = optusuario.get();
//                    u.addLibro(libro1);
//                    usuarioDAO.actualizarUsuario(u);
//                }
//            }
//
//            if(optLibro.isPresent()){
//                Libro libro2 = optLibro.get();
//                Usuario u2 = new Usuario("12345679T", "Pepe",
//                        "Perez", "pepeperez2@gmail.com");
//                u2 = usuarioDAO.actualizarUsuario(u2);
//                u2.addLibro(libro2);
//                u2 = usuarioDAO.actualizarUsuario(u2);
//            }

//
//            Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorId(1);
//            if(usuarioOptional.isPresent()){
//                System.out.println("====USUARIO ENCONTRADO====");
//                System.out.println(usuarioOptional.get());
//            }else {
//                System.out.println("====USUARIO NO ENCONTRADO====");
//            }
//
//            Optional<Autor> autorOptional = autorDAO.buscarPorId(2);
//            if(autorOptional.isPresent()){
//                System.out.println("====AUTOR ENCONTRADO====");
//                System.out.println(autorOptional.get());
//            }else {
//                System.out.println("====AUTOR NO ENCONTRADO====");
//            }
//
//            Optional<Categoria> categoriaOptinal = categoriaDAO.buscarPorId(3);
//            if(categoriaOptinal.isPresent()){
//                System.out.println("====CATEGORIA ENCONTRADO====");
//                System.out.println(categoriaOptinal.get());
//            } else {
//                System.out.println("====CATEGORIA NO ENCONTRADO====");
//            }
//
//            Optional<Libro> libroOptional = libroDAO.buscarPorId(1);
//            if(libroOptional.isPresent()){
//                System.out.println("====LIBRO ENCONTRADO====");
//                System.out.println(libroOptional.get());
//            } else {
//                System.out.println("====LIBRO NO ENCONTRADO====");
//            }
//
//            System.out.println("==== Ejercicio 1 con LAZY ====");
//            Optional<Prestamo> prestamoOptional = prestamoDAO.buscarPorId(1);
//            if(prestamoOptional.isPresent()){
//                Prestamo p = prestamoOptional.get();
//                System.out.println("====PRESTAMO ENCONTRADO====");
//                System.out.println("Antes de acceder al prestamo:");
//                System.out.println("Ejmplar class: " +  p.getEjemplar().getClass());
//                System.out.println(p);
//                System.out.println("Después de acceder al prestamo:");
//                System.out.println("UCódigo ejemplar: " + p.getEjemplar().getCodigo());
//                System.out.println("====Fin de Prestamo ENCONTRADO====");
//            }else {
//                System.out.println("====PRESTAMO NO ENCONTRADO====");
//            }
//
//            System.out.println("====== Prueba de Prestamos y Ejemplar Bidireccional ======");
//            Optional<Ejemplar>  ejemplarOptional = ejemplarDAO.buscarPorId(1);
//            if(ejemplarOptional.isPresent()){
//                System.out.println("====EJEMPLAR ENCONTRADO====");
//                System.out.println(ejemplarOptional.get());
//            } else {
//                System.out.println("====EJEMPLAR NO ENCONTRADO====");
//            }

//            Optional<Usuario> usuarioParaActualizar = usuarioDAO.buscarPorId(1);
//            if (usuarioParaActualizar.isPresent()) {
//                Usuario usuario = usuarioParaActualizar.get();
//                System.out.println("=====Usuario antes de actualizar======");
//                System.out.println(usuario);
//
//                Optional<Ejemplar> ejemplarParaPrestamo = ejemplarDAO.buscarPorId(1);
//                if(ejemplarParaPrestamo.isPresent()){
//                    Prestamo np = new Prestamo();
//                    np.setFechaInicio(java.time.LocalDate.now());
//                    np.setFechaFin(java.time.LocalDate.now().plusDays(10));
//                    np.setEstado(Prestamo.EstadoPrestamo.ACTIVO);
//                    np.setUsuario(usuario);
//                    np.setEjemplar(ejemplarParaPrestamo.get());
//
//                    usuario.a
//
//                }
//            }


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


            //System.out.println("Programa de prueba finalizado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
