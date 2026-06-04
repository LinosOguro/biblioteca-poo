package edu.curso.dao;

import java.util.List;
import edu.curso.model.Usuario;

public interface UsuarioDAO {
    void cadastrar(Usuario u);
    List<Usuario> consultarPorNome(String nome);
    void atualizar(long id, Usuario u);
    void apagar(long id);
}
