package com.vinicius.controlefinanceiro.service;

import com.vinicius.controlefinanceiro.dto.request.LancamentoRequestDTO;
import com.vinicius.controlefinanceiro.dto.request.LancamentoUpdateDTO;
import com.vinicius.controlefinanceiro.dto.response.ResumoFinanceiroDTO;
import com.vinicius.controlefinanceiro.entity.Categoria;
import com.vinicius.controlefinanceiro.entity.Lancamento;
import com.vinicius.controlefinanceiro.entity.Usuario;
import com.vinicius.controlefinanceiro.enums.TipoLancamento;
import com.vinicius.controlefinanceiro.exception.ResourceNotFoundException;
import com.vinicius.controlefinanceiro.repository.CategoriaRepository;
import com.vinicius.controlefinanceiro.repository.LancamentoRepository;
import com.vinicius.controlefinanceiro.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class LancamentoService {

    private final LancamentoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public LancamentoService(
            LancamentoRepository repository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Lancamento criar(LancamentoRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Lancamento l = new Lancamento();
        l.setDescricao(dto.getDescricao());
        l.setValor(dto.getValor());
        l.setData(dto.getData());
        l.setTipo(dto.getTipo());
        l.setUsuario(usuario);
        l.setCategoria(categoria);

        return repository.save(l);
    }

    // PATCH — atualiza apenas os campos enviados
    @Transactional
    public Lancamento atualizarParcial(Long id, LancamentoUpdateDTO dto) {

        Lancamento l = buscarPorId(id);

        if (dto.getDescricao() != null)
            l.setDescricao(dto.getDescricao());

        if (dto.getValor() != null)
            l.setValor(dto.getValor());

        if (dto.getData() != null)
            l.setData(dto.getData());

        if (dto.getTipo() != null)
            l.setTipo(dto.getTipo());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            l.setCategoria(categoria);
        }

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            l.setUsuario(usuario);
        }

        return repository.save(l);
    }

    // PUT — substitui todos os campos
    @Transactional
    public Lancamento atualizarCompleto(Long id, LancamentoUpdateDTO dto) {

        Lancamento l = buscarPorId(id);

        l.setDescricao(dto.getDescricao());
        l.setValor(dto.getValor());
        l.setData(dto.getData());
        l.setTipo(dto.getTipo());

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        l.setCategoria(categoria);
        l.setUsuario(usuario);

        return repository.save(l);
    }

    // Filtrar por período
    public Page<Lancamento> filtrarPeriodo(LocalDate inicio, LocalDate fim, Pageable pageable) {

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial maior que data final");
        }

        return repository.filtrarPorPeriodo(inicio, fim, pageable);
    }

    // GET ALL
    public Page<Lancamento> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // GET BY ID
    public Lancamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado"));
    }

    // GET BY USUÁRIO
    public Page<Lancamento> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        return repository.findByUsuarioId(usuarioId, pageable);
    }

    // DELETE BY ID
    public void deletar(Long id) {
        Lancamento lancamento = buscarPorId(id); // reusa o método que já lança a exceção certa
        repository.delete(lancamento);
    }

    // Calcular resumo
    public ResumoFinanceiroDTO calcularResumo(LocalDate inicio, LocalDate fim) {

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial maior que data final");
        }

        BigDecimal totalReceitas = repository.somarPorTipoEPeriodo(
                TipoLancamento.RECEITA, inicio, fim);

        BigDecimal totalDespesas = repository.somarPorTipoEPeriodo(
                TipoLancamento.DESPESA, inicio, fim);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new ResumoFinanceiroDTO(totalReceitas, totalDespesas, saldo);
    }
}