# Modificação: Chave API Groq Dinâmica na UI e Correção do Dropdown por Voz

## Resumo
Removeu a necessidade de compilar a chave API do Groq fixada no arquivo `.env` para produção, trazendo-a como uma configuração dinâmica na interface com máscara de senha e botão de visualização ("olhinho"). Também corrigiu o comportamento de UX da tela de confirmação por voz (`EntityDropdown`), substituindo o PopUp básico por um `ExposedDropdownMenuBox` nativo do Material 3, permitindo que a lista seja exibida automaticamente ao clicar ou digitar, mas mantendo a ordenação por similaridade inteligente.

## Arquivos Modificados
| Ação | Arquivo |
| :--- | :--- |
| **Alterado** | `app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/data/remote/groq/GroqConfig.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/voice/ExtractionReviewSheet.kt` |

## Instruções de Conferência na IDE
1. Execute `./gradlew assembleDebug` para checar que a compilação e a resolução cíclica do Hilt via `AppSettingsLocalDataSource` foram validadas.
2. Acesse a tela de **Configurações** e insira uma chave válida no campo "Chave API do Groq" com máscara de senha. Veja que o card do provedor Groq se torna selecionável dinamicamente.
3. Teste a confirmação por voz e interaja com o dropdown de produtos/fornecedores. A listagem agora abre na hora do clique e filtra conforme você digita mantendo a similaridade.
