package br.com.service;

import br.com.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario (Usuario usuario);
	
	void validarEmail(String email);

}
