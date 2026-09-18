# Log de Modificações - Reestruturação das Telas de Estoque (Agrupamento e Hierarquia)

Reestruturação completa da visualização do estoque para agrupar variedades por produto na listagem geral e adicionar um resumo consolidado por estado de maturação na tela de detalhes. Essa mudança aprimora a hierarquia visual do estoque de acordo com o design especificado no Figma.

## Tabela de Arquivos Modificados

| Status | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheScreen.kt` |

## Instruções para Conferência na IDE

1. **ConsultasViewModel.kt & ConsultasScreen.kt**: Verifique se o agrupamento por `produtoId` consolida corretamente os saldos e estados críticos, exibindo apenas um item por variedade de produto na tela inicial de Estoque.
2. **EstoqueDetalheViewModel.kt & EstoqueDetalheScreen.kt**: Abra a tela de detalhes de um produto perecível (como Banana Nanica) e valide se a nova seção "Quantidade por Estado" apresenta o resumo correto dos lotes.
3. **Compilação**: Certifique-se de que o build conclui sem erros.
