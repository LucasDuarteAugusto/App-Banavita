# Prompt — Agenda: visão de calendário (mês)

Copie o bloco abaixo e cole no agente que for implementar. Não alterar este arquivo no lugar do código.

---

## Papel

Você é o agente de implementação do app Android **Banavita** (Kotlin, Jetpack Compose, Room, Hilt, MVVM/Clean). Implemente a entrega abaixo. Siga `.cursor/skills/android-modern-stack/SKILL.md`, `.cursor/rules/modifications-log.mdc` e `.cursor/rules/ui-mockups.mdc`.

Não invente produto. As regras de negócio deste prompt são decisões de Product Owner e devem ser cumpridas à risca.

## Contexto do produto (hoje)

A Agenda **não** é um modo de Consultas. É um destino próprio (`BanavitaRoutes.AGENDA`), aberto pelo cartão **Próximo evento** no Início, pelo `+` / voz, ou pelo deep link da notificação (`banavita://agenda/edit/{id}`).

Hoje a tela Agenda é **só lista** (`AgendaListScreen`): chips **Hoje / Próximos 7 dias / Todos** e cartões (título + data/hora). Não há calendário. O formulário de evento (`EventoFormScreen`) e os lembretes (`ReminderScheduler`) **não mudam de regra** nesta entrega.

A voz na Agenda usa `VoiceScreenContext.AGENDA`: criar evento (revisão → confirmar) ou aplicar recorte da lista (`AgendaFiltroAplicado`). Sem confirmar, nada grava e nenhum filtro aplica.

## O que construir (escopo)

Substituir a **consulta** da Agenda: a tela deixa de abrir como lista filtrada por chip e passa a ser um **calendário de mês**, no espírito do Google Agenda **somente na visão de mês**.

Fluxo de uso:

1. A pessoa vê o **mês** (grade de dias).
2. Escolhe **mês e ano** (não fica presa no mês corrente).
3. Dias com compromisso ficam **marcados**.
4. Ao **tocar um dia**, aparece a **lista daquele dia**, ordenada pelo horário marcado.
5. Tocar um item da lista abre o evento (editar), como hoje.

Continua uma única rota `AGENDA`. Não criar aba nova. Não criar visão de semana, dia, 3 dias ou ano. Não sincronizar com Google Agenda nem com o calendário do aparelho.

Visual: mesma linguagem do app (Compose Material3, top bar verde do shell, FABs de voz + novo). Não copiar a UI pixel a pixel do Google; copiar o **modelo mental**: mês na grade → dia → lista daquele dia.

### 1. Cabeçalho do calendário (mês e ano)

- Título visível do mês/ano em português, ex.: **setembro de 2026**.
- Ao tocar o título (ou um controle equivalente óbvio), a pessoa **escolhe mês e ano** (seletor, não digitar na mão).
- Setas **mês anterior** / **mês seguinte** (como no Google Agenda). Trocar de mês **não** apaga eventos; só muda a grade.
- Ao abrir a Agenda, o mês inicial é o **mês de hoje** no fuso do aparelho. Dia pré-selecionado: **hoje**.

### 2. Grade do mês

- Semana começa no **domingo** (padrão do Google Agenda em pt-BR).
- Cabeçalho dos dias: D S T Q Q S S (ou equivalente curto em `strings.xml`).
- Dias do mês anterior/seguinte que completam a grade aparecem **esmaecidos**; toque neles **muda para aquele mês** e seleciona o dia.
- **Hoje** tem destaque visual claro (círculo/contorno), mesmo sem evento.
- Dia **selecionado** tem destaque diferente de “hoje”.
- Dia **com evento(s)**: marcador discreto (ponto ou badge de quantidade). **Não** listar títulos dentro da célula — a grade é pequena no celular.
- Dia **sem evento**: sem marcador; ainda é tocável.

### 3. Lista do dia (só depois do toque / seleção)

A lista **não** é mais o conteúdo principal da tela. Ela aparece **abaixo da grade**, no mesmo scroll da tela (calendário em cima, compromissos do dia embaixo).

