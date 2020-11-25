package br.com.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.model.entity.Lancamento;
import br.com.model.enuns.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar (Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);

	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	public Optional<Lancamento> obterPorId(Long id);
	
	public BigDecimal obterSaldoPorUsuario(Long id);
}
