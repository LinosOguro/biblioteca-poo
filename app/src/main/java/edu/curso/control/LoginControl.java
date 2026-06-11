package edu.curso.control;

import java.sql.SQLException;
import java.util.List;

import edu.curso.dao.UsuarioDAO;
import edu.curso.dao.UsuarioDAOImpl;
import edu.curso.model.Usuario;
import edu.curso.security.Sessao;

/**
 * CAMADA CONTROLE - Autenticacao (login).
 *
 * Fluxo: a tela de login entrega nome e senha digitados; este controle
 * busca o usuario no banco, confere a senha e, se tudo certo, REGISTRA
 * o usuario na Sessao (camada de seguranca). A partir dai, qualquer
 * parte do sistema sabe quem esta logado e qual o seu perfil.
 *
 * Devolve uma mensagem de erro, ou null quando o login deu certo
 * (padrao igual ao excluir() dos outros controles).
 *
 * Observacao didatica: a senha eh comparada em texto puro porque o
 * escopo do trabalho eh academico. Em um sistema real, o banco
 * guardaria apenas o "hash" da senha (ex: BCrypt), nunca a senha em si.
 */
public class LoginControl {

    private UsuarioDAO dao = new UsuarioDAOImpl();

    /** Tenta autenticar. Devolve null se OK, ou a mensagem de erro. */
    public String autenticar(String nome, String senha) {
        if (nome == null || nome.trim().isEmpty()
                || senha == null || senha.isEmpty()) {
            return "Preencha usuario e senha.";
        }

        try {
            // A consulta eh por LIKE (parte do nome), entao pode trazer mais
            // de um resultado; percorremos conferindo o nome EXATO.
            List<Usuario> candidatos = dao.consultarPorNome(nome.trim());
            for (Usuario u : candidatos) {
                if (u.getNome() != null && u.getNome().equalsIgnoreCase(nome.trim())) {
                    if (u.getSenha() != null && u.getSenha().equals(senha)) {
                        Sessao.login(u);   // registra na camada de seguranca
                        return null;       // sucesso!
                    }
                    return "Senha incorreta. Tente novamente.";
                }
            }
            return "Usuario nao encontrado. Cadastros sao feitos pelo administrador.";
        } catch (SQLException e) {
            return "Erro ao acessar o banco de dados. Verifique se o banco esta rodando.";
        }
    }
}
