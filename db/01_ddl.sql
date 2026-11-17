-- =====================================================================
-- CIVIA - Modelo fisico da equipe (Database Design) - Oracle
-- Global Solution 2026 - 2o semestre
-- Script da equipe, sem alteracoes. Sequencias para geracao de ids: 02_sequencias.sql
-- =====================================================================

-- 1. CREATE TABLE---------------------------------------------------------------------

-- Organizações que participam da rede (Centros POP, ONGs, cooperativas,
-- prefeitura e parceiros que doam recompensas)
CREATE TABLE T_ORGANIZACAO (
    id_organizacao   NUMBER(10)    NOT NULL,
    nm_organizacao   VARCHAR2(120) NOT NULL,
    tp_organizacao   VARCHAR2(20)  NOT NULL,
    nr_telefone      VARCHAR2(15),
    ds_email         VARCHAR2(120),
    CONSTRAINT pk_organizacao PRIMARY KEY (id_organizacao),
    CONSTRAINT un_org_nome UNIQUE (nm_organizacao),
    CONSTRAINT ck_org_tipo CHECK (tp_organizacao IN
        ('CENTRO_POP', 'ONG', 'COOPERATIVA', 'PREFEITURA', 'PARCEIRO'))
);

-- Tipos de serviço exibidos na tela inicial do totem
CREATE TABLE T_TIPO_SERVICO (
    id_tipo_servico  NUMBER(3)    NOT NULL,
    nm_tipo_servico  VARCHAR2(40) NOT NULL,
    ds_icone         VARCHAR2(30) NOT NULL,
    CONSTRAINT pk_tipo_servico PRIMARY KEY (id_tipo_servico),
    CONSTRAINT un_tipo_nome UNIQUE (nm_tipo_servico)
);

-- Perfis de público atendido (usados no filtro de abrigo - RN05)
CREATE TABLE T_PERFIL_ATENDIDO (
    id_perfil   NUMBER(3)    NOT NULL,
    ds_perfil   VARCHAR2(40) NOT NULL,
    CONSTRAINT pk_perfil PRIMARY KEY (id_perfil),
    CONSTRAINT un_perfil_desc UNIQUE (ds_perfil)
);

-- Serviços cadastrados (abrigo, refeição, banho, saúde, documentos)
CREATE TABLE T_SERVICO (
    id_servico       NUMBER(10)    NOT NULL,
    id_organizacao   NUMBER(10)    NOT NULL,
    id_tipo_servico  NUMBER(3)     NOT NULL,
    nm_servico       VARCHAR2(120) NOT NULL,
    ds_logradouro    VARCHAR2(120) NOT NULL,
    nr_endereco      VARCHAR2(10),
    nm_bairro        VARCHAR2(60)  NOT NULL,
    nr_latitude      NUMBER(9,6)   NOT NULL,
    nr_longitude     NUMBER(9,6)   NOT NULL,
    nr_capacidade    NUMBER(5),
    fl_24h           CHAR(1)       DEFAULT 'N' NOT NULL,
    st_aprovacao     VARCHAR2(10)  DEFAULT 'PENDENTE' NOT NULL,
    dt_cadastro      DATE          DEFAULT SYSDATE NOT NULL,
    CONSTRAINT pk_servico PRIMARY KEY (id_servico),
    CONSTRAINT ck_serv_24h CHECK (fl_24h IN ('S', 'N')),
    CONSTRAINT ck_serv_aprov CHECK (st_aprovacao IN ('PENDENTE', 'APROVADO', 'REPROVADO')),
    CONSTRAINT ck_serv_capac CHECK (nr_capacidade IS NULL OR nr_capacidade > 0)
);

-- Associativa N:N entre serviço e perfil atendido
CREATE TABLE T_SERVICO_PERFIL (
    id_servico  NUMBER(10) NOT NULL,
    id_perfil   NUMBER(3)  NOT NULL,
    CONSTRAINT pk_servico_perfil PRIMARY KEY (id_servico, id_perfil)
);

