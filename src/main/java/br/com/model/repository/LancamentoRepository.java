package br.com.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.model.entity.Lancamento;
import br.com.model.enuns.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	@Query(value = "SELECT sum(l.valor) FROM Lancamento l JOIN l.usuario u "
			+ "WHERE u.id = :idUsuario AND l.tipo = :tipo GROUP BY u ")
	BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario") Long id, @Param("tipo") TipoLancamento tipo);

}
