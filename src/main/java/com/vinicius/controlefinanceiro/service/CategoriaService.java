package com.vinicius.controlefinanceiro.service;

import com.vinicius.controlefinanceiro.dto.request.CategoriaRequestDTO;
import com.vinicius.controlefinanceiro.entity.Categoria;
import com.vinicius.controlefinanceiro.exception.ResourceNotFoundException;
import com.vinicius.controlefinanceiro.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public Categoria criar(CategoriaRequestDTO dto) {
        Categoria c = new Categoria();
        c.setNome(dto.getNome());
        c.setTipo(dto.getTipo());
        return repository.save(c);
    }

    public List<Categoria> listarTodas() {
        return repository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }

    public void deletar(Long id) {
        Categoria categoria = buscarPorId(id);
        repository.delete(categoria);
    }
}