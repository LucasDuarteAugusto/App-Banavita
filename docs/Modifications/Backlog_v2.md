# Backlog Banavita v2 — Consultas e controle

Documento de produto. Sem implementação neste arquivo.

**Objetivo da v2:** o dono do negócio passa a **saber a situação** (estoque, movimento do período, e depois a receber / a pagar) e, a partir do bloco 9, a **não esquecer o combinado** (agenda com notificação no celular). Consulta não se mistura com cadastro nem com a lista operacional de venda/compra. Agenda não é um modo de Consultas.

**Formato de tela:** um lugar **Consultas**, com **modos** (chips/abas). Mesmo chrome (busca, período quando couber, total no topo). Conteúdo muda conforme a pergunta. Não é uma lista única misturando entidades. Não é uma tela isolada por entidade no menu inferior.

**Navegação:** não abrir quinta aba agora. O Início vira painel de atalhos das consultas do dia (e, no bloco 9, do próximo evento); Consultas e Agenda são destinos próprios. Cadastros fica só para criar/editar mestre. Estoque sai de Cadastros.

**Voz (igual à v1, em toda operação da v2):** o dono não precisa só tocar. Em cada tela de Consultas, Início, financeiro e Agenda vale o mesmo ciclo da v1: **microfone → transcrever → extrair campos no contexto da tela → modal de revisão (editar se estiver errado) → confirmar → o app executa**. Sem confirmar, nada grava e nenhum filtro “fantasma” é aplicado. A intenção vem do **contexto da tela** (como cadastro/venda hoje), não de um classificador genérico solto. Consulta por voz **entra neste corte**, junto de cada bloco — não fica para depois.

---

## Ordem de implementação

Entregar de cima para baixo. Um bloco só começa quando o anterior estiver usável no aparelho.

| Ordem | Bloco | Depende de | Entrega para o negócio |
|-------|--------|------------|-------------------------|
| 1 | Hub Consultas + modos | v1 | Um lugar para consultar |
| 2 | Início como painel | bloco 1 | Consulta do dia a 1 toque |
| 3 | Modo Estoque | bloco 1 | O que tem / o que está acabando |
| 4 | Modo Vendas do período | bloco 1 | Quanto vendi, para quem, o quê |
| 5 | Modo Compras do período | bloco 4 | Quanto comprei, de quem, o quê |
| 6 | Financeiro (receber / pagar) | blocos 4–5 | Fiado e pagamento existem de verdade |
| 7 | Modo A receber | bloco 6 | Quem deve e quanto cobrar |
| 8 | Modo A pagar | bloco 6 | O que devemos ao fornecedor |
| 9 | Agenda (eventos) | v1 (prioridade: depois de 1–8) | Compromissos do negócio na mão |
| 10 | Notificações personalizadas | bloco 9 | Lembrete aparece na barra do celular |
| 11 | Indicadores | blocos 7–8 | Giro, margem, inadimplência — **fora deste corte** |

Blocos 1–5 usam só dados da v1. Blocos 6–8 exigem modelo novo; sem o 6, as consultas 7 e 8 mentem.

Blocos 9–10 (agenda) **não dependem** do financeiro: podem ser puxados para frente se a prioridade mudar. Ficam a partir do 9 porque consultas e controle de caixa vêm antes na entrega.

---

## Bloco 1 — Hub Consultas com modos

**Por que primeiro:** sem o lugar e o padrão de modos, cada consulta vira tela solta e o menu inchado.

### 1.1 Destino Consultas

- Entrada única (rota própria), acessível pelo Início.
- Área de modos: chips **Estoque**, **Vendas**, **Compras**. Os modos **A receber** e **A pagar** só aparecem depois do bloco 6 (não deixar chip morto).
- Um modo ativo por vez. Chrome compartilhado: título, modos, busca, filtros do modo, lista, total no topo.
- Cadastro e edição continuam nas telas atuais. Consulta é leitura; no máximo abre detalhe.

### 1.2 Estoque sai de Cadastros

- Remover o card Estoque do hub de Cadastros.
- A consulta de estoque existente passa a ser o **modo Estoque** (não duplicar lista em dois lugares).
- Cadastros permanece: clientes, fornecedores, produtos, grupos.

### 1.3 Voz no hub

