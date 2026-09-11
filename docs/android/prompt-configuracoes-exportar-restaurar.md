# Prompt — Configurações: seções + exportar/restaurar banco

Copie o bloco abaixo e cole no agente que for implementar. Não alterar este arquivo no lugar do código.

---

## Papel

Você é o agente de implementação do app Android **Banavita** (Kotlin, Jetpack Compose, Room, Hilt, MVVM/Clean). Implemente a entrega abaixo. Siga `.cursor/skills/android-modern-stack/SKILL.md`, `.cursor/rules/modifications-log.mdc` e `.cursor/rules/ui-mockups.mdc`.

Não invente produto. As regras de negócio deste prompt são decisões de Product Owner e devem ser cumpridas à risca.

## Contexto do produto

O Banavita é **offline**. O negócio (grupos, produtos, clientes, fornecedores, vendas, compras, estoque, financeiro, agenda) vive no Room: arquivo `banavita.db` (`BanavitaDatabase.DATABASE_NAME`), schema versão **3**, com migrações `MIGRATION_1_2` e `MIGRATION_2_3` e **sem** `fallbackToDestructiveMigration`.

A licença de ativação **não** está no Room: fica em `EncryptedSharedPreferences` (`ActivationLocalDataSource`). Preferências de transcrição e chave Groq também **não** são o banco de negócio.

**Atualizar o APK por cima** (mesmo pacote, mesma assinatura) já preserva o banco. O problema de produto é **desinstalar**, trocar de celular ou formatar: a pasta do app some. A solução **não** é o SQLite “viver” em Downloads. A solução é **cópia de segurança explícita**, escolhida pelo usuário, com restaurar explícito.

## O que construir (escopo)

### 1. Reorganizar a tela Configurações por tipos

Hoje a tela (`SettingsScreen`, rota `BanavitaRoutes.SETTINGS`, ícone na TopAppBar do Início) já tem a seção **Modelo de transcrição** ( Groq vs local ). O subtítulo global ainda fala só de voz (`settings_subtitle`).

Passar a ter **duas seções visíveis**, nesta ordem:

1. **Modelo de transcrição** — comportamento atual intacto (cards, rádio, Groq desabilitado sem chave, strings existentes).
2. **Banco de dados** — nova seção com Exportar e Restaurar.

Ajustar o texto de cabeçalho da tela para não parecer que Configurações é só transcrição (ex.: subtítulo genérico, ou um subtítulo por seção). A seção de transcrição já tem título (`settings_transcription_section`); não quebrar esse bloco.

Não criar novas rotas/abas. Continua uma única tela Configurações, com scroll.

Visual: mesma linguagem da tela atual (Compose Material3, cards, espaçamento 16.dp, top bar verde do shell). Não redesenhar o app.

### 2. Seção Banco de dados

Textos em português, em `strings.xml`. Intenção para o usuário leigo:

- **Exportar dados:** “Salva uma cópia dos cadastros, vendas, compras, estoque, financeiro e agenda num arquivo que você escolhe (pasta, Drive, etc.).”
- **Restaurar dados:** “Substitui os dados atuais do aplicativo pelos do arquivo escolhido.”

Dois botões claros:

- **Exportar dados**
- **Restaurar dados**

Restaurar **sempre** pede confirmação antes de apagar o banco atual. Mensagem do diálogo deve deixar explícito que a operação **substitui tudo** o que está no aparelho agora; não mescla.

Estados de UI: carregando (export/restore em andamento, botões desabilitados), sucesso e erro (Snackbar ou equivalente já usado no app). Não falhar em silêncio.

## Regras de negócio (obrigatórias)

1. O banco de trabalho **continua dentro do app** (Room / armazenamento privado). Exportar gera uma **cópia**. Nunca gravar o SQLite ao vivo em pasta pública.
2. O usuário **escolhe o local** do arquivo (Storage Access Framework: criar/abrir documento). Não usar `getExternalFilesDir` como destino “que sobrevive à desinstalação” — essa pasta também some na desinstalação.
3. **Não restaurar automaticamente** na primeira abertura. Só quando a pessoa tocar em Restaurar e confirmar.
4. Restaurar **substitui** o banco atual; não faz merge.
5. **Não incluir** no backup:
   - licença / ativação (`EncryptedSharedPreferences` de ativação);
   - chave Groq / segredos;
   - modelo de voz (recria pelo APK).
