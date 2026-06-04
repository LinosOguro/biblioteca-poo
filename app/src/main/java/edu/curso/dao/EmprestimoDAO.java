package edu.curso.dao;

import java.util.List;
import edu.curso.model.Emprestimo;

public interface EmprestimoDAO {
    void cadastrar(Emprestimo e);
    List<Emprestimo> consultarPorUsuario(long idUsuario);
    void atualizar(long id, Emprestimo e);
    void apagar(long id);
}