- FAB de microfone no destino Consultas (mesmo padrão visual da v1).
- Interpretação no contexto do hub: mudar de modo (“estoque”, “vendas”, “compras”) e, se houver, uma busca genérica.
- Revisão: mostrar o que foi entendido (modo, busca); permitir editar antes de aplicar.
- Confirmar aplica o modo/busca na tela; descartar não muda nada.

**Pronto quando:** dá para abrir Consultas, trocar de modo e ver Estoque só ali; Cadastros não oferece mais estoque; dá para ir a um modo por voz com confirmação.

---

## Bloco 2 — Início como painel

**Por que agora:** o Início hoje só aponta Nova venda. Consulta precisa estar no caminho diário, sem quinta aba.

### 2.1 Cartões de situação

- Manter atalho **Nova venda**.
- Incluir cartões que abrem Consultas já no modo certo:
  - Estoque (saldo crítico ou “ver estoque”).
  - Vendas do período (ex.: hoje / esta semana — total).
  - Compras do período (mesmo recorte).
- Depois do bloco 7/8: cartões **A receber** e **A pagar** com o total em aberto.
- Cartão sem dado: estado vazio curto (“Nenhuma venda hoje”), não número inventado.

### 2.2 Voz no Início

- Mic no Início. Intenção no contexto da home: **nova venda** (segue o fluxo de voz de venda da v1, com revisão), **abrir consulta** (modo + recorte se falado), ou, depois do bloco 9, **agenda**.
- Revisão editável; confirmar navega ou lança a operação. Não inventar total falado que o banco não tem.

**Pronto quando:** do Início, em um toque **ou por voz**, chega na consulta certa; Nova venda continua o atalho principal de movimento.

---

## Bloco 3 — Modo Estoque (consulta operacional)

**Por que antes de vendas/compras do período:** já existe lista; é o ganho mais barato e o uso de manhã (reposição).

A lista atual (produto, unidade, saldo) continua a base.

### 3.1 Busca e leitura

- Busca por nome do produto.
- Total no topo: quantos produtos com saldo > 0 (ou recorte que o modo usar).
- Ordenação útil no balcão: saldo crescente (acabando primeiro) ou nome — uma padrão, a outra opcional.

### 3.2 Alerta de baixo estoque

- Destaque visual para saldo zerado e para saldo baixo.
- Primeiro corte: regra simples (zero vs. positivo). Mínimo por produto fica para depois, se o cadastro de produto ganhar esse campo.

### 3.3 Detalhe do produto (leitura)

- Toque na linha: saldo e movimentações recentes (compras que entram, vendas que saem), sem editar cadastro por aqui.
- Atalho opcional para o cadastro do produto (fluxo já existente).

### 3.4 Voz no modo Estoque

- Falar o produto (e opcionalmente “zerado” / “acabando”) preenche busca/filtro após revisão.
- Se o produto resolver com confiança, a confirmação pode abrir o **detalhe** daquele item.
- Edição no modal: nome do produto entendido.

**Pronto quando:** dá para achar um produto (toque ou voz), ver o que está zerado e entender de onde veio o saldo, sem passar por Cadastros.

---

## Bloco 4 — Modo Vendas do período

**Por que agora:** o dono precisa do “quanto vendi”, distinto da lista de Vendas (que existe para lançar e corrigir documento).

### 4.1 Recorte de período

- Filtros: hoje, esta semana, este mês, intervalo.
- Total no topo: soma das vendas do recorte (R$).
- Lista: data, cliente (ou sem cliente), valor. Toque abre a venda já existente (somente para conferir; edição continua no fluxo de Vendas).

### 4.2 Quebras (mesmo modo, filtros)

- Por cliente: quanto cada um comprou no período.
- Por produto: quantidade e valor vendidos no período.
- Sem tela nova: seletor de visão dentro do modo Vendas.

### 4.3 Voz no modo Vendas

- Extrair período (“hoje”, “esta semana”, “este mês”, datas) e visão (lista / por cliente / por produto).
- Opcional: filtrar por cliente ou produto cadastrado (resolver contra o banco, como na v1).
- Revisão editável; confirmar aplica filtros e atualiza o total. Não cria venda por aqui (lançamento continua na aba Vendas / voz de venda).

