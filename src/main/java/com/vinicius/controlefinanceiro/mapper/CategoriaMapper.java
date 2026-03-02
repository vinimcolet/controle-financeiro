package com.vinicius.controlefinanceiro.mapper;

import com.vinicius.controlefinanceiro.dto.response.CategoriaResponseDTO;
import com.vinicius.controlefinanceiro.entity.Categoria;

public class CategoriaMapper {

    public static CategoriaResponseDTO toResponse(Categoria c) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setTipo(c.getTipo());
        return dto;
    }
}

