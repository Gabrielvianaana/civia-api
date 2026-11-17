"""Gera a colecao Postman (CIVIA.postman_collection.json) e o arquivo HTTP (civia-api.http) a partir
de uma unica lista de requisicoes. Uso: python3 postman/gerar_colecao.py
A sequencia funciona como roteiro de ponta a ponta: cria registros, consulta, testa erros e limpa no final.
"""
import json
import os

AQUI = os.path.dirname(os.path.abspath(__file__))

# (pasta, nome, metodo, caminho, corpo, status esperado, variavel a salvar {var: campo})
R = []


def req(pasta, nome, metodo, caminho, corpo=None, status=200, salvar=None):
    R.append(dict(pasta=pasta, nome=nome, metodo=metodo, caminho=caminho, corpo=corpo, status=status, salvar=salvar or {}))


P = "00 - Saude"
req(P, "Health (conexao com o Oracle)", "GET", "/actuator/health")

P = "01 - Tipos de servico (T_TIPO_SERVICO)"
req(P, "Listar tipos", "GET", "/api/v1/tipos-servico")
req(P, "Cadastrar tipo", "POST", "/api/v1/tipos-servico", {"nome": "Saude", "icone": "cruz"}, 201, {"tipoId": "id"})
req(P, "Buscar tipo", "GET", "/api/v1/tipos-servico/{{tipoId}}")
req(P, "Atualizar tipo", "PUT", "/api/v1/tipos-servico/{{tipoId}}", {"nome": "Saude e documentos", "icone": "cruz"})

P = "02 - Perfis atendidos (T_PERFIL_ATENDIDO)"
req(P, "Listar perfis", "GET", "/api/v1/perfis")
req(P, "Cadastrar perfil", "POST", "/api/v1/perfis", {"descricao": "Pessoa migrante"}, 201, {"perfilId": "id"})
req(P, "Buscar perfil", "GET", "/api/v1/perfis/{{perfilId}}")
req(P, "Atualizar perfil", "PUT", "/api/v1/perfis/{{perfilId}}", {"descricao": "Pessoa migrante ou refugiada"})

P = "03 - Organizacoes (T_ORGANIZACAO)"
req(P, "Listar organizacoes (filtro opcional por tipo)", "GET", "/api/v1/organizacoes?tipo=ONG")
req(P, "Cadastrar organizacao", "POST", "/api/v1/organizacoes",
    {"nome": "ONG Teste Postman (simulada)", "tipo": "ONG", "telefone": "11900000099", "email": "postman@civia.exemplo.org"},
    201, {"organizacaoId": "id"})
req(P, "Buscar organizacao", "GET", "/api/v1/organizacoes/{{organizacaoId}}")
req(P, "Atualizar organizacao", "PUT", "/api/v1/organizacoes/{{organizacaoId}}",
    {"nome": "ONG Teste Postman (simulada)", "tipo": "ONG", "telefone": "11900000098", "email": "postman@civia.exemplo.org"})

P = "04 - Usuarios do painel (T_USUARIO_PAINEL)"
req(P, "Listar usuarios (filtro opcional por organizacao)", "GET", "/api/v1/usuarios")
req(P, "Cadastrar usuario", "POST", "/api/v1/usuarios",
    {"organizacaoId": "{{organizacaoId}}", "nome": "Equipe Postman (simulada)", "email": "equipe.postman@civia.exemplo.org",
     "perfilAcesso": "EQUIPE"}, 201, {"usuarioId": "id"})
req(P, "Buscar usuario", "GET", "/api/v1/usuarios/{{usuarioId}}")
req(P, "Atualizar usuario", "PUT", "/api/v1/usuarios/{{usuarioId}}",
    {"organizacaoId": "{{organizacaoId}}", "nome": "Equipe Postman (simulada)", "email": "equipe.postman@civia.exemplo.org",
     "perfilAcesso": "EQUIPE", "ativo": True})

