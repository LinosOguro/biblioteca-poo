package edu.curso.control;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.curso.dao.EmprestimoDAO;
import edu.curso.dao.EmprestimoDAOImpl;
import edu.curso.dao.LivroDAO;
import edu.curso.dao.LivroDAOImpl;
import edu.curso.model.Emprestimo;
import edu.curso.model.Livro;
import edu.curso.model.Usuario;
import edu.curso.security.Sessao;

/**
 * CAMADA CONTROLE - Regras e validacoes de Emprestimo.
 * (O papel da camada esta explicado no comentario de AutorControl.)
 *
 * Este eh o controle mais rico em REGRAS DE NEGOCIO do sistema:
 *
 *  1. So pode emprestar livro DISPONIVEL (livro.status = true).
 *  2. Ao criar um emprestimo, o livro vira EMPRESTADO (status = false).
 *  3. Ao marcar o emprestimo como devolvido, o livro volta a DISPONIVEL.
 *  4. PERFIS: o ADMIN enxerga todos os emprestimos; o usuario COMUM
 *     enxerga apenas os proprios (regra aplicada no metodo listar()).
 */
public class EmprestimoControl {

    private EmprestimoDAO dao = new EmprestimoDAOImpl();
    // Este controle tambem fala com o DAO de Livro, pois as regras de
    // emprestimo alteram a disponibilidade do livro.
    private LivroDAO livroDao = new LivroDAOImpl();

    /**
     * Salva um emprestimo (id == null -> INSERT / id != null -> UPDATE).
     * "livro" e "usuario" vem dos ComboBox; "devolvido" vem do CheckBox.
     * Devolve a lista de erros de validacao (vazia = sucesso).
     */
    public List<String> salvar(Long id, Livro livro, Usuario usuario,
                               LocalDate dataEmprestimo, LocalDate dataDevolucao,
                               boolean devolvido, String multaTexto) {
        List<String> erros = new ArrayList<>();

        if (livro == null) {
            erros.add("Livro: selecione um livro na lista.");
        }
        if (usuario == null) {
            erros.add("Usuario: selecione um usuario na lista.");
        }
        if (dataEmprestimo == null) {
            erros.add("Data do emprestimo: campo obrigatorio.");
        }
        if (dataDevolucao == null) {
            erros.add("Data de devolucao: campo obrigatorio.");
        }
        if (dataEmprestimo != null && dataDevolucao != null
                && dataDevolucao.isBefore(dataEmprestimo)) {
            erros.add("Data de devolucao: nao pode ser anterior a data do emprestimo.");
        }

        // Multa: campo opcional; vazio vale 0. Aceita virgula ou ponto.
        BigDecimal multa = BigDecimal.ZERO;
        if (multaTexto != null && !multaTexto.trim().isEmpty()) {
            try {
                multa = new BigDecimal(multaTexto.trim().replace(",", "."));
                if (multa.compareTo(BigDecimal.ZERO) < 0) {
                    erros.add("Multa: nao pode ser negativa.");
                }
            } catch (NumberFormatException e) {
                erros.add("Multa: informe um valor numerico valido (ex: 5.50).");
            }
        }

        // REGRA 1: emprestimo NOVO exige livro disponivel.
        // (Na edicao nao se aplica: o livro ja esta emprestado por ESTE registro.)
        if (id == null && livro != null && !Boolean.TRUE.equals(livro.getStatus())) {
            erros.add("Livro: '" + livro.getTitulo() + "' nao esta disponivel para emprestimo.");
        }

        if (!erros.isEmpty()) {
            return erros;
        }

        try {
            Emprestimo e = new Emprestimo(dataEmprestimo, dataDevolucao, devolvido,
                                          multa, livro.getId(), usuario.getId());
            if (id == null) {
                dao.cadastrar(e);
            } else {
                dao.atualizar(id, e);
            }

            // REGRAS 2 e 3: sincronizar a disponibilidade do livro.
            // Emprestimo em aberto (devolvido = false) -> livro indisponivel.
            // Emprestimo devolvido (devolvido = true)  -> livro disponivel.
            atualizarDisponibilidadeDoLivro(livro.getId(), devolvido);

        } catch (SQLException e) {
            erros.add("Erro ao salvar no banco de dados: " + e.getMessage());
        }
        return erros;
    }

    /**
     * Lista emprestimos APLICANDO A REGRA DE PERFIL:
     *   ADMIN -> todos os emprestimos do sistema;
     *   COMUM -> somente os emprestimos do proprio usuario logado.
     */
    public List<Emprestimo> listar() {
        try {
            if (Sessao.isAdmin()) {
                return dao.listarTodos();
            }
            return dao.consultarPorUsuario(Sessao.getUsuarioLogado().getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Exclui um emprestimo e libera o livro correspondente.
     * Devolve null se deu certo, ou a mensagem de erro.
     */
    public String excluir(long id, long idLivro) {
        try {
            dao.apagar(id);
            // Sem o registro de emprestimo, o livro volta a ficar disponivel.
            atualizarDisponibilidadeDoLivro(idLivro, true);
            return null;
        } catch (SQLException e) {
            return "Erro ao excluir o emprestimo: " + e.getMessage();
        }
    }

    /**
     * Busca o livro no banco e grava o novo status de disponibilidade.
     * disponivel = true  -> livro liberado;
     * disponivel = false -> livro marcado como emprestado.
     */
    private void atualizarDisponibilidadeDoLivro(long idLivro, boolean disponivel) throws SQLException {
        Livro l = livroDao.buscarPorId(idLivro);
        if (l != null) {
            l.setStatus(disponivel);
            livroDao.atualizar(idLivro, l);
        }
    }
}
