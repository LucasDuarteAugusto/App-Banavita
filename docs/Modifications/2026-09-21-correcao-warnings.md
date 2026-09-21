# Correção Geral de Warnings do Android Lint e Otimização do Compose

Foram corrigidos múltiplos avisos (*warnings*) em todo o projeto, incluindo problemas de ordenação de parâmetros de modificadores no Jetpack Compose, falta de especificação explícita de localidade em formatação de Strings e substituições de chamadas de análise de URIs por funções de extensão nativas do Android KTX.

## Arquivos Alterados

| Status | Caminho do Arquivo |
| :--- | :--- |
| **Alterado** | `gradle/libs.versions.toml` |
| **Alterado** | `app/build.gradle.kts` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/cadastros/clientes/ClienteListScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/cadastros/fornecedores/FornecedorListScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/cadastros/grupos/GrupoListScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoListScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasViewModel.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/core/reminder/AgendaAlarmReceiver.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisAlarmReceiver.kt` |
| **Alterado** | `app/src/test/java/br/com/banavita/app/TestRepositories.kt` |
| **Alterado** | `app/src/test/java/br/com/banavita/app/PereciveisIntegrationTest.kt` |

## O que conferir na IDE

1. Execute `./gradlew lintDebug` para validar a ausência dos avisos corrigidos.
2. Sincronize o Gradle para confirmar que a dependência do `okhttp` via Version Catalog (`libs.okhttp`) está correta.
