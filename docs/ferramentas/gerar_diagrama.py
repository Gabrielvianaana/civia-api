"""Gera diagramas de classes PlantUML a partir dos fontes Java do projeto CIVIA.
Uso: python3 -I gerar_diagrama.py <raiz-do-projeto> <pasta-docs>
"""
import os
import re
import sys

RAIZ, DOCS = sys.argv[1], sys.argv[2]
MODULOS = ["civia-domain", "civia-infrastructure", "civia-application", "civia-presentation"]

classes = {}  # nome -> dict


def limpar(fonte):
    fonte = re.sub(r"/\*.*?\*/", "", fonte, flags=re.S)
    fonte = re.sub(r"//[^\n]*", "", fonte)
    fonte = re.sub(r'"(?:\\.|[^"\\])*"', '""', fonte)
    return fonte


def corpo_topo(fonte, inicio):
    """Texto do corpo da classe apenas no nivel 1 (sem corpos de metodos)."""
    nivel, saida, i = 0, [], inicio
    while i < len(fonte):
        c = fonte[i]
        if c == "{":
            nivel += 1
            if nivel == 1:
                i += 1
                continue
        elif c == "}":
            nivel -= 1
            if nivel == 0:
                break
        if nivel == 1:
            saida.append(c)
        elif nivel == 2 and c == "{":
            saida.append(c)
        i += 1
    return "".join(saida)


def tipo_simples(t):
    t = re.sub(r"@\w+(\([^)]*\))?\s*", "", t).strip()
    return t.replace("java.util.function.", "")


def separar_parametros(s):
    partes, nivel, atual = [], 0, ""
    for c in s:
        if c in "<(":
            nivel += 1
        elif c in ">)":
            nivel -= 1
        if c == "," and nivel == 0:
            partes.append(atual)
            atual = ""
        else:
            atual += c
    if atual.strip():
        partes.append(atual)
    return [p.strip() for p in partes]


def params_curto(s):
    nomes = []
    for p in separar_parametros(s):
        p = re.sub(r"@\w+(\([^)]*\))?\s*", "", p).strip()
        if not p:
            continue
        tokens = p.rsplit(" ", 1)
        nomes.append(f"{tokens[1]}: {tipo_simples(tokens[0])}" if len(tokens) == 2 else p)
    return ", ".join(nomes)