P = "05 - Servicos (T_SERVICO e T_SERVICO_PERFIL)"
req(P, "Listar servicos (filtros opcionais)", "GET", "/api/v1/servicos?statusAprovacao=APROVADO")
req(P, "Cadastrar servico (abrigo)", "POST", "/api/v1/servicos",
    {"organizacaoId": "{{organizacaoId}}", "tipoServicoId": 1, "nome": "Abrigo Postman (simulado)",
     "logradouro": "Rua Teste", "numero": "100", "bairro": "Se", "latitude": -23.5512, "longitude": -46.6340,
     "capacidade": 20, "funciona24h": False, "statusAprovacao": "APROVADO", "perfilIds": [2, "{{perfilId}}"]},
    201, {"servicoId": "id"})
req(P, "Buscar servico", "GET", "/api/v1/servicos/{{servicoId}}")
req(P, "Atualizar servico", "PUT", "/api/v1/servicos/{{servicoId}}",
    {"organizacaoId": "{{organizacaoId}}", "tipoServicoId": 1, "nome": "Abrigo Postman (simulado)",
     "logradouro": "Rua Teste", "numero": "100", "bairro": "Se", "latitude": -23.5512, "longitude": -46.6340,
     "capacidade": 25, "funciona24h": True, "statusAprovacao": "APROVADO", "perfilIds": [2, 3, "{{perfilId}}"]})

P = "06 - Horarios (T_HORARIO) - sub-recurso de servico"
req(P, "Listar horarios de um servico", "GET", "/api/v1/servicos/1/horarios")
req(P, "Cadastrar horario (quarta, 18:00-23:59)", "POST", "/api/v1/servicos/5/horarios",
    {"diaSemana": 4, "horaAbertura": "18:00", "horaFechamento": "23:59"}, 201, {"horarioId": "id"})
req(P, "Buscar horario", "GET", "/api/v1/servicos/5/horarios/{{horarioId}}")
req(P, "Atualizar horario", "PUT", "/api/v1/servicos/5/horarios/{{horarioId}}",
    {"diaSemana": 4, "horaAbertura": "19:00", "horaFechamento": "22:00"})

P = "07 - Atualizacoes de disponibilidade (T_ATUALIZACAO)"
req(P, "Registrar vagas de abrigo pelo painel (RN03: grava a faixa)", "POST", "/api/v1/atualizacoes",
    {"servicoId": "{{servicoId}}", "origem": "PAINEL", "usuarioId": "{{usuarioId}}", "vagas": 3}, 201, {"atualizacaoId": "id"})
req(P, "Registrar vagas pela Central de Vagas (sem usuario)", "POST", "/api/v1/atualizacoes",
    {"servicoId": "{{servicoId}}", "origem": "CENTRAL_VAGAS", "vagas": 12}, 201)
req(P, "Listar atualizacoes de um servico", "GET", "/api/v1/atualizacoes?servicoId={{servicoId}}")
req(P, "Buscar atualizacao", "GET", "/api/v1/atualizacoes/{{atualizacaoId}}")
req(P, "Corrigir atualizacao", "PUT", "/api/v1/atualizacoes/{{atualizacaoId}}",
    {"servicoId": "{{servicoId}}", "origem": "PAINEL", "usuarioId": "{{usuarioId}}", "vagas": 0})

P = "08 - Totens (T_TOTEM) e mapa de servicos"
req(P, "Listar totens", "GET", "/api/v1/totens")
req(P, "Cadastrar totem", "POST", "/api/v1/totens",
    {"codigo": "TOT-PM-01", "logradouro": "Praca Teste Postman", "bairro": "Se", "latitude": -23.5508,
     "longitude": -46.6335, "papelImpressora": True}, 201, {"totemId": "id"})
req(P, "Buscar totem", "GET", "/api/v1/totens/{{totemId}}")
req(P, "Atualizar totem", "PUT", "/api/v1/totens/{{totemId}}",
    {"codigo": "TOT-PM-01", "logradouro": "Praca Teste Postman", "bairro": "Se", "latitude": -23.5508,
     "longitude": -46.6335, "status": "ONLINE", "papelImpressora": False})
