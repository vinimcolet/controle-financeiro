package com.vinicius.controlefinanceiro.test;

import com.vinicius.controlefinanceiro.entity.Categoria;
import com.vinicius.controlefinanceiro.entity.Usuario;
import com.vinicius.controlefinanceiro.enums.Role;
import com.vinicius.controlefinanceiro.enums.TipoLancamento;

public class TestDataFactory {



    public static Usuario usuarioPadrao() {
        Usuario u = new Usuario();
        u.setNome("Usuario Teste");
        u.setEmail("teste@email.com");
        u.setSenha("123");
        u.setRole(Role.USER);
        return u;
    }

    public static Categoria categoriaReceita() {
        Categoria c = new Categoria();
        c.setNome("Salário");
        c.setTipo(TipoLancamento.RECEITA);
        return c;
    }

    public static Categoria categoriaDespesa() {
        Categoria c = new Categoria();
        c.setNome("Aluguel");
        c.setTipo(TipoLancamento.DESPESA);
        return c;
    }


}
