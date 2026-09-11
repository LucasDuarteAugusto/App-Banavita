# Prompt — Padrões de áudio por tela (extração sem IA)

Copie o bloco abaixo e cole no agente que for implementar. Não alterar este arquivo no lugar do código.

Este documento serve a dois públicos:

1. **Dono / operação:** o que falar no microfone de cada tela (treinar o uso).
2. **Agente de código:** o parser deve **privilegiar estes padrões** (ordem de campos). Frases longas (“cadastrar cliente que tem o telefone…”) são **tolerância**, não o desenho.

Não invente produto. Não introduza LLM, classificador de intenção genérico nem agente de IA local. A **tela já é a classificação**. O áudio só preenche slots.

---

## Papel (agente)

Você implementa no app Android **Banavita** (Kotlin, Compose, parsers em `br.com.banavita.app.core.extraction`). Siga `.cursor/skills/android-modern-stack/SKILL.md`, `.cursor/rules/modifications-log.mdc`.

Ajuste parsers, testes e, se couber, uma linha de dica na revisão de voz. **Não** mude schema Room nem telas de negócio além do necessário para extração/preenchimento.

---

## Decisão de produto (resposta direta)

**Sim.** Na tela de cadastro de **cliente**, o áudio ideal é:

> `Armando 14936586721`

Não: “Cadastrar cliente Armando que tem o telefone 14936586721”.

A tela já sabe que é cliente. “Cadastrar”, “cliente”, “que tem o telefone” só atrapalham o regex e o ASR (Whisper inventa pontuação e “telefoni”).

O mesmo princípio vale em **todas** as telas com mic: **falar só o que vai para os campos, na ordem combinada.**

O ciclo não muda: **segurar o mic → transcrever → revisar o modal → confirmar**. Sem confirmar, nada grava. Formulário **em edição** continua **sem** mic.

Lista e formulário **novo** da mesma entidade usam o **mesmo** padrão (mesmo `VoiceScreenContext`). Na lista, confirmar a voz **cria** o registro (não busca na lista).

---

## Regras para quem fala (o pai)

1. Abrir a tela certa **antes** de gravar.
2. Frase **curta**. Sem “cadastrar”, “quero”, “faz pra mim”.
3. Números: quantidade e preço em número ou palavra simples (`vinte`, `oito reais`). Telefone: **um bloco** com DDD (`14936586721`), não dígito por dígito.
4. Nomes de produto/cliente como estão no cadastro (`nanica`, `padaria`).
5. Se o modal vier errado: **editar no papel** e confirmar. Não repetir a frase longa.

Unidades que o app já entende: **quilo / kg**, **caixa**, **dúzia**, **unidade**.

---

## Padrão por tela

Ordem da linha = ordem dos campos. Colchetes = opcional.

### Cadastros

#### Clientes — lista e novo (`VoiceScreenContext.CLIENTE`)

| | |
|--|--|
| **Padrão** | `[nome] [telefone]` |
| **Falar** | `Armando 14936586721` |
| **Só nome** | `Armando` |
| **Não falar** | cadastrar, cliente, que tem o telefone, celular, whatsapp |

Nome: até 3 palavras (ex. `João da Silva`). Telefone: 10 ou 11 dígitos.

#### Fornecedores — lista e novo (`FORNECEDOR`)

| | |
|--|--|
| **Padrão** | `[nome] [telefone]` |
| **Falar** | `Sítio Verde 14936586721` |
| **Não falar** | cadastrar fornecedor, telefone |

#### Produtos — lista e novo (`PRODUTO`)

| | |
|--|--|
| **Padrão** | `[nome] [preço] [unidade?] [grupo nome?]` |
| **Falar** | `Nanica 8 reais quilo` |
| **Com grupo** | `Nanica 8 reais quilo grupo banana` |
| **Não falar** | cadastrar produto, preço de |

Preço com a palavra **reais** (senão o número vira quantidade). Unidade omitida → `UN`. Curva perecível e dias **não** entram neste áudio (tela / toque).

#### Grupos — lista e novo (`GRUPO`)

| | |
|--|--|
| **Padrão** | `[nome]` ou `[nome] obs [texto]` |
| **Falar** | `Banana` |
| **Com obs** | `Banana obs frutas` |
| **Não falar** | cadastrar grupo |

### Vendas e compras

#### Vendas — lista e nova (`VENDA`) e Início se for lançar venda

Este é o áudio mais rico. Ordem **fixa**; a única palavra-gancho necessária é **pra** (cliente).

| | |
|--|--|
| **Padrão** | `[qtde] [unidade] [produto] [estado?] pra [cliente] [preço?] [fiado\|pago]?` |
| **Mínimo** | `20 quilos nanica pra padaria` |
| **Completo** | `20 quilos nanica amarela pra padaria 8 reais fiado` |
| **À vista** | `20 quilos nanica pra padaria 8 reais pago` (ou omitir: padrão à vista) |
| **Não falar** | “vende”, “fazer uma venda de”, “o cliente chamado” |

