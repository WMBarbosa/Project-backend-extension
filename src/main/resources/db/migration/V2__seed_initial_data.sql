-- V2__seed_initial_data.sql
-- Dados iniciais para desenvolvimento e testes

-- ============================================================
-- USUÁRIOS
-- Senhas de desenvolvimento: usar os valores documentados no projeto/testes
-- ============================================================
INSERT INTO usuarios (
    nome,
    email,
    senha,
    perfil,
    ativo,
    consentimento_fidelidade,
    data_consentimento
) VALUES
      ('Administrador', 'admin@raizesnordeste.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE, FALSE, NULL),
      ('Gerente Recife', 'gerente@raizesnordeste.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'GERENTE', TRUE, FALSE, NULL),
      ('Atendente João', 'atendente@raizesnordeste.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATENDENTE', TRUE, FALSE, NULL),
      ('Cozinha Maria', 'cozinha@raizesnordeste.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'COZINHA', TRUE, FALSE, NULL),
      ('Cliente Teste', 'cliente@raizesnordeste.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'CLIENTE', TRUE, TRUE, NOW());

-- ============================================================
-- SALDO DE FIDELIDADE
-- ============================================================
INSERT INTO saldo_fidelidade (
    usuario_id,
    pontos_acumulados,
    pontos_resgatados
)
SELECT
    id,
    500,
    0
FROM usuarios
WHERE email = 'cliente@raizesnordeste.com';

-- ============================================================
-- UNIDADES
-- ============================================================
INSERT INTO unidades (
    nome,
    endereco,
    telefone,
    cidade,
    estado,
    cnpj,
    ativa
) VALUES
      ('Raízes do Nordeste — Recife Centro', 'Rua da Aurora, 100 — Boa Vista', '(81) 3000-0001', 'Recife', 'PE', '00.000.001/0001-01', TRUE),
      ('Raízes do Nordeste — Recife Shopping', 'Av. Agamenon Magalhães, 153 — Derby', '(81) 3000-0002', 'Recife', 'PE', '00.000.001/0002-02', TRUE),
      ('Raízes do Nordeste — Fortaleza', 'Av. Beira Mar, 3500 — Meireles', '(85) 3000-0003', 'Fortaleza', 'CE', '00.000.001/0003-03', TRUE);

-- ============================================================
-- PRODUTOS
-- ============================================================
INSERT INTO produtos (
    nome,
    descricao,
    preco,
    categoria,
    disponivel
) VALUES
      ('Baião de Dois', 'Arroz com feijão de corda, queijo coalho e charque', 28.90, 'Pratos', TRUE),
      ('Carne de Sol', 'Carne de sol grelhada com macaxeira e manteiga de garrafa', 42.50, 'Pratos', TRUE),
      ('Galinha Caipira', 'Galinha ao molho pardo com pirão e farofa', 38.00, 'Pratos', TRUE),
      ('Tapioca Nordestina', 'Tapioca com carne seca, queijo e coco', 18.50, 'Lanches', TRUE),
      ('Acarajé', 'Bolinho de feijão-fradinho com vatapá e camarão', 15.00, 'Lanches', TRUE),
      ('Suco de Caju Natural', 'Suco natural de caju gelado — 400ml', 9.90, 'Bebidas', TRUE),
      ('Água de Coco', 'Água de coco natural — 300ml', 8.00, 'Bebidas', TRUE),
      ('Cocada', 'Cocada branca ou queimada', 6.50, 'Sobremesas', TRUE),
      ('Cartola', 'Banana frita com queijo coalho e canela', 12.00, 'Sobremesas', TRUE),
      ('Combo Sertanejo', 'Baião de Dois + Carne de Sol + Bebida', 65.00, 'Combos', TRUE);

-- ============================================================
-- ESTOQUE — Unidade 1: Recife Centro
-- ============================================================
INSERT INTO estoque_unidades (
    unidade_id,
    produto_id,
    quantidade,
    quantidade_minima
)
SELECT
    u.id,
    p.id,
    CASE
        WHEN p.nome IN ('Baião de Dois', 'Carne de Sol', 'Galinha Caipira') THEN 50
        WHEN p.nome IN ('Tapioca Nordestina', 'Acarajé') THEN 100
        WHEN p.nome IN ('Suco de Caju Natural', 'Água de Coco') THEN 200
        ELSE 80
        END AS quantidade,
    CASE
        WHEN p.nome IN ('Baião de Dois', 'Carne de Sol', 'Galinha Caipira') THEN 5
        WHEN p.nome IN ('Tapioca Nordestina', 'Acarajé') THEN 10
        WHEN p.nome IN ('Suco de Caju Natural', 'Água de Coco') THEN 20
        ELSE 10
        END AS quantidade_minima
FROM unidades u
         CROSS JOIN produtos p
WHERE u.nome = 'Raízes do Nordeste — Recife Centro';

-- ============================================================
-- ESTOQUE — Unidade 2: Recife Shopping
-- ============================================================
INSERT INTO estoque_unidades (
    unidade_id,
    produto_id,
    quantidade,
    quantidade_minima
)
SELECT
    u.id,
    p.id,
    30,
    5
FROM unidades u
         CROSS JOIN produtos p
WHERE u.nome = 'Raízes do Nordeste — Recife Shopping';

-- ============================================================
-- ESTOQUE — Unidade 3: Fortaleza
-- ============================================================
INSERT INTO estoque_unidades (
    unidade_id,
    produto_id,
    quantidade,
    quantidade_minima
)
SELECT
    u.id,
    p.id,
    25,
    5
FROM unidades u
         CROSS JOIN produtos p
WHERE u.nome = 'Raízes do Nordeste — Fortaleza';