# Simplificação das Telas de Consultas e Atualização de Títulos

## Resumo
Simplificadas todas as telas de consultas/relatórios do aplicativo removendo os componentes de seleção de modo (`ModeChips`), campo de busca de texto (`OutlinedTextField`) e cartões de resumo (`StockSummaryCard`), seguindo o padrão limpo e focado previamente adotado na tela de estoque. Além disso, os títulos das telas foram ajustados para corresponder exatamente aos nomes dos botões/cards da tela inicial que as acionam.

## Arquivos Modificados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/res/values/strings.xml` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasScreen.kt` |

## Instruções para Conferência na IDE
1. Abra a tela inicial (`HomeScreen`) e observe que os botões foram renomeados simplificadamente (ex: "Vendas", "Compras", "Perecíveis", "Encomendas").
2. Clique em qualquer um dos botões (ex: Vendas, Compras, A receber, A pagar, Perecíveis) e verifique se o título exibido na barra superior (`TopAppBar`) condiz perfeitamente com o botão acionado.
3. Certifique-se de que a listagem de dados ocupa a tela de forma direta e limpa, livre de chips de navegação redundantes entre consultas ou campos de busca de texto no topo.
