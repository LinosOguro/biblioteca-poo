package edu.curso.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Livro;

/**
 * CAMADA DAO - Implementacao do acesso a dados de Livro com JDBC + MariaDB.
 * (O padrao de cada metodo esta explicado no comentario de AutorDAOImpl.)
 *
 * Detalhe extra desta classe: os SELECTs usam JOIN com a tabela autor
 * para trazer tambem o NOME do autor (campo de exibicao nomeAutor do model),
 * assim a TableView mostra "Machado de Assis" em vez de apenas "101".
 */
public class LivroDAOImpl implements LivroDAO {

    @Override
    public void cadastrar(Livro l) throws SQLException {
        String sql = "INSERT INTO livro (titulo, isbn, ano_publicacao, num_paginas, idioma, status, id_autor) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, l.getTitulo());
            stm.setString(2, l.getIsbn());
            stm.setInt(3, l.getAnoPublicacao());
            stm.setInt(4, l.getNumPaginas());
            stm.setString(5, l.getIdioma());
            stm.setBoolean(6, l.getStatus());
            stm.setLong(7, l.getIdAutor());
            stm.executeUpdate();
        }
    }

    @Override
    public List<Livro> consultarPorTitulo(String titulo) throws SQLException {
        // Com titulo = "" lista todos (LIKE '%%').
        // LEFT JOIN: traz o livro mesmo se o autor nao for encontrado.
        // "a.nome AS nome_autor" apelida a coluna para leitura no ResultSet.
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT l.*, a.nome AS nome_autor "
                   + "FROM livro l LEFT JOIN autor a ON l.id_autor = a.id "
                   + "WHERE l.titulo LIKE ? ORDER BY l.titulo";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, "%" + titulo + "%");
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarLivro(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Livro buscarPorId(long id) throws SQLException {
        // Usado pelas regras de emprestimo (verificar/alterar disponibilidade).
        String sql = "SELECT l.*, a.nome AS nome_autor "
                   + "FROM livro l LEFT JOIN autor a ON l.id_autor = a.id "
                   + "WHERE l.id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return montarLivro(rs);
                }
            }
        }
        return null;  // nao encontrado
    }

    @Override
    public void atualizar(long id, Livro l) throws SQLException {
        String sql = "UPDATE livro SET titulo = ?, isbn = ?, ano_publicacao = ?, "
                   + "num_paginas = ?, idioma = ?, status = ?, id_autor = ? WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, l.getTitulo());
            stm.setString(2, l.getIsbn());
            stm.setInt(3, l.getAnoPublicacao());
            stm.setInt(4, l.getNumPaginas());
            stm.setString(5, l.getIdioma());
            stm.setBoolean(6, l.getStatus());
            stm.setLong(7, l.getIdAutor());
            stm.setLong(8, id);
            stm.executeUpdate();
        }
    }

    @Override
    public void apagar(long id) throws SQLException {
        String sql = "DELETE FROM livro WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }

    /**
     * Le a linha atual do ResultSet e monta um objeto Livro.
     * Metodo auxiliar privado para nao repetir este codigo em
     * consultarPorTitulo e buscarPorId.
     */
    private Livro montarLivro(ResultSet rs) throws SQLException {
        Livro l = new Livro();
        l.setId(rs.getLong("id"));
        l.setTitulo(rs.getString("titulo"));
        l.setIsbn(rs.getString("isbn"));
        l.setAnoPublicacao(rs.getInt("ano_publicacao"));
        l.setNumPaginas(rs.getInt("num_paginas"));
        l.setIdioma(rs.getString("idioma"));
        l.setStatus(rs.getBoolean("status"));
        l.setIdAutor(rs.getLong("id_autor"));
        l.setNomeAutor(rs.getString("nome_autor"));  // vem do JOIN
        return l;
    }
}
