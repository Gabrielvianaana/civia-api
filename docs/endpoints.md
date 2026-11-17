# Endpoints da CIVIA API

Base local: `http://localhost:8080` | Deploy: `https://civia-api-ibn6.onrender.com`. Recursos sob `/api/v1`.

Os exemplos abaixo sao **respostas reais** capturadas executando a colecao Postman (`postman/CIVIA.postman_collection.json`) contra a API conectada ao Oracle da FIAP. Listas foram encurtadas para o primeiro item. Todos os erros seguem o formato Problem Details (RFC 9457).

## Resumo

| Metodo | Rota | Descricao |
|---|---|---|
| GET | `/actuator/health` | Saude da aplicacao e da conexao com o Oracle |
| GET | `/api/v1/tipos-servico` | Lista os tipos de servico (tela inicial do totem) |
| POST | `/api/v1/tipos-servico` | Cadastra tipo de servico |
| GET | `/api/v1/tipos-servico/{id}` | Busca tipo de servico |
| PUT | `/api/v1/tipos-servico/{id}` | Atualiza tipo de servico |
| GET | `/api/v1/perfis` | Lista perfis de publico atendido |
| POST | `/api/v1/perfis` | Cadastra perfil |
| GET | `/api/v1/perfis/{id}` | Busca perfil |
| PUT | `/api/v1/perfis/{id}` | Atualiza perfil |
| GET | `/api/v1/organizacoes` | Lista organizacoes (filtro opcional ?tipo=) |
| POST | `/api/v1/organizacoes` | Cadastra organizacao (nome unico) |
| GET | `/api/v1/organizacoes/{id}` | Busca organizacao |
| PUT | `/api/v1/organizacoes/{id}` | Atualiza organizacao |
| GET | `/api/v1/usuarios` | Lista usuarios do painel (filtro opcional ?organizacaoId=) |
| POST | `/api/v1/usuarios` | Cadastra usuario do painel (e-mail unico) |
| GET | `/api/v1/usuarios/{id}` | Busca usuario |
| PUT | `/api/v1/usuarios/{id}` | Atualiza usuario (inclusive ativo=false) |
| GET | `/api/v1/servicos` | Lista servicos (filtros ?tipoServicoId= &statusAprovacao= &organizacaoId=) |
| POST | `/api/v1/servicos` | Cadastra servico com perfis atendidos (nasce PENDENTE se nao informado) |
| GET | `/api/v1/servicos/{id}` | Busca servico |
| PUT | `/api/v1/servicos/{id}` | Atualiza servico (inclusive aprovacao pelo gestor - RN06) |
| GET | `/api/v1/servicos/{servicoId}/horarios` | Lista horarios do servico |
| POST | `/api/v1/servicos/{servicoId}/horarios` | Cadastra faixa de horario (dia 1=domingo..7=sabado) |
| GET | `/api/v1/servicos/{servicoId}/horarios/{id}` | Busca horario |
| PUT | `/api/v1/servicos/{servicoId}/horarios/{id}` | Atualiza horario (sem sobreposicao no mesmo dia) |
| POST | `/api/v1/atualizacoes` | Registra disponibilidade: abrigo informa vagas e grava a faixa (RN03); demais informam situacao |
| GET | `/api/v1/atualizacoes` | Lista atualizacoes (filtro ?servicoId=), da mais recente para a mais antiga |
| GET | `/api/v1/atualizacoes/{id}` | Busca atualizacao |
| PUT | `/api/v1/atualizacoes/{id}` | Corrige atualizacao |
| GET | `/api/v1/totens` | Lista totens |
| POST | `/api/v1/totens` | Cadastra totem (codigo unico) |
| GET | `/api/v1/totens/{id}` | Busca totem |
| PUT | `/api/v1/totens/{id}` | Atualiza totem (status ONLINE/OFFLINE/MANUTENCAO) |
| GET | `/api/v1/totens/{id}/servicos` | Mapa do totem: ate 3 servicos mais proximos com status (RN03 a RN06). ?tipo= e ?perfil= aceitam id ou nome |
| POST | `/api/v1/consultas` | Registra consulta (sem dado pessoal); com rota impressa gera codigo de chegada |
| GET | `/api/v1/consultas` | Lista consultas feitas nos totens (filtro ?totemId=) |
| GET | `/api/v1/consultas/{id}` | Busca consulta |
| PUT | `/api/v1/consultas/{id}` | Atualiza consulta |
| PUT | `/api/v1/consultas/chegada/{codigo}` | Registra a chegada da pessoa pelo codigo impresso (US14) |
| GET | `/api/v1/materiais` | Lista materiais (RN09: so lata de aluminio e garrafa PET sao aceitas) |
| POST | `/api/v1/materiais` | Cadastra material (aceito gera creditos por kg; nao aceito tem 0) |
| GET | `/api/v1/materiais/{id}` | Busca material |
| PUT | `/api/v1/materiais/{id}` | Atualiza material |
| GET | `/api/v1/postos-reciclagem` | Lista postos de reciclagem (filtro ?status=) com material acumulado desde a ultima coleta |
| POST | `/api/v1/postos-reciclagem` | Cadastra posto (um por totem, atendido por COOPERATIVA; limite diario padrao 10 kg - RN08) |
| GET | `/api/v1/postos-reciclagem/{id}` | Busca posto |
| PUT | `/api/v1/postos-reciclagem/{id}` | Atualiza posto (status ATIVO/CHEIO/MANUTENCAO) |
| POST | `/api/v1/cartoes` | Emite cartao anonimo no Centro POP (RN07); com cartaoAnteriorId substitui o perdido e transfere o saldo (RN12) |
| GET | `/api/v1/cartoes` | Lista cartoes (filtro ?status=) |
| GET | `/api/v1/cartoes/{id}` | Busca cartao |
| GET | `/api/v1/cartoes/codigo/{codigo}` | Consulta o saldo pelo codigo do cartao |
| PUT | `/api/v1/cartoes/{id}` | Atualiza emissora/status (bloquear com saldo exige substituicao) |
| POST | `/api/v1/entregas` | Registra entrega: material aceito (RN09), limite diario (RN08), capacidade do posto; credita o cartao |
| GET | `/api/v1/entregas` | Lista entregas (filtros ?cartaoId= e ?postoId=) |
| GET | `/api/v1/entregas/{id}` | Busca entrega |
| PUT | `/api/v1/entregas/{id}` | Corrige entrega e recalcula os creditos |
| GET | `/api/v1/recompensas` | Lista o catalogo de recompensas (filtro ?ativo=) |
| POST | `/api/v1/recompensas` | Cadastra recompensa (nome unico) |
| GET | `/api/v1/recompensas/{id}` | Busca recompensa |
| PUT | `/api/v1/recompensas/{id}` | Atualiza recompensa (inclusive ativo=false) |
| POST | `/api/v1/trocas` | Registra troca de creditos por item do catalogo (RN11: saldo nunca negativo) |
| GET | `/api/v1/trocas` | Lista trocas (filtro ?cartaoId=) |
| GET | `/api/v1/trocas/{id}` | Busca troca |
| PUT | `/api/v1/trocas/{id}` | Corrige troca (devolve e debita de novo) |
| POST | `/api/v1/coletas` | Registra coleta pela cooperativa do posto (sem peso: usa o acumulado); posto CHEIO volta a ATIVO |
| GET | `/api/v1/coletas` | Lista coletas (filtro ?postoId=) |
| GET | `/api/v1/coletas/{id}` | Busca coleta |
| PUT | `/api/v1/coletas/{id}` | Corrige coleta |
| DELETE | `/api/v1/trocas/{id}` | Cancela troca devolvendo os creditos |
| DELETE | `/api/v1/entregas/{id}` | Exclui entrega estornando os creditos (422 se ja foram usados) |
| DELETE | `/api/v1/coletas/{id}` | Exclui coleta |
| DELETE | `/api/v1/postos-reciclagem/{id}` | Exclui posto (409 se tiver entregas ou coletas: use MANUTENCAO) |
| DELETE | `/api/v1/cartoes/{id}` | Exclui cartao (409 se tiver entregas, trocas ou substituto) |
| DELETE | `/api/v1/recompensas/{id}` | Exclui recompensa (409 se tiver trocas: desative) |
| DELETE | `/api/v1/materiais/{id}` | Exclui material (409 se tiver entregas) |
| DELETE | `/api/v1/consultas/{id}` | Exclui consulta |
| DELETE | `/api/v1/totens/{id}` | Exclui totem (409 se tiver consultas: use MANUTENCAO) |
| DELETE | `/api/v1/servicos/{servicoId}/horarios/{id}` | Exclui horario |
| DELETE | `/api/v1/atualizacoes/{id}` | Exclui atualizacao |
| DELETE | `/api/v1/servicos/{id}` | Exclui servico com horarios e atualizacoes (409 se ja escolhido em consultas) |
| DELETE | `/api/v1/usuarios/{id}` | Exclui usuario (409 se tiver atualizacoes: desative) |
| DELETE | `/api/v1/organizacoes/{id}` | Exclui organizacao (409 se tiver servicos/usuarios) |
| DELETE | `/api/v1/perfis/{id}` | Exclui perfil (409 se estiver em uso) |
| DELETE | `/api/v1/tipos-servico/{id}` | Exclui tipo de servico (409 se estiver em uso) |

