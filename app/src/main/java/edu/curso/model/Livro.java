package edu.curso.model;

/**
 * CAMADA ENTIDADE - Livro do acervo (8 campos no banco).
 *
 * Representa uma linha da tabela "livro".
 *
 * Significado do campo "status":
 *   true  = DISPONIVEL para emprestimo
 *   false = EMPRESTADO (alguem esta com o livro)
 *
 * O campo "nomeAutor" NAO existe na tabela livro: ele eh preenchido pelo
 * DAO atraves de um JOIN com a tabela autor, apenas para a TableView
 * conseguir mostrar o nome do autor (e nao so o numero do id).
 */
public class Livro {
    private Long id;
    private String titulo;
    private String isbn;
    private Integer anoPublicacao;
    private Integer numPaginas;
    private String idioma;
    private Boolean status;          // true = disponivel / false = emprestado
    private Long idAutor;            // chave estrangeira -> autor.id
    private String nomeAutor;        // somente exibicao (vem do JOIN, nao eh coluna)

    public Livro() {
    }

    public Livro(String titulo, String isbn, Integer anoPublicacao, Integer numPaginas,
                 String idioma, Boolean status, Long idAutor) {
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

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    /**
     * Coluna "Situacao" da TableView: traduz o boolean para texto legivel.
     * (PropertyValueFactory("situacao") chama este getter.)
     */
    public String getSituacao() {
        return Boolean.TRUE.equals(status) ? "Disponivel" : "Emprestado";
    }

    /**
     * Texto exibido quando o objeto aparece em um ComboBox
     * (ex: escolha do livro na tela de Emprestimos).
     */
    @Override
    public String toString() {
        return id + " - " + titulo;
    }
}
