package com.vinicius.controlefinanceiro.service;

import com.vinicius.controlefinanceiro.dto.request.CategoriaRequestDTO;
import com.vinicius.controlefinanceiro.entity.Categoria;
import com.vinicius.controlefinanceiro.enums.TipoLancamento;
import com.vinicius.controlefinanceiro.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    private CategoriaRequestDTO dtoReceita;
    private CategoriaRequestDTO dtoDespesa;

    @BeforeEach
    void setUp() {
        dtoReceita = new CategoriaRequestDTO();
        dtoReceita.setNome("Salário");
        dtoReceita.setTipo(TipoLancamento.RECEITA);

        dtoDespesa = new CategoriaRequestDTO();
        dtoDespesa.setNome("Aluguel");
        dtoDespesa.setTipo(TipoLancamento.DESPESA);
    }

    @Test
    void deveCriarCategoriaReceitaComSucesso() {
        Categoria categoriaSalva = new Categoria();
        categoriaSalva.setId(1L);
        categoriaSalva.setNome("Salário");
        categoriaSalva.setTipo(TipoLancamento.RECEITA);

        when(repository.save(any(Categoria.class))).thenReturn(categoriaSalva);

        Categoria resultado = service.criar(dtoReceita);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Salário");
        assertThat(resultado.getTipo()).isEqualTo(TipoLancamento.RECEITA);
    }

    @Test
    void deveCriarCategoriaDespesaComSucesso() {
        when(repository.save(any(Categoria.class))).thenAnswer(i -> {
            Categoria c = i.getArgument(0);
            c.setId(2L);
            return c;
        });

        Categoria resultado = service.criar(dtoDespesa);

        assertThat(resultado.getNome()).isEqualTo("Aluguel");
        assertThat(resultado.getTipo()).isEqualTo(TipoLancamento.DESPESA);
    }

    @Test
    void deveMappearNomeETipoCorretamenteAntesDeSlavar() {

        when(repository.save(any(Categoria.class))).thenAnswer(i -> i.getArgument(0));

        Categoria resultado = service.criar(dtoReceita);

        assertThat(resultado.getNome()).isEqualTo(dtoReceita.getNome());
        assertThat(resultado.getTipo()).isEqualTo(dtoReceita.getTipo());
    }

    @Test
    void deveChamarRepositorySaveUmaVez() {
        when(repository.save(any(Categoria.class))).thenAnswer(i -> i.getArgument(0));

        service.criar(dtoReceita);

        verify(repository, times(1)).save(any(Categoria.class));
    }
}