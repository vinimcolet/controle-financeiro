package com.vinicius.controlefinanceiro.entity;

import com.vinicius.controlefinanceiro.enums.TipoLancamento;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table  (name = "categoria")
public class Categoria {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String nome;


    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;


    @OneToMany(mappedBy = "categoria")
    private List<Lancamento> lancamentos;

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLancamentos(List<Lancamento> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public List<Lancamento> getLancamentos() {
        return lancamentos;
    }
}