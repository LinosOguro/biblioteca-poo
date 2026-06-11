package edu.curso.model;

/**
 * CAMADA ENTIDADE - Usuario do sistema (7 campos).
 *
 * Representa uma linha da tabela "usuario" do banco. Classe simples,
 * apenas com atributos + getters/setters (padrao JavaBean). Os getters
 * tambem sao usados pela TableView (via PropertyValueFactory) para
 * preencher as colunas: PropertyValueFactory("nome") chama getNome().
 *
 * Observacoes:
 *  - O campo "nome" tambem funciona como LOGIN do sistema.
 *  - "perfil" define o nivel de acesso: "ADMIN" ou "COMUM".
 */
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String senha;
    private String perfil;   // "ADMIN" ou "COMUM"

    public Usuario() {
    }

    public Usuario(String nome, String email, String telefone, String cpf, String senha, String perfil) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.senha = senha;
        this.perfil = perfil;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    /** Atalho da seguranca: este usuario tem o perfil de administrador? */
    public boolean isAdmin() {
        return "ADMIN".equals(perfil);
    }

    /**
     * Texto exibido quando o objeto aparece em um ComboBox
     * (ex: escolha do usuario na tela de Emprestimos).
     */
    @Override
    public String toString() {
        return id + " - " + nome;
    }
}
