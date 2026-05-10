package com.barbosa.extension_project.service;

import com.barbosa.extension_project.domain.entity.Pedido;
import com.barbosa.extension_project.domain.enums.StatusPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("T05-T08 — Domínio Pedido — Transições de Status")
class PedidoDomainTest {

    @Test
    @DisplayName("T05 — Pedido pode transitar de AGUARDANDO_PAGAMENTO para PAGAMENTO_APROVADO")
    void t05_transicaoValida_aguardandoParaAprovado() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertThat(pedido.podeTransicionarPara(StatusPedido.PAGAMENTO_APROVADO)).isTrue();
    }


    @Test
    @DisplayName("T06 — Pedido ENTREGUE não pode ser cancelado")
    void t06_transicaoInvalida_entregueNaoPodeCancelar() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.ENTREGUE);
        assertThat(pedido.podeTransicionarPara(StatusPedido.CANCELADO)).isFalse();
    }

    @Test
    @DisplayName("T07 — Pedido em preparo pode ficar pronto")
    void t07_transicaoValida_emPreparoParaPronto() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.EM_PREPARO);
        assertThat(pedido.podeTransicionarPara(StatusPedido.PRONTO)).isTrue();
    }

    @Test
    @DisplayName("T08 — Pedido aguardando pagamento não pode ir direto para EM_PREPARO")
    void t08_transicaoInvalida_pularStatus() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertThat(pedido.podeTransicionarPara(StatusPedido.EM_PREPARO)).isFalse();
    }
}
