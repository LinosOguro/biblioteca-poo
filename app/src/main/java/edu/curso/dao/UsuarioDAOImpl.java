package edu.curso.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Usuario;

/**
 * CAMADA DAO - Implementacao do acesso a dados de Usuario com JDBC + MariaDB.
 * (O padrao de cada metodo esta explicado no comentario de AutorDAOImpl.)
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public void cadastrar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nome, email, telefone, cpf, senha, perfil) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, u.getNome());
            stm.setString(2, u.getEmail());
            stm.setString(3, u.getTelefone());
            stm.setString(4, u.getCpf());
            stm.setString(5, u.getSenha());
            stm.setString(6, u.getPerfil());
            stm.executeUpdate();
        }
    }

    @Override
    public List<Usuario> consultarPorNome(String nome) throws SQLException {
        // Com nome = "" lista todos (LIKE '%%'). Veja explicacao em AutorDAOImpl.
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE nome LIKE ? ORDER BY nome";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, "%" + nome + "%");
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setTelefone(rs.getString("telefone"));
                    u.setCpf(rs.getString("cpf"));
                    u.setSenha(rs.getString("senha"));
                    u.setPerfil(rs.getString("perfil"));
                    lista.add(u);
                }
            }
        }
        return lista;
    }

    @Override
    public void atualizar(long id, Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, telefone = ?, cpf = ?, "
                   + "senha = ?, perfil = ? WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, u.getNome());
            stm.setString(2, u.getEmail());
            stm.setString(3, u.getTelefone());
            stm.setString(4, u.getCpf());
            stm.setString(5, u.getSenha());
            stm.setString(6, u.getPerfil());
            stm.setLong(7, id);
            stm.executeUpdate();
        }
    }

    @Override
    public void apagar(long id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }
}
