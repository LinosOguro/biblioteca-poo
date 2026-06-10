package edu.curso.control;

import java.util.List;

import edu.curso.dao.UsuarioDAO;
import edu.curso.dao.UsuarioDAOImpl;
import edu.curso.model.Usuario;

public class LoginControl {

    public String logar(String nome, String senha) {
        try {
            UsuarioDAO dao = new UsuarioDAOImpl();
            List<Usuario> lista = dao.consultarPorNome(nome);
            if (lista == null || lista.isEmpty()) {
                return "USUARIO_NAO_EXISTE";
            }

            for (Usuario u : lista) {
                if (u.getNome() != null && u.getNome().equalsIgnoreCase(nome)) {
                    if (u.getSenha() != null && u.getSenha().equals(senha)) {
                        return "LOGIN_OK";
                    } else {
                        return "SENHA_INCORRETA";
                    }
                }
            }

            return "USUARIO_NAO_EXISTE";
        } catch (RuntimeException e) {
            return "ERRO_BD";
        }
    }

    public Usuario autenticar(String nome, String senha) {
        try {
            UsuarioDAO dao = new UsuarioDAOImpl();
            List<Usuario> lista = dao.consultarPorNome(nome);
            if (lista == null) return null;
            for (Usuario u : lista) {
                if (u.getNome() != null && u.getNome().equalsIgnoreCase(nome)
                        && u.getSenha() != null && u.getSenha().equals(senha)) {
                    return u;
                }
            }
            return null;
        } catch (RuntimeException e) {
            return null;
        }
    }
}