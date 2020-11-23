package br.com.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
@ActiveProfiles("test")
@DataJpaTest // FAZ UM ROOLBACK AO FINAL DE CADA TESTE
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		// cenário
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").build();

		// repository.save(usuario);
		entityManager.persist(usuario);
		// ação/ execução
		boolean result = repository.existsByEmail("usuario@email.com");

		// verificação
		Assertions.assertThat(result).isTrue();

	}

	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		// cenário
		// repository.deleteAll();
		// ação/ execução
		boolean result = repository.existsByEmail("usuario@email.com");

		// verificação
		Assertions.assertThat(result).isFalse();

	}

	@Test
	public void devePersistirUmUsuarioNaBaseDeDaDos() {
		// cenário
		Usuario usuario = criarUsuario();
		
		//acao 
		repository.save(usuario);
		
		//verificação
		Assertions.assertThat(usuario.getId()).isNotNull();
	}

	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		//ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		//verificação
		Assertions.assertThat(result.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBaseDeDados() {
		//ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		//verificação
		Assertions.assertThat(result.isPresent()).isFalse();
		
	}

	private static Usuario criarUsuario() {
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").senha("1234").build();
		return usuario;
		
	}
}