**Pronto quando:** dá para responder “quanto vendi esta semana?”, “quem mais comprou?” e “o que mais saiu?” — por toque ou por voz — com um toque da lista para o documento.

---

## Bloco 5 — Modo Compras do período

**Por que depois de vendas do período:** mesmo padrão; evita inventar UX duas vezes.

### 5.1 Espelho do modo Vendas

- Mesmos períodos e total no topo (R$ comprado).
- Lista: data, fornecedor, valor. Toque abre a compra existente.
- Quebras: por fornecedor e por produto.

### 5.2 Voz no modo Compras

- Espelho do 4.3 (período, visão, fornecedor/produto), com revisão e confirmação.

**Pronto quando:** o modo Compras se comporta como o de Vendas, com as entidades certas, inclusive por voz.

**Corte da primeira entrega da v2 (consultas sobre a v1):** blocos 1–5 fechados e estáveis no aparelho.

---

## Bloco 6 — Financeiro (pré-requisito das consultas 7 e 8)

**Por que não antes:** hoje venda e compra são documento com total, sem pagamento. “Débito do cliente” ou “conta a pagar” sobre isso seria mentira (trata tudo como em aberto).

Este bloco é **movimento**, não consulta.

### 6.1 Regras de negócio (fechar antes de construir tela)

- Venda pode ser: à vista (quitada no lançamento) ou a prazo / fiado (gera valor a receber).
- Compra pode ser: à vista ou a prazo (gera valor a pagar).
- Recebimento: baixa (total ou parcial) do que o cliente deve.
- Pagamento: baixa do que devemos ao fornecedor.
- Saldo em aberto = documento − baixas. Consultas 7 e 8 só leem esse saldo.

Detalhe de parcela, juros e vencimento longo: **não** no primeiro corte do financeiro. Primeiro corte: à vista vs. em aberto + baixa total/parcial.

### 6.2 Onde lançar

- No fluxo de **Nova venda / Nova compra**: informar se foi pago agora ou ficou em aberto.
- Em Consultas (modos 7 e 8): ação de **receber** / **pagar** a partir da linha em aberto — operação curta, não formulário de cadastro.

### 6.3 Voz no lançamento (à vista / a prazo)

- No mic de **Nova venda** / **Nova compra** (já existente na v1): extrair também se foi **pago agora** ou **fiado / a prazo**.
- Revisão editável inclui essa escolha. Confirmar grava o documento com a regra do 6.1.
- Receber/pagar por voz entra nos blocos 7 e 8 (não neste).

**Pronto quando:** uma venda a prazo aparece como em aberto; um recebimento reduz o saldo; à vista não entra em A receber. O mesmo para compra e A pagar. Dá para lançar à vista vs a prazo por voz, com revisão.

---

## Bloco 7 — Modo A receber

**Depende do bloco 6.**

### 7.1 Lista de quem deve

- Por cliente: nome, telefone se houver, saldo em aberto, há quanto tempo (data da venda mais antiga em aberto).
- Total no topo: soma a receber.
- Busca por nome.
- Estado vazio: “Nenhum valor em aberto”.

### 7.2 Detalhe do cliente

- Vendas em aberto, valores, baixas.
- Ação: registrar recebimento.

### 7.3 Voz no modo A receber

- Consulta: “quanto o João está devendo?” → após revisão, busca/abre o detalhe daquele cliente.
- Movimento: “receber cinquenta do João” → rascunho de baixa (cliente resolvido, valor); revisão editável; confirmar registra o recebimento (valor ≤ saldo).
- Sem confirmar, nada baixa.

**Pronto quando:** o dono responde “quanto o João está devendo?” e registra a cobrança sem sair do contexto de consulta — por toque ou por voz.

---

## Bloco 8 — Modo A pagar

**Depende do bloco 6.** Mesmo padrão do bloco 7, do lado do fornecedor.

### 8.1 Lista do que devemos

- Por fornecedor: nome, saldo em aberto, referência de data.
- Total no topo: soma a pagar.
- Detalhe: compras em aberto + ação pagar.

### 8.2 Voz no modo A pagar

- Espelho do 7.3 (consultar saldo do fornecedor; “pagar X ao fornecedor Y”).

