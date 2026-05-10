package com.barbosa.extension_project.service;

import com.barbosa.extension_project.domain.entity.EstoqueUnidade;
import com.barbosa.extension_project.domain.entity.Produto;
import com.barbosa.extension_project.domain.entity.Unidade;
import com.barbosa.extension_project.domain.enums.FormaPagamento;
import com.barbosa.extension_project.domain.enums.StatusPagamento;
import com.barbosa.extension_project.infrastructure.integration.GatewayPagamentoMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("T09-T12 — Estoque e Gateway Mock")
class EstoqueDomainAndGatewayTest {

    @Test
    @DisplayName("T09 — EstoqueUnidade.reduzir() com saldo insuficiente deve lançar exceção")
    void t09_estoque_reduzirInsuficiente_lancaExcecao() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Baião de Dois");

        Unidade unidade = new Unidade();
        unidade.setId(1L);

        EstoqueUnidade estoque = EstoqueUnidade.builder()
            .produto(produto).unidade(unidade).quantidade(3).build();

        assertThatThrownBy(() -> estoque.reduzir(10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    @DisplayName("T10 — EstoqueUnidade.reduzir() com saldo suficiente atualiza quantidade")
    void t10_estoque_reduzirSuficiente_atualizaQtd() {
        EstoqueUnidade estoque = EstoqueUnidade.builder().quantidade(20).build();
        estoque.reduzir(5);
        assertThat(estoque.getQuantidade()).isEqualTo(15);
    }

    @Test
    @DisplayName("T11 — Gateway mock deve APROVAR pagamento com valor normal")
    void t11_gateway_aprovaPagamentoNormal() {
        GatewayPagamentoMock gateway = new GatewayPagamentoMock();
        GatewayPagamentoMock.RespostaPagamento resp =
            gateway.processar(1L, new BigDecimal("50.00"), FormaPagamento.PIX);

        assertThat(resp.status()).isEqualTo(StatusPagamento.APROVADO);
        assertThat(resp.transactionId()).startsWith("MOCK-");
        assertThat(resp.motivoRecusa()).isNull();
    }

    @Test
    @DisplayName("T12 — Gateway mock deve RECUSAR pagamento com valor terminando em .99")
    void t12_gateway_recusaPagamentoValor99() {
        GatewayPagamentoMock gateway = new GatewayPagamentoMock();
        GatewayPagamentoMock.RespostaPagamento resp =
            gateway.processar(2L, new BigDecimal("99.99"), FormaPagamento.CARTAO_CREDITO);

        assertThat(resp.status()).isEqualTo(StatusPagamento.RECUSADO);
        assertThat(resp.motivoRecusa()).isNotBlank();
    }
}