for modulo in MODULOS:
    base = os.path.join(RAIZ, modulo, "src", "main", "java")
    for dirpath, _, arquivos in os.walk(base):
        for arq in sorted(arquivos):
            if not arq.endswith(".java"):
                continue
            fonte = limpar(open(os.path.join(dirpath, arq), encoding="utf-8").read())
            pacote = re.search(r"package\s+([\w.]+);", fonte).group(1)
            m = re.search(r"((?:@\w+(?:\([^)]*\))?\s*)*)public\s+(?:final\s+|abstract\s+)*(class|interface|enum|record)\s+(\w+)"
                          r"(?:<[^>]*>)?\s*(\(((?:[^()]|\([^()]*\))*)\))?\s*(?:extends\s+([\w<>, ]+?))?\s*(?:implements\s+([\w<>, ]+?))?\s*\{", fonte, re.S)
            if not m:
                continue
            anotacoes, tipo, nome = m.group(1), m.group(2), m.group(3)
            componentes, ext, impl = m.group(5), m.group(6), m.group(7)
            abstrata = "abstract class" in fonte[m.start():m.end()]
            corpo = corpo_topo(fonte, m.end() - 1)
            info = dict(nome=nome, tipo=tipo, pacote=pacote, modulo=modulo, abstrata=abstrata, campos=[], metodos=[],
                        constantes=[], estereotipo=None, ext=ext.strip() if ext else None,
                        impl=[i.strip() for i in impl.split(",")] if impl else [], deps=[])
            for est in ["RestController", "RestControllerAdvice", "Service", "Repository", "Component", "Entity",
                        "Configuration", "Converter"]:
                if re.search(r"@" + est + r"\b", anotacoes):
                    info["estereotipo"] = est
                    break
            if tipo == "record" and componentes:
                for c in separar_parametros(componentes):
                    c = re.sub(r"@[\w.]+(\([^)]*\))?\s*", "", c)
                    c = " ".join(c.split())
                    t, n = c.rsplit(" ", 1)
                    info["campos"].append(("-", n, tipo_simples(t), False))
            if tipo == "enum":
                bloco = corpo.split(";")[0]
                info["constantes"] = [re.match(r"\s*(\w+)", x).group(1) for x in re.split(r",(?![^(]*\))", bloco)
                                      if re.match(r"\s*\w+", x)]
                corpo = corpo[len(bloco) + 1:] if ";" in corpo else ""
            # campos
            for f in re.finditer(r"(?:@[\w.]+(?:\([^;{}]*?\))?\s*)*(private|protected|public)\s+((?:static\s+|final\s+)*)"
                                 r"([\w.<>, ?\[\]]+?)\s+(\w+)\s*(=[^;]*)?;", corpo):
                vis, mods, t, n = f.group(1), f.group(2), f.group(3), f.group(4)
                estatico = "static" in mods
                if estatico and "final" in mods and n.isupper() and vis == "private":
                    continue
                simb = {"private": "-", "protected": "#", "public": "+"}[vis]
                info["campos"].append((simb, n, tipo_simples(t), estatico))
                if "final" in mods and not estatico and vis == "private" and info["estereotipo"] in (
                        "Service", "Repository", "Component", "RestController"):
                    info["deps"].append(tipo_simples(t))
            # metodos
            padrao_metodo = (r"(?m)^[ \t]*(?:@\w+(?:\([^()]*\))?\s*)*(public|protected|private)?\s*((?:static\s+|final\s+|default\s+|abstract\s+)*)"
                             r"(?:<[^>]+>\s+)?([\w.]+(?:<[^(){};]*?>)?(?:\[\])*)\s+(\w+)\s*\(((?:[^()]|\([^()]*\))*)\)\s*(?:throws[^{;]+)?[{;]")
            for mt in re.finditer(padrao_metodo, corpo):
                vis, mods, ret, n, ps = mt.groups()
                if ret in ("new", "return", "else", "throw", "record", "class") or n in ("if", "for", "while", "switch", "catch"):
                    continue
                if tipo == "interface":
                    vis = vis or "public"
                if vis not in ("public", "protected"):
                    continue
                simb = "+" if vis == "public" else "#"
                info["metodos"].append((simb, n, params_curto(ps), tipo_simples(ret), "static" in (mods or "") or vis == "static"))
            classes[nome] = info

GRUPOS = {
    "dominio": ["civia-domain"],
    "infraestrutura": ["civia-infrastructure"],
    "aplicacao": ["civia-application"],
    "apresentacao": ["civia-presentation"],
}


def tipos_referenciados(t):
    return set(re.findall(r"\b([A-Z]\w+)\b", t))


def params_resumidos(ps, limite=4):
    """Somente os nomes dos parametros; listas longas sao abreviadas (os tipos ja aparecem nos atributos)."""
    nomes = [x.split(":")[0].strip() for x in ps.split(", ")] if ps else []
    return ", ".join(nomes if len(nomes) <= limite else nomes[:limite - 1] + ["..."])


def bloco_classe(c, completo=True, resumido=False):
    kw = {"class": "abstract class" if c["abstrata"] else "class", "interface": "interface", "enum": "enum",
          "record": "class"}[c["tipo"]]
    est = []
    if c["tipo"] == "record":
        est.append("record")
    if c["estereotipo"]:
        est.append(c["estereotipo"])
    linhas = [f'{kw} {c["nome"]}' + (f' <<{", ".join(est)}>>' if est else "") + " {"]
    for k in c["constantes"]:
        linhas.append(f"  {k}")
    if completo:
        for simb, n, t, est_ in c["campos"]:
            linhas.append(f"  {simb}{'{static} ' if est_ else ''}{n} : {t}")
        for simb, n, ps, ret, est_ in c["metodos"]:
            ps = params_resumidos(ps) if resumido else ps
            linhas.append(f"  {simb}{'{static} ' if est_ else ''}{n}({ps}) : {ret}")
    linhas.append("}")
    return linhas


