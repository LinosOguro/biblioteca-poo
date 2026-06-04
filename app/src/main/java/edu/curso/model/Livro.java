package edu.curso.model;

public class Livro {
    private Long id;
    private String titulo;
    private String isbn;
    private Integer anoPublicacao;
    private Integer numPaginas;
    private String idioma;
    private Boolean status;
    private Long idAutor;

    public Livro() {
    }

    public Livro(String titulo, String isbn, Integer anoPublicacao, Integer numPaginas, String idioma, Boolean status, Long idAutor) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.numPaginas = numPaginas;
        this.idioma = idioma;
        this.status = status;
        this.idAutor = idAutor;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public Integer getNumPaginas() {
        return numPaginas;
    }

    public void setNumPaginas(Integer numPaginas) {
        this.numPaginas = numPaginas;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Long idAutor) {
        this.idAutor = idAutor;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", isbn='" + isbn + '\'' +
                ", anoPublicacao=" + anoPublicacao +
                ", numPaginas=" + numPaginas +
                ", idioma='" + idioma + '\'' +
                ", status=" + status +
                ", idAutor=" + idAutor +
                '}';
    }
}
