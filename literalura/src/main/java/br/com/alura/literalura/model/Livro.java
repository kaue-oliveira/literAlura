package br.com.alura.literalura.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String titulo;
    
    private String autor;
    
    private String idiomas;
    
    private Integer downloads;

    public Livro() {}

    public Livro(DadosLivro dadosLivro) {
        this.titulo = dadosLivro.titulo();
        this.autor = pegaAutor(dadosLivro).getNome();
        this.idiomas = idiomaMod(dadosLivro.idiomas());
        this.downloads = dadosLivro.downloads();
    }

    private String idiomaMod(List<String> idiomas) {
        return (idiomas == null || idiomas.isEmpty()) 
            ? "Desconhecido" 
            : idiomas.get(0);
    }

    public Autor pegaAutor(DadosLivro dadosLivro) {
        DadosAutor dadosAutor = dadosLivro.autor().get(0);
        return new Autor(dadosAutor);
    }

    // Getters and Setters remain the same

    @Override
    public String toString() {
        return String.format(
            "\n" +
            "===== Informações do Livro =====\n" +
            "Título:     %s\n" +
            "Autor:      %s\n" +
            "Idioma:     %s\n" +
            "Downloads:  %d\n" +
            "==================================",
            titulo, autor, idiomas, downloads
        );
    }

    // Getters and Setters remain unchanged
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getIdiomas() { return idiomas; }
    public void setIdiomas(String idiomas) { this.idiomas = idiomas; }
    public Integer getDownloads() { return downloads; }
    public void setDownloads(Integer downloads) { this.downloads = downloads; }
}
