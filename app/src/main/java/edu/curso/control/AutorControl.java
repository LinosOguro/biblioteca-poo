package edu.curso.control;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.curso.dao.AutorDAO;
import edu.curso.dao.AutorDAOImpl;
import edu.curso.model.Autor;

/**
 * CAMADA CONTROLE - Regras e validacoes de Autor.
 *
 * Papel desta camada: ficar ENTRE a tela (Boundary) e o banco (DAO).
 *  - A tela entrega os textos digitados, sem se preocupar com regras.
 *  - O Controle valida campo por campo, acumulando UMA mensagem por erro
 *    (por isso os metodos devolvem List<String>: lista VAZIA = sucesso).
 *  - Se tudo estiver valido, o Controle monta o objeto e chama o DAO.
 *
 * Este mesmo padrao se repete em LivroControl, UsuarioControl e
 * EmprestimoControl - aprendeu um, aprendeu todos.
 */
public class AutorControl {

    // Programamos "para a interface": se a implementacao do DAO mudar,
    // este controle continua funcionando sem alteracoes.
    private AutorDAO dao = new AutorDAOImpl();

    /**
     * Salva um autor. Se id == null, eh um cadastro novo (INSERT);
     * se id != null, eh uma edicao (UPDATE).
     * Recebe os textos crus da tela e devolve a lista de erros (vazia = ok).
     */
    public List<String> salvar(Long id, String nome, String nacionalidade,
                               LocalDate dataNasc, String email, String biografia) {
        List<String> erros = new ArrayList<>();

        // ---------- Validacao: uma mensagem especifica para cada campo ----------
        if (nome == null || nome.trim().isEmpty()) {
            erros.add("Nome: campo obrigatorio.");
        }
        if (nacionalidade == null || nacionalidade.trim().isEmpty()) {
            erros.add("Nacionalidade: campo obrigatorio.");
        }
        if (dataNasc == null) {
            erros.add("Data de nascimento: campo obrigatorio.");
        } else if (dataNasc.isAfter(LocalDate.now())) {
            erros.add("Data de nascimento: nao pode estar no futuro.");
        }
        if (email == null || email.trim().isEmpty()) {
            erros.add("Email: campo obrigatorio.");
        } else if (!email.contains("@")) {
            erros.add("Email: formato invalido (precisa conter @).");
        }
        if (biografia == null || biografia.trim().isEmpty()) {
            erros.add("Biografia: campo obrigatorio.");
        }

        // Se algum campo falhou, devolve os erros sem tocar no banco.
        if (!erros.isEmpty()) {
            return erros;
        }

        // ---------- Persistencia ----------
        try {
            Autor a = new Autor(nome.trim(), nacionalidade.trim(), dataNasc,
                                email.trim(), biografia.trim());
            if (id == null) {
                dao.cadastrar(a);      // novo registro
            } else {
                dao.atualizar(id, a);  // edicao de registro existente
            }
        } catch (SQLException e) {
            erros.add("Erro ao salvar no banco de dados: " + e.getMessage());
        }
        return erros;
    }

    /** Busca autores por parte do nome ("" lista todos). */
    public List<Autor> listar(String filtro) {
        try {
            return dao.consultarPorNome(filtro == null ? "" : filtro.trim());
        } catch (SQLException e) {
            // Em erro de banco, devolve lista vazia para a tela nao quebrar.
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Exclui um autor. Devolve null se deu certo, ou a mensagem de erro. */
    public String excluir(long id) {
        try {
            dao.apagar(id);
            return null;
        } catch (SQLException e) {
            // Erro tipico aqui: o autor tem livros cadastrados (chave estrangeira).
            return "Nao foi possivel excluir: existem livros vinculados a este autor.";
        }
    }
}
