package br.com.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.api.dto.UsuarioDTO;
import br.com.model.entity.Usuario;
import br.com.service.LancamentoService;
import br.com.service.UsuarioService;
import br.com.service.exception.ErroAutenticacaoException;
import br.com.service.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

	private final UsuarioService service;
	
	private final LancamentoService lancamentoService; 

	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody UsuarioDTO dto) {
		Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto){
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok().body(usuarioAutenticado);
		} catch (ErroAutenticacaoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/{id}/saldo")
	public ResponseEntity<?> obterSaldo(@PathVariable("id") Long id){
		Optional<Usuario> usuario = service.obterPorId(id);
		if(!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}else {
			BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
			return ResponseEntity.ok().body(saldo);
		}
		
	}
	
}
