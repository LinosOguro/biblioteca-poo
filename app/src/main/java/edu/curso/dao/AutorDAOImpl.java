package edu.curso.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Autor;

/**
 * CAMADA DAO - Implementacao do acesso a dados de Autor com JDBC + MariaDB.
 *
 * Padrao usado em TODOS os metodos:
 *  1. Escrever o SQL com "?" no lugar dos valores (PreparedStatement evita
 *     SQL Injection e cuida da formatacao de datas, aspas etc).
 *  2. Abrir a conexao com try-with-resources: o "try (...)" fecha a conexao
 *     e o statement automaticamente no final, mesmo se der erro.
 *  3. Preencher cada "?" com stm.setXxx(posicao, valor) - posicao comeca em 1.
 *  4. executeUpdate() para INSERT/UPDATE/DELETE; executeQuery() para SELECT.
 */
public class AutorDAOImpl implements AutorDAO {

    @Override
    public void cadastrar(Autor a) throws SQLException {
        String sql = "INSERT INTO autor (nome, nacionalidade, dataNasc, email, biografia) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, a.getNome());
            stm.setString(2, a.getNacionalidade());
            stm.setDate(3, java.sql.Date.valueOf(a.getDataNasc())); // LocalDate -> DATE do banco
            stm.setString(4, a.getEmail());
            stm.setString(5, a.getBiografia());
            stm.executeUpdate();
        }
    }

    @Override
    public List<Autor> consultarPorNome(String nome) throws SQLException {
        // LIKE %texto% busca por parte do nome. Com nome = "" vira LIKE '%%',
        // que casa com tudo -> serve como "listar todos".
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT * FROM autor WHERE nome LIKE ? ORDER BY nome";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, "%" + nome + "%");
            try (ResultSet rs = stm.executeQuery()) {
                // rs.next() avanca para a proxima linha do resultado;
                // o while percorre todas as linhas e monta um objeto por linha.
                while (rs.next()) {
                    Autor a = new Autor();
                    a.setId(rs.getLong("id"));
                    a.setNome(rs.getString("nome"));
                    a.setNacionalidade(rs.getString("nacionalidade"));
                    a.setDataNasc(rs.getDate("dataNasc").toLocalDate());
                    a.setEmail(rs.getString("email"));
                    a.setBiografia(rs.getString("biografia"));
                    lista.add(a);
                }
            }
        }
        return lista;
    }

    @Override
    public void atualizar(long id, Autor a) throws SQLException {
        String sql = "UPDATE autor SET nome = ?, nacionalidade = ?, dataNasc = ?, "
                   + "email = ?, biografia = ? WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, a.getNome());
            stm.setString(2, a.getNacionalidade());
            stm.setDate(3, java.sql.Date.valueOf(a.getDataNasc()));
            stm.setString(4, a.getEmail());
            stm.setString(5, a.getBiografia());
            stm.setLong(6, id);  // WHERE id = ? -> qual registro alterar
            stm.executeUpdate();
        }
    }

    @Override
    public void apagar(long id) throws SQLException {
        String sql = "DELETE FROM autor WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }
}