**Pronto quando:** o dono responde “quanto devemos ao fornecedor X?” e registra o pagamento — por toque ou por voz.

Com 7 e 8 no ar: voltar ao bloco 2 e ligar os cartões do Início com os totais em aberto.

---

## Bloco 9 — Agenda (eventos)

**Por que a partir daqui:** consultas e financeiro resolvem “qual é a situação”. Agenda resolve “o que não posso esquecer” (visita a cliente, receber carga, pagar fornecedor, fechar a banca). Não é modo de Consultas: é **criar e acompanhar compromisso**, não ler estoque ou saldo.

**Navegação:** destino próprio **Agenda**, acessível por cartão no Início. Sem quinta aba neste bloco. Não misturar eventos na lista de Vendas/Compras nem nos modos de Consultas.

Primeiro corte: evento avulso (data e hora). Sem repetição semanal, sem sincronizar com o calendário do Google/celular.

### 9.1 Destino Agenda

- Lista do que vem: próximos eventos, data/hora, título.
- Recortes úteis: hoje, próximos 7 dias, todos (incluindo passados).
- Estado vazio: “Nenhum evento. Crie um lembrete.”
- Ação clara de **Novo evento**.

### 9.2 Criar e editar evento

Campos do primeiro corte:

- Título (obrigatório).
- Data e hora.
- Observação (opcional).
- Ligação opcional a **cliente** ou **fornecedor** já cadastrado (ajuda na cobrança/visita). Sem isso o evento ainda vale.

Ações: salvar, editar, excluir. Excluir pede confirmação e cancela lembretes daquele evento (bloco 10).

### 9.3 Cartão no Início

- “Próximo evento” (título + quando) ou “Nenhum evento hoje”.
- Toque abre a Agenda (ou o evento, se houver um só no dia).

### 9.4 Voz na Agenda

- Mic na lista e em **Novo evento** (criação; edição manual continua no form, como cadastros da v1).
- Extrair título, data/hora (“amanhã às dez”), observação, cliente ou fornecedor se falado.
- Recortes por voz na lista: “hoje”, “próximos sete dias”.
- Revisão editável; confirmar cria o evento (ou aplica o recorte). Excluir por voz **não** entra neste corte (risco demais sem toque).

**Pronto quando:** dá para criar, ver, alterar e apagar compromissos no app, achar o que é hoje e chegar na Agenda pelo Início — criar e filtrar também por voz. Ainda **não** exige notificação no sistema do celular (isso é o bloco 10).

---

## Bloco 10 — Notificações personalizadas

**Depende do bloco 9.** Sem evento, não há o que avisar.

O lembrete tem que aparecer como **notificação do Android** (barra / gaveta do celular), com o app fechado ou em segundo plano. Aviso só dentro do Banavita não conta como pronto.

### 10.1 Permissão e canal

- Na primeira vez que o usuário ativar um lembrete, pedir permissão de notificação (e explicar por quê).
- Se negar: o evento continua salvo; avisar que o lembrete não vai tocar até permitir, com atalho para as configurações do sistema.
- Notificações do Banavita identificáveis (canal próprio), para o usuário silenciar só a agenda se quiser, sem matar o app.

### 10.2 Lembrete por evento

No formulário do evento (bloco 9.2), o usuário define o aviso:

- Ligar ou não a notificação (padrão: ligada).
- **Quando:** na hora do evento; opcionalmente um antecipado (15 min, 1 h, 1 dia — um conjunto fixo, não texto livre de intervalo).
- **Texto personalizado:** título e corpo que aparecem na notificação. Se vazio, usar o título do evento e a data/hora.

Um evento pode ter o aviso na hora e no máximo **um** antecipado neste corte. Vários disparos livres e repetição ficam para depois.

### 10.3 Comportamento no celular

- No horário combinado, a notificação aparece mesmo com o Banavita fechado (aparelho ligado e com o app instalado; sem depender de internet).
- Toque na notificação abre o evento correspondente.
- Alterar data/hora ou o texto do lembrete reagenda o aviso. Excluir o evento (ou desligar o lembrete) cancela o que ainda não disparou.
- Evento no passado não agenda notificação nova. Não reenviar o mesmo aviso se o usuário já abriu (primeiro corte: um disparo por lembrete).

