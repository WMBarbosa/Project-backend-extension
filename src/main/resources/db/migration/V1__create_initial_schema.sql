-- V1__create_initial_schema.sql
-- Projeto: Raízes do Nordeste — Back-end API
-- Banco: PostgreSQL

-- ============================================================
-- USUÁRIOS
-- ============================================================
CREATE TABLE usuarios (
    id                      BIGSERIAL PRIMARY KEY,
    nome                    VARCHAR(150) NOT NULL,
    email                   VARCHAR(150) NOT NULL UNIQUE,
    senha                   VARCHAR(255) NOT NULL,  -- BCrypt hash
    telefone                VARCHAR(20),
    perfil                  VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    ativo                   BOOLEAN NOT NULL DEFAULT TRUE,
    consentimento_fidelidade BOOLEAN NOT NULL DEFAULT FALSE,
    data_consentimento      TIMESTAMP,
    criado_em               TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em           TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- UNIDADES DA REDE
-- ============================================================
CREATE TABLE unidades (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    endereco    VARCHAR(300) NOT NULL,
    telefone    VARCHAR(20),
    cidade      VARCHAR(150),
    estado      CHAR(2),
    cnpj        VARCHAR(20),
    ativa       BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- PRODUTOS (CARDÁPIO)
-- ============================================================
CREATE TABLE produtos (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    descricao   VARCHAR(500),
    preco       NUMERIC(10,2) NOT NULL,
    categoria   VARCHAR(100),
    imagem_url  VARCHAR(300),
    disponivel  BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- ESTOQUE POR UNIDADE
-- ============================================================
CREATE TABLE estoque_unidades (
    id                  BIGSERIAL PRIMARY KEY,
    unidade_id          BIGINT NOT NULL REFERENCES unidades(id),
    produto_id          BIGINT NOT NULL REFERENCES produtos(id),
    quantidade          INTEGER NOT NULL DEFAULT 0,
    quantidade_minima   INTEGER NOT NULL DEFAULT 0,
    atualizado_em       TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (unidade_id, produto_id)
);

-- ============================================================
-- MOVIMENTOS DE ESTOQUE (AUDITORIA)
-- ============================================================
CREATE TABLE movimentos_estoque (
    id                   BIGSERIAL PRIMARY KEY,
    unidade_id           BIGINT NOT NULL REFERENCES unidades(id),
    produto_id           BIGINT NOT NULL REFERENCES produtos(id),
    tipo                 VARCHAR(20) NOT NULL,
    quantidade           INTEGER NOT NULL,
    quantidade_anterior  INTEGER,
    quantidade_posterior INTEGER,
    observacao           VARCHAR(300),
    usuario_id           BIGINT REFERENCES usuarios(id),
    pedido_id            BIGINT,
    criado_em            TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- PEDIDOS
-- ============================================================
CREATE TABLE pedidos (
    id                  BIGSERIAL PRIMARY KEY,
    cliente_id          BIGINT NOT NULL REFERENCES usuarios(id),
    unidade_id          BIGINT NOT NULL REFERENCES unidades(id),
    canal_pedido        VARCHAR(20) NOT NULL,  -- APP, TOTEM, BALCAO, PICKUP, WEB
    status              VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    forma_pagamento     VARCHAR(20) NOT NULL,
    valor_total         NUMERIC(10,2) NOT NULL DEFAULT 0,
    desconto_fidelidade NUMERIC(10,2) NOT NULL DEFAULT 0,
    pontos_utilizados   INTEGER NOT NULL DEFAULT 0,
    observacao          VARCHAR(500),
    criado_em           TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pedidos_canal ON pedidos(canal_pedido);
CREATE INDEX idx_pedidos_status ON pedidos(status);
CREATE INDEX idx_pedidos_unidade ON pedidos(unidade_id);
CREATE INDEX idx_pedidos_cliente ON pedidos(cliente_id);

-- ============================================================
-- ITENS DO PEDIDO
-- ============================================================
CREATE TABLE itens_pedido (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    quantidade      INTEGER NOT NULL,
    preco_unitario  NUMERIC(10,2) NOT NULL,
    subtotal        NUMERIC(10,2) NOT NULL DEFAULT 0,
    observacao      VARCHAR(300)
);

-- ============================================================
-- PAGAMENTOS
-- ============================================================
CREATE TABLE pagamentos (
    id                      BIGSERIAL PRIMARY KEY,
    pedido_id               BIGINT NOT NULL UNIQUE REFERENCES pedidos(id),
    forma_pagamento         VARCHAR(20) NOT NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    valor                   NUMERIC(10,2) NOT NULL,
    gateway_transaction_id  VARCHAR(100),
    gateway_response        TEXT,
    motivo_recusa           VARCHAR(300),
    processado_em           TIMESTAMP,
    criado_em               TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em           TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- FIDELIDADE
-- ============================================================
CREATE TABLE saldo_fidelidade (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL UNIQUE REFERENCES usuarios(id),
    pontos_acumulados   INTEGER NOT NULL DEFAULT 0,
    pontos_resgatados   INTEGER NOT NULL DEFAULT 0,
    atualizado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE historico_fidelidade (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT NOT NULL REFERENCES usuarios(id),
    pontos      INTEGER NOT NULL,
    tipo        VARCHAR(20) NOT NULL,  -- ACUMULO, RESGATE
    descricao   VARCHAR(300),
    pedido_id   BIGINT,
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- AUDIT LOGS (LGPD / Segurança)
-- ============================================================
CREATE TABLE audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    usuario_email   VARCHAR(150),
    usuario_perfil  VARCHAR(20),
    acao            VARCHAR(100) NOT NULL,
    recurso         VARCHAR(100) NOT NULL,
    recurso_id      BIGINT,
    detalhes        TEXT,
    ip_origem       VARCHAR(50),
    timestamp       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_acao ON audit_logs(acao);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
