# Log de Modificações - Correções de Unidade de Medida e Persistência de Título

Correção do texto "Kg" hardcoded no dashboard e notificações para respeitar a unidade cadastrada no produto. Ajuste na persistência do título da barra superior na tela de detalhes de estoque.

## Tabela de Arquivos Modificados

| Status | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/domain/model/PereciveisModels.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/LoteRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisAlarmReceiver.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |

## Instruções para Conferência na IDE

1. **Título da Barra Superior**: Entre nos detalhes de um produto de estoque e verifique se o nome do produto permanece no título sem voltar para "Estoque" após o carregamento.
2. **Unidade de Medida**: No card de "Perecíveis" da tela inicial e nas notificações do sistema, verifique se a unidade exibida corresponde à cadastrada (ex: "CX MADEIRA" em vez de sempre "Kg").
3. **Compilação**: O build deve ser concluído com sucesso.
