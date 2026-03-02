package com.vinicius.controlefinanceiro.controller;

import com.vinicius.controlefinanceiro.dto.request.LancamentoRequestDTO;
import com.vinicius.controlefinanceiro.dto.request.LancamentoUpdateDTO;
import com.vinicius.controlefinanceiro.dto.response.LancamentoResponseDTO;
import com.vinicius.controlefinanceiro.dto.response.ResumoFinanceiroDTO;
import com.vinicius.controlefinanceiro.entity.Lancamento;
import com.vinicius.controlefinanceiro.mapper.LancamentoMapper;
import com.vinicius.controlefinanceiro.service.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LancamentoResponseDTO> criar(
            @Valid @RequestBody LancamentoRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LancamentoMapper.toResponse(service.criar(dto)));
    }

    // PUT atualiza tudo e os recursos vem por request
    @PutMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> atualizarCompleto(
            @PathVariable Long id,
            @Valid @RequestBody LancamentoUpdateDTO dto) {

        return ResponseEntity.ok(
                LancamentoMapper.toResponse(service.atualizarCompleto(id, dto))
        );
    }

    // PATCH atualiza alguns campos
    @PatchMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> atualizarParcial(
            @PathVariable Long id,
            @RequestBody LancamentoUpdateDTO dto) {

        return ResponseEntity.ok(
                LancamentoMapper.toResponse(service.atualizarParcial(id, dto))
        );
    }

    // Filtrar por periodo
    @GetMapping("/periodo")
    public ResponseEntity<Page<LancamentoResponseDTO>> filtrarPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.filtrarPeriodo(inicio, fim, pageable)
                        .map(LancamentoMapper::toResponse)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<Page<LancamentoResponseDTO>> listar(Pageable pageable) {

        Page<Lancamento> page = service.listarPaginado(pageable);

        return ResponseEntity.ok(
                page.map(LancamentoMapper::toResponse)
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                LancamentoMapper.toResponse(service.buscarPorId(id))
        );
    }

    // GET BY USUÁRIO
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<LancamentoResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.listarPorUsuarioPaginado(usuarioId, pageable)
                        .map(LancamentoMapper::toResponse)
        );
    }

    // DELETE BY ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // CALULAR RESUMO
    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiroDTO> resumo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {

        return ResponseEntity.ok(service.calcularResumo(inicio, fim));
    }



}