req(P, "Mapa: 3 abrigos mais proximos para mulher", "GET", "/api/v1/totens/1/servicos?tipo=Abrigo&perfil=Mulher")
req(P, "Mapa: refeicoes (tipo pelo id)", "GET", "/api/v1/totens/1/servicos?tipo=2")
req(P, "Mapa: todos os tipos", "GET", "/api/v1/totens/{{totemId}}/servicos")

P = "09 - Consultas no totem (T_CONSULTA_TOTEM)"
req(P, "Registrar consulta com rota impressa (gera codigo)", "POST", "/api/v1/consultas",
    {"totemId": "{{totemId}}", "tipoServicoId": 1, "perfilId": 2, "servicoEscolhidoId": "{{servicoId}}",
     "imprimiuRota": True, "pediuAbordagem": False}, 201, {"consultaId": "id", "codigoChegada": "codigoChegada"})
req(P, "Listar consultas de um totem", "GET", "/api/v1/consultas?totemId={{totemId}}")
req(P, "Buscar consulta", "GET", "/api/v1/consultas/{{consultaId}}")
req(P, "Atualizar consulta (pediu abordagem social)", "PUT", "/api/v1/consultas/{{consultaId}}",
    {"totemId": "{{totemId}}", "tipoServicoId": 1, "perfilId": 2, "servicoEscolhidoId": "{{servicoId}}",
     "imprimiuRota": True, "pediuAbordagem": True})
req(P, "Registrar chegada pelo codigo impresso (US14)", "PUT", "/api/v1/consultas/chegada/{{codigoChegada}}")

P = "10 - Reciclagem: materiais e postos (T_MATERIAL, T_POSTO_RECICLAGEM)"
req(P, "Listar materiais (RN09: so lata e PET sao aceitas)", "GET", "/api/v1/materiais")
req(P, "Cadastrar material", "POST", "/api/v1/materiais",
    {"nome": "Aluminio Postman (simulado)", "creditosPorKg": 4, "aceito": True}, 201, {"materialId": "id"})
req(P, "Buscar material", "GET", "/api/v1/materiais/{{materialId}}")
req(P, "Atualizar material", "PUT", "/api/v1/materiais/{{materialId}}",
    {"nome": "Aluminio Postman (simulado)", "creditosPorKg": 6, "aceito": True})
req(P, "Listar postos (filtro opcional por status)", "GET", "/api/v1/postos-reciclagem?status=ATIVO")
req(P, "Cadastrar posto no totem criado (limite diario 3 kg)", "POST", "/api/v1/postos-reciclagem",
    {"totemId": "{{totemId}}", "cooperativaId": 4, "capacidadeKg": 50, "limiteDiarioKg": 3}, 201, {"postoId": "id"})
req(P, "Buscar posto (com material acumulado)", "GET", "/api/v1/postos-reciclagem/{{postoId}}")
req(P, "Atualizar posto", "PUT", "/api/v1/postos-reciclagem/{{postoId}}",
    {"totemId": "{{totemId}}", "cooperativaId": 4, "capacidadeKg": 60, "limiteDiarioKg": 3, "status": "ATIVO"})

P = "11 - Reciclagem: cartoes, entregas, recompensas e trocas"
req(P, "Emitir cartao anonimo no Centro POP (RN07)", "POST", "/api/v1/cartoes",
    {"organizacaoEmissoraId": 1}, 201, {"cartaoId": "id", "codigoCartao": "codigo"})
req(P, "Listar cartoes ativos", "GET", "/api/v1/cartoes?status=ATIVO")
req(P, "Buscar cartao", "GET", "/api/v1/cartoes/{{cartaoId}}")
req(P, "Consultar saldo pelo codigo do cartao", "GET", "/api/v1/cartoes/codigo/{{codigoCartao}}")
req(P, "Atualizar cartao", "PUT", "/api/v1/cartoes/{{cartaoId}}", {"organizacaoEmissoraId": 1, "status": "ATIVO"})
req(P, "Registrar entrega de 2 kg de lata (gera 20 creditos)", "POST", "/api/v1/entregas",
    {"codigoCartao": "{{codigoCartao}}", "postoId": "{{postoId}}", "materialId": 1, "pesoKg": 2}, 201, {"entregaId": "id"})
