# Configurações: tema claro / escuro

## Resumo

A tela Configurações ganhou a seção **Aparência** com seletor Claro/Escuro. A escolha fica no SharedPreferences e aplica o `BanavitaTheme` em todo o app na hora, inclusive barra de status.

## Arquivos

| Status | Caminho |
| ------ | ------- |
| Criado | `app/src/main/java/br/com/banavita/app/domain/model/ThemeMode.kt` |
| Criado | `app/src/test/java/br/com/banavita/app/data/repository/AppSettingsRepositoryTest.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/theme/Theme.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/theme/Color.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/app/BanavitaApp.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/app/BanavitaAppViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/app/AppUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/MainActivity.kt` |
| Alterado | `app/src/main/res/values/strings.xml` |

## O que conferir na IDE

1. Sync Gradle / rebuild.
2. Configurações: seção **Aparência** acima da transcrição, opções Claro e Escuro.
3. Trocar para Escuro: telas (Início, listas, formulários) e barras do sistema mudam na hora.
4. Fechar e reabrir o app: o tema escolhido permanece.
5. Rodar `AppSettingsRepositoryTest`.
