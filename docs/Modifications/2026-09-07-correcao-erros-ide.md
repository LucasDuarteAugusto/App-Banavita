# Correção de Erros de Inspeção da IDE e Internacionalização

Correção de avisos de internacionalização, extração de strings hardcoded para o `strings.xml` e pequenos ajustes em arquivos Kotlin para satisfazer o Lint (parâmetros nomeados e imports não utilizados).

## Arquivos Modificados

| Arquivo | Ação | Descrição |
|---------|------|-----------|
| [strings.xml](file:///C:/projetos-Lucas/App-Banavita/app/src/main/res/values/strings.xml) | Alterado | Adição de recursos de string para erros, labels e correção de hífen em telefone. |
| [ExtractionReviewSheet.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/voice/ExtractionReviewSheet.kt) | Alterado | Substituição de labels hardcoded por `stringResource`. |
| [ProdutoFormScreen.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormScreen.kt) | Alterado | Adição de parâmetro nomeado `value` em `mutableStateOf`. |
| [VendaFormScreen.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormScreen.kt) | Alterado | Remoção de import não utilizado (`Arrangement`). |
| [ActivationViewModel.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/activation/ActivationViewModel.kt) | Alterado | Limpeza e correção de erros introduzidos durante a tentativa de extração de strings. |

## O que conferir na IDE

1. **strings.xml**: Verifique se o aviso de "EN dash" no hint de telefone desapareceu.
2. **ExtractionReviewSheet.kt**: Confira se os labels na revisão de voz continuam em português e sem avisos de hardcoded string.
3. **ProdutoFormScreen.kt**: Verifique se a variável `unidadeExpanded` e `grupoExpanded` continuam funcionando (a mudança foi apenas de estilo/Lint).
4. **Build**: Execute `./gradlew assembleDebug -PskipSpeechModel` para garantir que o projeto compila sem erros.
