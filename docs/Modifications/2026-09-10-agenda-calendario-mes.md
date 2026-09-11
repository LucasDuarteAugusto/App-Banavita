# Agenda: calendário de mês

## Resumo

A tela Agenda deixa de ser lista com chips Hoje / 7 dias / Todos e passa a ser um calendário de mês (semana começando no domingo), com lista só do dia selecionado. O `+` abre o formulário já na data escolhida; o Início abre a Agenda no dia do próximo evento; a voz de filtro seleciona um dia em vez de recortar a lista.

## Arquivos

| Status | Caminho |
| ------ | ------- |
| Criado | `app/src/main/java/br/com/banavita/app/core/time/MonthCalendar.kt` |
| Criado | `app/src/test/java/br/com/banavita/app/core/time/MonthCalendarTest.kt` |
| Criado | `app/src/main/res/drawable/ic_chevron_left.xml` |
| Criado | `app/src/main/res/drawable/ic_chevron_right.xml` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/agenda/AgendaUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/agenda/AgendaListViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/agenda/AgendaListScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/agenda/EventoFormViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/agenda/EventoFormScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/navigation/BanavitaRoutes.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/extraction/V2Parsers.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/ExtractionRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/domain/model/ConsultaModels.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/domain/model/extraction/ExtractionModels.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/common/DateTimeInputParser.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/common/Formatters.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/common/BanavitaIcons.kt` |
| Alterado | `app/src/main/res/values/strings.xml` |
| Alterado | `app/src/test/java/br/com/banavita/app/core/extraction/ExtractionParsersTest.kt` |
| Alterado | `docs/ui/mockups/agenda-list.png` |
| Alterado | `docs/ui/README.md` |

## O que conferir na IDE

1. Sync Gradle / rebuild.
2. Abrir Agenda: mês atual, hoje selecionado, lista de hoje (sem chips).
3. Setas e toque no título (seletor mês/ano); dezembro → janeiro troca o ano.
4. Dia com ponto mostra só os eventos daquele dia, por horário; dia vazio: “Nenhum compromisso neste dia.”
5. `+` abre novo evento com a data do dia selecionado; hora continua a atual.
6. Início → Próximo evento abre a Agenda naquele dia; notificação continua no formulário.
7. Voz: “hoje” / “amanhã” selecionam o dia; “próximos 7 dias” / “esta semana” ficam no mês com Snackbar; criar por voz foca o dia criado.
8. Rodar `MonthCalendarTest` e `ExtractionParsersTest`.
