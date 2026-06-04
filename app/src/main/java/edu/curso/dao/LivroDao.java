package edu.curso.dao;

import java.util.List;
import edu.curso.model.Livro;

public interface LivroDao {
    void cadastrar(Livro l);
    List<Livro> consultarPorTitulo(String titulo);
    void atualizar(long id, Livro l);
    void apagar(long id);
}