## Exemplos de requisicao e resposta

| Metodo | Rota | Descricao | Exemplo de body | Exemplo de resposta (status) |
|---|---|---|---|---|
| GET | `/actuator/health` | Health (conexao com o Oracle) | - | `200 {"components":{"db":{"details":{"database":"Oracle","validationQuery":"isValid()"},"status":"UP"},"diskSpace":{"details":{"total":499963174912,"free":248487854080,"threshold":10485760,"path":"/Users/macbookpro/Downloads/fiap-ddd-checkpoint2/civia-api/.","exists":true},"status":"UP"},"livenessState":{"status":"UP"},"ping":{"status":"UP"},"readinessState":{"status":"UP"},"ssl":{"details":{"expiringChains":[],"invalidCh ...` |
| GET | `/api/v1/tipos-servico` | Listar tipos | - | `200 [{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"..."]` |
| POST | `/api/v1/tipos-servico` | Cadastrar tipo | `{"nome":"Saude","icone":"cruz"}` | `201 {"id":7,"nome":"Saude","icone":"cruz","abrigo":false}` |
| GET | `/api/v1/tipos-servico/{{tipoId}}` | Buscar tipo | - | `200 {"id":7,"nome":"Saude","icone":"cruz","abrigo":false}` |
| PUT | `/api/v1/tipos-servico/{{tipoId}}` | Atualizar tipo | `{"nome":"Saude e documentos","icone":"cruz"}` | `200 {"id":7,"nome":"Saude e documentos","icone":"cruz","abrigo":false}` |
| GET | `/api/v1/perfis` | Listar perfis | - | `200 [{"id":1,"descricao":"Homem"},"..."]` |
| POST | `/api/v1/perfis` | Cadastrar perfil | `{"descricao":"Pessoa migrante"}` | `201 {"id":9,"descricao":"Pessoa migrante"}` |
| GET | `/api/v1/perfis/{{perfilId}}` | Buscar perfil | - | `200 {"id":9,"descricao":"Pessoa migrante"}` |
| PUT | `/api/v1/perfis/{{perfilId}}` | Atualizar perfil | `{"descricao":"Pessoa migrante ou refugiada"}` | `200 {"id":9,"descricao":"Pessoa migrante ou refugiada"}` |
| GET | `/api/v1/organizacoes?tipo=ONG` | Listar organizacoes (filtro opcional por tipo) | - | `200 [{"id":2,"nome":"ONG Mesa Solidaria (simulada)","tipo":"ONG","telefone":"11900000002","email":"mesa@civia.exemplo.org"}]` |
| POST | `/api/v1/organizacoes` | Cadastrar organizacao | `{"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000099","email":"postman@civia.exemplo.org"}` | `201 {"id":8,"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000099","email":"postman@civia.exemplo.org"}` |
| GET | `/api/v1/organizacoes/{{organizacaoId}}` | Buscar organizacao | - | `200 {"id":8,"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000099","email":"postman@civia.exemplo.org"}` |
| PUT | `/api/v1/organizacoes/{{organizacaoId}}` | Atualizar organizacao | `{"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000098","email":"postman@civia.exemplo.org"}` | `200 {"id":8,"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000098","email":"postman@civia.exemplo.org"}` |
| GET | `/api/v1/usuarios` | Listar usuarios (filtro opcional por organizacao) | - | `200 [{"id":1,"organizacaoId":1,"nome":"Ana Equipe (simulada)","email":"ana.equipe@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true},"..."]` |
| POST | `/api/v1/usuarios` | Cadastrar usuario | `{
  "organizacaoId": {{organizacaoId}},
  "nome": "Equipe Postman (simulada)",
  "email": "equipe.postman@civia.exemplo.org",
  "perfilAcesso": "EQUIPE"
}` | `201 {"id":7,"organizacaoId":8,"nome":"Equipe Postman (simulada)","email":"equipe.postman@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true}` |
| GET | `/api/v1/usuarios/{{usuarioId}}` | Buscar usuario | - | `200 {"id":7,"organizacaoId":8,"nome":"Equipe Postman (simulada)","email":"equipe.postman@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true}` |
| PUT | `/api/v1/usuarios/{{usuarioId}}` | Atualizar usuario | `{
  "organizacaoId": {{organizacaoId}},
  "nome": "Equipe Postman (simulada)",
  "email": "equipe.postman@civia.exemplo.org",
  "perfilAcesso": "EQUIPE",
  "ativo": true
}` | `200 {"id":7,"organizacaoId":8,"nome":"Equipe Postman (simulada)","email":"equipe.postman@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true}` |
| GET | `/api/v1/servicos?statusAprovacao=APROVADO` | Listar servicos (filtros opcionais) | - | `200 [{"id":1,"organizacaoId":3,"tipoServico":{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"nome":"Centro de Acolhida Liberdade (simulado)","logradouro":"Rua dos Estudantes","numero":"200","bairro":"Liberdade","latitude":-23.55587,"longitude":-46.63521,"capacidade":80,"funciona24h":false,"statusAprovacao":"APROVADO","dataCadastro":"2026-10-17T22:32:18","perfis":[{"id":1,"descricao":"Homem"},{"id":5,"descricao":"P ...` |
| POST | `/api/v1/servicos` | Cadastrar servico (abrigo) | `{
  "organizacaoId": {{organizacaoId}},
  "tipoServicoId": 1,
  "nome": "Abrigo Postman (simulado)",
  "logradouro": "Rua Teste",
  "numero": "100",
  "bairro": "Se",
  "latitude": -23.5512,
  "longitude": -46.634,
  "capacidade": 20,
  "funciona24h": false,
  "statusAprovacao": "APROVADO",
  "perfilIds": [
    2,
    {{perfilId}}
  ]
}` | `201 {"id":13,"organizacaoId":8,"tipoServico":{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"nome":"Abrigo Postman (simulado)","logradouro":"Rua Teste","numero":"100","bairro":"Se","latitude":-23.5512,"longitude":-46.634,"capacidade":20,"funciona24h":false,"statusAprovacao":"APROVADO","dataCadastro":"2026-11-17T12:05:16","perfis":[{"id":2,"descricao":"Mulher"},{"id":9,"descricao":"Pessoa migrante ou refugiada"}]}` |
| GET | `/api/v1/servicos/{{servicoId}}` | Buscar servico | - | `200 {"id":13,"organizacaoId":8,"tipoServico":{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"nome":"Abrigo Postman (simulado)","logradouro":"Rua Teste","numero":"100","bairro":"Se","latitude":-23.5512,"longitude":-46.634,"capacidade":20,"funciona24h":false,"statusAprovacao":"APROVADO","dataCadastro":"2026-11-17T12:05:16","perfis":[{"id":2,"descricao":"Mulher"},{"id":9,"descricao":"Pessoa migrante ou refugiada"}]}` |
| PUT | `/api/v1/servicos/{{servicoId}}` | Atualizar servico | `{
  "organizacaoId": {{organizacaoId}},
  "tipoServicoId": 1,
  "nome": "Abrigo Postman (simulado)",
  "logradouro": "Rua Teste",
  "numero": "100",
  "bairro": "Se",
  "latitude": -23.5512,
  "longitude": -46.634,
  "capacidade": 25,
  "funciona24h": true,
  "statusAprovacao": "APROVADO",
  "perfilIds": [
    2,
    3,
    {{perfilId}}
  ]
}` | `200 {"id":13,"organizacaoId":8,"tipoServico":{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"nome":"Abrigo Postman (simulado)","logradouro":"Rua Teste","numero":"100","bairro":"Se","latitude":-23.5512,"longitude":-46.634,"capacidade":25,"funciona24h":true,"statusAprovacao":"APROVADO","dataCadastro":"2026-11-17T12:05:16","perfis":[{"id":2,"descricao":"Mulher"},{"id":3,"descricao":"Familia com crianca"},{"id":9,"des ...` |
| GET | `/api/v1/servicos/1/horarios` | Listar horarios de um servico | - | `200 [{"id":22,"servicoId":1,"diaSemana":1,"nomeDia":"domingo","horaAbertura":"00:00","horaFechamento":"08:00"},"..."]` |
| POST | `/api/v1/servicos/5/horarios` | Cadastrar horario (quarta, 18:00-23:59) | `{"diaSemana":4,"horaAbertura":"18:00","horaFechamento":"23:59"}` | `201 {"id":80,"servicoId":5,"diaSemana":4,"nomeDia":"quarta","horaAbertura":"18:00","horaFechamento":"23:59"}` |
| GET | `/api/v1/servicos/5/horarios/{{horarioId}}` | Buscar horario | - | `200 {"id":80,"servicoId":5,"diaSemana":4,"nomeDia":"quarta","horaAbertura":"18:00","horaFechamento":"23:59"}` |
| PUT | `/api/v1/servicos/5/horarios/{{horarioId}}` | Atualizar horario | `{"diaSemana":4,"horaAbertura":"19:00","horaFechamento":"22:00"}` | `200 {"id":80,"servicoId":5,"diaSemana":4,"nomeDia":"quarta","horaAbertura":"19:00","horaFechamento":"22:00"}` |
| POST | `/api/v1/atualizacoes` | Registrar vagas de abrigo pelo painel (RN03: grava a faixa) | `{
  "servicoId": {{servicoId}},
  "origem": "PAINEL",
  "usuarioId": {{usuarioId}},
  "vagas": 3
}` | `201 {"id":11,"servicoId":13,"usuarioId":7,"status":"POUCAS_VAGAS","vagas":3,"origem":"PAINEL","dataHora":"2026-11-17T12:05:17"}` |
| POST | `/api/v1/atualizacoes` | Registrar vagas pela Central de Vagas (sem usuario) | `{
  "servicoId": {{servicoId}},
  "origem": "CENTRAL_VAGAS",
  "vagas": 12
}` | `201 {"id":12,"servicoId":13,"usuarioId":null,"status":"TEM_VAGA","vagas":12,"origem":"CENTRAL_VAGAS","dataHora":"2026-11-17T12:05:17"}` |
| GET | `/api/v1/atualizacoes?servicoId={{servicoId}}` | Listar atualizacoes de um servico | - | `200 [{"id":12,"servicoId":13,"usuarioId":null,"status":"TEM_VAGA","vagas":12,"origem":"CENTRAL_VAGAS","dataHora":"2026-11-17T12:05:17"},"..."]` |
| GET | `/api/v1/atualizacoes/{{atualizacaoId}}` | Buscar atualizacao | - | `200 {"id":11,"servicoId":13,"usuarioId":7,"status":"POUCAS_VAGAS","vagas":3,"origem":"PAINEL","dataHora":"2026-11-17T12:05:17"}` |
| PUT | `/api/v1/atualizacoes/{{atualizacaoId}}` | Corrigir atualizacao | `{
  "servicoId": {{servicoId}},
  "origem": "PAINEL",
  "usuarioId": {{usuarioId}},
  "vagas": 0
}` | `200 {"id":11,"servicoId":13,"usuarioId":7,"status":"LOTADO","vagas":0,"origem":"PAINEL","dataHora":"2026-11-17T12:05:17"}` |
| GET | `/api/v1/totens` | Listar totens | - | `200 [{"id":1,"codigo":"TOT-SE-01","logradouro":"Praca da Se, em frente a Catedral","bairro":"Se","latitude":-23.55052,"longitude":-46.633308,"status":"ONLINE","papelImpressora":true,"dataInstalacao":"2026-09-01","ultimaSincronizacao":"2026-11-16T22:27:19"},"..."]` |
| POST | `/api/v1/totens` | Cadastrar totem | `{"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"papelImpressora":true}` | `201 {"id":6,"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":true,"dataInstalacao":"2026-11-17","ultimaSincronizacao":null}` |
| GET | `/api/v1/totens/{{totemId}}` | Buscar totem | - | `200 {"id":6,"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":true,"dataInstalacao":"2026-11-17","ultimaSincronizacao":null}` |
| PUT | `/api/v1/totens/{{totemId}}` | Atualizar totem | `{"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":false}` | `200 {"id":6,"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":false,"dataInstalacao":"2026-11-17","ultimaSincronizacao":null}` |
| GET | `/api/v1/totens/1/servicos?tipo=Abrigo&perfil=Mulher` | Mapa: 3 abrigos mais proximos para mulher | - | `200 {"totemId":1,"codigoTotem":"TOT-SE-01","tipoServicoFiltrado":"Abrigo","perfilFiltrado":"Mulher","consultadoEm":"2026-11-17T12:05:17","servicos":[{"servicoId":13,"nome":"Abrigo Postman (simulado)","tipoServico":"Abrigo","endereco":"Rua Teste, 100 - Se","latitude":-23.5512,"longitude":-46.634,"distanciaMetros":103,"minutosCaminhada":2,"status":"TEM_VAGA","mensagem":"Tem vaga","origemInformacao":"ATUALIZACAO_RECENTE","u ...` |
| GET | `/api/v1/totens/1/servicos?tipo=2` | Mapa: refeicoes (tipo pelo id) | - | `200 {"totemId":1,"codigoTotem":"TOT-SE-01","tipoServicoFiltrado":"Refeicao","perfilFiltrado":null,"consultadoEm":"2026-11-17T12:05:17","servicos":[{"servicoId":5,"nome":"Restaurante Comunitario Se (simulado)","tipoServico":"Refeicao","endereco":"Rua Roberto Simonsen, 80 - Se","latitude":-23.5496,"longitude":-46.6312,"distanciaMetros":238,"minutosCaminhada":3,"status":"ABERTO","mensagem":"Servindo agora","origemInformacao ...` |
| GET | `/api/v1/totens/{{totemId}}/servicos` | Mapa: todos os tipos | - | `200 {"totemId":6,"codigoTotem":"TOT-PM-01","tipoServicoFiltrado":null,"perfilFiltrado":null,"consultadoEm":"2026-11-17T12:05:17","servicos":[{"servicoId":13,"nome":"Abrigo Postman (simulado)","tipoServico":"Abrigo","endereco":"Rua Teste, 100 - Se","latitude":-23.5512,"longitude":-46.634,"distanciaMetros":68,"minutosCaminhada":1,"status":"TEM_VAGA","mensagem":"Tem vaga","origemInformacao":"ATUALIZACAO_RECENTE","ultimaAtua ...` |
| POST | `/api/v1/consultas` | Registrar consulta com rota impressa (gera codigo) | `{
  "totemId": {{totemId}},
  "tipoServicoId": 1,
  "perfilId": 2,
  "servicoEscolhidoId": {{servicoId}},
  "imprimiuRota": true,
  "pediuAbordagem": false
}` | `201 {"id":6,"totemId":6,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":13,"dataConsulta":"2026-11-17T12:05:17","imprimiuRota":true,"pediuAbordagem":false,"codigoChegada":"63TN34S3","dataChegada":null}` |
| GET | `/api/v1/consultas?totemId={{totemId}}` | Listar consultas de um totem | - | `200 [{"id":6,"totemId":6,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":13,"dataConsulta":"2026-11-17T12:05:17","imprimiuRota":true,"pediuAbordagem":false,"codigoChegada":"63TN34S3","dataChegada":null}]` |
| GET | `/api/v1/consultas/{{consultaId}}` | Buscar consulta | - | `200 {"id":6,"totemId":6,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":13,"dataConsulta":"2026-11-17T12:05:17","imprimiuRota":true,"pediuAbordagem":false,"codigoChegada":"63TN34S3","dataChegada":null}` |
| PUT | `/api/v1/consultas/{{consultaId}}` | Atualizar consulta (pediu abordagem social) | `{
  "totemId": {{totemId}},
  "tipoServicoId": 1,
  "perfilId": 2,
  "servicoEscolhidoId": {{servicoId}},
  "imprimiuRota": true,
  "pediuAbordagem": true
}` | `200 {"id":6,"totemId":6,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":13,"dataConsulta":"2026-11-17T12:05:17","imprimiuRota":true,"pediuAbordagem":true,"codigoChegada":"63TN34S3","dataChegada":null}` |
| PUT | `/api/v1/consultas/chegada/{{codigoChegada}}` | Registrar chegada pelo codigo impresso (US14) | - | `200 {"id":6,"totemId":6,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":13,"dataConsulta":"2026-11-17T12:05:17","imprimiuRota":true,"pediuAbordagem":true,"codigoChegada":"63TN34S3","dataChegada":"2026-11-17T12:05:18"}` |
| GET | `/api/v1/materiais` | Listar materiais (RN09: so lata e PET sao aceitas) | - | `200 [{"id":1,"nome":"Lata de aluminio","creditosPorKg":10,"aceito":true},"..."]` |
| POST | `/api/v1/materiais` | Cadastrar material | `{"nome":"Aluminio Postman (simulado)","creditosPorKg":4,"aceito":true}` | `201 {"id":5,"nome":"Aluminio Postman (simulado)","creditosPorKg":4,"aceito":true}` |
| GET | `/api/v1/materiais/{{materialId}}` | Buscar material | - | `200 {"id":5,"nome":"Aluminio Postman (simulado)","creditosPorKg":4,"aceito":true}` |
| PUT | `/api/v1/materiais/{{materialId}}` | Atualizar material | `{"nome":"Aluminio Postman (simulado)","creditosPorKg":6,"aceito":true}` | `200 {"id":5,"nome":"Aluminio Postman (simulado)","creditosPorKg":6,"aceito":true}` |
| GET | `/api/v1/postos-reciclagem?status=ATIVO` | Listar postos (filtro opcional por status) | - | `200 [{"id":1,"totemId":1,"cooperativaId":4,"capacidadeKg":200.0,"limiteDiarioKg":10.0,"status":"ATIVO","materialAcumuladoKg":5.5},"..."]` |
| POST | `/api/v1/postos-reciclagem` | Cadastrar posto no totem criado (limite diario 3 kg) | `{
  "totemId": {{totemId}},
  "cooperativaId": 4,
  "capacidadeKg": 50,
  "limiteDiarioKg": 3
}` | `201 {"id":3,"totemId":6,"cooperativaId":4,"capacidadeKg":50.0,"limiteDiarioKg":3.0,"status":"ATIVO","materialAcumuladoKg":0.0}` |
| GET | `/api/v1/postos-reciclagem/{{postoId}}` | Buscar posto (com material acumulado) | - | `200 {"id":3,"totemId":6,"cooperativaId":4,"capacidadeKg":50.0,"limiteDiarioKg":3.0,"status":"ATIVO","materialAcumuladoKg":0.0}` |
| PUT | `/api/v1/postos-reciclagem/{{postoId}}` | Atualizar posto | `{
  "totemId": {{totemId}},
  "cooperativaId": 4,
  "capacidadeKg": 60,
  "limiteDiarioKg": 3,
  "status": "ATIVO"
}` | `200 {"id":3,"totemId":6,"cooperativaId":4,"capacidadeKg":60.0,"limiteDiarioKg":3.0,"status":"ATIVO","materialAcumuladoKg":0.0}` |
| POST | `/api/v1/cartoes` | Emitir cartao anonimo no Centro POP (RN07) | `{"organizacaoEmissoraId":1}` | `201 {"id":6,"codigo":"WK6N6M6L","organizacaoEmissoraId":1,"cartaoAnteriorId":null,"dataEmissao":"2026-11-17T12:05:18","status":"ATIVO","saldoCreditos":0}` |
| GET | `/api/v1/cartoes?status=ATIVO` | Listar cartoes ativos | - | `200 [{"id":1,"codigo":"CVA7K2M9","organizacaoEmissoraId":1,"cartaoAnteriorId":null,"dataEmissao":"2026-10-28T12:01:18","status":"ATIVO","saldoCreditos":30},"..."]` |
| GET | `/api/v1/cartoes/{{cartaoId}}` | Buscar cartao | - | `200 {"id":6,"codigo":"WK6N6M6L","organizacaoEmissoraId":1,"cartaoAnteriorId":null,"dataEmissao":"2026-11-17T12:05:18","status":"ATIVO","saldoCreditos":0}` |
| GET | `/api/v1/cartoes/codigo/{{codigoCartao}}` | Consultar saldo pelo codigo do cartao | - | `200 {"id":6,"codigo":"WK6N6M6L","organizacaoEmissoraId":1,"cartaoAnteriorId":null,"dataEmissao":"2026-11-17T12:05:18","status":"ATIVO","saldoCreditos":0}` |
| PUT | `/api/v1/cartoes/{{cartaoId}}` | Atualizar cartao | `{"organizacaoEmissoraId":1,"status":"ATIVO"}` | `200 {"id":6,"codigo":"WK6N6M6L","organizacaoEmissoraId":1,"cartaoAnteriorId":null,"dataEmissao":"2026-11-17T12:05:18","status":"ATIVO","saldoCreditos":0}` |
| POST | `/api/v1/entregas` | Registrar entrega de 2 kg de lata (gera 20 creditos) | `{"codigoCartao":"{{codigoCartao}}","postoId":"{{postoId}}","materialId":1,"pesoKg":2}` | `201 {"id":7,"cartaoId":6,"postoId":3,"materialId":1,"pesoKg":2.0,"creditosGerados":20,"dataEntrega":"2026-11-17T12:05:18"}` |
| GET | `/api/v1/entregas?cartaoId={{cartaoId}}` | Listar entregas do cartao | - | `200 [{"id":7,"cartaoId":6,"postoId":3,"materialId":1,"pesoKg":2.0,"creditosGerados":20,"dataEntrega":"2026-11-17T12:05:18"}]` |
| GET | `/api/v1/entregas/{{entregaId}}` | Buscar entrega | - | `200 {"id":7,"cartaoId":6,"postoId":3,"materialId":1,"pesoKg":2.0,"creditosGerados":20,"dataEntrega":"2026-11-17T12:05:18"}` |
| PUT | `/api/v1/entregas/{{entregaId}}` | Corrigir peso da entrega (recalcula os creditos) | `{"codigoCartao":"{{codigoCartao}}","postoId":"{{postoId}}","materialId":1,"pesoKg":2.5}` | `200 {"id":7,"cartaoId":6,"postoId":3,"materialId":1,"pesoKg":2.5,"creditosGerados":25,"dataEntrega":"2026-11-17T12:05:18"}` |
| GET | `/api/v1/recompensas?ativo=true` | Listar recompensas ativas | - | `200 [{"id":1,"organizacaoParceiraId":5,"nome":"Kit de higiene (simulado)","custoCreditos":20,"ativo":true},"..."]` |
| POST | `/api/v1/recompensas` | Cadastrar recompensa | `{"organizacaoParceiraId":5,"nome":"Kit Postman (simulado)","custoCreditos":15}` | `201 {"id":5,"organizacaoParceiraId":5,"nome":"Kit Postman (simulado)","custoCreditos":15,"ativo":true}` |
| GET | `/api/v1/recompensas/{{recompensaId}}` | Buscar recompensa | - | `200 {"id":5,"organizacaoParceiraId":5,"nome":"Kit Postman (simulado)","custoCreditos":15,"ativo":true}` |
| PUT | `/api/v1/recompensas/{{recompensaId}}` | Atualizar recompensa | `{"organizacaoParceiraId":5,"nome":"Kit Postman (simulado)","custoCreditos":20,"ativo":true}` | `200 {"id":5,"organizacaoParceiraId":5,"nome":"Kit Postman (simulado)","custoCreditos":20,"ativo":true}` |
| POST | `/api/v1/trocas` | Registrar troca (debita o custo do saldo - RN11) | `{"codigoCartao":"{{codigoCartao}}","recompensaId":"{{recompensaId}}","usuarioId":1}` | `201 {"id":3,"cartaoId":6,"recompensaId":5,"usuarioId":1,"creditosUsados":20,"dataTroca":"2026-11-17T12:05:18"}` |
| GET | `/api/v1/trocas?cartaoId={{cartaoId}}` | Listar trocas do cartao | - | `200 [{"id":3,"cartaoId":6,"recompensaId":5,"usuarioId":1,"creditosUsados":20,"dataTroca":"2026-11-17T12:05:18"}]` |
| GET | `/api/v1/trocas/{{trocaId}}` | Buscar troca | - | `200 {"id":3,"cartaoId":6,"recompensaId":5,"usuarioId":1,"creditosUsados":20,"dataTroca":"2026-11-17T12:05:18"}` |
| PUT | `/api/v1/trocas/{{trocaId}}` | Atualizar troca (registrada pelo gestor) | `{"codigoCartao":"{{codigoCartao}}","recompensaId":"{{recompensaId}}","usuarioId":3}` | `200 {"id":3,"cartaoId":6,"recompensaId":5,"usuarioId":3,"creditosUsados":20,"dataTroca":"2026-11-17T12:05:18"}` |
| POST | `/api/v1/cartoes` | Emitir cartao que sera perdido | `{"organizacaoEmissoraId":1}` | `201 {"id":7,"codigo":"53AWHHHG","organizacaoEmissoraId":1,"cartaoAnteriorId":null,"dataEmissao":"2026-11-17T12:05:19","status":"ATIVO","saldoCreditos":0}` |
| POST | `/api/v1/cartoes` | Substituir cartao perdido (RN12: bloqueia e transfere o saldo) | `{"organizacaoEmissoraId":1,"cartaoAnteriorId":"{{cartaoPerdidoId}}"}` | `201 {"id":8,"codigo":"E7NQ289N","organizacaoEmissoraId":1,"cartaoAnteriorId":7,"dataEmissao":"2026-11-17T12:05:19","status":"ATIVO","saldoCreditos":0}` |
| POST | `/api/v1/coletas` | Registrar coleta (sem peso: usa o material acumulado) | `{"postoId":"{{postoId}}","cooperativaId":4}` | `201 {"id":3,"postoId":3,"cooperativaId":4,"pesoTotalKg":2.5,"dataColeta":"2026-11-17T12:05:19"}` |
| GET | `/api/v1/coletas?postoId={{postoId}}` | Listar coletas do posto | - | `200 [{"id":3,"postoId":3,"cooperativaId":4,"pesoTotalKg":2.5,"dataColeta":"2026-11-17T12:05:19"}]` |
| GET | `/api/v1/coletas/{{coletaId}}` | Buscar coleta | - | `200 {"id":3,"postoId":3,"cooperativaId":4,"pesoTotalKg":2.5,"dataColeta":"2026-11-17T12:05:19"}` |
| PUT | `/api/v1/coletas/{{coletaId}}` | Corrigir peso da coleta | `{"postoId":"{{postoId}}","cooperativaId":4,"pesoTotalKg":2.6}` | `200 {"id":3,"postoId":3,"cooperativaId":4,"pesoTotalKg":2.6,"dataColeta":"2026-11-17T12:05:19"}` |
| DELETE | `/api/v1/trocas/{{trocaId}}` | Excluir troca (devolve os creditos) | - | `204 (sem corpo)` |
| DELETE | `/api/v1/entregas/{{entregaId}}` | Excluir entrega (estorna os creditos) | - | `204 (sem corpo)` |
| DELETE | `/api/v1/coletas/{{coletaId}}` | Excluir coleta | - | `204 (sem corpo)` |
| DELETE | `/api/v1/postos-reciclagem/{{postoId}}` | Excluir posto | - | `204 (sem corpo)` |
| DELETE | `/api/v1/cartoes/{{cartaoNovoId}}` | Excluir cartao substituto | - | `204 (sem corpo)` |
| DELETE | `/api/v1/cartoes/{{cartaoPerdidoId}}` | Excluir cartao perdido | - | `204 (sem corpo)` |
| DELETE | `/api/v1/cartoes/{{cartaoId}}` | Excluir cartao | - | `204 (sem corpo)` |
| DELETE | `/api/v1/recompensas/{{recompensaId}}` | Excluir recompensa | - | `204 (sem corpo)` |
| DELETE | `/api/v1/materiais/{{materialId}}` | Excluir material | - | `204 (sem corpo)` |
| DELETE | `/api/v1/consultas/{{consultaId}}` | Excluir consulta | - | `204 (sem corpo)` |
| DELETE | `/api/v1/totens/{{totemId}}` | Excluir totem | - | `204 (sem corpo)` |
| DELETE | `/api/v1/servicos/5/horarios/{{horarioId}}` | Excluir horario | - | `204 (sem corpo)` |
| DELETE | `/api/v1/atualizacoes/{{atualizacaoId}}` | Excluir atualizacao | - | `204 (sem corpo)` |
| DELETE | `/api/v1/servicos/{{servicoId}}` | Excluir servico (remove horarios e atualizacoes) | - | `204 (sem corpo)` |
| DELETE | `/api/v1/usuarios/{{usuarioId}}` | Excluir usuario | - | `204 (sem corpo)` |
| DELETE | `/api/v1/organizacoes/{{organizacaoId}}` | Excluir organizacao | - | `204 (sem corpo)` |
| DELETE | `/api/v1/perfis/{{perfilId}}` | Excluir perfil | - | `204 (sem corpo)` |
| DELETE | `/api/v1/tipos-servico/{{tipoId}}` | Excluir tipo de servico | - | `204 (sem corpo)` |

