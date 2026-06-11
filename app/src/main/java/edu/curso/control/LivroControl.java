package edu.curso.control;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.curso.dao.LivroDAO;
import edu.curso.dao.LivroDAOImpl;
import edu.curso.model.Autor;
import edu.curso.model.Livro;

/**
 * CAMADA CONTROLE - Regras e validacoes de Livro.
 * (O papel da camada esta explicado no comentario de AutorControl.)
 *
 * Diferenca em relacao ao AutorControl: aqui alguns campos chegam como
 * TEXTO mas precisam virar NUMERO (ano e paginas). A conversao tambem eh
 * responsabilidade do Controle - se o texto nao for um numero valido,
 * isso vira uma mensagem de erro do campo, como qualquer outra.
 */
public class LivroControl {

    private LivroDAO dao = new LivroDAOImpl();

    /**
     * Salva um livro (id == null -> INSERT / id != null -> UPDATE).
     * "disponivel" vem do CheckBox da tela; "autor" vem do ComboBox.
     * Devolve a lista de erros de validacao (vazia = sucesso).
     */
    public List<String> salvar(Long id, String titulo, String isbn, String anoTexto,
                               String paginasTexto, String idioma, boolean disponivel,
                               Autor autor) {
        List<String> erros = new ArrayList<>();

        if (titulo == null || titulo.trim().isEmpty()) {
            erros.add("Titulo: campo obrigatorio.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            erros.add("ISBN: campo obrigatorio.");
        } else if (isbn.trim().length() > 20) {
            erros.add("ISBN: maximo de 20 caracteres.");
        }

        // Conversao de texto para numero, com mensagem propria se falhar.
        Integer ano = converterInteiro(anoTexto);
        if (ano == null) {
            erros.add("Ano de publicacao: informe um numero inteiro valido.");
        } else if (ano < 1 || ano > LocalDate.now().getYear() + 1) {
            erros.add("Ano de publicacao: deve estar entre 1 e " + (LocalDate.now().getYear() + 1) + ".");
        }

        Integer paginas = converterInteiro(paginasTexto);
        if (paginas == null) {
            erros.add("Numero de paginas: informe um numero inteiro valido.");
        } else if (paginas <= 0) {
            erros.add("Numero de paginas: deve ser maior que zero.");
        }

        if (idioma == null || idioma.trim().isEmpty()) {
            erros.add("Idioma: campo obrigatorio.");
        }
        if (autor == null) {
            erros.add("Autor: selecione um autor na lista.");
        }

        if (!erros.isEmpty()) {
            return erros;
        }

        try {
            Livro l = new Livro(titulo.trim(), isbn.trim(), ano, paginas,
                                idioma.trim(), disponivel, autor.getId());
            if (id == null) {
                dao.cadastrar(l);
            } else {
                dao.atualizar(id, l);
            }
        } catch (SQLException e) {
            erros.add("Erro ao salvar no banco de dados: " + e.getMessage());
        }
        return erros;
    }

    /** Busca livros por parte do titulo ("" lista todos). */
    public List<Livro> listar(String filtro) {
        try {
            return dao.consultarPorTitulo(filtro == null ? "" : filtro.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Exclui um livro. Devolve null se deu certo, ou a mensagem de erro. */
    public String excluir(long id) {
        try {
            dao.apagar(id);
            return null;
        } catch (SQLException e) {
            return "Nao foi possivel excluir: existem emprestimos vinculados a este livro.";
        }
    }

    /**
     * Tenta converter um texto em Integer.
     * Devolve null se o texto for vazio ou nao for um numero.
     */
    private Integer converterInteiro(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
