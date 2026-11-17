# CIVIA - API do mapa de servicos

## Ficha da CitizenTech

| | |
|---|---|
| **Nome** | CIVIA |
| **Cidade** | Sao Paulo - regiao da Se |
| **Cidadao atendido** | Pessoas em situacao de rua |
| **ODS** | 2 (Fome zero), 3 (Saude e bem-estar), 10 (Reducao das desigualdades) e 11 (Cidades e comunidades sustentaveis) |

## Equipe

| Nome | RM |
|---|---|
| Laura Dantas | 564064 |
| Raphael Aaron | 564067 |
| Felipe Catto | 562106 |
| Kimberly Kristina | 564080 |
| Gabriel Viana de Souza | 564382 |

FIAP - Engenharia de Software - Global Solution 2026 (2o semestre) - Domain Driven Design (Java).

## Links

- **Deploy (Render):** https://civia-api-ibn6.onrender.com
  ([health](https://civia-api-ibn6.onrender.com/actuator/health) |
  [exemplo de consulta do totem](https://civia-api-ibn6.onrender.com/api/v1/totens/1/servicos?tipo=Abrigo&perfil=Mulher))
- **Repositorio:** https://github.com/Gabrielvianaana/civia-api

## O que e

A CIVIA e uma rede de totens instalados em pontos de grande circulacao da regiao central de Sao Paulo. O totem
mostra, para quem esta na rua, os servicos mais proximos (abrigo, refeicao, banho e atendimento) com a
disponibilidade do momento e o tempo de caminhada - sem exigir celular, documento ou cadastro.

Esta API RESTful implementa o nucleo do MVP: o cadastro da rede de servicos e o mapa de disponibilidade
consultado pelos totens, alem do modulo de reciclagem (postos nos totens, cartao anonimo de creditos, entregas,
catalogo de recompensas, trocas e coletas das cooperativas), persistidos no Oracle da FIAP segundo o modelo de
dados da equipe (17 tabelas).

## Regras de negocio implementadas (camada BO)

| Regra | Implementacao |
|---|---|
| **RN03** - vagas de abrigo so em faixas | `DisponibilidadeBO.faixaDeVagas`: TEM_VAGA (4+), POUCAS_VAGAS (1 a 3), LOTADO (0). O totem nunca recebe o numero exato. |
| **RN04** - horario cadastrado sem atualizacao manual recente | `DisponibilidadeBO`: refeicao, banho e atendimento seguem `T_HORARIO` quando nao ha atualizacao das ultimas 2h. |
| **RN05** - mais de 2h sem atualizacao | Status `SEM_ATUALIZACAO_RECENTE`, enviado para o fim da lista. |
| **RN06** - somente servicos aprovados | `MapaServicosBO` consulta apenas `st_aprovacao = 'APROVADO'`. |
| Filtro de perfil (US04) | Aplicado somente a abrigos. |
| Ate 3 servicos mais proximos (RF02) | Distancia por Haversine; disponiveis primeiro, depois lotados/fechados e por ultimo sem atualizacao. |
| **RN07** - cartao anonimo | `Cartao`/`CartaoBO`: so um codigo aleatorio de 8 caracteres, sem nome, CPF ou foto; emitido pelo Centro POP. |
| **RN08** - limite diario por cartao | `EntregaBO`: o peso entregue pelo cartao no dia nao passa do limite do posto (padrao 10 kg). |
| **RN09** - so lata de aluminio e PET | `Material.garantirAceito`: vidro e demais materiais sao recusados com mensagem clara. |
| **RN11** - creditos so viram itens do catalogo | `Cartao.debitar`/`TrocaBO`: troca debita o custo da recompensa; saldo nunca fica negativo. |
| **RN12** - substituicao presencial do cartao | `Cartao.substituir`: o cartao perdido e bloqueado e o saldo passa para o novo (`id_cartao_anterior`). |

Outras regras derivadas do modelo da equipe: atualizacao pelo painel exige usuario e a da Central de Vagas nao
tem usuario (e so vale para abrigo); vagas nao podem exceder a capacidade; usuario de EQUIPE so atualiza servicos
da propria organizacao; faixas de horario nao se sobrepoem; exclusoes que apagariam historico retornam 409
indicando a alternativa (reprovar o servico, desativar o usuario, colocar o totem em manutencao). Na reciclagem:
posto atendido por COOPERATIVA (um por totem), posto que atinge a capacidade fica CHEIO ate a coleta, coleta feita
pela cooperativa do posto, troca registrada por equipe ou gestor (nunca pela cooperativa) e exclusao de entrega
ou troca estorna os creditos.

## Arquitetura

Maven multi-modulo em camadas (padrao DDD visto em aula):

| Modulo | Conteudo |
|---|---|
| `civia-domain` | **Modelo** (`Organizacao`, `TipoServico`, `PerfilAtendido`, `Servico`, `Horario`, `UsuarioPainel`, `AtualizacaoDisponibilidade`, `Totem`, `ConsultaTotem`, `PostoReciclagem`, `Material`, `Cartao`, `Entrega`, `Recompensa`, `Troca`, `Coleta`), value objects, excecoes e **interfaces DAO** |
| `civia-infrastructure` | **Implementacoes DAO** (`*DAOImpl`) com Spring Data JPA, entidades JPA mapeadas nas tabelas Oracle |
| `civia-application` | **BO** (`*BO`) com as regras de negocio, DTOs (records) e excecoes de aplicacao |
| `civia-presentation` | **Controllers** REST (GET, POST, PUT, DELETE), `GlobalExceptionHandler` (`@RestControllerAdvice`) e configuracao |

Erros retornam JSON padronizado (Problem Details - RFC 9457): 400 (dados invalidos), 404 (nao encontrado),
409 (conflito/duplicidade), 422 (regra de negocio) e 500 (inesperado).

## Documentacao

| Arquivo | Conteudo |
|---|---|
| [docs/como-executar.md](docs/como-executar.md) | Ferramentas, versoes, passo a passo local e deploy |
| [docs/endpoints.md](docs/endpoints.md) | Tabela de endpoints com exemplos reais de requisicao e resposta |
| [docs/diagrama-classes.md](docs/diagrama-classes.md) | Diagrama de classes gerado a partir do codigo (+ `.puml`) |
| [docs/der.md](docs/der.md) | DER das 17 tabelas Oracle (+ `der.puml`) |
| [db/](db) | DDL da equipe, sequencias e dados de exemplo |
| [postman/](postman) | Colecao Postman e arquivo `.http` cobrindo todos os endpoints |

Todos os dados do projeto sao simulados (a Global Solution proibe o uso de dados pessoais reais).