-- Horários de funcionamento (1 = domingo ... 7 = sábado).
-- Serviços 24h (fl_24h = 'S') não precisam de linhas aqui.
CREATE TABLE T_HORARIO (
    id_horario      NUMBER(10)  NOT NULL,
    id_servico      NUMBER(10)  NOT NULL,
    nr_dia_semana   NUMBER(1)   NOT NULL,
    hr_abertura     VARCHAR2(5) NOT NULL,
    hr_fechamento   VARCHAR2(5) NOT NULL,
    CONSTRAINT pk_horario PRIMARY KEY (id_horario),
    CONSTRAINT un_horario UNIQUE (id_servico, nr_dia_semana, hr_abertura),
    CONSTRAINT ck_hor_dia CHECK (nr_dia_semana BETWEEN 1 AND 7),
    CONSTRAINT ck_hor_intervalo CHECK (hr_abertura < hr_fechamento)
);

-- Usuários do painel (equipes, gestor da prefeitura e cooperativas).
-- O totem não tem usuário: a pessoa em situação de rua nunca se cadastra.
CREATE TABLE T_USUARIO_PAINEL (
    id_usuario         NUMBER(10)    NOT NULL,
    id_organizacao     NUMBER(10)    NOT NULL,
    nm_usuario         VARCHAR2(100) NOT NULL,
    ds_email           VARCHAR2(120) NOT NULL,
    tp_perfil_acesso   VARCHAR2(12)  NOT NULL,
    fl_ativo           CHAR(1)       DEFAULT 'S' NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT un_usuario_email UNIQUE (ds_email),
    CONSTRAINT ck_usu_perfil CHECK (tp_perfil_acesso IN ('GESTOR', 'EQUIPE', 'COOPERATIVA')),
    CONSTRAINT ck_usu_ativo CHECK (fl_ativo IN ('S', 'N'))
);

-- Histórico de disponibilidade de cada serviço.
-- Vagas de abrigo chegam da Central de Vagas (sem usuário); os demais
-- serviços são atualizados pelas equipes no painel (RN03).
CREATE TABLE T_ATUALIZACAO (
    id_atualizacao       NUMBER(10)   NOT NULL,
    id_servico           NUMBER(10)   NOT NULL,
    id_usuario           NUMBER(10),
    st_disponibilidade   VARCHAR2(15) NOT NULL,
    qt_vagas             NUMBER(5),
    tp_origem            VARCHAR2(15) NOT NULL,
    dt_atualizacao       DATE         NOT NULL,
    CONSTRAINT pk_atualizacao PRIMARY KEY (id_atualizacao),
    CONSTRAINT ck_atu_status CHECK (st_disponibilidade IN
        ('TEM_VAGA', 'POUCAS_VAGAS', 'LOTADO', 'ABERTO', 'FILA_GRANDE', 'FECHADO')),
    CONSTRAINT ck_atu_origem CHECK (tp_origem IN ('PAINEL', 'CENTRAL_VAGAS')),
    CONSTRAINT ck_atu_usuario CHECK (
        (tp_origem = 'PAINEL' AND id_usuario IS NOT NULL) OR
        (tp_origem = 'CENTRAL_VAGAS' AND id_usuario IS NULL)),
    CONSTRAINT ck_atu_faixa CHECK (
        (st_disponibilidade = 'TEM_VAGA'     AND qt_vagas IS NOT NULL AND qt_vagas >= 4) OR
        (st_disponibilidade = 'POUCAS_VAGAS' AND qt_vagas IS NOT NULL AND qt_vagas BETWEEN 1 AND 3) OR
        (st_disponibilidade = 'LOTADO'       AND qt_vagas IS NOT NULL AND qt_vagas = 0) OR
        (st_disponibilidade IN ('ABERTO', 'FILA_GRANDE', 'FECHADO') AND qt_vagas IS NULL))
);

-- Totens instalados na cidade
CREATE TABLE T_TOTEM (
    id_totem                  NUMBER(10)    NOT NULL,
    cd_totem                  VARCHAR2(12)  NOT NULL,
    ds_logradouro             VARCHAR2(120) NOT NULL,
    nm_bairro                 VARCHAR2(60)  NOT NULL,
    nr_latitude               NUMBER(9,6)   NOT NULL,
    nr_longitude              NUMBER(9,6)   NOT NULL,
    st_totem                  VARCHAR2(12)  DEFAULT 'ONLINE' NOT NULL,
    fl_papel_impressora       CHAR(1)       DEFAULT 'S' NOT NULL,
    dt_instalacao             DATE          NOT NULL,
    dt_ultima_sincronizacao   DATE,
    CONSTRAINT pk_totem PRIMARY KEY (id_totem),
    CONSTRAINT un_totem_codigo UNIQUE (cd_totem),
    CONSTRAINT ck_tot_status CHECK (st_totem IN ('ONLINE', 'OFFLINE', 'MANUTENCAO')),
    CONSTRAINT ck_tot_papel CHECK (fl_papel_impressora IN ('S', 'N'))
);

