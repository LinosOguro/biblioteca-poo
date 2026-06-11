package edu.curso.model;

import java.time.LocalDate;

/**
 * CAMADA ENTIDADE - Autor de livros (6 campos).
 *
 * Representa uma linha da tabela "autor" do banco. Os getters sao usados
 * pela TableView (PropertyValueFactory("nome") chama getNome(), e assim
 * por diante para cada coluna).
 */
public class Autor {
    private Long id;
    private String nome;
    private String nacionalidade;
    private LocalDate dataNasc;
    private String email;
    private String biografia;

    public Autor() {
    }

    public Autor(String nome, String nacionalidade, LocalDate dataNasc, String email, String biografia) {
        this.nome = nome;
        this.nacionalidade = nacionalidade;
        this.dataNasc = dataNasc;
        this.email = email;
        this.biografia = biografia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    /**
     * Texto exibido quando o objeto aparece em um ComboBox
     * (ex: escolha do autor na tela de Livros).
     */
    @Override
    public String toString() {
        return id + " - " + nome;
    }
}