req(P, "Listar entregas do cartao", "GET", "/api/v1/entregas?cartaoId={{cartaoId}}")
req(P, "Buscar entrega", "GET", "/api/v1/entregas/{{entregaId}}")
req(P, "Corrigir peso da entrega (recalcula os creditos)", "PUT", "/api/v1/entregas/{{entregaId}}",
    {"codigoCartao": "{{codigoCartao}}", "postoId": "{{postoId}}", "materialId": 1, "pesoKg": 2.5})
req(P, "Listar recompensas ativas", "GET", "/api/v1/recompensas?ativo=true")
req(P, "Cadastrar recompensa", "POST", "/api/v1/recompensas",
    {"organizacaoParceiraId": 5, "nome": "Kit Postman (simulado)", "custoCreditos": 15}, 201, {"recompensaId": "id"})
req(P, "Buscar recompensa", "GET", "/api/v1/recompensas/{{recompensaId}}")
req(P, "Atualizar recompensa", "PUT", "/api/v1/recompensas/{{recompensaId}}",
    {"organizacaoParceiraId": 5, "nome": "Kit Postman (simulado)", "custoCreditos": 20, "ativo": True})
req(P, "Registrar troca (debita o custo do saldo - RN11)", "POST", "/api/v1/trocas",
    {"codigoCartao": "{{codigoCartao}}", "recompensaId": "{{recompensaId}}", "usuarioId": 1}, 201, {"trocaId": "id"})
req(P, "Listar trocas do cartao", "GET", "/api/v1/trocas?cartaoId={{cartaoId}}")
req(P, "Buscar troca", "GET", "/api/v1/trocas/{{trocaId}}")
req(P, "Atualizar troca (registrada pelo gestor)", "PUT", "/api/v1/trocas/{{trocaId}}",
    {"codigoCartao": "{{codigoCartao}}", "recompensaId": "{{recompensaId}}", "usuarioId": 3})
req(P, "Emitir cartao que sera perdido", "POST", "/api/v1/cartoes", {"organizacaoEmissoraId": 1}, 201,
    {"cartaoPerdidoId": "id"})
req(P, "Substituir cartao perdido (RN12: bloqueia e transfere o saldo)", "POST", "/api/v1/cartoes",
    {"organizacaoEmissoraId": 1, "cartaoAnteriorId": "{{cartaoPerdidoId}}"}, 201, {"cartaoNovoId": "id"})

P = "12 - Reciclagem: coletas (T_COLETA)"
req(P, "Registrar coleta (sem peso: usa o material acumulado)", "POST", "/api/v1/coletas",
    {"postoId": "{{postoId}}", "cooperativaId": 4}, 201, {"coletaId": "id"})
req(P, "Listar coletas do posto", "GET", "/api/v1/coletas?postoId={{postoId}}")
req(P, "Buscar coleta", "GET", "/api/v1/coletas/{{coletaId}}")
req(P, "Corrigir peso da coleta", "PUT", "/api/v1/coletas/{{coletaId}}",
    {"postoId": "{{postoId}}", "cooperativaId": 4, "pesoTotalKg": 2.6})

P = "13 - Tratamento de erros"
req(P, "400 - campos invalidos (Bean Validation)", "POST", "/api/v1/servicos",
    {"nome": "", "latitude": 200}, 400)
req(P, "400 - valor de enum invalido", "GET", "/api/v1/servicos?statusAprovacao=TALVEZ", None, 400)
req(P, "400 - JSON mal formado", "POST", "/api/v1/totens", "{ isto nao e json", 400)
req(P, "404 - recurso inexistente", "GET", "/api/v1/servicos/999999", None, 404)
req(P, "409 - codigo de totem duplicado", "POST", "/api/v1/totens",
    {"codigo": "TOT-SE-01", "logradouro": "Duplicado", "bairro": "Se", "latitude": -23.55, "longitude": -46.63}, 409)
