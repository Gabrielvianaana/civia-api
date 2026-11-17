"""Gera o DER (Mermaid + PlantUML) a partir do DDL Oracle da equipe.
Uso: python3 -I gerar_der.py <db/01_ddl.sql> <pasta-docs>
"""
import os
import re
import sys

ddl = open(sys.argv[1], encoding="utf-8").read()
ddl = re.sub(r"--[^\n]*", "", ddl)
docs = sys.argv[2]

tabelas = {}  # nome -> {colunas: [(nome, tipo, notnull)], pk: [], uniques: [[...]]}
for m in re.finditer(r"CREATE TABLE (\w+)\s*\((.*?)\n\);", ddl, re.S):
    nome, corpo = m.group(1), m.group(2)
    t = {"colunas": [], "pk": [], "uniques": []}
    for linha in re.split(r",\s*\n", corpo):
        linha = " ".join(linha.split())
        if not linha:
            continue
        if linha.upper().startswith("CONSTRAINT"):
            pk = re.search(r"PRIMARY KEY \(([^)]*)\)", linha, re.I)
            un = re.search(r"UNIQUE \(([^)]*)\)", linha, re.I)
            if pk:
                t["pk"] = [c.strip().lower() for c in pk.group(1).split(",")]
            if un:
                t["uniques"].append([c.strip().lower() for c in un.group(1).split(",")])
            continue
        partes = linha.split()
        t["colunas"].append((partes[0].lower(), partes[1], "NOT NULL" in linha.upper()))
    tabelas[nome] = t

fks = []  # (tabela, coluna, tabela_ref)
for m in re.finditer(r"ALTER TABLE (\w+) ADD CONSTRAINT \w+\s+FOREIGN KEY \((\w+)\) REFERENCES (\w+)", ddl, re.S):
    fks.append((m.group(1), m.group(2).lower(), m.group(3)))
fk_cols = {(t, c) for t, c, _ in fks}


def curto(nome):
    return nome[2:] if nome.startswith("T_") else nome


def coluna_info(tab, col):
    for c in tabelas[tab]["colunas"]:
        if c[0] == col:
            return c


linhas = ["```mermaid", "erDiagram"]
for nome, t in tabelas.items():
    linhas.append(f"  {curto(nome)} {{")
    for col, tipo, notnull in t["colunas"]:
        chaves = []
        if col in t["pk"]:
            chaves.append("PK")
        if (nome, col) in fk_cols:
            chaves.append("FK")
        if any(col in u and len(u) == 1 for u in t["uniques"]):
            chaves.append("UK")
        base = re.match(r"\w+", tipo).group(0)
        tam = tipo[len(base):]
        coment = ", ".join(x for x in [tam.strip("()") if tam else "", "NOT NULL" if notnull else "NULL"] if x)
        linhas.append(f"    {base} {col} {','.join(chaves)} \"{coment}\"".replace("  ", " ").replace(' "', ' "'))
    linhas.append("  }")
for tab, col, ref in fks:
    _, _, notnull = coluna_info(tab, col)
    unico = any(u == [col] for u in tabelas[tab]["uniques"])
    lado_ref = "||" if notnull else "|o"
    lado_tab = "o|" if unico else "o{"
    linhas.append(f'  {curto(ref)} {lado_ref}--{lado_tab} {curto(tab)} : "{col}"')
linhas.append("```")

with open(os.path.join(docs, "der.md"), "w", encoding="utf-8") as f:
    f.write("# DER - Modelo fisico Oracle (CIVIA)\n\n"
            "Gerado a partir do script da equipe `db/01_ddl.sql` (17 tabelas). PK = chave primaria, "
            "FK = chave estrangeira, UK = unica. Cardinalidade: `||` obrigatorio, `|o` opcional, `o{` zero ou muitos, "
            "`o|` zero ou um.\n\n"
            "A API Java mapeia as 10 tabelas do mapa de servicos (MVP): T_ORGANIZACAO, T_TIPO_SERVICO, "
            "T_PERFIL_ATENDIDO, T_SERVICO, T_SERVICO_PERFIL, T_HORARIO, T_USUARIO_PAINEL, T_ATUALIZACAO, T_TOTEM e "
            "T_CONSULTA_TOTEM. As tabelas do modulo de reciclagem (T_POSTO_RECICLAGEM, T_MATERIAL, T_CARTAO, "
            "T_ENTREGA, T_RECOMPENSA, T_TROCA e T_COLETA) existem no banco e fazem parte do modelo da equipe.\n\n"
            "Os ids sao gerados pelas sequencias de `db/02_sequencias.sql` (`SQ_<TABELA>`).\n\n")
    f.write("\n".join(linhas) + "\n")

