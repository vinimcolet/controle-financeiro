package com.vinicius.controlefinanceiro.repository;

import com.vinicius.controlefinanceiro.entity.Categoria;
import com.vinicius.controlefinanceiro.entity.Lancamento;
import com.vinicius.controlefinanceiro.entity.Usuario;
import com.vinicius.controlefinanceiro.enums.TipoLancamento;
import com.vinicius.controlefinanceiro.test.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LancamentoRepositoryTest {

    @Autowired
    private LancamentoRepository repository;


    @Autowired
    private TestEntityManager em;

    @Test
    void deveSalvarLancamento() {
        Usuario usuario = em.persist(TestDataFactory.usuarioPadrao());
        Categoria categoria = em.persist(TestDataFactory.categoriaReceita());

        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Salário março");
        lancamento.setValor(new BigDecimal("5000.00"));
        lancamento.setData(LocalDate.of(2025, 3, 1));
        lancamento.setTipo(TipoLancamento.RECEITA);
        lancamento.setUsuario(usuario);
        lancamento.setCategoria(categoria);

        Lancamento salvo = repository.save(lancamento);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getDescricao()).isEqualTo("Salário março");
        assertThat(salvo.getValor()).isEqualByComparingTo("5000.00");
    }

    @Test
    void deveListarLancamentosPorUsuario() {
        Usuario usuario = em.persist(TestDataFactory.usuarioPadrao());
        Categoria categoria = em.persist(TestDataFactory.categoriaReceita());

        Lancamento l1 = new Lancamento();
        l1.setDescricao("Receita 1");
        l1.setValor(new BigDecimal("1000"));
        l1.setData(LocalDate.now());
        l1.setTipo(TipoLancamento.RECEITA);
        l1.setUsuario(usuario);
        l1.setCategoria(categoria);
        em.persist(l1);

        Lancamento l2 = new Lancamento();
        l2.setDescricao("Receita 2");
        l2.setValor(new BigDecimal("2000"));
        l2.setData(LocalDate.now());
        l2.setTipo(TipoLancamento.RECEITA);
        l2.setUsuario(usuario);
        l2.setCategoria(categoria);
        em.persist(l2);

        em.flush();

        var resultado = repository.findByUsuarioId(usuario.getId(), org.springframework.data.domain.Pageable.unpaged());

        assertThat(resultado.getTotalElements()).isEqualTo(2);
    }

    @Test
    void deveSomarReceitas() {

        Usuario usuario = em.persist(TestDataFactory.usuarioPadrao());
        Categoria categoria = em.persist(TestDataFactory.categoriaReceita());

        Lancamento l = new Lancamento();
        l.setDescricao("Salário");
        l.setValor(new BigDecimal("3000.00"));
        l.setData(LocalDate.of(2025, 1, 15));
        l.setTipo(TipoLancamento.RECEITA);
        l.setUsuario(usuario);
        l.setCategoria(categoria);
        em.persistAndFlush(l);

        BigDecimal total = repository.somarPorTipoEPeriodo(
                TipoLancamento.RECEITA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );

        assertThat(total).isEqualByComparingTo("3000.00");
    }
}