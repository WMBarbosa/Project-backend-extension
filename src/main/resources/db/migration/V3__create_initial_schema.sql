-- V3__alter_unidades_estado_to_varchar.sql
-- Projeto: Raízes do Nordeste — Back-end API
-- Banco: PostgreSQL
--
-- Corrige divergência entre o schema do banco e o mapeamento JPA.
-- No PostgreSQL, CHAR(2) aparece como bpchar, mas a entidade Unidade
-- com @Column(length = 2) espera VARCHAR(2).

ALTER TABLE unidades
ALTER COLUMN estado TYPE VARCHAR(2);