**Estado** (perecível): `verde` · `verdolenga` · `amarela` · `pintada`. Sem estado: usa o ponto preferido do cliente, se houver.

**Um item por áudio.** Segundo produto = segunda gravação (ou completar no formulário).

Preço omitido → preço de tabela (ou queima do dia). `fiado` / `a prazo` / `não pagou` = a prazo. Sem isso = à vista.

#### Compras — lista e nova (`COMPRA`)

| | |
|--|--|
| **Padrão** | `[qtde] [unidade] [produto] do [fornecedor] [preço] [prazo]?` |
| **Falar** | `10 caixas nanica do Sítio Verde 6 reais` |
| **A prazo** | `10 caixas nanica do Sítio Verde 6 reais a prazo` |
| **Não falar** | comprei, cadastrar compra |

Preço **obrigatório** na compra (custo). Gancho do fornecedor: **do** / **da**.

### Início (`INICIO`)

O Início **não** cadastra cliente. Três intenções, padrões **iguais** às telas de destino:

| Intenção | Falar | Efeito após confirmar |
|----------|--------|------------------------|
| Lançar venda | padrão de **Vendas** | cria venda |
| Só abrir venda | `nova venda` | vai ao formulário, sem gravar |
| Consulta | padrão de **Consultas** | navega / filtra |
| Agenda | padrão de **Agenda** | cria evento ou abre o dia |

### Consultas (`CONSULTAS`, e modos Estoque / Vendas / Compras / Previsão / Perdas / Encomendas)

Aqui o áudio **não cria** cadastro. Só filtra.

| Objetivo | Falar |
|----------|--------|
| Modo | `estoque` · `vendas` · `compras` · `a receber` · `a pagar` · `previsão` · `perdas` · `encomenda` |
| Período | `hoje` · `semana` · `mês` |
| Produto | `estoque nanica` |
| Zerado | `estoque zerado` |
| Críticos | `pintada` / `perecíveis` |
| Quebra | `vendas por cliente` · `vendas por produto` |

Combinar curto: `vendas semana`. **Não** narrar (“quero ver o relatório de…”).

### A receber / A pagar (`RECEBER` / `PAGAR`)

Dois usos no mesmo mic:

**Só consultar**

- `a receber` · `Maria` · `hoje`

**Lançar baixa** (precisa de **valor**)

| | |
|--|--|
| **Padrão receber** | `[valor] reais da [cliente]` ou `receber [valor] da [cliente]` |
| **Falar** | `50 reais da Maria` |
| **Padrão pagar** | `[valor] reais do [fornecedor]` |
| **Falar** | `200 reais do Sítio Verde` |

Sem valor em reais → trata como filtro, não baixa.

### Agenda — calendário e novo evento (`AGENDA`)

**Consultar o dia** (frase só de recorte):

- `hoje` · `amanhã` · `semana` (fica no mês; seleciona hoje)

**Criar compromisso**

| | |
|--|--|
| **Padrão** | `[título] [hoje\|amanhã] às [hora]` |
| **Falar** | `visita João amanhã às 15` |
| **Com aviso** | `visita João amanhã às 15 me avisa 1 hora` |
| **Não falar** | cadastrar evento, criar um lembrete que |

Hora: `às 15` ou `15h30`. Sem hora → 09:00. Cliente opcional: `pra Maria` no título/cue já existente.

### Telas **sem** microfone hoje

Não treinar áudio aqui (toque): hub Cadastros, Configurações, detalhe de estoque, detalhe a receber/pagar, formulário de **perda**, formulário de **encomenda**, ativação, edição de qualquer ficha.

Se no futuro essas telas ganharem mic, usar:

| Tela | Padrão |
|------|--------|
| Perda | `[qtde] [unidade] [produto] [estado] apodreceu` — ex. `10 quilos nanica pintada apodreceu` |
| Encomenda | `[qtde] [unidade] [produto] [estado] pra [cliente] [hoje\|amanhã]` |

---

## Orientação de implementação (agente)

### Princípio técnico

Parser **posicional por contexto**, não NLP. Ordem do padrão = ordem de extração.

1. Normalizar (`TextNormalizer` + números falados) como hoje.
2. Tirar **lixo tolerado** no começo: `cadastrar`, `cadastro`, `cadatrar`, `novo`, `cliente`, `fornecedor`, `produto`, `grupo`, `vende`, `vender`, `venda`, `comprei`, `compra` — **depois** de identificar o contexto da tela (já conhecido).
3. Extrair âncoras fáceis: telefone (10–11 dígitos), dinheiro (`N reais` / `a N`), quantidade+unidade, estado (`verde|verdolenga|amarela|pintada|podre`), prazo (`fiado` / `pago`…).
4. O que sobra no meio, na ordem, é nome / produto / parceiro.
5. Palavras-gancho **mínimas e estáveis** (não eliminar):
   - venda: **pra** / **para** / **pro** → cliente
   - compra: **do** / **da** → fornecedor
   - produto: **grupo** → nome do grupo
   - grupo: **obs** → observação
   - agenda: **às** / `Nh` → hora; **hoje** / **amanhã**
