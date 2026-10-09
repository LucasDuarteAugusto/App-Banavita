# Dashboards Banavita

Implementação de `Graficos.txt`, com sete painéis Compose alimentados pelo SQLite local. O Dashboard Geral é a nova tela inicial. Vendas, Compras, Estoque & Maturação, Financeiro, Encomendas e Agenda são acessados pelo grid; listas, cadastros, consultas e captura/revisão por voz continuam disponíveis.

## Apresentação e filtros

Cards de indicadores com ícone, gráficos Canvas de linhas, barras, rosca e barras empilhadas, listas com acesso aos registros e insights calculados a partir do período. O grid de painéis também leva ícone. O tema Material 3 acompanha o aplicativo. Os gráficos têm descrições de acessibilidade, seleção de pontos e valores textuais; não exigem biblioteca externa ou rede.

Os seis painéis específicos repetem a captura de voz da home. O botão de adicionar abre a operação principal do painel: nova venda, nova compra, registrar perda, contas a receber, nova encomenda ou novo evento. O comando confirmado navega para o registro ou a consulta correspondente.

Vendas e compras usam Hoje, últimos 7 dias, últimos 30 dias ou período personalizado. Financeiro, encomendas e agenda usam Hoje, próximos 7/30 dias ou personalizado. A data final personalizada é inclusiva, com limite de 366 dias. Comparações comerciais usam o período anterior de mesma duração; uma base zero não gera percentual inventado. Os dias sem movimento aparecem com zero.

Cliente/fornecedor, produto, unidade, estado, etapa e tipo de compromisso são oferecidos conforme o painel. Barras de parceiros e produtos comerciais aplicam o filtro correspondente. Quantidades de KG, UN, DZ, CX MADEIRA e CX PLASTICA permanecem separadas, sem conversão. Gráficos quantitativos de estoque exigem uma unidade. Categorias monetárias/quantitativas extensas são agrupadas em Outros; percentuais e custos médios não são somados.

## Regras dos indicadores

| Painel | Origem e interpretação |
|---|---|
| Geral | Vendas e compras pela data do documento; diferença comercial = vendas − compras; perdas pelo custo registrado, na data da perda. Até quatro itens críticos abrem o detalhe do produto, com quantidade, estado atual → próximo e cor de risco. |
| Vendas | Totais de itens, número distinto de documentos, ticket por operação ou por unidade selecionada, séries diárias, top clientes, variedades, maior/menor operação e margem estimada. A seção de margem destaca as três maiores e as três menores. |
| Compras | Totais e fornecedores; custo médio ponderado = valor comprado / quantidade, somente dentro da unidade selecionada. O resumo inclui o fornecedor com mais operações. |
| Estoque | Perecíveis pelos lotes abertos; demais produtos por compras − vendas, agrupados por unidade histórica do item. Valoração estimada pelas compras da mesma unidade. A lista crítica usa verde, amarelo, laranja e vermelho conforme o estado atual e a projeção em dois dias. |
| Financeiro | Saldo de cada título = valor do documento − baixas. Vencimento determina o período previsto; recebimentos/pagamentos determinam movimentos realizados. Inadimplência é o percentual dos recebíveis em aberto que já venceu. Cada parceiro do top mostra o percentual vencido. O resumo inclui os próximos 7 dias e o saldo final projetado. |
| Encomendas | Etapas, pendências, atrasos calculados pelo horário previsto e quantidades restantes. Valor conhecido usa somente itens com preço; cobertura exibida. O insight de atraso soma o valor conhecido dos itens atrasados. |
| Agenda | Entregas, reuniões, tarefas e lembretes. O dia seguinte e o posterior aparecem como Amanhã e Depois de amanhã. Tarefas pendentes anteriores continuam visíveis. Prioridade ordena tarefas; conclusão pode ser marcada no painel. |

A margem bruta estimada usa o custo médio ponderado das compras da mesma variedade/unidade cuja data seja anterior ou igual à venda. Compras futuras não alteram o custo daquela venda. A receita sem custo conhecido fica fora da margem e a cobertura de receita é exibida. Despesas operacionais não são computadas; o número não representa lucro líquido nem uma apuração contábil de custo FIFO.

O saldo projetado exige saldo inicial e data base, inclui baixas realizadas desde a base e os saldos com vencimento no período. A base deve ser anterior ou igual ao início do período. Títulos vencidos e sem vencimento são apresentados separadamente, sem nova data presumida. Não há movimentos avulsos de caixa nesta versão. Vendas − compras é diferença comercial, distinta do saldo financeiro.

