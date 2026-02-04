package com.challege.literalura.modelo;

import jakarta.persistence.*;


@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    private String idioma;
    private Double descargas;

    public Libro() {}

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.idioma = (datosLibro.idiomas() != null && !datosLibro.idiomas().isEmpty()) ? datosLibro.idiomas().get(0) : "Desconocido";
        this.descargas = datosLibro.numeroDeDescargas();
        if (datosLibro.autores() != null && !datosLibro.autores().isEmpty()) {
            this.autor = new Autor(datosLibro.autores().get(0));
        }
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getTitulo() {

        return titulo;
    }

    public void setTitulo(String titulo) {

        this.titulo = titulo;
    }

    public Autor getAutor() {

        return autor;
    }

    public void setAutor(Autor autor) {

        this.autor = autor;
    }

    public String getIdioma() {

        return idioma;
    }

    public void setIdioma(String idioma) {

        this.idioma = idioma;
    }

    public Double getDescargas() {

        return descargas;
    }

    public void setDescargas(Double descargas) {

        this.descargas = descargas;
    }

    @Override
    public String toString() {
        return "----- LIBRO -----" +
                "\nTítulo: " + titulo +
                "\nAutor: " + (autor != null ? autor.getNombre() : "Desconocido") +
                "\nIdioma: " + idioma +
                "\nNúmero de descargas: " + descargas +
                "\n-----------------";
    }
}


