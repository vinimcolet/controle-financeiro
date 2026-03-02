package com.vinicius.controlefinanceiro.repository;

import com.vinicius.controlefinanceiro.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}