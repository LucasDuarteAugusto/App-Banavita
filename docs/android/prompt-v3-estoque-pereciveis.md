# Prompt — v3: estoque de perecíveis (banana)

Copie o bloco abaixo e cole no agente que for implementar. Não alterar este arquivo no lugar do código.

Entregar **na ordem dos blocos**. Um bloco só começa quando o anterior estiver usável no aparelho. Não pule para relatório, queima de preço ou encomenda antes do lote, da câmara e da venda com estado existirem de verdade.

---

## Papel

Você é o agente de implementação do app Android **Banavita** (Kotlin, Jetpack Compose, Room, Hilt, MVVM/Clean). Implemente a entrega abaixo. Siga `.cursor/skills/android-modern-stack/SKILL.md`, `.cursor/rules/modifications-log.mdc` e `.cursor/rules/ui-mockups.mdc`.

Não invente produto. As regras de negócio deste prompt são decisões de Product Owner e devem ser cumpridas à risca.

O Banavita é o controle **offline** do negócio de distribuição de bananas. A v3 incrementa o estoque para perecíveis. Cadastro, venda, compra, financeiro, agenda, voz e backup **continuam**; esta entrega **estende** estoque, produto, compra, venda, consultas e avisos.

## Contexto do produto (hoje)

O saldo é um número só por produto:

```txt
saldo = SUM(compra_item) − SUM(venda_item)
```

View: `vw_produto_estoque`. Não há lote, câmara, estado de maturação nem perda. Venda valida saldo total do produto (`EstoqueValidator`); estoque negativo é proibido. Preço do item é snapshot. Schema Room atual: versão **3**, migrações 1→2 e 2→3, **sem** `fallbackToDestructiveMigration`.

Isso não atende o processo real:

1. A banana chega **verde** e vai para a **câmara fria** (maturação pausada).
2. Quando libera espaço, parte vai para a **câmara de etileno** (gás uma vez ao dia) — aí o relógio liga.
3. Depois de N dias **por variedade** (prata, nanica, ouro…): verde → **verdolenga** → **amarela** → **pintada** → **podre**.
4. Doença **não** entra neste corte.
5. O cliente pergunta o **estado**. A venda precisa baixar aquele estado. Perda se lança à mão. De manhã o app avisa o que está para estragar e sugere reduzir preço.

## Decisões travadas (não negociar)

1. **Não** cadastrar um produto por cor (Nanica verde, Nanica amarela…). O produto continua **Banana-nanica**; estado é do **lote**.
2. Produto **não perecível** (caixa, outro item) permanece com o saldo de hoje. Só produto marcado como perecível entra no jogo de lote/câmara/estado.
3. Relógio de maturação **só anda na câmara de etileno**. Na fria o estado fica **verde** e o tempo não conta para a curva.
4. Estados, nesta ordem, com estes nomes na UI: **verde → verdolenga → amarela → pintada → podre**. Dias de cada degrau são **do produto** (curva).
5. Duas câmaras no MVP: **fria** e **etileno**. Sem sensor, sem Google Agenda, sem SKU por cor.
6. **Perda não é venda a R$ 0** e não é apagar compra. É lançamento próprio (some do estoque, aparece em kg e R$ perdidos).
7. Aviso matinal **sugere**; **não** muda preço sozinho. Queima de preço é ação explícita do dono.
8. A curva é palpite. A pessoa **corrige o estado na mão** se o olho discordar do calendário.
9. Venda de perecível: **estado obrigatório**. Baixa FIFO: o lote **mais antigo naquele estado** (e na câmara de onde se vende — na prática etileno / já madurando).
10. Fuso: `ZoneId` do aparelho. “Dia” = meia-noite às meia-noite local.
11. Voz: mesmo ciclo (segurar → transcrever → revisar → confirmar). Sem confirmar, nada grava.
12. Migração Room **não destrutiva**. Atualizar `docs/database/README.md`. Strings em `strings.xml`.

## Modelo mental (camadas)

