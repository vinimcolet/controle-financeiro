package com.vinicius.controlefinanceiro.service;

import com.vinicius.controlefinanceiro.dto.request.UsuarioRequestDTO;
import com.vinicius.controlefinanceiro.entity.Usuario;
import com.vinicius.controlefinanceiro.enums.Role;
import com.vinicius.controlefinanceiro.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private UsuarioRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UsuarioRequestDTO() {
            @Override public String getNome()  { return "Vinicius"; }
            @Override public String getEmail() { return "vinicius@email.com"; }
            @Override public String getSenha() { return "123456"; }
        };
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        when(passwordEncoder.encode(anyString())).thenReturn("senha-criptografada");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome("Vinicius");
        usuarioSalvo.setEmail("vinicius@email.com");
        usuarioSalvo.setSenha("senha-criptografada");
        usuarioSalvo.setRole(Role.USER);

        when(repository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        Usuario resultado = service.criar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Vinicius");
        assertThat(resultado.getEmail()).isEqualTo("vinicius@email.com");
        assertThat(resultado.getSenha()).isEqualTo("senha-criptografada");
        assertThat(resultado.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void deveCriptografarSenhaAoCriarUsuario() {
        when(passwordEncoder.encode(anyString())).thenReturn("senha-criptografada");
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        service.criar(dto);

        verify(passwordEncoder, times(1)).encode("123456");
    }

    @Test
    void deveDefinirRoleUserAoCriarUsuario() {
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = service.criar(dto);

        assertThat(resultado.getRole()).isEqualTo(Role.USER);
        assertThat(resultado.getRole()).isNotEqualTo(Role.ADMIN);
    }

    @Test
    void deveChamarRepositorySaveUmaVez() {
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        service.criar(dto);

        verify(repository, times(1)).save(any(Usuario.class));
    }
}