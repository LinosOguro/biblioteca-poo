package edu.curso.dao;

import java.sql.SQLException;
import java.util.List;
import edu.curso.model.Autor;

/**
 * CAMADA DAO - Contrato de acesso a dados de Autor.
 *
 * A interface define O QUE se pode fazer com autores no banco;
 * a classe AutorDAOImpl define COMO (com SQL). A camada de Controle
 * conversa apenas com esta interface - se um dia o banco mudar
 * (ex: de MariaDB para arquivo), basta trocar a implementacao.
 *
 * Todos os metodos lancam SQLException: quem trata o erro e o
 * transforma em mensagem amigavel eh a camada de Controle.
 */
public interface AutorDAO {
    void cadastrar(Autor a) throws SQLException;
    List<Autor> consultarPorNome(String nome) throws SQLException;  // "" lista todos
    void atualizar(long id, Autor a) throws SQLException;
    void apagar(long id) throws SQLException;
}