- Ordenação: **horário crescente** (`EVE_DATAHORA` ASC). Dois eventos no mesmo horário: ordem estável (ex. título).
- Cada linha: **título** e **hora** (não precisa repetir a data, já está no dia). Se houver cliente ou fornecedor vinculado, pode ir no subtítulo; não é obrigatório no MVP se o card atual não tiver.
- Tocar o card → editar o evento (rota atual `agenda/edit/{id}`).
- Dia **sem eventos**: mensagem vazia clara, ex. “Nenhum compromisso neste dia.” O `+` continua disponível.
- Dia **com eventos**: a lista substitui o vazio. Não misturar eventos de outros dias nessa lista.

Abrir a Agenda já mostra a lista de **hoje** (se o mês for o corrente). Não exigir um segundo toque em “hoje” para ver o que tem hoje.

### 4. Criar evento a partir do calendário

- FAB `+` **Novo evento** permanece.
- Se um dia está selecionado, o formulário **já vem com essa data** (hora: manter o padrão atual do form, se houver; senão um horário razoável, ex. 09:00). A pessoa pode mudar data/hora no form.
- Mic na tela do calendário: mesmo ciclo (segurar → transcrever → revisão → confirmar). Criar evento por voz **não** some.
- Formulário de edição, exclusão, lembrete e notificação: **sem mudança de regra**.

### 5. Início e notificação (não redesenhar)

- Cartão **Próximo evento** no Início permanece: um compromisso, não a grade.
- Toque nesse cartão: se houver próximo evento, pode continuar abrindo o evento **ou** abrir a Agenda já no **mês e dia daquele evento**. Prefira **abrir a Agenda no dia do próximo evento** (mês certo + dia selecionado + lista daquele dia), para o calendário ser o hub. Se hoje o código abre direto o form, mude para o calendário no dia do evento — o dono vê o contexto do mês, não só o formulário.
- Deep link da notificação **continua abrindo o evento** (form), não o calendário.

### 6. Voz depois desta mudança

Os chips **Hoje / Próximos 7 dias / Todos** **saem da UI**. A consulta visual é o mês.

Mapear a voz antiga de “filtro de lista” assim (ainda com revisão e confirmar):

| O que a pessoa fala (intenção de ver, não criar) | O que o app faz ao confirmar |
| --- | --- |
| “hoje” / “agenda de hoje” | Mês corrente, dia = hoje, lista daquele dia |
| “amanhã” | Mês/dia de amanhã (troca o mês se virar o mês) |
| “próximos 7 dias” / “esta semana” | Permanece na **visão de mês**; seleciona **hoje**. Não inventar visão de semana. Se quiser, Snackbar curto: a agenda é por mês; os pontos marcam os dias com compromisso. |
| Criar (“lembrar visita João amanhã às 15”) | Igual hoje: cria o evento. Depois de confirmar, a Agenda deve **mostrar o mês/dia do evento criado**. |

Não use voz para pular para um mês distante neste corte, salvo se a data vier no próprio evento criado.

`AgendaRecorte` pode permanecer no domínio só se ainda servir ao parser; a UI do calendário **não** mostra esses três chips. Se o enum virar morto na UI, ajuste o parser para selecionar **uma data** em vez de recorte de lista.

### 7. O que não fazer

- Visão de semana, dia, 3 dias, ano ou agenda contínua estilo “schedule” do Google.
- Sincronizar Google Calendar / calendário do sistema.
- Eventos recorrentes (toda segunda, todo mês).
- Arrastar evento na grade.
- Colorir célula com o título do evento.
- Manter os chips Hoje / 7 dias / Todos **junto** com o calendário (é substituição, não os dois).
- Mudar schema Room só para o calendário.
- Mudar regras de lembrete/notificação.

## Diretrizes técnicas

