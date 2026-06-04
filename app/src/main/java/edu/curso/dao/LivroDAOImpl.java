package edu.curso.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Livro;

public class LivroDAOImpl implements LivroDao {
    private static final String DB_JDBC_URI = "jdbc:mariadb://localhost:3306/biblioteca?allowPublicKeyRetrieval=true&useSSL=false&allowMultiQueries=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123456";
    private Connection con;

    public LivroDAOImpl() {
        System.out.println("Livro DAO criado - com database");
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
    public void cadastrar(Livro l) {
        try {
            String sql = "INSERT INTO livro (titulo, isbn, ano_publicacao, num_paginas, idioma, status, id_autor) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, l.getTitulo());
            stm.setString(2, l.getIsbn());
            stm.setInt(3, l.getAnoPublicacao());
            stm.setInt(4, l.getNumPaginas());
            stm.setString(5, l.getIdioma());
            stm.setBoolean(6, l.getStatus());
            stm.setLong(7, l.getIdAutor());
            stm.executeUpdate();
            System.out.println("Comando executado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    @Override
    public List<Livro> consultarPorTitulo(String titulo) {
        List<Livro> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM livro WHERE titulo LIKE ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, "%" + titulo + "%");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String livroTitulo = rs.getString("titulo");
                String isbn = rs.getString("isbn");
                Integer anoPublicacao = rs.getInt("ano_publicacao");
                Integer numPaginas = rs.getInt("num_paginas");
                String idioma = rs.getString("idioma");
                Boolean status = rs.getBoolean("status");
                Long idAutor = rs.getLong("id_autor");
                Livro l = new Livro();
                l.setId(id);
                l.setTitulo(livroTitulo);
                l.setIsbn(isbn);
                l.setAnoPublicacao(anoPublicacao);
                l.setNumPaginas(numPaginas);
                l.setIdioma(idioma);
                l.setStatus(status);
                l.setIdAutor(idAutor);
                lista.add(l);
            }
            System.out.println("Comando executado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void atualizar(long id, Livro l) {
        try {
            String sql = "UPDATE livro SET titulo = ?, isbn = ?, ano_publicacao = ?, num_paginas = ?, idioma = ?, status = ?, id_autor = ? WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, l.getTitulo());
            stm.setString(2, l.getIsbn());
            stm.setInt(3, l.getAnoPublicacao());
            stm.setInt(4, l.getNumPaginas());
            stm.setString(5, l.getIdioma());
            stm.setBoolean(6, l.getStatus());
            stm.setLong(7, l.getIdAutor());
            stm.setLong(8, id);
            stm.executeUpdate();
            System.out.println("Livro atualizado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    @Override
    public void apagar(long id) {
        try {
            String sql = "DELETE FROM livro WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setLong(1, id);
            stm.executeUpdate();
            System.out.println("Livro apagado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }
}
