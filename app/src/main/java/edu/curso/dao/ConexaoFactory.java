package edu.curso.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CAMADA DAO - Fabrica de conexoes com o banco de dados.
 *
 * Centraliza os dados de conexao (URL, usuario e senha) em UM unico lugar.
 * Antes, cada DAO repetia essas constantes; agora, se a senha ou a porta do
 * banco mudar, basta alterar AQUI e todos os DAOs passam a usar o novo valor.
 *
 * Cada chamada a getConexao() abre uma conexao NOVA. Quem pede a conexao
 * (os DAOs) eh responsavel por fecha-la - por isso os DAOs usam
 * try-with-resources, que fecha a conexao automaticamente ao final do bloco.
 */
public class ConexaoFactory {

    // Endereco do banco: protocolo jdbc:mariadb, maquina localhost, porta 3306,
    // banco de dados "biblioteca" (criado pelo script bibliotecadb.sql).
    private static final String URL =
            "jdbc:mariadb://localhost:3306/biblioteca?allowPublicKeyRetrieval=true&useSSL=false&allowMultiQueries=true";
    private static final String USUARIO = "root";
    private static final String SENHA = "123456";

    /**
     * Abre e devolve uma nova conexao com o banco.
     * Se o banco estiver fora do ar (ex: container Docker parado),
     * uma SQLException eh lancada e tratada na camada de Controle.
     */
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