| Peça | O que é |
| ------ | --------- |
| **Produto** | Variedade (Nanica). Flag perecível, unidade (kg), preço padrão, **curva em dias**, **preço sugerido por estado**. |
| **Lote** | Quantidade que entrou numa compra (ou saldo restante). Data, produto, câmara, estado atual, quando o relógio da etapa atual começou. |
| **Câmara** | **Fria** (pausa) ou **etileno** (relógio liga). |
| **Estado** | O que se fala ao cliente e o que a venda baixa. |
| **Movimento** | Compra entra lote; transferência fria→etileno; venda sai FIFO; perda sai; correção de estado não muda kg. |

Estoque consultável = soma dos lotes com quantidade restante > 0, agrupável por produto + estado (+ câmara).

---

## Bloco 1 — Cadastro perecível + curva + preço por estado

**Por quê primeiro:** sem curva não há relógio; sem flag o resto do app não sabe quem é banana.

No formulário de produto (`ProdutoFormScreen`), se perecível:

- Interruptor **Perecível**.
- Curva: dias em **verde (já na etileno)**, **verdolenga**, **amarela**, **pintada** até virar o próximo. Podre não tem “dias seguintes”. Dias > 0.
- Preço sugerido por estado (centavos). Padrão: copiar o `PRO_PRECO` para verde/verdolenga/amarela; pintada pode nascer igual até o dono baixar. Podre: preço 0 (não se vende; só perda).

Produto não perecível: UI de curva/preço por estado **não aparece**. Grupo continua só classificação (ex. Banana).

**Pronto quando:** dá para cadastrar Nanica perecível com dias e preços por estado; um produto comum continua igual.

---

## Bloco 2 — Lote na compra + estoque por estado/câmara

**Depende do 1.**

Compra de item perecível **cria lote(s)**: produto, quantidade, data da compra, fornecedor (já no documento), estado inicial **verde**, câmara **fria**. Identificação visível: variedade + data (+ sufixo se a mesma compra tiver mais de um lote). Caixa/pallet **não** é obrigatório neste corte.

Consulta **Estoque**:

- Perecível não é mais uma linha só com saldo total.
- Linhas no mínimo: produto · estado · câmara · quantidade · unidade.
- Total do produto ainda pode aparecer no detalhe (soma dos estados).
- Detalhe (`EstoqueDetalheScreen`): lotes, kg restantes, estado, câmara, “vira o próximo estado em X dias” (só se estiver na etileno).

`vw_produto_estoque` (ou view nova) precisa refletir **lote/estado**. Saldo total do não-perecível permanece compras − vendas (− perdas, quando o bloco 5 existir). **Não** misturar perda com venda.

Transferência **fria → etileno**: ação explícita no detalhe do lote (quantidade parcial permitida: parte do lote vai, o resto fica na fria como lote restante). Ao entrar na etileno, **inicia o relógio** da etapa verde.

Capacidade da etileno (kg) e/ou **kg que posso soltar por dia**: campo de negócio em Configurações (seção nova, sem quebrar transcrição nem backup). Usado no bloco 8; já pode nascer aqui.

**Pronto quando:** comprar 800 kg de nanica gera lote verde na fria; estoque mostra isso; mover 200 kg para etileno deixa 600 na fria e 200 na etileno com relógio ligado.

---

## Bloco 3 — Relógio + correção visual

**Depende do 2.**

Uma vez por dia (abertura do app e/ou alarme matinal do bloco 6), **avançar estados** dos lotes na etileno conforme dias da curva e data de início da etapa.

- Fria: não avança.
- Etileno: verde → verdolenga → amarela → pintada → podre.
- Podre: permanece podre até o dono lançar **perda** (bloco 5). Não some sozinho.

**Conferência visual:** no lote, ação “marcar como [estado] agora”. Recalcula o início da etapa para **hoje**. Não altera kg. Motivo opcional não é obrigatório no MVP.

**Pronto quando:** lote 4 dias na etileno com curva verde=3 vira verdolenga sozinho; o dono pode forçar “já está amarela”.

