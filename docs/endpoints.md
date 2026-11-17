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
| GET | `/actuator/health` | Health (conexao com o Oracle) | - | `200 {"components":{"db":{"details":{"database":"Oracle","validationQuery":"isValid()"},"status":"UP"},"diskSpace":{"details":{"total":499963174912,"free":249042022400,"threshold":10485760,"path":"/Users/macbookpro/Downloads/fiap-ddd-checkpoint2/civia-api/.","exists":true},"status":"UP"},"livenessState":{"status":"UP"},"ping":{"status":"UP"},"readinessState":{"status":"UP"},"ssl":{"details":{"expiringChains":[],"invalidCh ...` |
| GET | `/api/v1/tipos-servico` | Listar tipos | - | `200 [{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"..."]` |
| POST | `/api/v1/tipos-servico` | Cadastrar tipo | `{"nome":"Saude","icone":"cruz"}` | `201 {"id":6,"nome":"Saude","icone":"cruz","abrigo":false}` |
| GET | `/api/v1/tipos-servico/{{tipoId}}` | Buscar tipo | - | `200 {"id":6,"nome":"Saude","icone":"cruz","abrigo":false}` |
| PUT | `/api/v1/tipos-servico/{{tipoId}}` | Atualizar tipo | `{"nome":"Saude e documentos","icone":"cruz"}` | `200 {"id":6,"nome":"Saude e documentos","icone":"cruz","abrigo":false}` |
| GET | `/api/v1/perfis` | Listar perfis | - | `200 [{"id":1,"descricao":"Homem"},"..."]` |
| POST | `/api/v1/perfis` | Cadastrar perfil | `{"descricao":"Pessoa migrante"}` | `201 {"id":8,"descricao":"Pessoa migrante"}` |
| GET | `/api/v1/perfis/{{perfilId}}` | Buscar perfil | - | `200 {"id":8,"descricao":"Pessoa migrante"}` |
| PUT | `/api/v1/perfis/{{perfilId}}` | Atualizar perfil | `{"descricao":"Pessoa migrante ou refugiada"}` | `200 {"id":8,"descricao":"Pessoa migrante ou refugiada"}` |
| GET | `/api/v1/organizacoes?tipo=ONG` | Listar organizacoes (filtro opcional por tipo) | - | `200 [{"id":2,"nome":"ONG Mesa Solidaria (simulada)","tipo":"ONG","telefone":"11900000002","email":"mesa@civia.exemplo.org"}]` |
| POST | `/api/v1/organizacoes` | Cadastrar organizacao | `{"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000099","email":"postman@civia.exemplo.org"}` | `201 {"id":7,"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000099","email":"postman@civia.exemplo.org"}` |
| GET | `/api/v1/organizacoes/{{organizacaoId}}` | Buscar organizacao | - | `200 {"id":7,"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000099","email":"postman@civia.exemplo.org"}` |
| PUT | `/api/v1/organizacoes/{{organizacaoId}}` | Atualizar organizacao | `{"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000098","email":"postman@civia.exemplo.org"}` | `200 {"id":7,"nome":"ONG Teste Postman (simulada)","tipo":"ONG","telefone":"11900000098","email":"postman@civia.exemplo.org"}` |
| GET | `/api/v1/usuarios` | Listar usuarios (filtro opcional por organizacao) | - | `200 [{"id":1,"organizacaoId":1,"nome":"Ana Equipe (simulada)","email":"ana.equipe@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true},"..."]` |
| POST | `/api/v1/usuarios` | Cadastrar usuario | `{
  "organizacaoId": {{organizacaoId}},
  "nome": "Equipe Postman (simulada)",
  "email": "equipe.postman@civia.exemplo.org",
  "perfilAcesso": "EQUIPE"
}` | `201 {"id":6,"organizacaoId":7,"nome":"Equipe Postman (simulada)","email":"equipe.postman@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true}` |
| GET | `/api/v1/usuarios/{{usuarioId}}` | Buscar usuario | - | `200 {"id":6,"organizacaoId":7,"nome":"Equipe Postman (simulada)","email":"equipe.postman@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true}` |
| PUT | `/api/v1/usuarios/{{usuarioId}}` | Atualizar usuario | `{
  "organizacaoId": {{organizacaoId}},
  "nome": "Equipe Postman (simulada)",
  "email": "equipe.postman@civia.exemplo.org",
  "perfilAcesso": "EQUIPE",
  "ativo": true
}` | `200 {"id":6,"organizacaoId":7,"nome":"Equipe Postman (simulada)","email":"equipe.postman@civia.exemplo.org","perfilAcesso":"EQUIPE","ativo":true}` |
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
}` | `201 {"id":12,"organizacaoId":7,"tipoServico":{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"nome":"Abrigo Postman (simulado)","logradouro":"Rua Teste","numero":"100","bairro":"Se","latitude":-23.5512,"longitude":-46.634,"capacidade":20,"funciona24h":false,"statusAprovacao":"APROVADO","dataCadastro":"2026-11-16T23:32:06","perfis":[{"id":2,"descricao":"Mulher"},{"id":8,"descricao":"Pessoa migrante ou refugiada"}]}` |
| GET | `/api/v1/servicos/{{servicoId}}` | Buscar servico | - | `200 {"id":12,"organizacaoId":7,"tipoServico":{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"nome":"Abrigo Postman (simulado)","logradouro":"Rua Teste","numero":"100","bairro":"Se","latitude":-23.5512,"longitude":-46.634,"capacidade":20,"funciona24h":false,"statusAprovacao":"APROVADO","dataCadastro":"2026-11-16T23:32:06","perfis":[{"id":2,"descricao":"Mulher"},{"id":8,"descricao":"Pessoa migrante ou refugiada"}]}` |
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
}` | `200 {"id":12,"organizacaoId":7,"tipoServico":{"id":1,"nome":"Abrigo","icone":"cama","abrigo":true},"nome":"Abrigo Postman (simulado)","logradouro":"Rua Teste","numero":"100","bairro":"Se","latitude":-23.5512,"longitude":-46.634,"capacidade":25,"funciona24h":true,"statusAprovacao":"APROVADO","dataCadastro":"2026-11-16T23:32:06","perfis":[{"id":2,"descricao":"Mulher"},{"id":3,"descricao":"Familia com crianca"},{"id":8,"des ...` |
| GET | `/api/v1/servicos/1/horarios` | Listar horarios de um servico | - | `200 [{"id":22,"servicoId":1,"diaSemana":1,"nomeDia":"domingo","horaAbertura":"00:00","horaFechamento":"08:00"},"..."]` |
| POST | `/api/v1/servicos/5/horarios` | Cadastrar horario (quarta, 18:00-23:59) | `{"diaSemana":4,"horaAbertura":"18:00","horaFechamento":"23:59"}` | `201 {"id":79,"servicoId":5,"diaSemana":4,"nomeDia":"quarta","horaAbertura":"18:00","horaFechamento":"23:59"}` |
| GET | `/api/v1/servicos/5/horarios/{{horarioId}}` | Buscar horario | - | `200 {"id":79,"servicoId":5,"diaSemana":4,"nomeDia":"quarta","horaAbertura":"18:00","horaFechamento":"23:59"}` |
| PUT | `/api/v1/servicos/5/horarios/{{horarioId}}` | Atualizar horario | `{"diaSemana":4,"horaAbertura":"19:00","horaFechamento":"22:00"}` | `200 {"id":79,"servicoId":5,"diaSemana":4,"nomeDia":"quarta","horaAbertura":"19:00","horaFechamento":"22:00"}` |
| POST | `/api/v1/atualizacoes` | Registrar vagas de abrigo pelo painel (RN03: grava a faixa) | `{
  "servicoId": {{servicoId}},
  "origem": "PAINEL",
  "usuarioId": {{usuarioId}},
  "vagas": 3
}` | `201 {"id":9,"servicoId":12,"usuarioId":6,"status":"POUCAS_VAGAS","vagas":3,"origem":"PAINEL","dataHora":"2026-11-16T23:32:07"}` |
| POST | `/api/v1/atualizacoes` | Registrar vagas pela Central de Vagas (sem usuario) | `{
  "servicoId": {{servicoId}},
  "origem": "CENTRAL_VAGAS",
  "vagas": 12
}` | `201 {"id":10,"servicoId":12,"usuarioId":null,"status":"TEM_VAGA","vagas":12,"origem":"CENTRAL_VAGAS","dataHora":"2026-11-16T23:32:07"}` |
| GET | `/api/v1/atualizacoes?servicoId={{servicoId}}` | Listar atualizacoes de um servico | - | `200 [{"id":10,"servicoId":12,"usuarioId":null,"status":"TEM_VAGA","vagas":12,"origem":"CENTRAL_VAGAS","dataHora":"2026-11-16T23:32:07"},"..."]` |
| GET | `/api/v1/atualizacoes/{{atualizacaoId}}` | Buscar atualizacao | - | `200 {"id":9,"servicoId":12,"usuarioId":6,"status":"POUCAS_VAGAS","vagas":3,"origem":"PAINEL","dataHora":"2026-11-16T23:32:07"}` |
| PUT | `/api/v1/atualizacoes/{{atualizacaoId}}` | Corrigir atualizacao | `{
  "servicoId": {{servicoId}},
  "origem": "PAINEL",
  "usuarioId": {{usuarioId}},
  "vagas": 0
}` | `200 {"id":9,"servicoId":12,"usuarioId":6,"status":"LOTADO","vagas":0,"origem":"PAINEL","dataHora":"2026-11-16T23:32:07"}` |
| GET | `/api/v1/totens` | Listar totens | - | `200 [{"id":1,"codigo":"TOT-SE-01","logradouro":"Praca da Se, em frente a Catedral","bairro":"Se","latitude":-23.55052,"longitude":-46.633308,"status":"ONLINE","papelImpressora":true,"dataInstalacao":"2026-09-01","ultimaSincronizacao":"2026-11-16T22:27:19"},"..."]` |
| POST | `/api/v1/totens` | Cadastrar totem | `{"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"papelImpressora":true}` | `201 {"id":5,"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":true,"dataInstalacao":"2026-11-16","ultimaSincronizacao":null}` |
| GET | `/api/v1/totens/{{totemId}}` | Buscar totem | - | `200 {"id":5,"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":true,"dataInstalacao":"2026-11-16","ultimaSincronizacao":null}` |
| PUT | `/api/v1/totens/{{totemId}}` | Atualizar totem | `{"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":false}` | `200 {"id":5,"codigo":"TOT-PM-01","logradouro":"Praca Teste Postman","bairro":"Se","latitude":-23.5508,"longitude":-46.6335,"status":"ONLINE","papelImpressora":false,"dataInstalacao":"2026-11-16","ultimaSincronizacao":null}` |
| GET | `/api/v1/totens/1/servicos?tipo=Abrigo&perfil=Mulher` | Mapa: 3 abrigos mais proximos para mulher | - | `200 {"totemId":1,"codigoTotem":"TOT-SE-01","tipoServicoFiltrado":"Abrigo","perfilFiltrado":"Mulher","consultadoEm":"2026-11-16T23:32:07","servicos":[{"servicoId":12,"nome":"Abrigo Postman (simulado)","tipoServico":"Abrigo","endereco":"Rua Teste, 100 - Se","latitude":-23.5512,"longitude":-46.634,"distanciaMetros":103,"minutosCaminhada":2,"status":"TEM_VAGA","mensagem":"Tem vaga","origemInformacao":"ATUALIZACAO_RECENTE","u ...` |
| GET | `/api/v1/totens/1/servicos?tipo=2` | Mapa: refeicoes (tipo pelo id) | - | `200 {"totemId":1,"codigoTotem":"TOT-SE-01","tipoServicoFiltrado":"Refeicao","perfilFiltrado":null,"consultadoEm":"2026-11-16T23:32:07","servicos":[{"servicoId":5,"nome":"Restaurante Comunitario Se (simulado)","tipoServico":"Refeicao","endereco":"Rua Roberto Simonsen, 80 - Se","latitude":-23.5496,"longitude":-46.6312,"distanciaMetros":238,"minutosCaminhada":3,"status":"FILA_GRANDE","mensagem":"Fila grande","origemInformac ...` |
| GET | `/api/v1/totens/{{totemId}}/servicos` | Mapa: todos os tipos | - | `200 {"totemId":5,"codigoTotem":"TOT-PM-01","tipoServicoFiltrado":null,"perfilFiltrado":null,"consultadoEm":"2026-11-16T23:32:07","servicos":[{"servicoId":12,"nome":"Abrigo Postman (simulado)","tipoServico":"Abrigo","endereco":"Rua Teste, 100 - Se","latitude":-23.5512,"longitude":-46.634,"distanciaMetros":68,"minutosCaminhada":1,"status":"TEM_VAGA","mensagem":"Tem vaga","origemInformacao":"ATUALIZACAO_RECENTE","ultimaAtua ...` |
| POST | `/api/v1/consultas` | Registrar consulta com rota impressa (gera codigo) | `{
  "totemId": {{totemId}},
  "tipoServicoId": 1,
  "perfilId": 2,
  "servicoEscolhidoId": {{servicoId}},
  "imprimiuRota": true,
  "pediuAbordagem": false
}` | `201 {"id":5,"totemId":5,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":12,"dataConsulta":"2026-11-16T23:32:08","imprimiuRota":true,"pediuAbordagem":false,"codigoChegada":"FDWSZTBV","dataChegada":null}` |
| GET | `/api/v1/consultas?totemId={{totemId}}` | Listar consultas de um totem | - | `200 [{"id":5,"totemId":5,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":12,"dataConsulta":"2026-11-16T23:32:08","imprimiuRota":true,"pediuAbordagem":false,"codigoChegada":"FDWSZTBV","dataChegada":null}]` |
| GET | `/api/v1/consultas/{{consultaId}}` | Buscar consulta | - | `200 {"id":5,"totemId":5,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":12,"dataConsulta":"2026-11-16T23:32:08","imprimiuRota":true,"pediuAbordagem":false,"codigoChegada":"FDWSZTBV","dataChegada":null}` |
| PUT | `/api/v1/consultas/{{consultaId}}` | Atualizar consulta (pediu abordagem social) | `{
  "totemId": {{totemId}},
  "tipoServicoId": 1,
  "perfilId": 2,
  "servicoEscolhidoId": {{servicoId}},
  "imprimiuRota": true,
  "pediuAbordagem": true
}` | `200 {"id":5,"totemId":5,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":12,"dataConsulta":"2026-11-16T23:32:08","imprimiuRota":true,"pediuAbordagem":true,"codigoChegada":"FDWSZTBV","dataChegada":null}` |
| PUT | `/api/v1/consultas/chegada/{{codigoChegada}}` | Registrar chegada pelo codigo impresso (US14) | - | `200 {"id":5,"totemId":5,"tipoServicoId":1,"perfilId":2,"servicoEscolhidoId":12,"dataConsulta":"2026-11-16T23:32:08","imprimiuRota":true,"pediuAbordagem":true,"codigoChegada":"FDWSZTBV","dataChegada":"2026-11-16T23:32:08"}` |
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
| 400 | campos invalidos (Bean Validation) | `POST /api/v1/servicos {"nome":"","latitude":200}` | `{"detail":"Um ou mais campos nao passaram na validacao.","instance":"/api/v1/servicos","status":400,"title":"Dados de entrada invalidos","timestamp":"2026-11-16T23:32:08.259785-03:00","erros":[{"campo":"bairro","mensagem":"O bairro e obrigatorio."},{"campo":"longitude","mensagem":"A longitude e obrigatoria."},{"campo":"tipoServicoId","mensagem":"O tipoServicoId e obrigatorio."},{"campo":"nome","mensagem":"O nome e obrigatorio."},{"campo":"organizacaoId","mensagem":"O organizacaoId e obrigatorio. ...` |
| 400 | valor de enum invalido | `GET /api/v1/servicos?statusAprovacao=TALVEZ` | `{"detail":"Valor 'TALVEZ' invalido para statusAprovacao. Valores aceitos: PENDENTE, APROVADO, REPROVADO.","instance":"/api/v1/servicos","status":400,"title":"Dados de entrada invalidos","timestamp":"2026-11-16T23:32:08.278048-03:00"}` |
| 400 | JSON mal formado | `POST /api/v1/totens { isto nao e json` | `{"detail":"O JSON enviado esta mal formatado ou contem valores incompativeis (ex.: texto em campo numerico, data invalida).","instance":"/api/v1/totens","status":400,"title":"Corpo da requisicao invalido","timestamp":"2026-11-16T23:32:08.282325-03:00"}` |
| 404 | recurso inexistente | `GET /api/v1/servicos/999999` | `{"detail":"Servico 999999 nao encontrado.","instance":"/api/v1/servicos/999999","status":404,"title":"Recurso nao encontrado","timestamp":"2026-11-16T23:32:08.303893-03:00"}` |
| 409 | codigo de totem duplicado | `POST /api/v1/totens {"codigo":"TOT-SE-01","logradouro":"Duplicado","bairro":"Se","latitude":-23.55,"longitude":-46.63}` | `{"detail":"Ja existe um totem com o codigo TOT-SE-01.","instance":"/api/v1/totens","status":409,"title":"Conflito com o estado atual do recurso","timestamp":"2026-11-16T23:32:08.324790-03:00"}` |
| 409 | excluir totem com consultas | `DELETE /api/v1/totens/1` | `{"detail":"O totem 1 possui consultas registradas. Use o status MANUTENCAO em vez de excluir.","instance":"/api/v1/totens/1","status":409,"title":"Conflito com o estado atual do recurso","timestamp":"2026-11-16T23:32:08.355083-03:00"}` |
| 422 | vagas informadas para refeicao | `POST /api/v1/atualizacoes {"servicoId":5,"usuarioId":2,"vagas":10}` | `{"detail":"Vagas so podem ser informadas para abrigos. Para Refeicao, informe a situacao (ABERTO, FILA_GRANDE, LOTADO ou FECHADO).","instance":"/api/v1/atualizacoes","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-16T23:32:08.379587-03:00"}` |
| 422 | equipe atualizando servico de outra organizacao | `POST /api/v1/atualizacoes {
  "servicoId": 5,
  "usuarioId": {{usuarioId}},
  "situacao": "ABERTO"
}` | `{"detail":"Usuarios de EQUIPE so atualizam servicos da propria organizacao.","instance":"/api/v1/atualizacoes","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-16T23:32:08.405480-03:00"}` |
| 422 | totem em manutencao | `GET /api/v1/totens/3/servicos` | `{"detail":"O totem TOT-PDP-01 esta em manutencao e nao pode realizar consultas.","instance":"/api/v1/totens/3/servicos","status":422,"title":"Regra de negocio violada","timestamp":"2026-11-16T23:32:08.420648-03:00"}` |

## Exemplo completo: mapa de servicos do totem

`GET /api/v1/totens/1/servicos?tipo=Abrigo&perfil=Mulher`

```json
{
  "totemId": 1,
  "codigoTotem": "TOT-SE-01",
  "tipoServicoFiltrado": "Abrigo",
  "perfilFiltrado": "Mulher",
  "consultadoEm": "2026-11-16T23:32:07",
  "servicos": [
    {
      "servicoId": 12,
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
      "ultimaAtualizacao": "2026-11-16T23:32:07",
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
      "status": "POUCAS_VAGAS",
      "mensagem": "Poucas vagas",
      "origemInformacao": "ATUALIZACAO_RECENTE",
      "ultimaAtualizacao": "2026-11-16T21:47:19",
      "minutosDesdeAtualizacao": 104
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
