package br.com.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.model.entity.Lancamento;
import br.com.model.entity.Usuario;
import br.com.model.enuns.StatusLancamento;
import br.com.model.enuns.TipoLancamento;
import br.com.model.repository.LancamentoRepository;
import br.com.model.repository.LancamentoRepositoryTest;
import br.com.service.exception.RegraNegocioException;
import br.com.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;

	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		// cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// execucao
		service.salvar(lancamentoASalvar);

		Lancamento lancamento = service.salvar(lancamentoASalvar);

		// verificacao
		Assertions.assertEquals(lancamentoSalvo.getId(), lancamento.getId());
		Assertions.assertEquals(StatusLancamento.PENDENTE, lancamento.getStatus());
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		// verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.salvar(lancamentoASalvar));
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		// cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// execucao
		service.atualizar(lancamentoSalvo);

		// verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);

	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// execução e verificação
		Assertions.assertThrows(NullPointerException.class, () -> service.atualizar(lancamento));
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}

	@Test
	public void deveDeletarUmLancamento() {
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		// execucao
		service.deletar(lancamento);

		// verificacao
		Mockito.verify(repository).delete(lancamento);

	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// execucao e verificação
		Assertions.assertThrows(NullPointerException.class, () -> service.deletar(lancamento));
		Mockito.verify(repository, Mockito.never()).delete(lancamento);

	}

	@Test
	public void devaFiltrarLancamentos() {
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// execucao
		List<Lancamento> resultado = service.buscar(lancamento);

		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(Integer.valueOf(1), resultado.size());
		Assertions.assertTrue(resultado.contains(lancamento));

	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// execução
		service.atualizarStatus(lancamento, novoStatus);

		// verifivação
		Assertions.assertEquals(novoStatus, lancamento.getStatus());
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		// cenário
		Long id =1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		//execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificação
		Assertions.assertTrue(resultado.isPresent());
		
		
	}
	
	@Test
	public void deveRetornarVazioQuandoLancamentoNaoExistir() {
		
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		//execução
		Optional<Lancamento> resultado = service.obterPorId(1l);
		
		//verificação
		Assertions.assertFalse(resultado.isPresent());
	}
	
	@Test
	public void deveLancarErroAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertEquals("Informe uma descrição válida.", erro.getMessage());
		
		lancamento.setDescricao("Salário");
		
		erro = Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertEquals("Informe um mês válido.", erro.getMessage());
		
		lancamento.setMes(1);
		
		erro = Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertEquals("Informe um ano válido.", erro.getMessage());
		
		lancamento.setAno(Integer.valueOf(2020));
		
		erro = Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertEquals("Informe um usuário.", erro.getMessage());
		
		lancamento.setUsuario(Usuario.builder().id(1l).build());
		
		erro = Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertEquals("Informe um valor válido.", erro.getMessage());
		
		lancamento.setValor(BigDecimal.valueOf(1094));
		
		erro = Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertEquals("Informe um tipo de lançamento.", erro.getMessage());
		
		lancamento.setTipo(TipoLancamento.RECEITA);
		
		Assertions.assertDoesNotThrow(() -> service.validar(lancamento));

		
	}

}
