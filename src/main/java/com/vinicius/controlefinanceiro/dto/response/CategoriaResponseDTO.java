package com.vinicius.controlefinanceiro.dto.response;

import com.vinicius.controlefinanceiro.enums.TipoLancamento;

public class CategoriaResponseDTO {

    private Long id;
    private String nome;
    private TipoLancamento tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }
}
