-- =====================================================================
-- CIVIA - Dados de exemplo (TODOS SIMULADOS: a GS proibe dados pessoais reais)
-- Regiao central de Sao Paulo (Se e entorno). Executar apos 01_ddl.sql e 02_sequencias.sql.
-- As datas sao relativas ao momento da carga (SYSDATE do servidor, horario de Sao Paulo).
-- =====================================================================

-- Organizacoes
INSERT INTO T_ORGANIZACAO VALUES (SQ_ORGANIZACAO.NEXTVAL, 'Centro POP Se (simulado)', 'CENTRO_POP', '11900000001', 'pop.se@civia.exemplo.org');
INSERT INTO T_ORGANIZACAO VALUES (SQ_ORGANIZACAO.NEXTVAL, 'ONG Mesa Solidaria (simulada)', 'ONG', '11900000002', 'mesa@civia.exemplo.org');
INSERT INTO T_ORGANIZACAO VALUES (SQ_ORGANIZACAO.NEXTVAL, 'Prefeitura - Assistencia Social (simulado)', 'PREFEITURA', '156', 'gestao@civia.exemplo.org');
INSERT INTO T_ORGANIZACAO VALUES (SQ_ORGANIZACAO.NEXTVAL, 'Cooperativa Recicla Centro (simulada)', 'COOPERATIVA', '11900000004', 'coop@civia.exemplo.org');
INSERT INTO T_ORGANIZACAO VALUES (SQ_ORGANIZACAO.NEXTVAL, 'Parceiro Agasalho Amigo (simulado)', 'PARCEIRO', NULL, 'parceiro@civia.exemplo.org');

-- Tipos de servico (tela inicial do totem - RF01)
INSERT INTO T_TIPO_SERVICO VALUES (SQ_TIPO_SERVICO.NEXTVAL, 'Abrigo', 'cama');
INSERT INTO T_TIPO_SERVICO VALUES (SQ_TIPO_SERVICO.NEXTVAL, 'Refeicao', 'prato');
INSERT INTO T_TIPO_SERVICO VALUES (SQ_TIPO_SERVICO.NEXTVAL, 'Banho', 'chuveiro');
INSERT INTO T_TIPO_SERVICO VALUES (SQ_TIPO_SERVICO.NEXTVAL, 'Atendimento', 'pessoa');

-- Perfis atendidos (US04)
INSERT INTO T_PERFIL_ATENDIDO VALUES (SQ_PERFIL_ATENDIDO.NEXTVAL, 'Homem');
INSERT INTO T_PERFIL_ATENDIDO VALUES (SQ_PERFIL_ATENDIDO.NEXTVAL, 'Mulher');
INSERT INTO T_PERFIL_ATENDIDO VALUES (SQ_PERFIL_ATENDIDO.NEXTVAL, 'Familia com crianca');
INSERT INTO T_PERFIL_ATENDIDO VALUES (SQ_PERFIL_ATENDIDO.NEXTVAL, 'Pessoa LGBT+');
INSERT INTO T_PERFIL_ATENDIDO VALUES (SQ_PERFIL_ATENDIDO.NEXTVAL, 'Pessoa idosa');
INSERT INTO T_PERFIL_ATENDIDO VALUES (SQ_PERFIL_ATENDIDO.NEXTVAL, 'Pessoa com animal');