---

## Bloco 4 — Estado na venda + FIFO

**Depende do 2 (idealmente 3).**

No item de venda perecível, **estado obrigatório** (chips/lista). Sem estado, não grava.

- Quantidade não pode exceder o saldo **daquele produto naquele estado**.
- Preço inicial: tabela daquele estado; o dono altera na hora (snapshot, regra atual).
- Baixa: FIFO nos lotes daquele estado (mais antigo primeiro). Pode rachar quantidade em mais de um lote por baixo dos panos; a UI do item continua um produto + estado + kg + preço.
- Não perecível: venda igual à de hoje (saldo total).
- Voz: “vende vinte quilos de nanica amarela pra padaria” — estado entra no draft e na revisão.

Cliente com ponto preferido (bloco 9): se preenchido, **sugerir** esse estado ao escolher o cliente; a pessoa pode trocar.

**Pronto quando:** vender nanica amarela baixa só amarela; verde permanece; estoque negativo naquele estado é recusado.

---

## Bloco 5 — Perda manual

**Depende do 2.**

Lançamento **Perda** (entrada pelo detalhe do lote/estoque e/ou atalho no Início/Consultas, sem quinta aba):

- Produto, lote ou estado, quantidade, data, motivo simples: **apodreceu**, **manuseio**, **outro**.
- Confirmação. Some do lote. **Não** gera venda, **não** gera recebimento.
- Movimentação no detalhe de estoque: tipo **perda** (além de entrada/saída atuais).

**Pronto quando:** 10 kg podres saem do estoque e não aparecem em “vendas de hoje”.

---

## Bloco 6 — Aviso matinal

**Depende dos 3–5.**

Não é evento da Agenda. Canal de notificação **próprio** (não misturar com `banavita_agenda`), disparo cedo (horário em Configurações, padrão 07:00).

Conteúdo:

- O que está **pintada** (priorizar venda / sugerir baixar preço).
- O que **é podre** ou **vira podre hoje/amanhã**.

Toque na notificação abre Consultas Estoque (recortado perecíveis críticos) ou o cartão do Início. **Não** aplica preço sozinho.

Permissão de notificação: reutilizar o padrão da agenda (pedir quando fizer sentido; se negar, o resumo ainda aparece no Início).

**Pronto quando:** de manhã o celular avisa pintada/podre; o app aberto no Início mostra o mesmo recorte.

---

## Bloco 7 — Início: cartão Perecíveis do dia

**Depende do 6.**

Novo cartão no painel, no espírito de Estoque / Próximo evento:

- Ex.: “25 kg nanica pintada · 12 kg a apodrecer amanhã” ou vazio: “Nenhum perecível crítico hoje”.
- Toque: estoque filtrado nesses lotes.

**Pronto quando:** sem abrir Consultas, o dono vê o recorte do dia.

---

## Bloco 8 — Fila da câmara de etileno

**Depende do 2 e da capacidade em Configurações.**

Tela ou seção no detalhe/estoque: **quanto kg na fria** vs. **quanto ainda cabe / posso soltar hoje** (capacidade − o que já entrou na etileno hoje, ou capacidade total da câmara — deixe a regra explícita na UI: “kg que a etileno comporta” **e** se for o caso “já em maturação”).

Lista: lotes na fria (mais antigos primeiro). Ação: **soltar para etileno** (quantidade ≤ restante do lote e ≤ folga da capacidade).

Sem isso o “liberar espaço” continua no feeling.

**Pronto quando:** dá para ver a fila e mover kg para a etileno respeitando o teto configurado.

---

## Bloco 9 — Cliente × ponto preferido

**Depende do 4.**

No cadastro de cliente, campo opcional **Ponto preferido** (verdolenga, amarela, pintada…; verde raro, mas permitido). Vazio = sem sugestão.

Na venda, ao escolher o cliente, o estado do item perecível **sugere** esse ponto. Voz: se o cliente resolver e o estado não for falado, usar a preferência na revisão (editável).

