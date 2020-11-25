package br.com.service.impl;

import java.util.Optional;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.model.entity.Usuario;
import br.com.model.repository.UsuarioRepository;
import br.com.service.UsuarioService;
import br.com.service.exception.ErroAutenticacaoException;
import br.com.service.exception.RegraNegocioException;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;

	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if (!usuario.isPresent()) {
			throw new ErroAutenticacaoException("Usuário não encontrado para o email informado.");
		}
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacaoException("Senha inválida.");
		}

		return usuario.get();
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);

	}

	@Override
	@Transactional
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
		}

	}
	
	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}

}
