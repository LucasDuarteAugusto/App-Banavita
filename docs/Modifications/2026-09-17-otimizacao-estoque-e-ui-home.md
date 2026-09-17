# Modificação: Otimização de Estoque (Caixas), Notificações e Reformulação da Home

## Resumo
Simplificou a gestão da câmara de etileno mudando a unidade de capacidade de "Kg" para "Caixas" e removendo o limite operacional diário. Melhorou os avisos matinais para incluir sugestões de movimentação de produtos maduros para a armazenagem. Na interface, limpou a tela inicial removendo atalhos redundantes e moveu a Agenda para a barra superior para acesso global rápido. Garantiu que o som de notificação customizado/padrão seja aplicado corretamente.

## Arquivos Modificados
| Ação | Arquivo |
| :--- | :--- |
| **Alterado** | `app/src/main/res/values/strings.xml` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/data/local/dao/LoteDao.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/data/repository/LoteRepository.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/domain/model/PereciveisModels.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/home/HomeUiState.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/home/HomeViewModel.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisAlarmReceiver.kt` |
| **Alterado** | `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisMorningScheduler.kt` |

## Instruções de Conferência na IDE
1. Verifique se o ícone de Agenda (calendário) aparece na TopBar em todas as abas principais.
2. Na tela de Configurações, valide se o campo de cota diária foi removido e se a capacidade agora é solicitada em "Caixas".
3. Na Home, confirme que os cards de "Nova Venda" e "Próximo evento" foram removidos, deixando a tela mais limpa.
4. Execute o build para garantir que a transição de tipos e nomes de métodos no `LoteRepository` e `LoteDao` foi concluída sem erros.