**Pronto quando:** padaria com preferência pintada já abre a venda nesse estado.

---

## Bloco 10 — Previsão de oferta (3–7 dias)

**Depende do 3.**

Consulta (modo Estoque ou subvisão **Previsão**): para cada produto perecível, kg **esperados** em cada estado nos próximos 3 e 7 dias, com base nos lotes na etileno + curva (fria **não** entra na previsão de amadurecer até ser solta).

É leitura. Não simula vendas futuras.

**Pronto quando:** dá para responder “quinta terei quantos kg de nanica amarela?” pelo que já está madurando.

---

## Bloco 11 — Queima de preço do dia

**Depende dos 1, 4 e 6.**

No aviso matinal / cartão do Início / lote pintada (ou estado configurado como crítico): ação **Aplicar preço de queima hoje**.

- Grava um preço promocional **daquele produto + estado** válido até o fim do **dia local**.
- Vendas daquele estado no dia usam esse preço como padrão (ainda editável no item).
- No dia seguinte some sozinho; volta a tabela do cadastro.
- Não altera `PRO_PRECO` permanente nem a curva.

**Pronto quando:** um toque no alerta aplica o desconto da pintada só hoje; amanhã o preço volta.

---

## Bloco 12 — Relatório de perda

**Depende do 5.**

Consulta (visão ou modo simples, sem quinta aba): no período (hoje / semana / mês):

- kg comprados, kg vendidos, kg perdidos, **% perda** (perdidos / entradas do recorte, regra visível na tela).
- Quebra por produto/variedade.
- R$ perdido (qty × preço de referência do estado ou custo do lote — escolha **uma** regra, documente no log e na UI: recomendado **custo médio do lote/compra** se existir; senão preço de tabela do estado).

Não misturar com o total de vendas do Início.

**Pronto quando:** dá para ver % e R$ perdido na semana por nanica vs prata.

---

## Bloco 13 — Pedido / encomenda (por último)

**Só depois dos blocos 4 e 10** (estoque por estado confiável + previsão).

Pedido não é venda. Cabeçalho: cliente, data de entrega, itens (produto + estado + quantidade). Estados: **aberto**, **atendido** (vira venda ou baixa à parte — **atender gera venda** com os mesmos itens, se houver saldo; se faltar, recusar ou atender parcial com confirmação). Sem financeiro novo: a venda gerada segue à vista/a prazo como hoje.

Lista de encomendas da semana no Início ou em Consultas. Voz neste bloco é opcional; se fizer, mesmo ciclo de revisão.

**Fora deste bloco:** rota de caminhão, carga, WhatsApp automático.

**Pronto quando:** “segunda, 50 kg nanica verdolenga para a padaria” fica registrado e, no dia, atender gera a venda se houver saldo daquele estado.

---

## Voz (ao longo dos blocos)

Quando a tela ganhar o conceito, o parser do contexto correspondente extrai também:

| Tela | Exemplos |
| ------ | ---------- |
| Venda | estado (“amarela”, “verdolenga”, “pintada”) |
| Estoque | produto + estado; “zerado”; previsão (“quinta”, “semana”) |
| Início | atalho para perecíveis críticos / estoque |
| Perda | produto + estado + quantidade + apodreceu |
| Encomenda (13) | cliente + produto + estado + quantidade + dia |

Revisão editável. Sem confirmar, nada grava.

---

## Fora de escopo (não fazer)

- Doença / dinâmica diferente de lote doente.
- Sensor, IoT, balança, câmera.
- Um SKU / produto por cor.
- Sincronizar Google Agenda ou calendário do aparelho.
- Mais câmaras além de fria e etileno.
- Relógio andando na fria.
- Perda automática ao virar podre (pode **avisar**; o dono lança a perda).
- Mudança automática de preço sem o toque da queima.
- E-commerce, rota de entrega, repetição de evento.

---

## Diretrizes técnicas

