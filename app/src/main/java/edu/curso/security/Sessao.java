package edu.curso.security;

import edu.curso.model.Usuario;

/**
 * CAMADA DE SEGURANCA - Sessao do usuario logado.
 *
 * Guarda QUEM esta usando o sistema neste momento. Funciona como um
 * "cracha digital": o login registra o usuario aqui, e qualquer tela ou
 * controle pode perguntar quem esta logado e qual o seu perfil, sem
 * precisar ficar passando o objeto Usuario de tela em tela.
 *
 * Os membros sao "static" porque existe UMA unica sessao para o programa
 * inteiro (so uma pessoa usa o sistema por vez nesta aplicacao desktop).
 *
 * Os 2 perfis de acesso do sistema:
 *   ADMIN -> gerencia tudo (livros, autores, usuarios e emprestimos)
 *   COMUM -> apenas consulta livros e visualiza os proprios emprestimos
 */
public class Sessao {

    /** O usuario autenticado no momento (null = ninguem logado). */
    private static Usuario usuarioLogado;

    /** Registra o usuario que acabou de se autenticar (chamado pelo LoginControl). */
    public static void login(Usuario usuario) {
        usuarioLogado = usuario;
    }

    /** Encerra a sessao (chamado quando o usuario clica em Sair / Logout). */
    public static void logout() {
        usuarioLogado = null;
    }

    /** Devolve o usuario logado (ou null, se ninguem fez login ainda). */
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Pergunta central da camada de seguranca: o usuario logado eh ADMIN?
     * As telas usam esta resposta para esconder botoes e bloquear acessos.
     */
    public static boolean isAdmin() {
        return usuarioLogado != null && usuarioLogado.isAdmin();
    }
}