## Vencimentos, encomendas e agenda

Novas vendas/compras a prazo exigem um vencimento único, inclusive na revisão de voz e na confirmação de entrega. Documentos antigos permanecem sem vencimento até edição explícita. Operações à vista continuam gerando a baixa financeira automática.

Encomendas avançam Aberta → Em separação → Pronta → Em rota → Entregue. Avançar etapa não reserva ou baixa estoque. A confirmação de entrega cria a venda e baixa o estoque em uma transação. Pode-se escolher uma quantidade por item; zero mantém o item pendente. Uma entrega parcial conserva o saldo restante e volta à etapa Aberta. A redução por falta de estoque requer a confirmação já existente na UI.

Cada confirmação recebe um UUID de idempotência: repetir a mesma confirmação devolve a venda registrada. Quantidades inválidas ou superiores ao saldo são recusadas. O histórico liga cada item entregue à venda; pedidos com entrega e suas vendas não podem ser editados/excluídos pelos fluxos comuns, preservando as quantidades e os vínculos financeiros.

Entregas vinculadas sincronizam seu evento: pendente nas etapas iniciais, em andamento na rota e concluído quando todo o pedido foi entregue. Na tela do evento, tipo e status ficam desabilitados e a exclusão some; o aviso orienta alterar a etapa pela encomenda. Eventos comuns gravam tipo, status e prioridade. Tarefas concluídas cancelam o lembrete; tarefas reabertas voltam ao agendamento normal.

## Maturação e histórico

CRUA permanece pausada. Após iniciar a maturação, o relógio continua avançando na fria e na etileno, usando a mesma `RipeningClock` e a curva do produto nas projeções e operações. A correção do lote inteiro para um estado diferente de CRUA também inicia o relógio na data da correção, inclusive quando a câmara resultante é a fria. Projeções de Hoje até +5 dias mostram apenas a evolução do estoque atual, sem compras/vendas/transferências futuras. Risco inclui lotes já podres ou projetados para podre em três dias.

Snapshots diários são atualizados ao abrir um painel e pelo aviso matinal de perecíveis. A comparação com média histórica fica indisponível até haver 30 dias observados na janela; dias sem observação não são reconstruídos. Em aparelho desligado ou sem execução não há garantia de snapshot diário. Backup/restore inclui essas tabelas no arquivo SQLite.

## Persistência e migração

Schema 9, com migração explícita 8 → 9 registrada no holder: vencimentos nullable; classificação de eventos; quantidade entregue/unidade nos itens; tabelas `entrega_item`, `estoque_snapshot` e `caixa_base`. Não há migração destrutiva.

Pedidos antigos ATENDIDO passam a ENTREGUE com seus itens integralmente entregues, sem criar novas vendas. Eventos vinculados acompanham esse estado. Lotes iniciados preservam o estado atual e reiniciam a referência do relógio na data local da migração, evitando avanço retroativo provocado pela nova regra da câmara fria. CRUA continua sem relógio.

O JSON exportado pelo Room ainda precisa ser gerado pelo KSP em um ambiente Android configurado; não foi fabricado um hash de schema. O script SQL valida a migração real a partir do schema 3 versionado.

## Validação e revisão no Android Studio

Verificação SQLite: `node tools/dashboard-validation/validate-sql.cjs` (Node 22.13+). Executa as migrações 3 → 9 e 27 consultas extraídas dos fontes, com casos de custo temporal, unidades, baixas, registros antigos, unicidade e FKs. Não substitui a validação de schema do Room.

Foram executados 12 testes Kotlin de períodos/maturação com compilador 2.0.21 e adaptador local de assertions, além da análise sintática de 252 fontes Kotlin e checagem de tipos da camada de dados com declarações substitutas de Room/injeção. Os novos testes Robolectric de entregas parciais/etapas estão em `PereciveisIntegrationTest` e precisam de execução pelo Gradle.

O ambiente da implementação tem Java 8, nenhum SDK Android utilizável e rede bloqueada para distribuição/dependências Gradle. Portanto build Compose, KSP, testes Android/Room, lint e geração do JSON 9 permanecem sem verificação. Em ambiente configurado, executar:

```powershell
./gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug -PskipSpeechModel
```

Revisar migração de uma cópia de banco 8, unidades heterogêneas, documentos antigos sem vencimento, entrega parcial e repetição, conclusão de tarefa/lembretes, filtros personalizados, navegação por voz, tema escuro, fonte ampliada e gráficos em estoque vazio. Os PNGs são mockups com dados ilustrativos, não capturas de execução.
