package edu.curso.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {
    private static final String DB_JDBC_URI = "jdbc:mariadb://localhost:3306/biblioteca?allowPublicKeyRetrieval=true&useSSL=false&allowMultiQueries=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123456";
    private Connection con;

    public UsuarioDAOImpl() {
        System.out.println("Usuario DAO criado - com database");
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("Classe carregada...");
            con = DriverManager.getConnection(DB_JDBC_URI, DB_USER, DB_PASS);
            System.out.println("Conexao foi feita com sucesso");
        } catch (ClassNotFoundException e) {
            System.out.println("Erro ao carregar a classe");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    @Override
    public void cadastrar(Usuario u) {
        try {
            String sql = "INSERT INTO usuario (nome, email, telefone, cpf, senha) VALUES " +
                    "(?, ?, ?, ?, ?)";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, u.getNome());
            stm.setString(2, u.getEmail());
            stm.setString(3, u.getTelefone());
            stm.setString(4, u.getCpf());
            stm.setString(5, u.getSenha());
            stm.executeUpdate();
            System.out.println("Comando executado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    @Override
    public List<Usuario> consultarPorNome(String nome) {
        List<Usuario> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM usuario WHERE nome LIKE ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, "%" + nome + "%");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String usuarioNome = rs.getString("nome");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                String cpf = rs.getString("cpf");
                String senha = rs.getString("senha");
                Usuario u = new Usuario();
                u.setId(id);
                u.setNome(usuarioNome);
                u.setEmail(email);
                u.setTelefone(telefone);
                u.setCpf(cpf);
                u.setSenha(senha);
                lista.add(u);
            }
            System.out.println("Comando executado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void atualizar(long id, Usuario u) {
        try {
            String sql = "UPDATE usuario SET nome = ?, email = ?, telefone = ?, cpf = ?, senha = ? WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, u.getNome());
            stm.setString(2, u.getEmail());
            stm.setString(3, u.getTelefone());
            stm.setString(4, u.getCpf());
            stm.setString(5, u.getSenha());
            stm.setLong(6, id);
            stm.executeUpdate();
            System.out.println("Usuario atualizado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    @Override
    public void apagar(long id) {
        try {
            String sql = "DELETE FROM usuario WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setLong(1, id);
            stm.executeUpdate();
            System.out.println("Usuario apagado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }
}