-- Cada consulta feita no totem, sem nenhum dado pessoal (RNF07).
-- O código de chegada vem no papel impresso e é registrado pelo serviço.
CREATE TABLE T_CONSULTA_TOTEM (
    id_consulta            NUMBER(10)  NOT NULL,
    id_totem               NUMBER(10)  NOT NULL,
    id_tipo_servico        NUMBER(3)   NOT NULL,
    id_perfil              NUMBER(3),
    id_servico_escolhido   NUMBER(10),
    dt_consulta            DATE        NOT NULL,
    fl_imprimiu_rota       CHAR(1)     DEFAULT 'N' NOT NULL,
    fl_pediu_abordagem     CHAR(1)     DEFAULT 'N' NOT NULL,
    cd_chegada             VARCHAR2(8),
    dt_chegada             DATE,
    CONSTRAINT pk_consulta PRIMARY KEY (id_consulta),
    CONSTRAINT un_cons_chegada UNIQUE (cd_chegada),
    CONSTRAINT ck_cons_imprimiu CHECK (fl_imprimiu_rota IN ('S', 'N')),
    CONSTRAINT ck_cons_abordagem CHECK (fl_pediu_abordagem IN ('S', 'N')),
    CONSTRAINT ck_cons_codigo CHECK (cd_chegada IS NULL OR fl_imprimiu_rota = 'S'),
    CONSTRAINT ck_cons_dt_chegada CHECK (dt_chegada IS NULL OR
        (cd_chegada IS NOT NULL AND dt_chegada >= dt_consulta))
);

-- Postos de reciclagem instalados ao lado dos totens
CREATE TABLE T_POSTO_RECICLAGEM (
    id_posto              NUMBER(10)   NOT NULL,
    id_totem              NUMBER(10)   NOT NULL,
    id_cooperativa        NUMBER(10)   NOT NULL,
    nr_capacidade_kg      NUMBER(6,2)  NOT NULL,
    nr_limite_diario_kg   NUMBER(5,2)  DEFAULT 10 NOT NULL,
    st_posto              VARCHAR2(12) DEFAULT 'ATIVO' NOT NULL,
    CONSTRAINT pk_posto PRIMARY KEY (id_posto),
    CONSTRAINT un_posto_totem UNIQUE (id_totem),
    CONSTRAINT ck_pos_capac CHECK (nr_capacidade_kg > 0),
    CONSTRAINT ck_pos_limite CHECK (nr_limite_diario_kg > 0),
    CONSTRAINT ck_pos_status CHECK (st_posto IN ('ATIVO', 'CHEIO', 'MANUTENCAO'))
);

-- Materiais. Só lata de alumínio e PET são aceitos (RN09); os demais
-- ficam cadastrados para o posto recusar com uma mensagem clara.
CREATE TABLE T_MATERIAL (
    id_material          NUMBER(3)    NOT NULL,
    nm_material          VARCHAR2(40) NOT NULL,
    nr_creditos_por_kg   NUMBER(5)    NOT NULL,
    fl_aceito            CHAR(1)      NOT NULL,
    CONSTRAINT pk_material PRIMARY KEY (id_material),
    CONSTRAINT un_material_nome UNIQUE (nm_material),
    CONSTRAINT ck_mat_aceito CHECK (
        (fl_aceito = 'S' AND nr_creditos_por_kg > 0) OR
        (fl_aceito = 'N' AND nr_creditos_por_kg = 0))
);

