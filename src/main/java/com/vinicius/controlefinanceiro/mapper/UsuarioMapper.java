package com.vinicius.controlefinanceiro.mapper;

import com.vinicius.controlefinanceiro.dto.response.UsuarioResponseDTO;
import com.vinicius.controlefinanceiro.entity.Usuario;

public class UsuarioMapper {

    public static UsuarioResponseDTO toResponse(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setNome(u.getNome());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
        return dto;
    }
}