req(P, "409 - excluir totem com consultas", "DELETE", "/api/v1/totens/1", None, 409)
req(P, "422 - vagas informadas para refeicao", "POST", "/api/v1/atualizacoes",
    {"servicoId": 5, "usuarioId": 2, "vagas": 10}, 422)
req(P, "422 - equipe atualizando servico de outra organizacao", "POST", "/api/v1/atualizacoes",
    {"servicoId": 5, "usuarioId": "{{usuarioId}}", "situacao": "ABERTO"}, 422)
req(P, "422 - totem em manutencao", "GET", "/api/v1/totens/3/servicos", None, 422)
req(P, "422 - RN09 vidro nao e aceito no posto", "POST", "/api/v1/entregas",
    {"codigoCartao": "{{codigoCartao}}", "postoId": "{{postoId}}", "materialId": 3, "pesoKg": 1}, 422)
req(P, "422 - RN08 limite diario do cartao", "POST", "/api/v1/entregas",
    {"codigoCartao": "{{codigoCartao}}", "postoId": "{{postoId}}", "materialId": 1, "pesoKg": 1}, 422)
req(P, "422 - RN11 saldo insuficiente para a troca", "POST", "/api/v1/trocas",
    {"codigoCartao": "{{codigoCartao}}", "recompensaId": "{{recompensaId}}", "usuarioId": 1}, 422)
req(P, "422 - RN07 cartao emitido fora do Centro POP", "POST", "/api/v1/cartoes", {"organizacaoEmissoraId": 2}, 422)
req(P, "409 - cartao ja substituido", "POST", "/api/v1/cartoes",
    {"organizacaoEmissoraId": 1, "cartaoAnteriorId": "{{cartaoPerdidoId}}"}, 409)
req(P, "404 - codigo de cartao inexistente", "GET", "/api/v1/cartoes/codigo/ZZZZZZZZ", None, 404)

P = "14 - Limpeza (DELETE)"
req(P, "Excluir troca (devolve os creditos)", "DELETE", "/api/v1/trocas/{{trocaId}}", None, 204)
req(P, "Excluir entrega (estorna os creditos)", "DELETE", "/api/v1/entregas/{{entregaId}}", None, 204)
req(P, "Excluir coleta", "DELETE", "/api/v1/coletas/{{coletaId}}", None, 204)
req(P, "Excluir posto", "DELETE", "/api/v1/postos-reciclagem/{{postoId}}", None, 204)
req(P, "Excluir cartao substituto", "DELETE", "/api/v1/cartoes/{{cartaoNovoId}}", None, 204)
req(P, "Excluir cartao perdido", "DELETE", "/api/v1/cartoes/{{cartaoPerdidoId}}", None, 204)
req(P, "Excluir cartao", "DELETE", "/api/v1/cartoes/{{cartaoId}}", None, 204)
req(P, "Excluir recompensa", "DELETE", "/api/v1/recompensas/{{recompensaId}}", None, 204)
req(P, "Excluir material", "DELETE", "/api/v1/materiais/{{materialId}}", None, 204)
req(P, "Excluir consulta", "DELETE", "/api/v1/consultas/{{consultaId}}", None, 204)
req(P, "Excluir totem", "DELETE", "/api/v1/totens/{{totemId}}", None, 204)
req(P, "Excluir horario", "DELETE", "/api/v1/servicos/5/horarios/{{horarioId}}", None, 204)
req(P, "Excluir atualizacao", "DELETE", "/api/v1/atualizacoes/{{atualizacaoId}}", None, 204)
req(P, "Excluir servico (remove horarios e atualizacoes)", "DELETE", "/api/v1/servicos/{{servicoId}}", None, 204)
req(P, "Excluir usuario", "DELETE", "/api/v1/usuarios/{{usuarioId}}", None, 204)
req(P, "Excluir organizacao", "DELETE", "/api/v1/organizacoes/{{organizacaoId}}", None, 204)
req(P, "Excluir perfil", "DELETE", "/api/v1/perfis/{{perfilId}}", None, 204)
req(P, "Excluir tipo de servico", "DELETE", "/api/v1/tipos-servico/{{tipoId}}", None, 204)


