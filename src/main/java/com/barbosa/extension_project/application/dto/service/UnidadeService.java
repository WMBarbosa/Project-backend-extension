package com.barbosa.extension_project.application.dto.service;

import com.barbosa.extension_project.application.dto.request.UnidadeRequest;
import com.barbosa.extension_project.application.dto.response.UnidadeResponse;
import com.barbosa.extension_project.domain.entity.Unidade;
import com.barbosa.extension_project.domain.exception.RecursoNaoEncontradoException;
import com.barbosa.extension_project.infrastructure.persistence.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    public List<UnidadeResponse> listarAtivas() {
        return unidadeRepository.findByAtivaTrue().stream().map(UnidadeResponse::of).toList();
    }

    public List<UnidadeResponse> listarTodas() {
        return unidadeRepository.findAll().stream().map(UnidadeResponse::of).toList();
    }

    public UnidadeResponse buscarPorId(Long id) {
        return unidadeRepository.findById(id)
            .map(UnidadeResponse::of)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade", id));
    }

    @Transactional
    public UnidadeResponse criar(UnidadeRequest req) {
        Unidade unidade = Unidade.builder()
            .nome(req.nome())
            .endereco(req.endereco())
            .telefone(req.telefone())
            .cidade(req.cidade())
            .estado(req.estado())
            .cnpj(req.cnpj())
            .ativa(req.ativa() != null ? req.ativa() : true)
            .build();
        return UnidadeResponse.of(unidadeRepository.save(unidade));
    }

    @Transactional
    public UnidadeResponse atualizar(Long id, UnidadeRequest req) {
        Unidade unidade = unidadeRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade", id));

        unidade.setNome(req.nome());
        unidade.setEndereco(req.endereco());
        unidade.setTelefone(req.telefone());
        unidade.setCidade(req.cidade());
        unidade.setEstado(req.estado());
        unidade.setCnpj(req.cnpj());
        if (req.ativa() != null) unidade.setAtiva(req.ativa());

        return UnidadeResponse.of(unidadeRepository.save(unidade));
    }
}