- Schema: bump de versão Room (4+) com `MIGRATION_*` reais. Exportar schema. Sem `fallbackToDestructiveMigration`.
- IDs UUID, datas epoch millis, dinheiro centavos, quantidade `REAL` (kg).
- Repositórios transacionais: compra+lote, venda+baixa FIFO, perda+lote, transferência que parte lote.
- `EstoqueValidator` perecível: saldo **por estado**, não só por produto.
- Hilt: novos DAOs no `DatabaseModule`.
- Relógio: função pura testável (data de início da etapa + curva + hoje → estado). Não só “na UI”.
- FIFO testável com dois lotes do mesmo estado.
- Produtos já cadastrados: perecível = false; estoque antigo continua válido (compras − vendas) até haver perda (aí subtrai perda também no não-perecível, se perda for permitida só para perecível — **perda só perecível** neste corte).
- Backup/restore já existente deve incluir as tabelas novas (o arquivo é o SQLite inteiro; não excluir tabelas). Após restore, reagendar avisos matinais além da agenda.
- Testes de unidade/integração Room (Robolectric / in-memory) da curva, transferência, FIFO, perda, queima com validade no dia, previsão ignorando fria.

## Arquivos e pontos de partida

- Banco: `BanavitaDatabase`, `DatabaseModule`, `docs/database/README.md`, `app/schemas/`
- Produto: `ProdutoEntity`, `ProdutoFormScreen`, `ProdutoRepository`
- Cliente: `ClienteEntity`, form de cliente (ponto preferido)
- Compra/venda: `CompraForm*`, `VendaForm*`, `VendaRepository`, `EstoqueValidator`
- Estoque: `EstoqueRepository`, `EstoqueDao`, `vw_produto_estoque`, `ConsultasScreen` modo estoque, `EstoqueDetalheScreen`
- Início: `HomeScreen`, `HomeUiState`, `HomeViewModel`
- Configurações: `SettingsScreen` (nova seção; não quebrar transcrição nem backup)
- Notificação: `ReminderScheduler` / `AgendaNotifications` como **referência** de alarme; canal **novo** para perecíveis
- Voz: `VoiceScreenContext`, `ExtractionPipeline`, `VendaExtractionParser`, `ExtractionReviewSheet`
- Mockups: regenerar produto, estoque, detalhe, venda, início, settings, e criar os das telas novas (fila etileno, perda, previsão, relatório, encomenda). Atualizar `docs/ui/README.md`.

## Critérios de aceite (entrega completa)

- [ ] Nanica é um produto; estados não são produtos extras.
- [ ] Compra entra lote verde na fria; relógio parado.
- [ ] Soltar para etileno inicia a curva; capacidade respeitada.
- [ ] Estados avançam na etileno pelos dias do cadastro.
- [ ] Correção visual muda estado sem mudar kg.
- [ ] Estoque lista produto × estado × câmara.
- [ ] Venda exige estado e baixa FIFO daquele estado.
- [ ] Perda some do estoque e não conta como venda.
- [ ] Aviso matinal + cartão no Início para pintada/podre.
- [ ] Queima de preço vale só no dia local.
- [ ] Previsão 3–7 dias ignora o que está na fria.
- [ ] Cliente com ponto preferido sugere o estado na venda.
- [ ] Relatório: kg e % perda no período, por variedade.
- [ ] Encomenda é pedido; atender gera venda se houver saldo.
- [ ] Não perecível inalterado na operação diária.
- [ ] Migração não apaga dados da v2.
- [ ] Mockups + `docs/database/README.md` + log em `docs/Modifications/YYYY-MM-DD-v3-estoque-pereciveis.md` (data do dia da implementação).

## Ordem de trabalho (repetir)

1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10 → 11 → 12 → 13.

Se o tempo acabar no meio, **pare num bloco pronto no aparelho**. Não entregue relatório sem perda, nem venda sem estado, nem relógio sem lote.

Se algo do prompt conflitar com o código, preserve as regras de negócio e documente o desvio técnico no log. Não “simplifique” criando um produto por cor nem deixando o relógio andar na câmara fria.
