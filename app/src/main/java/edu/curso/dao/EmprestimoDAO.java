package edu.curso.dao;

import java.sql.SQLException;
import java.util.List;
import edu.curso.model.Emprestimo;

/**
 * CAMADA DAO - Contrato de acesso a dados de Emprestimo.
 * (Veja o comentario em AutorDAO: o papel da interface eh o mesmo.)
 */
public interface EmprestimoDAO {
    void cadastrar(Emprestimo e) throws SQLException;
    List<Emprestimo> listarTodos() throws SQLException;                       // visao do ADMIN
    List<Emprestimo> consultarPorUsuario(long idUsuario) throws SQLException; // visao do usuario COMUM
    void atualizar(long id, Emprestimo e) throws SQLException;
    void apagar(long id) throws SQLException;
}