## Tratamento de erros (`@RestControllerAdvice` - GlobalExceptionHandler)

| Status | Quando | Exemplo de requisicao | Exemplo de resposta |
|---|---|---|---|
| 400 | campos invalidos (Bean Validation) | `POST /api/v1/servicos {"nome":"","latitude":200}` | `{"detail":"Um ou mais campos nao passaram na validacao.","instance":"/api/v1/servicos","status":400,"title":"Dados de entrada invalidos","timestamp":"2026-11-17T12:05:19.265622-03:00","erros":[{"campo":"latitude","mensagem":"A latitude deve estar entre -90 e 90."},{"campo":"organizacaoId","mensagem":"O organizacaoId e obrigatorio."},{"campo":"bairro","mensagem":"O bairro e obrigatorio."},{"campo":"logradouro","mensagem":"O logradouro e obrigatorio."},{"campo":"tipoServicoId","mensagem":"O tipoSe ...` |
| 400 | valor de enum invalido | `GET /api/v1/servicos?statusAprovacao=TALVEZ` | `{"detail":"Valor 'TALVEZ' invalido para statusAprovacao. Valores aceitos: PENDENTE, APROVADO, REPROVADO.","instance":"/api/v1/servicos","status":400,"title":"Dados de entrada invalidos","timestamp":"2026-11-17T12:05:19.282355-03:00"}` |
| 400 | JSON mal formado | `POST /api/v1/totens { isto nao e json` | `{"detail":"O JSON enviado esta mal formatado ou contem valores incompativeis (ex.: texto em campo numerico, data invalida).","instance":"/api/v1/totens","status":400,"title":"Corpo da requisicao invalido","timestamp":"2026-11-17T12:05:19.286504-03:00"}` |
| 404 | recurso inexistente | `GET /api/v1/servicos/999999` | `{"detail":"Servico 999999 nao encontrado.","instance":"/api/v1/servicos/999999","status":404,"title":"Recurso nao encontrado","timestamp":"2026-11-17T12:05:19.303939-03:00"}` |
| 409 | codigo de totem duplicado | `POST /api/v1/totens {"codigo":"TOT-SE-01","logradouro":"Duplicado","bairro":"Se","latitude":-23.55,"longitude":-46.63}` | `{"detail":"Ja existe um totem com o codigo TOT-SE-01.","instance":"/api/v1/totens","status":409,"title":"Conflito com o estado atual do recurso","timestamp":"2026-11-17T12:05:19.322094-03:00"}` |
| 409 | excluir totem com consultas | `DELETE /api/v1/totens/1` | `{"detail":"O totem 1 possui consultas registradas. Use o status MANUTENCAO em vez de excluir.","instance":"/api/v1/totens/1","status":409,"title":"Conflito com o estado atual do recurso","timestamp":"2026-11-17T12:05:19.345663-03:00"}` |
| 422 | vagas informadas para refeicao | `POST /api/v1/atualizacoes {"servicoId":5,"usuarioId":2,"vagas":10}` | `{"detail":"Vagas so podem ser informadas para abrigos. Para Refeicao, informe a situacao (ABERTO, FILA_GRANDE, LOTADO ou FECHADO).","instance":"/api/v1/atualizacoes","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-17T12:05:19.367245-03:00"}` |
| 422 | equipe atualizando servico de outra organizacao | `POST /api/v1/atualizacoes {
  "servicoId": 5,
  "usuarioId": {{usuarioId}},
  "situacao": "ABERTO"
}` | `{"detail":"Usuarios de EQUIPE so atualizam servicos da propria organizacao.","instance":"/api/v1/atualizacoes","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-17T12:05:19.392117-03:00"}` |
| 422 | totem em manutencao | `GET /api/v1/totens/3/servicos` | `{"detail":"O totem TOT-PDP-01 esta em manutencao e nao pode realizar consultas.","instance":"/api/v1/totens/3/servicos","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-17T12:05:19.406935-03:00"}` |
| 422 | RN09 vidro nao e aceito no posto | `POST /api/v1/entregas {"codigoCartao":"{{codigoCartao}}","postoId":"{{postoId}}","materialId":3,"pesoKg":1}` | `{"detail":"O material Vidro nao e aceito no posto (RN09: somente latas de aluminio e garrafas PET).","instance":"/api/v1/entregas","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-17T12:05:19.471793-03:00"}` |
| 422 | RN08 limite diario do cartao | `POST /api/v1/entregas {"codigoCartao":"{{codigoCartao}}","postoId":"{{postoId}}","materialId":1,"pesoKg":1}` | `{"detail":"Limite diario de 3.00 kg por cartao atingido (RN08): o cartao WK6N6M6L ja entregou 2.50 kg hoje e pode entregar mais 0.50 kg.","instance":"/api/v1/entregas","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-17T12:05:19.519574-03:00"}` |
| 422 | RN11 saldo insuficiente para a troca | `POST /api/v1/trocas {"codigoCartao":"{{codigoCartao}}","recompensaId":"{{recompensaId}}","usuarioId":1}` | `{"detail":"Saldo insuficiente: o cartao WK6N6M6L tem 5 creditos e a operacao exige 20.","instance":"/api/v1/trocas","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-17T12:05:19.565302-03:00"}` |
| 422 | RN07 cartao emitido fora do Centro POP | `POST /api/v1/cartoes {"organizacaoEmissoraId":2}` | `{"detail":"Cartoes sao emitidos e substituidos somente presencialmente no Centro POP (RN07 e RN12).","instance":"/api/v1/cartoes","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-17T12:05:19.581928-03:00"}` |
| 409 | cartao ja substituido | `POST /api/v1/cartoes {"organizacaoEmissoraId":1,"cartaoAnteriorId":"{{cartaoPerdidoId}}"}` | `{"detail":"O cartao 53AWHHHG ja foi substituido.","instance":"/api/v1/cartoes","status":409,"title":"Conflito com o estado atual do recurso","timestamp":"2026-11-17T12:05:19.613227-03:00"}` |
| 404 | codigo de cartao inexistente | `GET /api/v1/cartoes/codigo/ZZZZZZZZ` | `{"detail":"Cartao ZZZZZZZZ nao encontrado.","instance":"/api/v1/cartoes/codigo/ZZZZZZZZ","status":404,"title":"Recurso nao encontrado","timestamp":"2026-11-17T12:05:19.630050-03:00"}` |

