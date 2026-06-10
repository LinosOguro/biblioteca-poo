package edu.curso.control;

import edu.curso.dao.UsuarioDAOImpl;
import edu.curso.model.Usuario;

public class UsuarioControl {

	public void cadastrarUsuario(String nome, String email, String telefone, String cpf, String senha) {
		if (nome == null || nome.trim().isEmpty()) {
			throw new IllegalArgumentException("Nome é obrigatório");
		}
		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("Email é obrigatório");
		}
		if (cpf == null || cpf.trim().isEmpty()) {
			throw new IllegalArgumentException("CPF é obrigatório");
		}
		if (senha == null || senha.isEmpty()) {
			throw new IllegalArgumentException("Senha é obrigatória");
		}

		if (telefone != null && !telefone.isEmpty() && !telefone.matches("\\d{10}|\\d{11}")) {
			throw new IllegalArgumentException("Telefone inválido. Use apenas dígitos (10 ou 11 caracteres).");
		}

		Usuario usuario = new Usuario(nome.trim(), email.trim(), (telefone == null ? null : telefone.trim()), cpf.trim(), senha);
		new UsuarioDAOImpl().cadastrar(usuario);
	}
}
