# Melhorias na UX de Voz e Troca de Modelos Offline

Correção de bugs de preenchimento no review de áudio e implementação da escolha entre modelos Whisper Tiny e Base para uso offline.

## Arquivos Criados
| Arquivo | Ação |
| :--- | :--- |
| `docs/Modifications/2026-09-16-voice-ux-and-model-switching.md` | Criado |

## Arquivos Alterados
| Arquivo | Ação |
| :--- | :--- |
| `app/build.gradle.kts` | Atualizado para embutir múltiplos modelos |
| `app/src/main/java/br/com/banavita/app/core/extraction/V2Parsers.kt` | Melhorada extração de prazo (fiado/pago) |
| `app/src/main/java/br/com/banavita/app/core/speech/SpeechConstants.kt` | Adicionadas constantes para múltiplos modelos |
| `app/src/main/java/br/com/banavita/app/core/speech/SpeechModelCatalog.kt` | Catálogo atualizado com Tiny e Base |
| `app/src/main/java/br/com/banavita/app/core/speech/WhisperCppEngine.kt` | Suporte a identificação do modelo carregado |
| `app/src/main/java/br/com/banavita/app/core/speech/WhisperEngine.kt` | Interface atualizada |
| `app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt` | Persistência do modelo selecionado |
| `app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt` | Repositório atualizado |
| `app/src/main/java/br/com/banavita/app/data/repository/SpeechToTextRepository.kt` | Lógica de carga dinâmica de modelos |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` | UI para seleção de modelo offline |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` | Estado de UI atualizado |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` | ViewModel atualizada |
| `app/src/main/java/br/com/banavita/app/ui/voice/ExtractionReviewSheet.kt` | Reformulação completa da UI de revisão (Dropdowns/Editores) |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceCaptureUiState.kt` | Adicionadas listas de entidades |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceCaptureViewModel.kt` | Carregamento de entidades para review |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceEnabledScreen.kt` | Passagem de estado para o review |
| `app/src/main/res/values/strings.xml` | Novas strings de configuração |

## O que conferir na IDE
1.  **Revisão de Voz:** Ao capturar um áudio, os campos de Produto, Cliente e Fornecedor agora são dropdowns pesquisáveis.
2.  **Edição de Preço:** O bug que limpava o campo de preço ao digitar o segundo dígito no review foi corrigido.
3.  **Configurações:** Verifique em "Modelo de Transcrição" a nova opção de escolher entre "Tiny" e "Base" quando o modo local estiver selecionado.
4.  **Extração de Prazo:** O sistema agora reconhece variações de gênero como "fiada", "anotada" e "devida".
