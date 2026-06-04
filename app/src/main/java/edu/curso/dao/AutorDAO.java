package edu.curso.dao;

import java.util.List;
import edu.curso.model.Autor;

public interface AutorDAO {
    void cadastrar(Autor a);
    List<Autor> consultarPorNome(String nome);
    void atualizar(long id, Autor a);
    void apagar(long id);
}
