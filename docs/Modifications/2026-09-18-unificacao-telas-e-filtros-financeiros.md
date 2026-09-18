# Unificação de Telas, Remoção de Atalhos e Filtros Financeiros

## Resumo
Realizada a limpeza da tela inicial e unificação das telas de listagem:
1. **Home Screen:** Removidos os cards de "Vendas" e "Compras" (mantidos apenas nas abas inferiores) e o botão global de voz.
2. **Unificação de Telas:** As telas de Vendas e Compras agora utilizam o motor de consultas, permitindo filtrar por período e mudar a visão (Lista ou Agrupado por Cliente/Fornecedor).
3. **Filtros Financeiros:** Adicionado suporte a filtros de período e visão nas telas "A receber" e "A pagar". As legendas foram ajustadas para "Por cliente" e "Por fornecedor" conforme o contexto.
4. **Ajuste UI Encomenda:** Corrigido o layout dos chips de lembrete na tela de Encomenda para evitar que o texto do botão "1 dia antes" fique vertical em telas estreitas.

## Arquivos Modificados / Removidos
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/home/HomeViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormScreen.kt` |
| Removido | `app/src/main/java/br/com/banavita/app/ui/vendas/VendaListScreen.kt` (e associados) |
| Removido | `app/src/main/java/br/com/banavita/app/ui/compras/CompraListScreen.kt` (e associados) |

## Instruções para Conferência na IDE
1. Na **Home**, confirme que os cards de Vendas/Compras e o Mic sumiram.
2. Vá para a aba **Vendas** (inferior) e verifique se agora existem filtros de Hoje/Semana/Mês e a opção "Por cliente".
3. Na tela de **Nova Encomenda**, verifique se o botão "1 dia antes" agora aparece em uma nova linha, com texto horizontal correto.
4. Teste os filtros nas telas de **A receber** e **A pagar** a partir da Home.