def relacoes(nomes):
    rel = set()
    for n in nomes:
        c = classes[n]
        if c["ext"] and c["ext"].split("<")[0] in nomes:
            rel.add(f'{c["ext"].split("<")[0]} <|-- {n}')
        for i in c["impl"]:
            base = i.split("<")[0]
            if base in nomes:
                rel.add(f"{base} <|.. {n}")
        if c["deps"]:
            for d in c["deps"]:
                for t in tipos_referenciados(d):
                    if t in nomes and t != n:
                        rel.add(f"{n} ..> {t}")
        elif c["tipo"] in ("class", "record") and c["estereotipo"] is None:
            for _, _, t, _ in c["campos"]:
                for r in tipos_referenciados(t):
                    if r in nomes and r != n and classes[r]["tipo"] != "interface":
                        mult = '"*"' if re.search(r"(Set|List|Collection)<", t) else '"1"'
                        rel.add(f"{n} --> {mult} {r}")
    return sorted(rel)


def escrever(arquivo, titulo, filtro, completo=True, layout_lr=False):
    nomes = [n for n, c in classes.items() if filtro(c)]
    linhas = ["@startuml", f"title {titulo}", "' Gerado a partir do codigo-fonte Java (pasta civia-*/src/main/java).",
              "!pragma layout smetana", "skinparam classAttributeIconSize 0", "skinparam shadowing false",
              "skinparam packageStyle rectangle", "hide empty members"]
    if layout_lr:
        linhas.append("left to right direction")
    por_pacote = {}
    for n in nomes:
        por_pacote.setdefault(classes[n]["pacote"], []).append(n)
    for pac in sorted(por_pacote):
        linhas.append(f'package "{pac}" {{')
        for n in sorted(por_pacote[pac]):
            linhas += ["  " + l for l in bloco_classe(classes[n], completo)]
        linhas.append("}")
    linhas += relacoes(set(nomes))
    linhas.append("@enduml")
    with open(os.path.join(DOCS, arquivo), "w", encoding="utf-8") as f:
        f.write("\n".join(linhas) + "\n")
    return len(nomes)


os.makedirs(DOCS, exist_ok=True)
n1 = escrever("diagrama-classes-dominio.puml", "CIVIA - Camada de dominio (modelo, value objects, excecoes e DAOs)",
              lambda c: c["modulo"] == "civia-domain")
n2 = escrever("diagrama-classes-camadas.puml",
              "CIVIA - Camadas: Controller -> BO -> DAO (interface) <- DAOImpl -> JPA",
              lambda c: (c["estereotipo"] in ("RestController", "Service", "Repository", "Component")
                         or (c["modulo"] == "civia-domain" and c["pacote"].endswith(".dao"))
                         or c["nome"].endswith("JpaRepository")) and c["nome"] != "CiviaApplication",
              completo=False, layout_lr=True)
n3 = escrever("diagrama-classes-completo.puml", "CIVIA - Diagrama de classes completo", lambda c: True)
print(f"dominio={n1} camadas={n2} completo={n3} classes")



# ---------- Mermaid (renderizado pelo proprio GitHub) ----------
def mermaid_tipo(t):
    return t.replace("<", "~").replace(">", "~").replace(", ", ",")