# PlantUML (notacao IE)
p = ["@startuml", "title CIVIA - DER (Oracle)", "' Gerado a partir de db/01_ddl.sql", "!pragma layout smetana",
     "hide circle", "skinparam linetype ortho", "skinparam shadowing false"]
for nome, t in tabelas.items():
    p.append(f'entity "{nome}" as {curto(nome)} {{')
    for col, tipo, notnull in t["colunas"]:
        if col in t["pk"]:
            p.append(f"  * **{col}** : {tipo} <<PK{', FK' if (nome, col) in fk_cols else ''}>>")
    p.append("  --")
    for col, tipo, notnull in t["colunas"]:
        if col in t["pk"]:
            continue
        marcas = [m for m, cond in (("FK", (nome, col) in fk_cols), ("UK", any(u == [col] for u in t["uniques"]))) if cond]
        p.append(f"  {'*' if notnull else ' '} {col} : {tipo}" + (f" <<{', '.join(marcas)}>>" if marcas else ""))
    p.append("}")
for tab, col, ref in fks:
    _, _, notnull = coluna_info(tab, col)
    unico = any(u == [col] for u in tabelas[tab]["uniques"])
    p.append(f'{curto(ref)} {"||" if notnull else "|o"}--{"o|" if unico else "o{"} {curto(tab)} : {col}')
p.append("@enduml")
with open(os.path.join(docs, "der.puml"), "w", encoding="utf-8") as f:
    f.write("\n".join(p) + "\n")
print(len(tabelas), "tabelas,", len(fks), "FKs")


# ---------- DER em duas partes (cada uma cabe em uma pagina do PDF) ----------
def der_parte(arquivo, titulo, nomes):
    q = ["@startuml", f"title {titulo}", "' Gerado a partir de db/01_ddl.sql", "!pragma layout smetana",
         "hide circle", "skinparam shadowing false", "skinparam dpi 150", "top to bottom direction"]
    for nome in nomes:
        t = tabelas[nome]
        q.append(f'entity "{nome}" as {curto(nome)} {{')
        for col, tipo, notnull in t["colunas"]:
            if col in t["pk"]:
                q.append(f"  * **{col}** : {tipo} <<PK{', FK' if (nome, col) in fk_cols else ''}>>")
        q.append("  --")
        for col, tipo, notnull in t["colunas"]:
            if col in t["pk"]:
                continue
            marcas = [m for m, cond in (("FK", (nome, col) in fk_cols), ("UK", any(u == [col] for u in t["uniques"]))) if cond]
            q.append(f"  {'*' if notnull else ' '} {col} : {tipo}" + (f" <<{', '.join(marcas)}>>" if marcas else ""))
        q.append("}")
    for tab, col, ref in fks:
        if tab in nomes and ref in nomes:
            _, _, notnull = coluna_info(tab, col)
            unico = any(u == [col] for u in tabelas[tab]["uniques"])
            q.append(f'{curto(ref)} {"||" if notnull else "|o"}--{"o|" if unico else "o{"} {curto(tab)} : {col}')
    q.append("@enduml")
    with open(os.path.join(docs, arquivo), "w", encoding="utf-8") as f:
        f.write("\n".join(q) + "\n")


der_parte("der-1-mapa-servicos.puml", "CIVIA - DER parte 1: mapa de servicos (tabelas usadas pela API)",
          ["T_ORGANIZACAO", "T_TIPO_SERVICO", "T_PERFIL_ATENDIDO", "T_SERVICO", "T_SERVICO_PERFIL", "T_HORARIO",
           "T_USUARIO_PAINEL", "T_ATUALIZACAO", "T_TOTEM", "T_CONSULTA_TOTEM"])
der_parte("der-2-reciclagem.puml", "CIVIA - DER parte 2: modulo de reciclagem",
          ["T_ORGANIZACAO", "T_TOTEM", "T_USUARIO_PAINEL", "T_POSTO_RECICLAGEM", "T_MATERIAL", "T_CARTAO",
           "T_ENTREGA", "T_RECOMPENSA", "T_TROCA", "T_COLETA"])
print("DER em partes: der-1-mapa-servicos.puml e der-2-reciclagem.puml")
