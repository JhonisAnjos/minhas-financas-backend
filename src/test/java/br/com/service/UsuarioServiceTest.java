package br.com.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.model.entity.Usuario;
import br.com.model.repository.UsuarioRepository;
import br.com.service.exception.ErroAutenticacaoException;
import br.com.service.exception.RegraNegocioException;
import br.com.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

//	UsuarioService service;
	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

//	@BeforeEach
//	public void setUp() {
//		// repository = Mockito.mock(UsuarioRepository.class);
//		service = new UsuarioServiceImpl(repository);
//	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		// cenário
		String email = "email@gmail.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

		// ação
		Usuario result = service.autenticar(email, senha);

		// verificação
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void deveSalvarUmUsuario() {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().nome("nome").senha("1234").email("email@email.com").id(1l).build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertNotNull(usuarioSalvo);
		Assertions.assertEquals(1l, usuarioSalvo.getId());
		Assertions.assertEquals("nome", usuarioSalvo.getNome());
		Assertions.assertEquals("email@email.com", usuarioSalvo.getEmail());
		Assertions.assertEquals("1234", usuarioSalvo.getSenha());
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComOEmailJaCadastrado() {
		//cenário
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//acao
		Assertions.assertThrows(RegraNegocioException.class, ()->service.salvarUsuario(usuario));
		
		//verificação
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
	}

	@Test
	public void deveLancarUmErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		// cenário
		String email = "email@gmail.com";
		String senha = "senha";

		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		// ação

		Throwable exception = Assertions.assertThrows(ErroAutenticacaoException.class, () -> service.autenticar(email, senha));
		Assertions.assertEquals("Usuário não encontrado para o email informado.", exception.getMessage());
	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		// cenário

		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		Throwable exception = Assertions.assertThrows(ErroAutenticacaoException.class,
				() -> service.autenticar("email@email.com", "diferente"));
		Assertions.assertEquals("Senha inválida.", exception.getMessage());

	}

	@Test
	public void deveValidarEmail() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		// acao
		Assertions.assertDoesNotThrow(() -> service.validarEmail("email@email.com"));
		

	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		// cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		// ação/ execução
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("usuario@email.com"));
	}

}
