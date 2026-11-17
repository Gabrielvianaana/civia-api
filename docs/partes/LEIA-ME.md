# Diagrama de classes em partes (para o PDF)

O diagrama completo da CIVIA tem 186 classes e nao cabe legivel em uma pagina. Estes arquivos dividem o
mesmo diagrama em partes, cada uma pensada para uma pagina do PDF. Todos sao gerados a partir do codigo-fonte
por `docs/ferramentas/gerar_diagrama.py`, portanto refletem exatamente as classes do projeto.

As partes 01 a 10 cobrem o mapa de servicos e as partes 11 a 16 o modulo de reciclagem.

| Arquivo | Conteudo |
|---|---|
| `01-dominio-servico.puml` | Servico, TipoServico, PerfilAtendido, Horario, GradeHorario, Endereco, Coordenada |
| `02-dominio-disponibilidade.puml` | AtualizacaoDisponibilidade, UsuarioPainel, Organizacao e enums |
| `03-dominio-totem.puml` | Totem e ConsultaTotem |
| `04-dominio-dao.puml` | Interfaces DAO (camada de dominio) |
| `05-dao-implementacoes.puml` | Cada DAO com sua implementacao (DAOImpl) e o repositorio JPA |
| `06-controllers-bos.puml` | Cada Controller com o BO que ele usa |
| `07-bos-detalhe.puml` | BOs com seus metodos (regras de negocio) |
| `08-excecoes.puml` | Excecoes de dominio e de aplicacao, tradutor e GlobalExceptionHandler |
| `09-entidades-jpa.puml` | Entidades JPA mapeadas nas tabelas Oracle |
| `10-dtos.puml` | DTOs (records) de entrada e saida |
| `11-reciclagem-dominio.puml` | Reciclagem: PostoReciclagem, Material, Cartao, Entrega, Recompensa, Troca, Coleta e enums |
| `12-reciclagem-dao.puml` | Reciclagem: cada DAO com sua implementacao (DAOImpl) e o repositorio JPA |
| `13-reciclagem-controllers-bos.puml` | Reciclagem: cada Controller com o BO que ele usa |
| `14-reciclagem-bos-detalhe.puml` | Reciclagem: BOs com seus metodos (RN07 a RN12) |
| `15-reciclagem-entidades-jpa.puml` | Reciclagem: entidades JPA e o mapper de persistencia |
| `16-reciclagem-dtos.puml` | Reciclagem: DTOs (records) de entrada e saida |

Para exportar: abra o arquivo no VS Code (extensao PlantUML) e rode **PlantUML: Export Current Diagram** (png).
