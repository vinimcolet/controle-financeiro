package com.vinicius.controlefinanceiro.mapper;

import com.vinicius.controlefinanceiro.dto.response.LancamentoResponseDTO;
import com.vinicius.controlefinanceiro.entity.Lancamento;

public class LancamentoMapper {

    public static LancamentoResponseDTO toResponse(Lancamento l) {
        LancamentoResponseDTO dto = new LancamentoResponseDTO();
        dto.setId(l.getId());
        dto.setDescricao(l.getDescricao());
        dto.setValor(l.getValor());
        dto.setData(l.getData());
        dto.setTipo(l.getTipo().name());
        dto.setCategoria(CategoriaMapper.toResponse(l.getCategoria()));
        dto.setUsuario(UsuarioMapper.toResponse(l.getUsuario()));
        return dto;
    }
}