## Exemplo completo: mapa de servicos do totem

`GET /api/v1/totens/1/servicos?tipo=Abrigo&perfil=Mulher`

```json
{
  "totemId": 1,
  "codigoTotem": "TOT-SE-01",
  "tipoServicoFiltrado": "Abrigo",
  "perfilFiltrado": "Mulher",
  "consultadoEm": "2026-11-17T12:05:17",
  "servicos": [
    {
      "servicoId": 13,
      "nome": "Abrigo Postman (simulado)",
      "tipoServico": "Abrigo",
      "endereco": "Rua Teste, 100 - Se",
      "latitude": -23.5512,
      "longitude": -46.634,
      "distanciaMetros": 103,
      "minutosCaminhada": 2,
      "status": "TEM_VAGA",
      "mensagem": "Tem vaga",
      "origemInformacao": "ATUALIZACAO_RECENTE",
      "ultimaAtualizacao": "2026-11-17T12:05:17",
      "minutosDesdeAtualizacao": 0
    },
    {
      "servicoId": 2,
      "nome": "Casa de Acolhida Mulheres Se (simulado)",
      "tipoServico": "Abrigo",
      "endereco": "Rua Tabatinguera, 150 - Se",
      "latitude": -23.55191,
      "longitude": -46.63095,
      "distanciaMetros": 286,
      "minutosCaminhada": 4,
      "status": "SEM_ATUALIZACAO_RECENTE",
      "mensagem": "Sem atualizacao recente",
      "origemInformacao": "SEM_ATUALIZACAO_RECENTE",
      "ultimaAtualizacao": null,
      "minutosDesdeAtualizacao": null
    },
    {
      "servicoId": 3,
      "nome": "Abrigo Gloria com Pets (simulado)",
      "tipoServico": "Abrigo",
      "endereco": "Rua da Gloria, 410 - Liberdade",
      "latitude": -23.5574,
      "longitude": -46.6341,
      "distanciaMetros": 769,
      "minutosCaminhada": 10,
      "status": "SEM_ATUALIZACAO_RECENTE",
      "mensagem": "Sem atualizacao recente",
      "origemInformacao": "SEM_ATUALIZACAO_RECENTE",
      "ultimaAtualizacao": null,
      "minutosDesdeAtualizacao": null
    }
  ],
  "orientacao": "A entrada no abrigo e feita por encaminhamento: procure o Centro POP mais proximo ou ligue 156."
}
```

- `status` sai sempre em faixa (TEM_VAGA, POUCAS_VAGAS, LOTADO) - o numero de vagas nunca vai para o totem (RN03).
- `origemInformacao`: ATUALIZACAO_RECENTE (ate 2h), HORARIO_CADASTRADO (RN04) ou SEM_ATUALIZACAO_RECENTE (RN05, vai para o fim).
- Somente servicos APROVADOS aparecem (RN06); o filtro de perfil vale apenas para abrigos.
