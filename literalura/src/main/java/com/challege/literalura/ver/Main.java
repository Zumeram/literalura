package com.challege.literalura.ver;



import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;    
import com.challege.literalura.servicio.ConvierteDatos;
import com.challege.literalura.servicio.ConsumoAPI;  
import com.challege.literalura.modelo.Autor;
import com.challege.literalura.modelo.DatosLibro;
import com.challege.literalura.modelo.Libro;
import com.challege.literalura.modelo.DatosRespuesta;
import com.challege.literalura.repository.AutorRepository;
import com.challege.literalura.repository.LibroRepository;
import jakarta.transaction.Transactional;



@Component
public class Main implements CommandLineRunner {

    private Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI api;
    private final ConvierteDatos conversor;
    private final LibroRepository libroRepo;
    private final AutorRepository autorRepo;

    public Main(ConsumoAPI api, ConvierteDatos conversor, LibroRepository libroRepo, AutorRepository autorRepo) {
        this.api = api;
        this.conversor = conversor;
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
    }

    @Transactional
    @Override
    public void run(String... args) {
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Por favor, ingrese un número.");
                continue;
            }

            switch (opcion) {
                case 1 -> buscarLibro();
                case 2 -> listarLibros();
                case 3 -> listarAutores();
                case 4 -> autoresVivos();
                case 5 -> librosPorIdioma();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("""
        ***************************************************
        1 - Buscar libro por título 
        2 - Listar libros registrados
        3 - Listar autores registrados
        4 - Listar autores vivos en un determinado año
        5 - Listar libros por idioma
        0 - Salir
        ***************************************************
        Elija una opción: """);
    }

    private void buscarLibro() {
        System.out.println("Escriba el nombre del libro que desea buscar:");
        var nombreLibro = scanner.nextLine();
        var json = api.obtenerDatos("https://gutendex.com/books/?search=" + nombreLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, DatosRespuesta.class);

        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println("Libro Encontrado");
            Libro libro = new Libro(libroBuscado.get());

            if (libroRepo.findByTituloContainsIgnoreCase(libro.getTitulo()).isPresent()) {
                System.out.println("El libro ya está registrado.");
            } else {
                // Verificar si el autor ya existe para evitar duplicados
                if (libro.getAutor() != null) {
                    var autorExistente = autorRepo.findAll().stream()
                            .filter(a -> a.getNombre().equals(libro.getAutor().getNombre()))
                            .findFirst();
                    if (autorExistente.isPresent()) {
                        libro.setAutor(autorExistente.get());
                    } else {
                        autorRepo.save(libro.getAutor());
                    }
                }
                libroRepo.save(libro);
                System.out.println(libro);
            }
        } else {
            System.out.println("Libro no encontrado.");
        }
    }

    private void listarLibros() {
        List<Libro> libros = libroRepo.findAll();
        libros.forEach(System.out::println);
    }

    private void listarAutores() {
        List<Autor> autores = autorRepo.findAll();
        autores.forEach(System.out::println);
    }

    private void autoresVivos() {
        System.out.println("Ingrese el año para buscar autores vivos:");
        try {
            var anio = Integer.parseInt(scanner.nextLine());
            List<Autor> autores = autorRepo.buscarAutoresVivos(anio);
            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en ese año.");
            } else {
                autores.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            System.out.println("Año no válido.");
        }
    }

    private void librosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar los libros:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """);
        var idioma = scanner.nextLine();
        List<Libro> librosPorIdioma = libroRepo.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
        } else {
            librosPorIdioma.forEach(System.out::println);
        }
    }
}