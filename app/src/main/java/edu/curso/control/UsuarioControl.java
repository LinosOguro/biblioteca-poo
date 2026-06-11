package edu.curso.control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.curso.dao.UsuarioDAO;
import edu.curso.dao.UsuarioDAOImpl;
import edu.curso.model.Usuario;
import edu.curso.security.Sessao;

/**
 * CAMADA CONTROLE - Regras e validacoes de Usuario.
 * (O papel da camada esta explicado no comentario de AutorControl.)
 *
 * As validacoes espelham os CHECKs do banco (cpf com 11 digitos, senha
 * entre 4 e 50 etc): validamos aqui ANTES para dar mensagens amigaveis,
 * e o banco confere de novo como ultima barreira.
 */
public class UsuarioControl {

    private UsuarioDAO dao = new UsuarioDAOImpl();

    /**
     * Salva um usuario (id == null -> INSERT / id != null -> UPDATE).
     * Devolve a lista de erros de validacao (vazia = sucesso).
     */
    public List<String> salvar(Long id, String nome, String email, String telefone,
                               String cpf, String senha, String perfil) {
        List<String> erros = new ArrayList<>();

        if (nome == null || nome.trim().isEmpty()) {
            erros.add("Nome: campo obrigatorio.");
        }
        if (email == null || email.trim().isEmpty()) {
            erros.add("Email: campo obrigatorio.");
        } else if (!email.contains("@")) {
            erros.add("Email: formato invalido (precisa conter @).");
        }
        // Telefone eh opcional, mas se preenchido deve ter 10 ou 11 digitos.
        // \\d{10} significa "exatamente 10 digitos" (expressao regular).
        if (telefone != null && !telefone.trim().isEmpty()
                && !telefone.trim().matches("\\d{10}|\\d{11}")) {
            erros.add("Telefone: use apenas numeros, com 10 ou 11 digitos.");
        }
        if (cpf == null || !cpf.trim().matches("\\d{11}")) {
            erros.add("CPF: use apenas numeros, com exatamente 11 digitos.");
        }
        if (senha == null || senha.length() < 4 || senha.length() > 50) {
            erros.add("Senha: deve ter entre 4 e 50 caracteres.");
        }
        if (perfil == null || perfil.trim().isEmpty()) {
            erros.add("Perfil: selecione ADMIN ou COMUM.");
        }

        if (!erros.isEmpty()) {
            return erros;
        }

        try {
            String tel = (telefone == null || telefone.trim().isEmpty()) ? null : telefone.trim();
            Usuario u = new Usuario(nome.trim(), email.trim(), tel, cpf.trim(), senha, perfil);
            if (id == null) {
                dao.cadastrar(u);
            } else {
                dao.atualizar(id, u);
            }
        } catch (SQLException e) {
            erros.add("Erro ao salvar no banco de dados: " + e.getMessage());
        }
        return erros;
    }

    /** Busca usuarios por parte do nome ("" lista todos). */
    public List<Usuario> listar(String filtro) {
        try {
            return dao.consultarPorNome(filtro == null ? "" : filtro.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Exclui um usuario. Devolve null se deu certo, ou a mensagem de erro. */
    public String excluir(long id) {
        // Regra de seguranca: ninguem pode excluir a si mesmo logado.
        Usuario logado = Sessao.getUsuarioLogado();
        if (logado != null && logado.getId() != null && logado.getId() == id) {
            return "Voce nao pode excluir o usuario que esta logado.";
        }
        try {
            dao.apagar(id);
            return null;
        } catch (SQLException e) {
            return "Nao foi possivel excluir: existem emprestimos vinculados a este usuario.";
        }
    }
}