-- Servicos (colunas: id, organizacao, tipo, nome, logradouro, numero, bairro, lat, lon, capacidade, 24h, aprovacao, cadastro)
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Prefeitura - Assistencia Social (simulado)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Abrigo'),
    'Centro de Acolhida Liberdade (simulado)', 'Rua dos Estudantes', '200', 'Liberdade', -23.555870, -46.635210, 80, 'N', 'APROVADO', SYSDATE - 30);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Prefeitura - Assistencia Social (simulado)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Abrigo'),
    'Casa de Acolhida Mulheres Se (simulado)', 'Rua Tabatinguera', '150', 'Se', -23.551910, -46.630950, 40, 'S', 'APROVADO', SYSDATE - 30);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Centro POP Se (simulado)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Abrigo'),
    'Abrigo Gloria com Pets (simulado)', 'Rua da Gloria', '410', 'Liberdade', -23.557400, -46.634100, 30, 'N', 'APROVADO', SYSDATE - 20);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'ONG Mesa Solidaria (simulada)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Abrigo'),
    'Abrigo Bela Vista (simulado)', 'Rua Santo Antonio', '600', 'Bela Vista', -23.554300, -46.642100, 25, 'N', 'PENDENTE', SYSDATE - 1);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'ONG Mesa Solidaria (simulada)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Refeicao'),
    'Restaurante Comunitario Se (simulado)', 'Rua Roberto Simonsen', '80', 'Se', -23.549600, -46.631200, NULL, 'N', 'APROVADO', SYSDATE - 30);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'ONG Mesa Solidaria (simulada)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Refeicao'),
    'Sopao Solidario Anhangabau (simulado)', 'Rua Formosa', '120', 'Centro', -23.546300, -46.637900, NULL, 'N', 'APROVADO', SYSDATE - 15);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Centro POP Se (simulado)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Banho'),
    'Nucleo de Convivencia Banho Se (simulado)', 'Rua do Carmo', '50', 'Se', -23.550900, -46.632100, NULL, 'N', 'APROVADO', SYSDATE - 30);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'ONG Mesa Solidaria (simulada)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Banho'),
    'Banho Solidario Glicerio (simulado)', 'Rua do Glicerio', '300', 'Liberdade', -23.555100, -46.629800, NULL, 'N', 'REPROVADO', SYSDATE - 10);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Centro POP Se (simulado)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Atendimento'),
    'Centro POP Se - Atendimento (simulado)', 'Rua Riachuelo', '268', 'Se', -23.551700, -46.637000, NULL, 'N', 'APROVADO', SYSDATE - 30);
INSERT INTO T_SERVICO VALUES (SQ_SERVICO.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Prefeitura - Assistencia Social (simulado)'),
    (SELECT id_tipo_servico FROM T_TIPO_SERVICO WHERE nm_tipo_servico = 'Atendimento'),
    'CREAS Centro - Atendimento (simulado)', 'Rua Prof. Batista de Andrade', '20', 'Bras', -23.544800, -46.622900, NULL, 'N', 'APROVADO', SYSDATE - 30);

-- Perfis atendidos pelos abrigos (T_SERVICO_PERFIL)
INSERT INTO T_SERVICO_PERFIL SELECT s.id_servico, p.id_perfil FROM T_SERVICO s, T_PERFIL_ATENDIDO p
 WHERE s.nm_servico = 'Centro de Acolhida Liberdade (simulado)' AND p.ds_perfil IN ('Homem', 'Pessoa idosa');
INSERT INTO T_SERVICO_PERFIL SELECT s.id_servico, p.id_perfil FROM T_SERVICO s, T_PERFIL_ATENDIDO p
 WHERE s.nm_servico = 'Casa de Acolhida Mulheres Se (simulado)' AND p.ds_perfil IN ('Mulher', 'Familia com crianca', 'Pessoa LGBT+');
INSERT INTO T_SERVICO_PERFIL SELECT s.id_servico, p.id_perfil FROM T_SERVICO s, T_PERFIL_ATENDIDO p
 WHERE s.nm_servico = 'Abrigo Gloria com Pets (simulado)' AND p.ds_perfil IN ('Homem', 'Mulher', 'Pessoa com animal');
INSERT INTO T_SERVICO_PERFIL SELECT s.id_servico, p.id_perfil FROM T_SERVICO s, T_PERFIL_ATENDIDO p
 WHERE s.nm_servico = 'Abrigo Bela Vista (simulado)' AND p.ds_perfil IN ('Homem');

-- Horarios (1 = domingo ... 7 = sabado). Abrigos noturnos usam duas faixas: 18:00-23:59 e 00:00-08:00.
INSERT INTO T_HORARIO SELECT SQ_HORARIO.NEXTVAL, s.id_servico, d.dia, '18:00', '23:59'
  FROM T_SERVICO s, (SELECT LEVEL AS dia FROM dual CONNECT BY LEVEL <= 7) d
 WHERE s.nm_servico IN ('Centro de Acolhida Liberdade (simulado)', 'Abrigo Gloria com Pets (simulado)', 'Abrigo Bela Vista (simulado)');
INSERT INTO T_HORARIO SELECT SQ_HORARIO.NEXTVAL, s.id_servico, d.dia, '00:00', '08:00'
  FROM T_SERVICO s, (SELECT LEVEL AS dia FROM dual CONNECT BY LEVEL <= 7) d
 WHERE s.nm_servico IN ('Centro de Acolhida Liberdade (simulado)', 'Abrigo Gloria com Pets (simulado)', 'Abrigo Bela Vista (simulado)');
