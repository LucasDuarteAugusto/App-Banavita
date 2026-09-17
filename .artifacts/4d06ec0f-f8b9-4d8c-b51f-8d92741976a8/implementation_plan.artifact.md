# Atualização de Ícone e Som de Lembrete

Este plano descreve a substituição do ícone do aplicativo e a implementação de um som personalizado ("Olha a banana") para as notificações de lembrete, com opção de personalização nas configurações.

## Propostos

### [Recursos e Ativos]

#### [MODIFY] [Icone Banavita.png](file:///C:/projetos-Lucas/App-Banavita/app/src/main/res/mipmap/Icone%20Banavita.png) -> `ic_launcher_banavita.png`
- O arquivo será renomeado para cumprir as regras de nomenclatura de recursos do Android (sem espaços).

#### [NEW] [olha_a_banana.m4a](file:///C:/projetos-Lucas/App-Banavita/app/src/main/res/raw/olha_a_banana.m4a)
- O arquivo será movido de `assets/sons/` para `res/raw/` para ser acessível via URI de recurso para canais de notificação.

### [Android Manifest]

#### [MODIFY] [AndroidManifest.xml](file:///C:/projetos-Lucas/App-Banavita/app/src/main/AndroidManifest.xml)
- Atualizar `android:icon` e `android:roundIcon` para `@mipmap/ic_launcher_banavita`.

### [Persistência de Configurações]

#### [MODIFY] [AppSettingsLocalDataSource.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt)
- Adicionar chave `PREF_RINGTONE_URI`.
- Adicionar métodos `getRingtoneUri()` e `setRingtoneUri(uri: String)`.

#### [MODIFY] [AppSettingsRepository.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt)
- Expor métodos para ler e gravar a URI do toque.

### [UI de Configurações]

#### [MODIFY] [SettingsUiState.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt)
- Adicionar `selectedRingtoneName: String?`.

#### [MODIFY] [SettingsViewModel.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt)
- Adicionar lógica para buscar o nome do ringtone atual.
- Adicionar método `onRingtoneSelected(uri: Uri)`.

#### [MODIFY] [SettingsScreen.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/ui/settings/SettingsScreen.kt)
- Adicionar seção "Notificações".
- Implementar seletor de toque usando `rememberLauncherForActivityResult` com `RingtoneManager.ACTION_RINGTONE_PICKER`.

### [Notificações]

#### [MODIFY] [AgendaAlarmReceiver.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/core/reminder/AgendaAlarmReceiver.kt)
#### [MODIFY] [PereciveisAlarmReceiver.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/core/reminder/PereciveisAlarmReceiver.kt)
- Atualizar `ensureChannel` para configurar o som padrão (`res/raw/olha_a_banana`) no canal.
- Nota: Como os canais são imutáveis após criados para som, pode ser necessário recriar o canal ou aceitar que o som só mudará para novos usuários ou se o app for reinstalado (limitação do Android). *Alternativa:* Usar `setSound` no Builder da notificação se a prioridade for alta o suficiente, mas o canal é o local correto para o padrão.

## Plano de Verificação

### Testes Manuais
- Abrir o app e verificar o novo ícone na gaveta de apps.
- Ir em Configurações e alterar o toque.
- Verificar se o toque selecionado é salvo e exibido.
- Forçar uma notificação (ex: lembrete de agenda) e verificar se o áudio "Olha a banana" toca por padrão.
