package edu.curso.dao;

import java.sql.SQLException;
import java.util.List;
import edu.curso.model.Livro;

/**
 * CAMADA DAO - Contrato de acesso a dados de Livro.
 * (Veja o comentario em AutorDAO: o papel da interface eh o mesmo.)
 */
public interface LivroDAO {
    void cadastrar(Livro l) throws SQLException;
    List<Livro> consultarPorTitulo(String titulo) throws SQLException;  // "" lista todos
    Livro buscarPorId(long id) throws SQLException;                     // usado pelas regras de emprestimo
    void atualizar(long id, Livro l) throws SQLException;
    void apagar(long id) throws SQLException;
}
