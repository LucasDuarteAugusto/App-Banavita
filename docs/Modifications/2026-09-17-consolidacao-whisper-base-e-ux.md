# Modificação: Consolidação do Modelo Whisper Base e Ajuste de UX do Botão de Voz

## Resumo
Removeu a seleção manual entre os modelos Whisper Tiny e Base na tela de Configurações, fixando o modelo Base como a engine local padrão devido à maior precisão e eliminação de alucinações ("fiat", "40 mil"). Aumentou em 50% a área de toque do FAB de microfone (`VoiceFabLayout`) para mitigar o escape involuntário do dedo durante a gravação e adicionou normalizações lexicais para os tipos de Banana.

## Arquivos Modificados
| Ação | Arquivo |
| :--- | :--- |
| **Alterado** | `app/src/main/java/br/com/banavita/app/core/speech/PtBrPromptHints.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/core/speech/SpeechToTextRepository.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/voice/VoiceFabLayout.kt` |

## Instruções de Conferência na IDE
1. Verifique se o projeto sincroniza e compila normalmente via `./gradlew assembleDebug`.
2. Vá para a tela de **Configurações** e confirme que a caixa de seleção para Tiny/Base foi removida sob a opção do provedor Local.
3. Teste a gravação de voz pressionando e segurando o botão de microfone nas abas de Vendas/Compras e valide o ganho na área de toque do botão (agora com 60.dp).
