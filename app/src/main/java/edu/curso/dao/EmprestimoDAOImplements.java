package edu.curso.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import edu.curso.model.Emprestimo;

public class EmprestimoDAOImplements implements EmprestimoDAO {
    private static final String DB_JDBC_URI = "jdbc:mariadb://localhost:3306/biblioteca?allowPublicKeyRetrieval=true&useSSL=false&allowMultiQueries=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123456";
    private Connection con;

    public EmprestimoDAOImplements() {
        System.out.println("Emprestimo DAO criado - com database");
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
    public void cadastrar(Emprestimo e) {
        try {
            String sql = "INSERT INTO emprestimo (data_emprestimo, data_devolucao, status, valor_multa, id_livro, id_usuario) VALUES " +
                    "(?, ?, ?, ?, ?, ?)";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setDate(1, java.sql.Date.valueOf(e.getDataEmprestimo()));
            stm.setDate(2, java.sql.Date.valueOf(e.getDataDevolucao()));
            stm.setBoolean(3, e.getStatus());
            stm.setBigDecimal(4, e.getValorMulta());
            stm.setLong(5, e.getIdLivro());
            stm.setLong(6, e.getIdUsuario());
            stm.executeUpdate();
            System.out.println("Comando executado com sucesso");
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar");
            ex.printStackTrace();
        }
    }

    @Override
    public List<Emprestimo> consultarPorUsuario(long idUsuario) {
        List<Emprestimo> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM emprestimo WHERE id_usuario = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setLong(1, idUsuario);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                LocalDate dataEmprestimo = rs.getDate("data_emprestimo").toLocalDate();
                LocalDate dataDevolucao = rs.getDate("data_devolucao").toLocalDate();
                Boolean status = rs.getBoolean("status");
                BigDecimal valorMulta = rs.getBigDecimal("valor_multa");
                Long idLivro = rs.getLong("id_livro");
                Long idUsuario2 = rs.getLong("id_usuario");
                Emprestimo emp = new Emprestimo();
                emp.setId(id);
                emp.setDataEmprestimo(dataEmprestimo);
                emp.setDataDevolucao(dataDevolucao);
                emp.setStatus(status);
                emp.setValorMulta(valorMulta);
                emp.setIdLivro(idLivro);
                emp.setIdUsuario(idUsuario2);
                lista.add(emp);
            }
            System.out.println("Comando executado com sucesso");
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar");
            ex.printStackTrace();
        }
        return lista;
    }

    @Override
    public void atualizar(long id, Emprestimo e) {
        try {
            String sql = "UPDATE emprestimo SET data_emprestimo = ?, data_devolucao = ?, status = ?, valor_multa = ?, id_livro = ?, id_usuario = ? WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setDate(1, java.sql.Date.valueOf(e.getDataEmprestimo()));
            stm.setDate(2, java.sql.Date.valueOf(e.getDataDevolucao()));
            stm.setBoolean(3, e.getStatus());
            stm.setBigDecimal(4, e.getValorMulta());
            stm.setLong(5, e.getIdLivro());
            stm.setLong(6, e.getIdUsuario());
            stm.setLong(7, id);
            stm.executeUpdate();
            System.out.println("Emprestimo atualizado com sucesso");
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar");
            ex.printStackTrace();
        }
    }

    @Override
    public void apagar(long id) {
        try {
            String sql = "DELETE FROM emprestimo WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setLong(1, id);
            stm.executeUpdate();
            System.out.println("Emprestimo apagado com sucesso");
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar");
            ex.printStackTrace();
        }
    }
}