### 10.4 Voz no lembrete

- Ao criar evento por voz (9.4), aceitar também “me avisa”, “quinze minutos antes”, texto curto do aviso se falado.
- Revisão inclui ligar/desligar o lembrete e o antecipado. Confirmar grava os campos; o disparo no sistema continua o mesmo do 10.3.
- Sem confirmar, não agenda alarme.

### 10.5 Fora deste bloco (não fazer agora)

- Sincronizar com Google Agenda / calendário do aparelho.
- Evento que se repete (toda segunda, todo mês).
- Som, vibração ou ícone personalizados além do padrão do sistema.
- Notificação gerada automaticamente por atraso de cliente ou conta a pagar (isso seria regra do financeiro, não agenda).

**Pronto quando:** o usuário cria um evento (toque ou voz), escolhe texto e horário do aviso, e a notificação aparece na barra do celular no momento certo; toque abre o evento; editar ou apagar não deixa aviso fantasma.

---

## Fora do corte (bloco 11 e adiante)

Não entram com os blocos 1–10. Registrar para não misturar no meio da entrega:

- Indicadores: giro, margem, ranking de inadimplência, produtos parados.
- Quinta aba na barra inferior.
- Lista única misturando estoque, débitos e contas a pagar.
- Duplicar CRUD de cliente/produto/fornecedor dentro de Consultas.
- Estoque mínimo por produto (salvo se o cadastro ganhar o campo de propósito).
- Parcelamento, boleto, juros, conciliação bancária.
- Classificador de voz genérico (uma frase em qualquer tela fazendo qualquer operação). A intenção continua **amarrada ao contexto da tela**, como na v1.
- Excluir evento ou baixa financeira **só** por voz, sem toque (confirmação no modal vale para criar/aplicar, não para apagar neste corte).
- Multi-usuário, nuvem, sincronização.
- Agenda recorrente, sincronização com calendário do celular, vários lembretes livres por evento.

---

## Critérios de pronto da v2

**Consultas e financeiro (blocos 1–8)** estão prontos quando:

1. Existe **um** lugar Consultas, com modos, sem poluir o menu inferior.
2. Estoque não mora mais em Cadastros.
3. Início mostra situação e leva ao modo certo (além de Nova venda).
4. Dá para consultar estoque, vendas do período e compras do período com total no topo.
5. Financeiro existe de verdade; A receber e A pagar refletem saldo em aberto, não o total histórico da venda/compra.
6. Consulta não substitui as listas de Vendas e Compras usadas para lançar e corrigir.
7. Toda tela nova da v2 (Consultas, Início, A receber/A pagar) tem **mic**; o fluxo é o da v1 (captura → interpretação no contexto → revisão **editável** → confirmar). Sem confirmar, filtro não aplica e baixa não grava.

**Agenda (blocos 9–10)** está pronta quando:

8. Existe um lugar Agenda para criar, editar e listar eventos (incluindo vínculo opcional a cliente/fornecedor), também por voz na criação/filtro.
9. O Início mostra o próximo compromisso e abre a Agenda (toque ou voz).
10. Lembretes personalizados disparam como notificação do celular, com o app fechado; toque abre o evento; exclusão/edição cancela ou reagenda o aviso; voz pode preencher o lembrete na criação.

---

## Como usar este backlog na implementação

- Uma história = um item numerado (1.1, 3.2, …), na ordem dos blocos. A história de **voz do bloco** (1.3, 2.2, 3.4, …) entrega **junto** com o bloco, não num epic depois.
- No bloco 1, evoluir o modal de revisão da v1 para **editar campos** (hoje só confirma ou descarta). Os blocos seguintes reusam isso.
- Não abrir bloco 6 enquanto 1–5 não estiverem no aparelho e revisados.
- Não construir telas 7 e 8 “com total da venda” como atalho: espera o 6.
- Não abrir bloco 10 sem o 9 estável (senão a notificação não tem evento para abrir).
- Agenda (9–10) pode ser antecipada se o negócio pedir lembrete antes do financeiro; avisar no log da tarefa se a ordem da tabela mudar.
- Alterações de código da v2 continuam gerando o log diário em `docs/Modifications/YYYY-MM-DD-titulo.md`, como nas outras tarefas.
