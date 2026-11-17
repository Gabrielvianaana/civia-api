# Como executar a CIVIA API

## Links

| | |
|---|---|
| Repositorio GitHub | https://github.com/Gabrielvianaana/civia-api |
| Deploy (Render.com) | https://civia-api-ibn6.onrender.com |
| Health check (Oracle) | https://civia-api-ibn6.onrender.com/actuator/health |
| Exemplo de consulta do totem | https://civia-api-ibn6.onrender.com/api/v1/totens/1/servicos?tipo=Abrigo&perfil=Mulher |

> O plano gratuito do Render desliga a aplicacao apos 15 minutos sem uso. A primeira requisicao depois disso
> demora cerca de 1 minuto para responder (a aplicacao esta "acordando").

## Ferramentas e versoes

| Ferramenta | Versao |
|---|---|
| Java (JDK) | 21 ou superior (o projeto compila com `release 21`; o deploy usa Eclipse Temurin 21) |
| Spring Boot | 4.1.1 (Spring Web MVC, Spring Data JPA, Bean Validation, Actuator) |
| Hibernate ORM | 7.4 |
| Banco de dados | Oracle Database 19c (servidor Oracle da FIAP: `oracle.fiap.com.br:1521:ORCL`) |
| Driver JDBC | Oracle ojdbc11 23.26 |
| Maven | 3.9 ou superior |
| Lombok | gerenciado pelo Spring Boot (somente em tempo de compilacao) |
| Testes | JUnit 5 e Mockito |
| IDE sugerida | IntelliJ IDEA (ou VS Code com Extension Pack for Java) |
| Testes de API | Postman (colecao em `postman/`) ou IntelliJ HTTP Client (`postman/civia-api.http`) |
| Deploy | Render.com (Web Service com Docker) |

## Estrutura do projeto

Projeto Maven multi-modulo, seguindo o padrao de camadas visto em aula (DDD):

```
civia-api/
├── civia-domain/          modelo (entidades = tabelas), value objects, excecoes de dominio e interfaces DAO
├── civia-infrastructure/  implementacoes DAO (JPA/Oracle): entidades JPA, repositorios e mappers
├── civia-application/     BO (regras de negocio), DTOs (records) e excecoes de aplicacao
├── civia-presentation/    controllers REST, GlobalExceptionHandler, configuracao e bootstrap Spring Boot
├── db/                    scripts SQL (DDL da equipe, sequencias e dados de exemplo)
├── postman/               colecao Postman e arquivo .http com todos os endpoints
├── docs/                  diagrama de classes, DER, endpoints e este guia
├── Dockerfile             build e execucao no Render
└── render.yaml            configuracao do servico no Render
```

Dependencias entre camadas (verificadas pelo `maven-enforcer-plugin` no build):
`presentation -> application -> infrastructure -> domain`.

## Passo a passo (execucao local)

### 1. Clonar o repositorio

```bash
git clone https://github.com/Gabrielvianaana/civia-api.git
cd civia-api
```

### 2. Criar as tabelas no Oracle

Conecte no Oracle da FIAP com o seu usuario (SQL Developer, por exemplo) e execute, nesta ordem:

1. `db/01_ddl.sql` - as 17 tabelas do modelo da equipe, com PKs, FKs, UNIQUEs e CHECKs;
2. `db/02_sequencias.sql` - as sequencias usadas para gerar os ids;
3. `db/03_dados.sql` - dados de exemplo (todos simulados).

Para recriar tudo do zero, rode antes `db/00_drop.sql`.

> Mesmo sem o passo 3, a aplicacao cadastra sozinha os tipos de servico e os perfis atendidos ao subir
> (`DataInitializer`), se essas tabelas estiverem vazias.

### 3. Informar as credenciais do banco

A senha **nao** fica no repositorio. A aplicacao le as variaveis `DB_URL`, `DB_USER` e `DB_PASSWORD`.
Localmente, copie o modelo e preencha com o seu usuario e senha do Oracle da FIAP:

```bash
cp .env.example .env
```

```properties
DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
DB_USER=rmXXXXXX
DB_PASSWORD=sua_senha
```

O arquivo `.env` esta no `.gitignore`. Tambem e possivel exportar as variaveis no terminal em vez de usar o arquivo.

### 4. Compilar e rodar os testes

```bash
mvn verify
```

Executa os testes unitarios das regras de negocio (RN03 a RN06) e das invariantes do dominio, e valida as
dependencias entre as camadas.

### 5. Subir a aplicacao

```bash
java -jar civia-presentation/target/civia-api.jar
```

Ou, pelo IntelliJ, execute a classe `br.com.fiap.civia.CiviaApplication` (modulo `civia-presentation`).

A API sobe em `http://localhost:8080`. Ao iniciar, o Hibernate valida (`ddl-auto: validate`) que as classes
de modelo batem com as tabelas do Oracle.

### 6. Testar

- Health: `GET http://localhost:8080/actuator/health` (deve mostrar `"db": {"status": "UP"}`);
- Postman: importe `postman/CIVIA.postman_collection.json` e rode a colecao inteira (Collection Runner).
  As pastas criam registros, consultam, testam os erros e a ultima pasta apaga o que foi criado.
  Para testar o deploy, troque a variavel `baseUrl` para `https://civia-api-ibn6.onrender.com`;
- IntelliJ: abra `postman/civia-api.http` e execute as requisicoes em ordem.

A lista completa de endpoints, com exemplos reais, esta em [`endpoints.md`](endpoints.md).

## Deploy no Render.com

1. No Render, **New > Web Service**, origem **Public Git Repository**:
   `https://github.com/Gabrielvianaana/civia-api`;
2. Language **Docker** (usa o `Dockerfile` da raiz), branch `main`, instancia **Free**;
3. Em **Environment Variables**, cadastre `DB_USER` e `DB_PASSWORD` (o `DB_URL` padrao ja aponta para o Oracle da FIAP);
4. Em **Advanced**, **Health Check Path** = `/actuator/health`;
5. **Deploy Web Service**. A cada push na `main` o Render faz um novo deploy automaticamente.

O `Dockerfile` compila o projeto com Maven + JDK 21 e executa o jar em uma imagem JRE 21, com o fuso
`America/Sao_Paulo` (os horarios de funcionamento dos servicos sao do horario local).

## Configuracoes uteis

| Variavel | Padrao | Uso |
|---|---|---|
| `DB_URL` | `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL` | URL JDBC do Oracle |
| `DB_USER` / `DB_PASSWORD` | - | credenciais do Oracle (obrigatorias) |
| `PORT` | `8080` | porta HTTP (o Render define automaticamente) |
| `CIVIA_CARGA_INICIAL` | `true` | cadastra tipos de servico e perfis se as tabelas estiverem vazias |

## Diagramas

- Diagrama de classes: [`diagrama-classes.md`](diagrama-classes.md) (Mermaid, renderizado pelo GitHub) e
  arquivos `diagrama-classes-*.puml` (PlantUML). Gerados a partir do codigo por `docs/ferramentas/gerar_diagrama.py`.
- Diagrama de classes em partes (uma pagina cada, para o PDF): pasta [`partes/`](partes/LEIA-ME.md).
- DER: [`der.md`](der.md) (Mermaid), `der.puml` (completo) e, para o PDF, `der-1-mapa-servicos.puml` e `der-2-reciclagem.puml` (PlantUML), gerados a partir de `db/01_ddl.sql`
  por `docs/ferramentas/gerar_der.py`.

Para exportar os `.puml` como imagem: plugin PlantUML do IntelliJ (botao de exportar PNG) ou extensao
PlantUML do VS Code.
