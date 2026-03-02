package com.vinicius.controlefinanceiro.controller;

import com.vinicius.controlefinanceiro.entity.Categoria;
import com.vinicius.controlefinanceiro.entity.Lancamento;
import com.vinicius.controlefinanceiro.entity.Usuario;
import com.vinicius.controlefinanceiro.enums.Role;
import com.vinicius.controlefinanceiro.enums.TipoLancamento;
import com.vinicius.controlefinanceiro.dto.response.ResumoFinanceiroDTO;
import com.vinicius.controlefinanceiro.security.JwtFilter;
import com.vinicius.controlefinanceiro.security.JwtService;
import com.vinicius.controlefinanceiro.service.LancamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LancamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class LancamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LancamentoService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private Lancamento lancamentoEntity;

    @BeforeEach
    void setUp() {
        // O LancamentoMapper acessa: id, descricao, valor, data, tipo.name(),
        // categoria (via CategoriaMapper) e usuario (via UsuarioMapper).
        // Todos precisam estar preenchidos para evitar NPE.

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Salário");
        categoria.setTipo(TipoLancamento.RECEITA);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Vinicius");
        usuario.setEmail("vinicius@email.com");
        usuario.setRole(Role.USER);

        lancamentoEntity = new Lancamento();
        lancamentoEntity.setId(1L);
        lancamentoEntity.setDescricao("Salário mensal");
        lancamentoEntity.setValor(new BigDecimal("5000.00"));
        lancamentoEntity.setData(LocalDate.of(2025, 1, 10));
        lancamentoEntity.setTipo(TipoLancamento.RECEITA);
        lancamentoEntity.setCategoria(categoria);
        lancamentoEntity.setUsuario(usuario);
    }

    // ─────────────────────────────────────────
    // GET /lancamentos/resumo
    // ─────────────────────────────────────────

    @Test
    void deveRetornarResumoFinanceiro() throws Exception {
        ResumoFinanceiroDTO resumo = new ResumoFinanceiroDTO(
                new BigDecimal("5000"),
                new BigDecimal("2000"),
                new BigDecimal("3000")
        );

        when(service.calcularResumo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(resumo);

        mockMvc.perform(get("/lancamentos/resumo")
                        .param("inicio", "2025-01-01")
                        .param("fim", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceitas").value(5000))
                .andExpect(jsonPath("$.totalDespesas").value(2000))
                .andExpect(jsonPath("$.saldo").value(3000));
    }

    // ─────────────────────────────────────────
    // GET /lancamentos/{id}
    // ─────────────────────────────────────────

    @Test
    void deveBuscarLancamentoPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(lancamentoEntity);

        mockMvc.perform(get("/lancamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Salário mensal"))
                .andExpect(jsonPath("$.tipo").value("RECEITA"));
    }

    // ─────────────────────────────────────────
    // GET /lancamentos (paginado)
    // ─────────────────────────────────────────

    @Test
    void deveListarLancamentosPaginados() throws Exception {
        var page = new PageImpl<Lancamento>(List.of(), PageRequest.of(0, 10), 0);
        when(service.listarPaginado(any())).thenReturn(page);

        mockMvc.perform(get("/lancamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ─────────────────────────────────────────
    // GET /lancamentos/usuario/{usuarioId}
    // ─────────────────────────────────────────

    @Test
    void deveListarLancamentosPorUsuario() throws Exception {
        var page = new PageImpl<Lancamento>(List.of(), PageRequest.of(0, 10), 0);
        when(service.listarPorUsuarioPaginado(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/lancamentos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ─────────────────────────────────────────
    // GET /lancamentos/periodo
    // ─────────────────────────────────────────

    @Test
    void deveFiltrarLancamentosPorPeriodo() throws Exception {
        var page = new PageImpl<Lancamento>(List.of(), PageRequest.of(0, 10), 0);
        when(service.filtrarPeriodo(any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(page);

        mockMvc.perform(get("/lancamentos/periodo")
                        .param("inicio", "2025-01-01")
                        .param("fim", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ─────────────────────────────────────────
    // POST /lancamentos
    // ─────────────────────────────────────────

    @Test
    void deveCriarLancamentoERetornar201() throws Exception {
        String json = """
                {
                  "descricao": "Salário mensal",
                  "valor": 5000.00,
                  "data": "2025-01-10",
                  "tipo": "RECEITA",
                  "categoriaId": 1,
                  "usuarioId": 1
                }
                """;

        when(service.criar(any())).thenReturn(lancamentoEntity);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Salário mensal"));

        verify(service, times(1)).criar(any());
    }

    @Test
    void deveRetornar400QuandoPostComCamposObrigatoriosFaltando() throws Exception {
        String jsonInvalido = """
                {
                  "descricao": "",
                  "valor": -100
                }
                """;

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────
    // PUT /lancamentos/{id}
    // ─────────────────────────────────────────

    @Test
    void deveAtualizarLancamentoCompletoERetornar200() throws Exception {
        String json = """
                {
                  "descricao": "Salário atualizado",
                  "valor": 6000.00,
                  "data": "2025-02-10",
                  "tipo": "RECEITA",
                  "categoriaId": 1,
                  "usuarioId": 1
                }
                """;

        when(service.atualizarCompleto(eq(1L), any())).thenReturn(lancamentoEntity);

        mockMvc.perform(put("/lancamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).atualizarCompleto(eq(1L), any());
    }

    // ─────────────────────────────────────────
    // PATCH /lancamentos/{id}
    // ─────────────────────────────────────────

    @Test
    void deveAtualizarLancamentoParcialERetornar200() throws Exception {
        String json = """
                {
                  "descricao": "Atualização parcial",
                  "valor": 7000.00
                }
                """;

        when(service.atualizarParcial(eq(1L), any())).thenReturn(lancamentoEntity);

        mockMvc.perform(patch("/lancamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).atualizarParcial(eq(1L), any());
    }

    // ─────────────────────────────────────────
    // DELETE /lancamentos/{id}
    // ─────────────────────────────────────────

    @Test
    void deveDeletarLancamentoERetornar204() throws Exception {
        doNothing().when(service).deletar(1L);

        mockMvc.perform(delete("/lancamentos/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(1L);
    }

    @Test
    void deveChamarDeletarComIdCorreto() throws Exception {
        doNothing().when(service).deletar(42L);

        mockMvc.perform(delete("/lancamentos/42"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(42L);
        verify(service, never()).deletar(1L);
    }
}