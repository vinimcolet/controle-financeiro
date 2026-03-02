package com.vinicius.controlefinanceiro.repository;

import com.vinicius.controlefinanceiro.entity.Lancamento;
import com.vinicius.controlefinanceiro.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    Page<Lancamento> findAll(Pageable pageable);

    Page<Lancamento> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("""
        SELECT l FROM Lancamento l
        WHERE l.data BETWEEN :inicio AND :fim
    """)
    Page<Lancamento> filtrarPorPeriodo(
            LocalDate inicio,
            LocalDate fim,
            Pageable pageable
    );


    @Query("""
    SELECT COALESCE(SUM(l.valor), 0)
    FROM Lancamento l
    WHERE l.tipo = :tipo
      AND l.data BETWEEN :inicio AND :fim
""")
    BigDecimal somarPorTipoEPeriodo(
            TipoLancamento tipo,
            LocalDate inicio,
            LocalDate fim
    );
}


