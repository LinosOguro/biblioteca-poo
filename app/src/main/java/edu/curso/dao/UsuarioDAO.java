package edu.curso.dao;

import java.sql.SQLException;
import java.util.List;
import edu.curso.model.Usuario;

/**
 * CAMADA DAO - Contrato de acesso a dados de Usuario.
 * (Veja o comentario em AutorDAO: o papel da interface eh o mesmo.)
 */
public interface UsuarioDAO {
    void cadastrar(Usuario u) throws SQLException;
    List<Usuario> consultarPorNome(String nome) throws SQLException;  // "" lista todos
    void atualizar(long id, Usuario u) throws SQLException;
    void apagar(long id) throws SQLException;
}
