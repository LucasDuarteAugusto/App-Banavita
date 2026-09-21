# Melhoria de UX na Extração de Voz e Ajustes Visuais

## Resumo
Implementadas melhorias significativas na experiência de uso do microfone e ajustes finos de interface:
1. **Dicas JIT (Just-in-Time):** Durante a gravação de áudio, uma barra de ajuda agora aparece acima das ondas sonoras indicando exatamente o que o usuário deve falar para aquela tela específica (ex: "Fale: nome e telefone").
2. **Cheat Sheet (Botão ?):** Adicionado um botão de ajuda ao lado do microfone que abre um Bottom Sheet com o padrão detalhado e um exemplo real de fala para o contexto atual.
3. **Limpeza da Home:** Removido o botão de microfone da tela inicial, mantendo-o apenas em contextos onde a entrada de dados é necessária.
4. **Layout de Encomenda:** Ajustado o posicionamento do chip de lembrete "1 dia antes" para garantir que o texto não fique vertical em telas estreitas.

## Arquivos Modificados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/res/values/strings.xml` |
| Criado | `app/src/main/java/br/com/banavita/app/ui/voice/VoiceHints.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/voice/VoiceRecordingBar.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/voice/VoiceFabLayout.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/voice/VoiceEnabledScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormScreen.kt` |

## Instruções para Conferência na IDE
1. Em qualquer tela com microfone (ex: Clientes, Vendas), observe o novo ícone **(?)** ao lado do botão de gravar. Toque nele para ver a colinha de fala.
2. Segure o botão de microfone e verifique se o balão *"Fale: ..."* aparece acima da barra de gravação.
3. Verifique na tela de **Nova Encomenda** se os chips de lembrete estão bem distribuídos e legíveis.
4. Confirme que a **Home** está limpa, sem o ícone de microfone flutuante.
