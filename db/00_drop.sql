-- =====================================================================
-- CIVIA - remocao das tabelas (use apenas para recriar o banco do zero)
-- Erros ORA-00942 ("tabela nao existe") podem ser ignorados.
-- =====================================================================
DROP TABLE T_CIVIA_ATUALIZACAO_DISP CASCADE CONSTRAINTS PURGE;
DROP TABLE T_CIVIA_SERVICO_PERFIL CASCADE CONSTRAINTS PURGE;
DROP TABLE T_CIVIA_SERVICO CASCADE CONSTRAINTS PURGE;
DROP TABLE T_CIVIA_TOTEM CASCADE CONSTRAINTS PURGE;