def mermaid(titulo, filtro, completo=True):
    nomes = [n for n, c in classes.items() if filtro(c)]
    linhas = ["```mermaid", "---", f"title: {titulo}", "---", "classDiagram", "  direction LR" if not completo else "  direction TB"]
    for n in sorted(nomes):
        c = classes[n]
        linhas.append(f"  class {n} {{")
        est = {"interface": "interface", "enum": "enumeration", "record": "record"}.get(c["tipo"]) or (
            "abstract" if c["abstrata"] else c["estereotipo"])
        if est:
            linhas.append(f"    <<{est}>>")
        for k in c["constantes"]:
            linhas.append(f"    {k}")
        if completo:
            for simb, nome, t, estatico in c["campos"]:
                linhas.append(f"    {simb}{mermaid_tipo(t)} {nome}{'$' if estatico else ''}")
            for simb, nome, ps, ret, estatico in c["metodos"]:
                ps_curto = ", ".join(p.split(":")[0] for p in ps.split(", ")) if ps else ""
                linhas.append(f"    {simb}{nome}({ps_curto}){'$' if estatico else ''} {mermaid_tipo(ret)}")
        linhas.append("  }")
    for r in relacoes(set(nomes)):
        linhas.append("  " + r.replace('"1" ', "").replace('"*" ', "").replace(" --> ", " --> ")
                      if '"*"' not in r else "  " + r.replace(' --> "*" ', ' --> "*" '))
    linhas.append("```")
    return "\n".join(linhas)


with open(os.path.join(DOCS, "diagrama-classes.md"), "w", encoding="utf-8") as f:
    f.write("# Diagrama de classes - CIVIA API\n\n"
            "Gerado automaticamente a partir do codigo-fonte Java (`civia-*/src/main/java`), portanto reflete "
            "exatamente as classes do projeto. Os mesmos diagramas estao em PlantUML nos arquivos `.puml` desta pasta "
            "(`diagrama-classes-dominio.puml`, `diagrama-classes-camadas.puml` e `diagrama-classes-completo.puml`).\n\n"
            "## 1. Camada de dominio\n\nModelo (entidades que correspondem as tabelas Oracle), value objects, "
            "excecoes de dominio e interfaces DAO.\n\n")
    f.write(mermaid("CIVIA - Dominio", lambda c: c["modulo"] == "civia-domain"))
    f.write("\n\n## 2. Camadas da aplicacao\n\nFluxo de dependencias: Controller -> BO -> DAO (interface do dominio) "
            "<- DAOImpl (infraestrutura) -> JpaRepository.\n\n")
    f.write(mermaid("CIVIA - Controller, BO e DAO",
                    lambda c: (c["estereotipo"] in ("RestController", "Service", "Repository", "Component")
                               or (c["modulo"] == "civia-domain" and c["pacote"].endswith(".dao"))
                               or c["nome"].endswith("JpaRepository")) and c["nome"] != "CiviaApplication",
                    completo=False))
    f.write("\n")


# ---------- Diagramas em partes (cada um cabe em uma pagina do PDF) ----------
def parte(arquivo, titulo, nomes, completo=True, colunas=3, relacoes_extra=True):
    nomes = [n for n in nomes if n in classes]
    linhas = ["@startuml", f"title {titulo}", "' Gerado a partir do codigo-fonte Java (pasta civia-*/src/main/java).",
              "!pragma layout smetana", "skinparam classAttributeIconSize 0", "skinparam shadowing false",
              "skinparam dpi 150", "hide empty members", "top to bottom direction"]
    for n in nomes:
        linhas += bloco_classe(classes[n], completo, resumido=True)
    rel = relacoes(set(nomes)) if relacoes_extra else []
    linhas += rel
    # grade invisivel: empilha as classes em colunas para a imagem nao virar uma faixa horizontal
    for i in range(len(nomes) - colunas):
        linhas.append(f"{nomes[i]} -[hidden]down- {nomes[i + colunas]}")
    linhas.append("@enduml")
    with open(os.path.join(DOCS, "partes", arquivo), "w", encoding="utf-8") as f:
        f.write("\n".join(linhas) + "\n")