6. **Pode** incluir só o banco de negócio Room (`banavita.db` e o que for necessário para cópia consistente, p.ex. checkpoint WAL). Preferências de transcrição **não** precisam ir no MVP.
7. Backup de **schema mais novo** que o app instalado: **recusar**, com mensagem para atualizar o aplicativo primeiro.
8. Backup de **schema mais antigo** (ou igual): restaurar e deixar o Room **aplicar as migrações** existentes. Não destruir dados.
9. Depois de restaurar com sucesso: **reagendar lembretes** da agenda (`ReminderScheduler.rescheduleAll`), no mesmo espírito do `BootCompletedReceiver`. Sem isso o banco volta e os alarmes não.
10. Depois de restaurar, o restante do app precisa **enxergar o banco novo** (fechar/reabrir Room de forma segura; não deixar DAOs apontando para arquivo morto).
11. Licença: restaurar dados **não** reativa o app. Se o usuário desinstalou, ele libera o aparelho de novo e depois restaura. Não tentar copiar prefs criptografadas (a MasterKey some na desinstalação).

## Diretrizes técnicas (para não errar o produto)

- Copiar SQLite com o banco **em checkpoint** (WAL/SHM). Não copiar só o `.db` no meio de escrita.
- Durante export/restore, não deixar o usuário gravar vendas em paralelo (UI em loading / bloquear botões).
- Formato do arquivo: um pacote único e reconhecível (ex. extensão própria tipo `.banavita` ou zip contendo o db + metadados: `schemaVersion`, data de exportação, nome do app). O metadado de versão de schema é obrigatório para a regra 7.
- Nome sugerido ao exportar: `Banavita-backup-AAAA-MM-DD` (data do dia).
- Validar o arquivo na restauração (não é um PDF aleatório; schema/versão presentes; arquivo de banco legível).
- Seguir camadas: não colocar I/O de arquivo na Composable. Repository (ou use case) + ViewModel; UI só estado e eventos.
- Hilt: o `BanavitaDatabase` hoje é `@Singleton` via `DatabaseModule`. O restore provavelmente exige fechar e reabrir o singleton de forma controlada — resolva isso sem quebrar o grafo (ex. holder/`Provider` do database, ou estratégia equivalente limpa). Não espalhar `Room.databaseBuilder` na UI.
- Strings todas em `res/values/strings.xml`. Sem texto hardcoded na UI.
- Testes: pelo menos testes de unidade/integração da lógica de backup (checkpoint, recusar schema mais novo, aceitar schema antigo com migração se for testável sem device). UI Compose: preview ou teste só se o padrão do módulo já fizer isso; não inventar suíte enorme.

## Fora de escopo (não fazer)

- Backup automático no Google Drive / Auto Backup como solução.
- Banco “sempre aberto” em Downloads/Documentos.
- Tela nova de onboarding só para restaurar.
- Cloud, conta, senha no arquivo (MVP sem criptografia extra; o usuário escolhe pasta/Drive).
- Exportar licença, chave Groq ou modelo Whisper.
- Mudar o fluxo de ativação.
- Alterar o schema Room só para esta feature.

## Arquivos e pontos de partida

- UI atual: `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt`, `SettingsUiState.kt`, `SettingsViewModel.kt`
- Strings: `app/src/main/res/values/strings.xml` (`settings_*`)
- Banco: `BanavitaDatabase`, `DatabaseModule`, `docs/database/README.md`
- Agenda após restore: `ReminderScheduler` / `EventoRepository.getPendingReminders` / `BootCompletedReceiver`
- Navegação: não mudar a rota; só conteúdo da tela Configurações
- Mockup: regenerar PNG da tela Configurações em `docs/ui/mockups/` e atualizar `docs/ui/README.md`

## Critérios de aceite

- [ ] Configurações mostra as duas seções: **Modelo de transcrição** e **Banco de dados**.
- [ ] Seleção Groq/local continua igual.
- [ ] Exportar abre o seletor de local, gera um arquivo, mostra sucesso.
- [ ] Restaurar pede confirmação; cancela não altera o banco.
- [ ] Restaurar com arquivo válido substitui cadastros/vendas/compras/agenda; a UI das outras telas reflete os dados novos sem reinstalar o app.
- [ ] Restaurar arquivo de schema mais novo: erro claro, banco atual intacto.
- [ ] Desinstalar o app, instalar de novo, ativar licença, restaurar o arquivo exportado: os dados de negócio voltam.
- [ ] Licença e chave Groq **não** voltam pelo arquivo.
- [ ] Eventos futuros da agenda voltam a notificar após o restore.
- [ ] Log em `docs/Modifications/YYYY-MM-DD-titulo-curto.md` (hoje) com Criado/Alterado/Removido e o que conferir na IDE.
- [ ] Mockup da tela Configurações atualizado.

## Ordem sugerida de trabalho

1. Camada de dados: exportar/restaurar com metadados de schema + checkpoint; testes.
2. Encaixe no Hilt/Room (fechar/reabrir) + reagendar agenda.
3. UI Configurações em duas seções + diálogos/snackbars.
4. Mockup + log de modificações.

Se algo do prompt conflitar com o código, preserve as regras de negócio e documente o desvio técnico no log de modificações — não “simplifique” restaurando em silêncio ou gravando o `.db` em pasta pública.
