package com.vinicius.controlefinanceiro.controller;

import com.vinicius.controlefinanceiro.entity.Categoria;
import com.vinicius.controlefinanceiro.enums.TipoLancamento;
import com.vinicius.controlefinanceiro.security.JwtFilter;
import com.vinicius.controlefinanceiro.security.JwtService;
import com.vinicius.controlefinanceiro.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private Categoria categoriaEntity;

    @BeforeEach
    void setUp() {
        categoriaEntity = new Categoria();
        categoriaEntity.setId(1L);
        categoriaEntity.setNome("Alimentação");
        categoriaEntity.setTipo(TipoLancamento.DESPESA);
    }

    // ─────────────────────────────────────────
    // GET /categorias
    // ─────────────────────────────────────────

    @Test
    void deveListarTodasAsCategorias() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(categoriaEntity));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Alimentação"))
                .andExpect(jsonPath("$[0].tipo").value("DESPESA"));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaCategorias() throws Exception {
        when(service.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ─────────────────────────────────────────
    // GET /categorias/{id}
    // ─────────────────────────────────────────

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(categoriaEntity);

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Alimentação"))
                .andExpect(jsonPath("$.tipo").value("DESPESA"));
    }

    // ─────────────────────────────────────────
    // POST /categorias
    // ─────────────────────────────────────────

    @Test
    void deveCriarCategoriaERetornar201() throws Exception {
        String json = """
                {
                  "nome": "Alimentação",
                  "tipo": "DESPESA"
                }
                """;

        when(service.criar(any())).thenReturn(categoriaEntity);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Alimentação"));

        verify(service, times(1)).criar(any());
    }

    @Test
    void deveRetornar400QuandoPostSemNome() throws Exception {
        String jsonInvalido = """
                {
                  "tipo": "DESPESA"
                }
                """;

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoPostSemTipo() throws Exception {
        String jsonInvalido = """
                {
                  "nome": "Alimentação"
                }
                """;

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────
    // DELETE /categorias/{id}
    // ─────────────────────────────────────────

    @Test
    void deveDeletarCategoriaERetornar204() throws Exception {
        doNothing().when(service).deletar(1L);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(1L);
    }

    @Test
    void deveChamarDeletarComIdCorreto() throws Exception {
        doNothing().when(service).deletar(99L);

        mockMvc.perform(delete("/categorias/99"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(99L);
        verify(service, never()).deletar(1L);
    }
}