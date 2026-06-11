package edu.curso.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Emprestimo;

/**
 * CAMADA DAO - Implementacao do acesso a dados de Emprestimo com JDBC + MariaDB.
 * (O padrao de cada metodo esta explicado no comentario de AutorDAOImpl.)
 *
 * Os SELECTs usam JOIN com livro e usuario para trazer o titulo do livro
 * e o nome do usuario (campos de exibicao do model), deixando a TableView
 * legivel sem precisar decorar ids.
 */
public class EmprestimoDAOImpl implements EmprestimoDAO {

    // Parte do SELECT compartilhada pelos dois metodos de consulta.
    private static final String SELECT_BASE =
            "SELECT e.*, l.titulo AS titulo_livro, u.nome AS nome_usuario "
          + "FROM emprestimo e "
          + "JOIN livro l ON e.id_livro = l.id "
          + "JOIN usuario u ON e.id_usuario = u.id ";

    @Override
    public void cadastrar(Emprestimo e) throws SQLException {
        String sql = "INSERT INTO emprestimo (data_emprestimo, data_devolucao, status, valor_multa, id_livro, id_usuario) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setDate(1, java.sql.Date.valueOf(e.getDataEmprestimo()));
            stm.setDate(2, java.sql.Date.valueOf(e.getDataDevolucao()));
            stm.setBoolean(3, e.getStatus());
            stm.setBigDecimal(4, e.getValorMulta());
            stm.setLong(5, e.getIdLivro());
            stm.setLong(6, e.getIdUsuario());
            stm.executeUpdate();
        }
    }

    @Override
    public List<Emprestimo> listarTodos() throws SQLException {
        // Visao do ADMIN: todos os emprestimos do sistema.
        List<Emprestimo> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY e.data_emprestimo DESC";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                lista.add(montarEmprestimo(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Emprestimo> consultarPorUsuario(long idUsuario) throws SQLException {
        // Visao do usuario COMUM: apenas os emprestimos dele.
        List<Emprestimo> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE e.id_usuario = ? ORDER BY e.data_emprestimo DESC";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, idUsuario);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarEmprestimo(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void atualizar(long id, Emprestimo e) throws SQLException {
        String sql = "UPDATE emprestimo SET data_emprestimo = ?, data_devolucao = ?, "
                   + "status = ?, valor_multa = ?, id_livro = ?, id_usuario = ? WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setDate(1, java.sql.Date.valueOf(e.getDataEmprestimo()));
            stm.setDate(2, java.sql.Date.valueOf(e.getDataDevolucao()));
            stm.setBoolean(3, e.getStatus());
            stm.setBigDecimal(4, e.getValorMulta());
            stm.setLong(5, e.getIdLivro());
            stm.setLong(6, e.getIdUsuario());
            stm.setLong(7, id);
            stm.executeUpdate();
        }
    }

    @Override
    public void apagar(long id) throws SQLException {
        String sql = "DELETE FROM emprestimo WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }

    /**
     * Le a linha atual do ResultSet e monta um objeto Emprestimo.
     * Auxiliar privado para nao repetir codigo nas duas consultas.
     */
    private Emprestimo montarEmprestimo(ResultSet rs) throws SQLException {
        Emprestimo e = new Emprestimo();
        e.setId(rs.getLong("id"));
        e.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        e.setDataDevolucao(rs.getDate("data_devolucao").toLocalDate());
        e.setStatus(rs.getBoolean("status"));
        e.setValorMulta(rs.getBigDecimal("valor_multa"));
        e.setIdLivro(rs.getLong("id_livro"));
        e.setIdUsuario(rs.getLong("id_usuario"));
        e.setTituloLivro(rs.getString("titulo_livro"));    // vem do JOIN
        e.setNomeUsuario(rs.getString("nome_usuario"));    // vem do JOIN
        return e;
    }
}
