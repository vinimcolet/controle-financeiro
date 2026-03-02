package com.vinicius.controlefinanceiro.controller;

import com.vinicius.controlefinanceiro.entity.Usuario;
import com.vinicius.controlefinanceiro.enums.Role;
import com.vinicius.controlefinanceiro.security.JwtFilter;
import com.vinicius.controlefinanceiro.security.JwtService;
import com.vinicius.controlefinanceiro.service.UsuarioService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private Usuario usuarioEntity;

    @BeforeEach
    void setUp() {
        usuarioEntity = new Usuario();
        usuarioEntity.setId(1L);
        usuarioEntity.setNome("Vinicius");
        usuarioEntity.setEmail("vinicius@email.com");
        usuarioEntity.setRole(Role.USER);
    }

    // ─────────────────────────────────────────
    // GET /usuarios
    // ─────────────────────────────────────────

    @Test
    void deveListarUsuariosPaginados() throws Exception {
        var page = new PageImpl<>(List.of(usuarioEntity), PageRequest.of(0, 10), 1);
        when(service.listarPaginado(any())).thenReturn(page);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoHaUsuarios() throws Exception {
        var page = new PageImpl<Usuario>(List.of(), PageRequest.of(0, 10), 0);
        when(service.listarPaginado(any())).thenReturn(page);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ─────────────────────────────────────────
    // GET /usuarios/{id}
    // ─────────────────────────────────────────

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(usuarioEntity);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Vinicius"))
                .andExpect(jsonPath("$.email").value("vinicius@email.com"));
    }

    // ─────────────────────────────────────────
    // POST /usuarios
    // ─────────────────────────────────────────

    @Test
    void deveCriarUsuarioERetornar201() throws Exception {
        String json = """
                {
                  "nome": "Vinicius",
                  "email": "vinicius@email.com",
                  "senha": "senha123"
                }
                """;

        // Retorna entity válida para evitar NullPointerException no mapper
        when(service.criar(any())).thenReturn(usuarioEntity);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Vinicius"));

        verify(service, times(1)).criar(any());
    }

    @Test
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        String json = """
                {
                  "nome": "Vinicius",
                  "email": "email-invalido",
                  "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoNomeFaltando() throws Exception {
        String json = """
                {
                  "email": "vinicius@email.com",
                  "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoSenhaFaltando() throws Exception {
        String json = """
                {
                  "nome": "Vinicius",
                  "email": "vinicius@email.com"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────
    // DELETE /usuarios/{id}
    // ─────────────────────────────────────────

    @Test
    void deveDeletarUsuarioERetornar204() throws Exception {
        doNothing().when(service).deletar(1L);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(1L);
    }

    @Test
    void deveChamarDeletarComIdCorreto() throws Exception {
        doNothing().when(service).deletar(7L);

        mockMvc.perform(delete("/usuarios/7"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(7L);
        verify(service, never()).deletar(1L);
    }
}