os.makedirs(os.path.join(DOCS, "partes"), exist_ok=True)
DAOS = [n for n, c in classes.items() if c["pacote"].endswith("domain.dao")]
parte("01-dominio-servico.puml", "CIVIA - Dominio: servico, tipo, perfil e horarios",
      ["Servico", "TipoServico", "PerfilAtendido", "Horario", "GradeHorario", "StatusAprovacao", "Endereco",
       "Coordenada", "Validacoes"])
parte("02-dominio-disponibilidade.puml", "CIVIA - Dominio: disponibilidade, usuarios e organizacoes",
      ["AtualizacaoDisponibilidade", "StatusDisponibilidade", "OrigemAtualizacao", "SituacaoServico",
       "UsuarioPainel", "PerfilAcesso", "Organizacao", "TipoOrganizacao"])
parte("03-dominio-totem.puml", "CIVIA - Dominio: totem e consultas",
      ["Totem", "ConsultaTotem", "StatusTotem", "Endereco", "Coordenada"])
parte("04-dominio-dao.puml", "CIVIA - Interfaces DAO (dominio)", sorted(DAOS), colunas=2)
def parte_dao(arquivo, titulo):
    """Uma linha por agregado: DAO (dominio) <|.. DAOImpl (infraestrutura) ..> JpaRepository."""
    linhas = ["@startuml", f"title {titulo}", "' Gerado a partir do codigo-fonte Java (pasta civia-*/src/main/java).",
              "!pragma layout smetana", "skinparam classAttributeIconSize 0", "skinparam shadowing false",
              "skinparam dpi 150", "hide empty members"]
    anterior = None
    for dao in sorted(DAOS):
        impl, jpa = dao + "Impl", dao.replace("DAO", "JpaRepository")
        for n in (dao, impl, jpa):
            linhas += bloco_classe(classes[n], False)
        linhas.append(f"{dao} <|.right. {impl}")
        linhas.append(f"{impl} .right.> {jpa}")
        if anterior:
            linhas.append(f"{anterior} -[hidden]down- {dao}")
        anterior = dao
    linhas.append("@enduml")
    with open(os.path.join(DOCS, "partes", arquivo), "w", encoding="utf-8") as f:
        f.write("\n".join(linhas) + "\n")


parte_dao("05-dao-implementacoes.puml", "CIVIA - DAO: interface (dominio) <- DAOImpl (infraestrutura) -> JpaRepository")
PARES = []
for ctrl in sorted(n for n in classes if n.endswith("Controller")):
    bo = ctrl.replace("Controller", "BO")
    PARES += [ctrl, bo]
PARES += ["MapaServicosBO", "DisponibilidadeBO", "DataInitializer", "CargaInicialBO"]
parte("06-controllers-bos.puml", "CIVIA - Controllers e BOs (regras de negocio)", PARES, completo=False, colunas=2)
parte("07-bos-detalhe.puml", "CIVIA - BOs com metodos",
      sorted(n for n in classes if n.endswith("BO")) + ["Disponibilidade"], colunas=2, relacoes_extra=False)
parte("08-excecoes.puml", "CIVIA - Tratamento de excecoes",
      ["DomainException", "DadoInvalidoException", "RegraNegocioVioladaException", "TradutorExcecaoDominio",
       "DadosInvalidosException", "RegraNegocioException", "RecursoNaoEncontradoException", "ConflitoException",
       "GlobalExceptionHandler"], colunas=2)
parte("09-entidades-jpa.puml", "CIVIA - Entidades JPA (mapeamento das tabelas Oracle)",
      sorted(n for n in classes if n.endswith("Entity")) + ["HoraConverter", "SimNaoConverter"], colunas=3)
parte("10-dtos.puml", "CIVIA - DTOs (records de entrada e saida)",
      sorted(n for n in classes if n.endswith(("Request", "Response"))), colunas=4)
print("partes geradas em docs/partes")