INSERT INTO T_HORARIO SELECT SQ_HORARIO.NEXTVAL, s.id_servico, d.dia, '11:00', '14:00'
  FROM T_SERVICO s, (SELECT LEVEL AS dia FROM dual CONNECT BY LEVEL <= 7) d
 WHERE s.nm_servico = 'Restaurante Comunitario Se (simulado)';
INSERT INTO T_HORARIO SELECT SQ_HORARIO.NEXTVAL, s.id_servico, d.dia, '18:00', '21:00'
  FROM T_SERVICO s, (SELECT LEVEL AS dia FROM dual CONNECT BY LEVEL <= 7) d
 WHERE s.nm_servico = 'Sopao Solidario Anhangabau (simulado)';
INSERT INTO T_HORARIO SELECT SQ_HORARIO.NEXTVAL, s.id_servico, d.dia, '07:00', '17:00'
  FROM T_SERVICO s, (SELECT LEVEL + 1 AS dia FROM dual CONNECT BY LEVEL <= 6) d
 WHERE s.nm_servico = 'Nucleo de Convivencia Banho Se (simulado)';
INSERT INTO T_HORARIO SELECT SQ_HORARIO.NEXTVAL, s.id_servico, d.dia, '08:00', '12:00'
  FROM T_SERVICO s, (SELECT LEVEL + 1 AS dia FROM dual CONNECT BY LEVEL <= 5) d
 WHERE s.nm_servico = 'Banho Solidario Glicerio (simulado)';
INSERT INTO T_HORARIO SELECT SQ_HORARIO.NEXTVAL, s.id_servico, d.dia, '08:00', '17:00'
  FROM T_SERVICO s, (SELECT LEVEL + 1 AS dia FROM dual CONNECT BY LEVEL <= 5) d
 WHERE s.nm_servico IN ('Centro POP Se - Atendimento (simulado)', 'CREAS Centro - Atendimento (simulado)');

-- Usuarios do painel (nomes e e-mails ficticios)
INSERT INTO T_USUARIO_PAINEL VALUES (SQ_USUARIO_PAINEL.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Centro POP Se (simulado)'),
    'Ana Equipe (simulada)', 'ana.equipe@civia.exemplo.org', 'EQUIPE', 'S');
INSERT INTO T_USUARIO_PAINEL VALUES (SQ_USUARIO_PAINEL.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'ONG Mesa Solidaria (simulada)'),
    'Carla Voluntaria (simulada)', 'carla.voluntaria@civia.exemplo.org', 'EQUIPE', 'S');
INSERT INTO T_USUARIO_PAINEL VALUES (SQ_USUARIO_PAINEL.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Prefeitura - Assistencia Social (simulado)'),
    'Bruno Gestor (simulado)', 'bruno.gestor@civia.exemplo.org', 'GESTOR', 'S');
INSERT INTO T_USUARIO_PAINEL VALUES (SQ_USUARIO_PAINEL.NEXTVAL,
    (SELECT id_organizacao FROM T_ORGANIZACAO WHERE nm_organizacao = 'Cooperativa Recicla Centro (simulada)'),
    'Diego Cooperativa (simulado)', 'diego.coop@civia.exemplo.org', 'COOPERATIVA', 'S');

-- Atualizacoes de disponibilidade
-- Abrigos: vagas vindas da Central de Vagas (sem usuario); status sempre em faixa (RN03)
INSERT INTO T_ATUALIZACAO SELECT SQ_ATUALIZACAO.NEXTVAL, id_servico, NULL, 'TEM_VAGA', 6, 'CENTRAL_VAGAS', SYSDATE - 20/1440
  FROM T_SERVICO WHERE nm_servico = 'Centro de Acolhida Liberdade (simulado)';
INSERT INTO T_ATUALIZACAO SELECT SQ_ATUALIZACAO.NEXTVAL, id_servico, NULL, 'POUCAS_VAGAS', 2, 'CENTRAL_VAGAS', SYSDATE - 45/1440
  FROM T_SERVICO WHERE nm_servico = 'Casa de Acolhida Mulheres Se (simulado)';