-- Cartão anônimo: só código aleatório, sem nome, CPF ou foto (RN07).
-- id_cartao_anterior liga o cartão novo ao cartão perdido (RN12).
CREATE TABLE T_CARTAO (
    id_cartao                NUMBER(10)  NOT NULL,
    cd_cartao                VARCHAR2(10) NOT NULL,
    id_organizacao_emissora  NUMBER(10)  NOT NULL,
    id_cartao_anterior       NUMBER(10),
    dt_emissao               DATE        NOT NULL,
    st_cartao                VARCHAR2(10) DEFAULT 'ATIVO' NOT NULL,
    nr_saldo_creditos        NUMBER(8)   DEFAULT 0 NOT NULL,
    CONSTRAINT pk_cartao PRIMARY KEY (id_cartao),
    CONSTRAINT un_cartao_codigo UNIQUE (cd_cartao),
    CONSTRAINT un_cartao_anterior UNIQUE (id_cartao_anterior),
    CONSTRAINT ck_car_status CHECK (st_cartao IN ('ATIVO', 'BLOQUEADO')),
    CONSTRAINT ck_car_saldo CHECK (nr_saldo_creditos >= 0),
    CONSTRAINT ck_car_bloq_saldo CHECK (st_cartao = 'ATIVO' OR nr_saldo_creditos = 0)
);

-- Cada entrega de material no posto
CREATE TABLE T_ENTREGA (
    id_entrega            NUMBER(10)  NOT NULL,
    id_cartao             NUMBER(10)  NOT NULL,
    id_posto              NUMBER(10)  NOT NULL,
    id_material           NUMBER(3)   NOT NULL,
    nr_peso_kg            NUMBER(5,2) NOT NULL,
    nr_creditos_gerados   NUMBER(6)   NOT NULL,
    dt_entrega            DATE        NOT NULL,
    CONSTRAINT pk_entrega PRIMARY KEY (id_entrega),
    CONSTRAINT ck_ent_peso CHECK (nr_peso_kg > 0 AND nr_peso_kg <= 10),
    CONSTRAINT ck_ent_creditos CHECK (nr_creditos_gerados > 0)
);

-- Catálogo de recompensas (só itens extras - RN01 e RN11)
CREATE TABLE T_RECOMPENSA (
    id_recompensa             NUMBER(10)   NOT NULL,
    id_organizacao_parceira   NUMBER(10)   NOT NULL,
    nm_recompensa             VARCHAR2(80) NOT NULL,
    nr_custo_creditos         NUMBER(6)    NOT NULL,
    fl_ativo                  CHAR(1)      DEFAULT 'S' NOT NULL,
    CONSTRAINT pk_recompensa PRIMARY KEY (id_recompensa),
    CONSTRAINT un_recompensa_nome UNIQUE (nm_recompensa),
    CONSTRAINT ck_rec_custo CHECK (nr_custo_creditos > 0),
    CONSTRAINT ck_rec_ativo CHECK (fl_ativo IN ('S', 'N'))
);

-- Trocas de créditos por recompensas, registradas pela equipe
CREATE TABLE T_TROCA (
    id_troca             NUMBER(10) NOT NULL,
    id_cartao            NUMBER(10) NOT NULL,
    id_recompensa        NUMBER(10) NOT NULL,
    id_usuario           NUMBER(10) NOT NULL,
    nr_creditos_usados   NUMBER(6)  NOT NULL,
    dt_troca             DATE       NOT NULL,
    CONSTRAINT pk_troca PRIMARY KEY (id_troca),
    CONSTRAINT ck_tro_creditos CHECK (nr_creditos_usados > 0)
);

-- Coletas do material dos postos feitas pelas cooperativas
CREATE TABLE T_COLETA (
    id_coleta          NUMBER(10)  NOT NULL,
    id_posto           NUMBER(10)  NOT NULL,
    id_cooperativa     NUMBER(10)  NOT NULL,
    nr_peso_total_kg   NUMBER(7,2) NOT NULL,
    dt_coleta          DATE        NOT NULL,
    CONSTRAINT pk_coleta PRIMARY KEY (id_coleta),
    CONSTRAINT ck_col_peso CHECK (nr_peso_total_kg > 0)
);

-- 2. ALTER TABLE - chaves estrangeiras ---------------------------------------------------------------------

ALTER TABLE T_SERVICO ADD CONSTRAINT fk_serv_organizacao
    FOREIGN KEY (id_organizacao) REFERENCES T_ORGANIZACAO (id_organizacao);
ALTER TABLE T_SERVICO ADD CONSTRAINT fk_serv_tipo
    FOREIGN KEY (id_tipo_servico) REFERENCES T_TIPO_SERVICO (id_tipo_servico);

ALTER TABLE T_SERVICO_PERFIL ADD CONSTRAINT fk_sp_servico
    FOREIGN KEY (id_servico) REFERENCES T_SERVICO (id_servico);
