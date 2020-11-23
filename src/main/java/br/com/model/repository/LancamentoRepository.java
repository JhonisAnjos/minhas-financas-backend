package br.com.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