INSERT INTO T_ATUALIZACAO SELECT SQ_ATUALIZACAO.NEXTVAL, s.id_servico, u.id_usuario, 'LOTADO', 0, 'PAINEL', SYSDATE - 3/24
  FROM T_SERVICO s, T_USUARIO_PAINEL u
 WHERE s.nm_servico = 'Abrigo Gloria com Pets (simulado)' AND u.ds_email = 'ana.equipe@civia.exemplo.org';
-- Demais servicos: equipes pelo painel
INSERT INTO T_ATUALIZACAO SELECT SQ_ATUALIZACAO.NEXTVAL, s.id_servico, u.id_usuario, 'FILA_GRANDE', NULL, 'PAINEL', SYSDATE - 10/1440
  FROM T_SERVICO s, T_USUARIO_PAINEL u
 WHERE s.nm_servico = 'Restaurante Comunitario Se (simulado)' AND u.ds_email = 'carla.voluntaria@civia.exemplo.org';
INSERT INTO T_ATUALIZACAO SELECT SQ_ATUALIZACAO.NEXTVAL, s.id_servico, u.id_usuario, 'ABERTO', NULL, 'PAINEL', SYSDATE - 30/1440
  FROM T_SERVICO s, T_USUARIO_PAINEL u
 WHERE s.nm_servico = 'Nucleo de Convivencia Banho Se (simulado)' AND u.ds_email = 'ana.equipe@civia.exemplo.org';

-- Totens
INSERT INTO T_TOTEM VALUES (SQ_TOTEM.NEXTVAL, 'TOT-SE-01', 'Praca da Se, em frente a Catedral', 'Se',
    -23.550520, -46.633308, 'ONLINE', 'S', DATE '2026-09-01', SYSDATE - 5/1440);
INSERT INTO T_TOTEM VALUES (SQ_TOTEM.NEXTVAL, 'TOT-ANH-01', 'Vale do Anhangabau, proximo a escadaria', 'Centro',
    -23.545210, -46.636780, 'ONLINE', 'N', DATE '2026-09-15', SYSDATE - 2/1440);
INSERT INTO T_TOTEM VALUES (SQ_TOTEM.NEXTVAL, 'TOT-PDP-01', 'Terminal Parque Dom Pedro II', 'Se',
    -23.549540, -46.627880, 'MANUTENCAO', 'S', DATE '2026-10-01', SYSDATE - 2);

-- Consultas feitas nos totens (sem dado pessoal - RNF07)
INSERT INTO T_CONSULTA_TOTEM SELECT SQ_CONSULTA_TOTEM.NEXTVAL, t.id_totem, ts.id_tipo_servico, p.id_perfil, s.id_servico,
       SYSDATE - 2/24, 'S', 'N', 'K7M2Q9XA', SYSDATE - 1/24
  FROM T_TOTEM t, T_TIPO_SERVICO ts, T_PERFIL_ATENDIDO p, T_SERVICO s
 WHERE t.cd_totem = 'TOT-SE-01' AND ts.nm_tipo_servico = 'Abrigo' AND p.ds_perfil = 'Mulher'
   AND s.nm_servico = 'Casa de Acolhida Mulheres Se (simulado)';
INSERT INTO T_CONSULTA_TOTEM SELECT SQ_CONSULTA_TOTEM.NEXTVAL, t.id_totem, ts.id_tipo_servico, NULL, s.id_servico,
       SYSDATE - 50/1440, 'S', 'N', 'P3XR8TWD', NULL
  FROM T_TOTEM t, T_TIPO_SERVICO ts, T_SERVICO s
 WHERE t.cd_totem = 'TOT-SE-01' AND ts.nm_tipo_servico = 'Refeicao' AND s.nm_servico = 'Restaurante Comunitario Se (simulado)';
INSERT INTO T_CONSULTA_TOTEM SELECT SQ_CONSULTA_TOTEM.NEXTVAL, t.id_totem, ts.id_tipo_servico, p.id_perfil, NULL,
       SYSDATE - 15/1440, 'N', 'S', NULL, NULL
  FROM T_TOTEM t, T_TIPO_SERVICO ts, T_PERFIL_ATENDIDO p
 WHERE t.cd_totem = 'TOT-ANH-01' AND ts.nm_tipo_servico = 'Abrigo' AND p.ds_perfil = 'Homem';

COMMIT;