ALTER TABLE T_SERVICO_PERFIL ADD CONSTRAINT fk_sp_perfil
    FOREIGN KEY (id_perfil) REFERENCES T_PERFIL_ATENDIDO (id_perfil);

ALTER TABLE T_HORARIO ADD CONSTRAINT fk_hor_servico
    FOREIGN KEY (id_servico) REFERENCES T_SERVICO (id_servico);

ALTER TABLE T_USUARIO_PAINEL ADD CONSTRAINT fk_usu_organizacao
    FOREIGN KEY (id_organizacao) REFERENCES T_ORGANIZACAO (id_organizacao);

ALTER TABLE T_ATUALIZACAO ADD CONSTRAINT fk_atu_servico
    FOREIGN KEY (id_servico) REFERENCES T_SERVICO (id_servico);
ALTER TABLE T_ATUALIZACAO ADD CONSTRAINT fk_atu_usuario
    FOREIGN KEY (id_usuario) REFERENCES T_USUARIO_PAINEL (id_usuario);

ALTER TABLE T_CONSULTA_TOTEM ADD CONSTRAINT fk_cons_totem
    FOREIGN KEY (id_totem) REFERENCES T_TOTEM (id_totem);
ALTER TABLE T_CONSULTA_TOTEM ADD CONSTRAINT fk_cons_tipo
    FOREIGN KEY (id_tipo_servico) REFERENCES T_TIPO_SERVICO (id_tipo_servico);
ALTER TABLE T_CONSULTA_TOTEM ADD CONSTRAINT fk_cons_perfil
    FOREIGN KEY (id_perfil) REFERENCES T_PERFIL_ATENDIDO (id_perfil);
ALTER TABLE T_CONSULTA_TOTEM ADD CONSTRAINT fk_cons_servico
    FOREIGN KEY (id_servico_escolhido) REFERENCES T_SERVICO (id_servico);

ALTER TABLE T_POSTO_RECICLAGEM ADD CONSTRAINT fk_pos_totem
    FOREIGN KEY (id_totem) REFERENCES T_TOTEM (id_totem);
ALTER TABLE T_POSTO_RECICLAGEM ADD CONSTRAINT fk_pos_cooperativa
    FOREIGN KEY (id_cooperativa) REFERENCES T_ORGANIZACAO (id_organizacao);

ALTER TABLE T_CARTAO ADD CONSTRAINT fk_car_organizacao
    FOREIGN KEY (id_organizacao_emissora) REFERENCES T_ORGANIZACAO (id_organizacao);
ALTER TABLE T_CARTAO ADD CONSTRAINT fk_car_anterior
    FOREIGN KEY (id_cartao_anterior) REFERENCES T_CARTAO (id_cartao);

ALTER TABLE T_ENTREGA ADD CONSTRAINT fk_ent_cartao
    FOREIGN KEY (id_cartao) REFERENCES T_CARTAO (id_cartao);
ALTER TABLE T_ENTREGA ADD CONSTRAINT fk_ent_posto
    FOREIGN KEY (id_posto) REFERENCES T_POSTO_RECICLAGEM (id_posto);
ALTER TABLE T_ENTREGA ADD CONSTRAINT fk_ent_material
    FOREIGN KEY (id_material) REFERENCES T_MATERIAL (id_material);

ALTER TABLE T_RECOMPENSA ADD CONSTRAINT fk_rec_organizacao
    FOREIGN KEY (id_organizacao_parceira) REFERENCES T_ORGANIZACAO (id_organizacao);

ALTER TABLE T_TROCA ADD CONSTRAINT fk_tro_cartao
    FOREIGN KEY (id_cartao) REFERENCES T_CARTAO (id_cartao);
ALTER TABLE T_TROCA ADD CONSTRAINT fk_tro_recompensa
    FOREIGN KEY (id_recompensa) REFERENCES T_RECOMPENSA (id_recompensa);
ALTER TABLE T_TROCA ADD CONSTRAINT fk_tro_usuario
    FOREIGN KEY (id_usuario) REFERENCES T_USUARIO_PAINEL (id_usuario);

ALTER TABLE T_COLETA ADD CONSTRAINT fk_col_posto
    FOREIGN KEY (id_posto) REFERENCES T_POSTO_RECICLAGEM (id_posto);
ALTER TABLE T_COLETA ADD CONSTRAINT fk_col_cooperativa
    FOREIGN KEY (id_cooperativa) REFERENCES T_ORGANIZACAO (id_organizacao);