6. Continuar aceitando as frases antigas dos testes atuais (regressão), mas os **novos testes do padrão curto devem passar e ser a referência**.

### Cliente / fornecedor (`NomeTelefoneParser`)

O padrão `Armando 14936586721` já é o alvo. Garantir:

- Sem a palavra “telefone”, dígitos 10–11 no fim (ou em bloco) = telefone; o resto = nome.
- `Armando` sozinho = nome, telefone vazio (modal deixa completar).
- Não copiar “cliente” / “cadastrar” para o nome.
- Manter fallback de dígitos isolados (ASR ruim) **sem** ensinar isso ao usuário.
- Testes obrigatórios:
  - `Armando 14936586721` → nome Armando, fone 14936586721
  - `João da Silva 11987654321`
  - `Armando` → só nome
  - Frases longas antigas ainda não quebram

### Venda (`VendaExtractionParser` / `DocumentLineParser`)

Implementar leitura na ordem do padrão (além do “de banana” atual):

1. Primeira quantidade + unidade.
2. Estado, se houver palavra da lista.
3. Preço (`N reais` ou `a N reais`).
4. Cliente após **pra/para/pro** (ou `cliente`).
5. Prazo (`PrazoParser`).
6. Produto = tokens que restam (não stopword, não estado, não unidade).

Deve preencher:

- `20 quilos nanica amarela pra padaria 8 reais fiado`
- `20 quilos nanica pra padaria` (sem preço, sem estado)
- Frase antiga: `vende tres quilos de banana nanica pra Maria a doze reais`

Resolver produto/cliente no cadastro como hoje (`EntityResolver`). Modal de revisão **já existente** continua; incluir estado no sheet se ainda não estiver editável.

**Não** implementar multi-item no mesmo áudio neste corte.

### Compra

Espelho da venda, gancho **do/da**, preço obrigatório para `needsReview` baixo.

Teste: `10 caixas nanica do Sitio Verde 6 reais`

### Produto

Ordem: descrição, depois dinheiro, depois unidade, depois `grupo X`.

Teste: `Nanica 8 reais quilo` → descrição Nanica, 800 centavos, KG.

### Consultas / Início / Agenda / Baixa

Não redesenhar. Só garantir que frases **curtas** da tabela acima continuam a funcionar. Não exigir “mostrar o estoque de”.

Início: se a fala bater no padrão de venda (qtde + produto, com ou sem a palavra “vende”), tratar como venda — hoje `InicioIntentParser.looksLikeVenda` exige `vende|vender|…` **e** quantidade/`de `. **Ajustar** para o padrão novo também disparar venda no Início (`20 quilos nanica pra padaria` no Início = venda, não busca “nanica”).

### O que não fazer

- LLM / embeddings / “agente” de extração.
- Mudar o ciclo segurar-soltar-revisar-confirmar.
- Mic em tela de edição.
- Tratar áudio na lista como busca (continua **criar** após confirmar).
- Hardcodar o telefone de exemplo do spec.

### Arquivos típicos

- `NomeTelefoneParser.kt`, `PhoneParser.kt`
- `ContextParsers.kt` (`VendaExtractionParser`, `CompraExtractionParser`, `ProdutoExtractionParser`, `DocumentLineParser`, `MaturacaoEstadoParser`)
- `V2Parsers.kt` (`InicioIntentParser`, consultas, evento, baixa, prazo)
- `ExtractionPipeline.kt`
- `ExtractionReviewSheet.kt` (dica de uma linha opcional com o padrão da tela)
- Testes: `ExtractionParsersTest.kt` (e pipeline se existir)

Dica opcional no modal: texto curto do padrão daquele `VoiceScreenContext` (ex. clientes: “Fale: nome e telefone”). Ajuda o treino no aparelho. Se fizer, strings em `strings.xml`.

### Critérios de aceite

- [ ] `Armando 14936586721` na tela/lista de cliente preenche nome + telefone.
- [ ] `20 quilos nanica amarela pra padaria 8 reais fiado` preenche qtde, KG, produto, estado, cliente, preço, a prazo.
- [ ] Mesma fala de venda no **Início** abre revisão de venda (não só consulta).
- [ ] `10 caixas nanica do Sítio Verde 6 reais` preenche compra.
- [ ] `Nanica 8 reais quilo` preenche produto.
- [ ] Testes novos + os testes antigos de frase longa ainda passam.
- [ ] Sem LLM. Log em `docs/Modifications/YYYY-MM-DD-padroes-audio-extracao.md`.
- [ ] Atualizar o exemplo em `docs/extraction/README.md` para o padrão curto (o longo pode ficar como “ainda aceito”).

### Ordem de trabalho

1. Cliente/fornecedor (padrão nome + fone) + testes.
2. Venda posicional + Início reconhece venda sem a palavra “vende”.
3. Compra + produto.
4. Regressão consultas/agenda/baixa.
5. Dica no modal (opcional) + README de extração + log.
