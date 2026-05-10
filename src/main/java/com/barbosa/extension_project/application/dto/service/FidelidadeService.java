package com.barbosa.extension_project.application.dto.service;

import com.barbosa.extension_project.application.dto.response.FidelidadeResponse;
import com.barbosa.extension_project.application.dto.response.HistoricoFidelidadeResponse;
import com.barbosa.extension_project.domain.entity.HistoricoFidelidade;
import com.barbosa.extension_project.domain.entity.SaldoFidelidade;
import com.barbosa.extension_project.domain.entity.Usuario;
import com.barbosa.extension_project.domain.exception.RecursoNaoEncontradoException;
import com.barbosa.extension_project.domain.exception.RegraDeNegocioException;
import com.barbosa.extension_project.infrastructure.persistence.repository.HistoricoFidelidadeRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.SaldoFidelidadeRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FidelidadeService {

    private static final int PONTOS_POR_REAL = 10;
    private static final int PONTOS_POR_DESCONTO = 100;

    private final SaldoFidelidadeRepository saldoRepository;
    private final HistoricoFidelidadeRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;

    public FidelidadeResponse consultarSaldo(Long usuarioId) {
        SaldoFidelidade saldo = saldoRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Programa de fidelidade não ativo para o usuário. Ative o consentimento."));
        return FidelidadeResponse.of(saldo);
    }

    public Page<HistoricoFidelidadeResponse> historico(Long usuarioId, Pageable pageable) {
        return historicoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId, pageable)
            .map(HistoricoFidelidadeResponse::of);
    }

    @Transactional
    public void acumularPontosPorPedido(Long usuarioId, BigDecimal valorPedido, Long pedidoId) {
        saldoRepository.findByUsuarioId(usuarioId).ifPresent(saldo -> {
            int pontos = valorPedido.intValue() * PONTOS_POR_REAL;
            if (pontos > 0) {
                saldo.acumularPontos(pontos);
                saldoRepository.save(saldo);

                HistoricoFidelidade hist = HistoricoFidelidade.builder()
                    .usuario(saldo.getUsuario())
                    .pontos(pontos)
                    .tipo("ACUMULO")
                    .descricao("Pontos acumulados pelo pedido #" + pedidoId)
                    .pedidoId(pedidoId)
                    .build();
                historicoRepository.save(hist);
            }
        });
    }


    @Transactional
    public BigDecimal resgatarPontos(Long usuarioId, int pontosParaResgatar, Long pedidoId) {
        if (pontosParaResgatar <= 0) return BigDecimal.ZERO;

        SaldoFidelidade saldo = saldoRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new RegraDeNegocioException(
                "Programa de fidelidade não ativo. Ative o consentimento primeiro."));

        saldo.resgatar(pontosParaResgatar); // lança exceção se saldo insuficiente
        saldoRepository.save(saldo);

        HistoricoFidelidade hist = HistoricoFidelidade.builder()
            .usuario(saldo.getUsuario())
            .pontos(pontosParaResgatar)
            .tipo("RESGATE")
            .descricao("Resgate de pontos no pedido #" + pedidoId)
            .pedidoId(pedidoId)
            .build();
        historicoRepository.save(hist);

        // 100 pontos = R$1,00
        int reais = pontosParaResgatar / PONTOS_POR_DESCONTO;
        return BigDecimal.valueOf(reais);
    }

    @Transactional
    public FidelidadeResponse ativarConsentimento(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", usuarioId));

        if (!usuario.getConsentimentoFidelidade()) {
            usuario.setConsentimentoFidelidade(true);
            usuario.setDataConsentimento(java.time.LocalDateTime.now());
            usuarioRepository.save(usuario);
        }

        SaldoFidelidade saldo = saldoRepository.findByUsuarioId(usuarioId)
            .orElseGet(() -> {
                SaldoFidelidade novo = SaldoFidelidade.builder().usuario(usuario).build();
                return saldoRepository.save(novo);
            });

        return FidelidadeResponse.of(saldo);
    }
}
