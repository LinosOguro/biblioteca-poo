package edu.curso.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Autor;

public class AutorDAOImpl implements AutorDAO {
    private static final String DB_JDBC_URI = "jdbc:mariadb://localhost:3306/biblioteca?allowPublicKeyRetrieval=true&useSSL=false&allowMultiQueries=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123456";
    private Connection con;

    public AutorDAOImpl() {
        System.out.println("Autor DAO criado - com database");
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
    public void cadastrar(Autor a) {
        try {
            String sql = "INSERT INTO autor (nome, nacionalidade, dataNasc, email, biografia) VALUES " +
                    "(?, ?, ?, ?, ?)";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, a.getNome());
            stm.setString(2, a.getNacionalidade());
            stm.setDate(3, java.sql.Date.valueOf(a.getDataNasc()));
            stm.setString(4, a.getEmail());
            stm.setString(5, a.getBiografia());
            stm.executeUpdate();
            System.out.println("Comando executado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    @Override
    public List<Autor> consultarPorNome(String nome) {
        List<Autor> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM autor WHERE nome LIKE ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, "%" + nome + "%");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String autorNome = rs.getString("nome");
                String nacionalidade = rs.getString("nacionalidade");
                LocalDate dataNasc = rs.getDate("dataNasc").toLocalDate();
                String email = rs.getString("email");
                String biografia = rs.getString("biografia");
                Autor a = new Autor();
                a.setId(id);
                a.setNome(autorNome);
                a.setNacionalidade(nacionalidade);
                a.setDataNasc(dataNasc);
                a.setEmail(email);
                a.setBiografia(biografia);
                lista.add(a);
            }
            System.out.println("Comando executado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void atualizar(long id, Autor a) {
        try {
            String sql = "UPDATE autor SET nome = ?, nacionalidade = ?, dataNasc = ?, email = ?, biografia = ? WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, a.getNome());
            stm.setString(2, a.getNacionalidade());
            stm.setDate(3, java.sql.Date.valueOf(a.getDataNasc()));
            stm.setString(4, a.getEmail());
            stm.setString(5, a.getBiografia());
            stm.setLong(6, id);
            stm.executeUpdate();
            System.out.println("Autor atualizado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    @Override
    public void apagar(long id) {
        try {
            String sql = "DELETE FROM autor WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setLong(1, id);
            stm.executeUpdate();
            System.out.println("Autor apagado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }
}