- Fuso: `ZoneId` do aparelho (já usado em `PeriodRanges` / parsers). Um “dia” é meia-noite à meia-noite local, não UTC solto.
- Não carregar a grade evento a evento na UI: o ViewModel expõe o mês visível, o dia selecionado, o conjunto de **dias do mês que têm evento** (para os pontos) e a **lista do dia selecionado**.
- `EventoDao.observeAll()` já ordena por `EVE_DATAHORA ASC`. Filtrar no ViewModel por intervalo do mês e do dia, ou query por intervalo — o que for mais limpo, sem mudar regra de negócio.
- Camadas: UI só estado e eventos; sem `java.util.Calendar` espalhado na Composable se o projeto já usa `java.time`.
- Strings em `res/values/strings.xml`. Sem texto hardcoded.
- Acessibilidade: dia selecionado, hoje, e “tem compromisso” precisam de `contentDescription`.
- Testes de unidade da lógica de mês (grade incluindo dias esmaecidos, intervalo do dia, ordenação da lista, mês seguinte/anterior na virada de ano). Sem depender de device.
- Formulário: aceitar data inicial quando vier do calendário (query arg na rota de novo evento **ou** estado compartilhado). Não quebrar edição nem o deep link.

## Arquivos e pontos de partida

- Lista atual: `app/src/main/java/br/com/banavita/app/ui/agenda/AgendaListScreen.kt`, `AgendaListViewModel.kt`, `AgendaUiState.kt`
- Form: `EventoFormScreen.kt`, `EventoFormViewModel.kt`
- Rotas: `BanavitaRoutes.AGENDA`, `AGENDA_FORM_NEW`, `eventoEdit`
- Início: `HomeScreen.kt` / `HomeViewModel.kt` / `HomeNavEvent`
- Voz: `VoiceScreenContext.AGENDA`, `EventoExtractionParser`, `ConfirmResult.AgendaFiltroAplicado` / `EventoCriado`
- DAO: `EventoDao` (`ORDER BY EVE_DATAHORA ASC`)
- Mockups: substituir o sentido de `docs/ui/mockups/agenda-list.png` (calendário de mês + lista do dia). Atualizar `docs/ui/README.md`. Se o PNG antigo não representar mais a tela, regenere-o (não deixe mockup de chips mentindo).

## Critérios de aceite

- [ ] Abrir Agenda mostra **calendário do mês atual**, não a lista com chips Hoje / 7 dias / Todos.
- [ ] Dá para ir a outro mês e outro ano pelo seletor e pelas setas; dezembro → janeiro troca o ano.
- [ ] Dias com evento têm marcador; dias sem evento não têm.
- [ ] Tocar um dia com eventos mostra **só** esses eventos, **ordenados por horário**.
- [ ] Tocar um dia sem eventos mostra vazio daquele dia, sem listar o resto do mês.
- [ ] Tocar um evento da lista abre a edição.
- [ ] `+` abre novo evento **na data do dia selecionado**.
- [ ] Início → Próximo evento leva à Agenda **naquele dia/mês**.
- [ ] Notificação continua abrindo o **formulário** do evento.
- [ ] Criar por voz continua com revisão; após confirmar, o calendário foca o dia criado.
- [ ] “hoje” por voz (só consulta) seleciona hoje e a lista do dia.
- [ ] Lembretes e schema do banco inalterados.
- [ ] Mockup da Agenda regenerado; `docs/ui/README.md` atualizado.
- [ ] Log em `docs/Modifications/YYYY-MM-DD-agenda-calendario-mes.md` (data do dia da implementação) com Criado/Alterado/Removido e o que conferir na IDE.

## Ordem sugerida de trabalho

1. Estado do calendário (mês visível, dia selecionado, dias com evento, lista do dia) + testes de data.
2. Grade Compose + seletor de mês/ano + setas.
3. Lista do dia + navegação para form (criar com data preenchida).
4. Ajuste Início + voz (sem chips).
5. Mockup + log de modificações.

Se algo do prompt conflitar com o código, preserve as regras de negócio (mês → dia → lista por hora) e documente o desvio técnico no log. Não recoloque os chips “para simplificar”.