def corpo_texto(corpo):
    if corpo is None:
        return None
    if isinstance(corpo, str):
        return corpo
    texto = json.dumps(corpo, ensure_ascii=False, indent=2)
    # ids vindos de variaveis devem ir como numero no JSON
    for var in ("organizacaoId", "perfilId", "usuarioId", "servicoId", "totemId"):
        texto = texto.replace(f'"{{{{{var}}}}}"', f"{{{{{var}}}}}")
    return texto


# ---------- Postman v2.1 ----------
pastas = {}
for r in R:
    script = [f'pm.test("Status {r["status"]}", function () {{ pm.response.to.have.status({r["status"]}); }});']
    for var, campo in r["salvar"].items():
        script.append(f'pm.collectionVariables.set("{var}", pm.response.json().{campo});')
    item = {
        "name": r["nome"],
        "event": [{"listen": "test", "script": {"type": "text/javascript", "exec": script}}],
        "request": {
            "method": r["metodo"],
            "header": [{"key": "Content-Type", "value": "application/json"}] if r["corpo"] is not None else [],
            "url": {"raw": "{{baseUrl}}" + r["caminho"]},
        },
    }
    if r["corpo"] is not None:
        item["request"]["body"] = {"mode": "raw", "raw": corpo_texto(r["corpo"]),
                                   "options": {"raw": {"language": "json"}}}
    pastas.setdefault(r["pasta"], []).append(item)

colecao = {
    "info": {
        "name": "CIVIA API - Global Solution 2026",
        "description": "API RESTful da CIVIA (mapa de servicos para totens). Rode na ordem (Collection Runner): cada pasta "
                       "cria registros, consulta, testa os erros e a ultima pasta apaga o que foi criado. Troque a variavel "
                       "baseUrl para a URL do Render para testar o deploy.",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
    },
    "variable": [{"key": "baseUrl", "value": "http://localhost:8080"}]
                + [{"key": v, "value": ""} for v in ("tipoId", "perfilId", "organizacaoId", "usuarioId", "servicoId",
                                                     "horarioId", "atualizacaoId", "totemId", "consultaId", "codigoChegada")],
    "item": [{"name": nome, "item": itens} for nome, itens in pastas.items()],
}
with open(os.path.join(AQUI, "CIVIA.postman_collection.json"), "w", encoding="utf-8") as f:
    json.dump(colecao, f, ensure_ascii=False, indent=2)

# ---------- Arquivo .http (IntelliJ HTTP Client / VS Code REST Client) ----------
linhas = ["# CIVIA API - todas as requisicoes (IntelliJ HTTP Client ou VS Code REST Client).",
          "# Rode de cima para baixo: os ids criados sao guardados e reaproveitados; o fim apaga o que foi criado.",
          "# Para testar o deploy, troque o valor de @baseUrl pela URL do Render.", "",
          "@baseUrl = http://localhost:8080", ""]
pasta_atual = None
for r in R:
    if r["pasta"] != pasta_atual:
        linhas += [f"### ===== {r['pasta']} =====", ""]
        pasta_atual = r["pasta"]
    linhas.append(f"### {r['nome']} (espera {r['status']})")
    linhas.append(f"{r['metodo']} {{{{baseUrl}}}}{r['caminho']}")
    if r["corpo"] is not None:
        linhas += ["Content-Type: application/json", "", corpo_texto(r["corpo"])]
    if r["salvar"]:
        sets = " ".join(f'client.global.set("{v}", response.body.{c});' for v, c in r["salvar"].items())
        linhas += ["", f"> {{% {sets} %}}"]
    linhas.append("")
texto_http = "\n".join(linhas)
for var in ("tipoId", "perfilId", "organizacaoId", "usuarioId", "servicoId", "horarioId", "atualizacaoId", "totemId",
            "consultaId", "codigoChegada"):
    texto_http = texto_http.replace(f"{{{{{var}}}}}", f"{{{{{var}}}}}")
with open(os.path.join(AQUI, "civia-api.http"), "w", encoding="utf-8") as f:
    f.write(texto_http)

print(f"{len(R)} requisicoes em {len(pastas)} pastas")
