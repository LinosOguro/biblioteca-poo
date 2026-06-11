package edu.curso.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CAMADA ENTIDADE - Emprestimo de um livro para um usuario (7 campos no banco).
 *
 * Representa uma linha da tabela "emprestimo".
 *
 * Significado do campo "status":
 *   true  = DEVOLVIDO (emprestimo encerrado)
 *   false = EM ABERTO (o usuario ainda esta com o livro)
 *
 * Os campos "tituloLivro" e "nomeUsuario" NAO existem na tabela: sao
 * preenchidos pelo DAO atraves de JOINs, apenas para a TableView mostrar
 * nomes legiveis em vez de numeros de id.
 */
public class Emprestimo {
    private Long id;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;     // data PREVISTA para devolucao
    private Boolean status;              // true = devolvido / false = em aberto
    private BigDecimal valorMulta;
    private Long idLivro;                // chave estrangeira -> livro.id
    private Long idUsuario;              // chave estrangeira -> usuario.id
    private String tituloLivro;          // somente exibicao (vem do JOIN)
    private String nomeUsuario;          // somente exibicao (vem do JOIN)

    public Emprestimo() {
    }

    public Emprestimo(LocalDate dataEmprestimo, LocalDate dataDevolucao, Boolean status,
                      BigDecimal valorMulta, Long idLivro, Long idUsuario) {
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
        this.valorMulta = valorMulta;
        this.idLivro = idLivro;
        this.idUsuario = idUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public Long getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Long idLivro) {
        this.idLivro = idLivro;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTituloLivro() {
        return tituloLivro;
    }

    public void setTituloLivro(String tituloLivro) {
        this.tituloLivro = tituloLivro;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    /**
     * Coluna "Situacao" da TableView: traduz o boolean para texto legivel.
     */
    public String getSituacao() {
        return Boolean.TRUE.equals(status) ? "Devolvido" : "Em aberto";
    }

    @Override
    public String toString() {
        return id + " - " + tituloLivro + " para " + nomeUsuario;
    }
}
