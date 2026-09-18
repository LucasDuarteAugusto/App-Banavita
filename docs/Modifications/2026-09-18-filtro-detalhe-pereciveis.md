# Filtro de Estados Críticos no Detalhe do Estoque

## Resumo
Ajustada a tela de detalhes de estoque (`EstoqueDetalhe`) para herdar o filtro de "Somente Críticos" quando acessada através do atalho de Perecíveis. Agora, ao clicar em uma variedade na lista de perecíveis, a tela de detalhes exibirá apenas os lotes nos estágios **Pintada** e **Podre**, mantendo a coerência visual e de negócio. O título da tela também foi ajustado para "Perecíveis" neste contexto.

## Arquivos Modificados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/navigation/BanavitaRoutes.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeViewModel.kt` |

## Instruções para Conferência na IDE
1. Na tela inicial, clique no card **Perecíveis**.
2. Na lista de produtos, clique em um item (ex: Nanica graúda).
3. Verifique se a tela de detalhes exibe apenas as linhas de estoque com estado **Pintada** ou **Podre**.
4. Observe que o título da TopBar deve ser **Perecíveis**.
5. Volte e acesse pelo botão **Estoque** (geral) e confirme que no detalhe aparecem todos os estágios normalmente.
