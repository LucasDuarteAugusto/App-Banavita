# Log de Modificações - Simplificação Extrema e Refinamento de Estoque (Figma-First)

Simplificação completa da interface de estoque, removendo elementos de busca/filtro na listagem geral e movendo ações de gestão para popups dinâmicos acionados pelos estágios de maturação. Alinhamento de títulos dinâmicos na TopBar.

## Tabela de Arquivos Modificados

| Status | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheScreen.kt` |
| Criado | `docs/Modifications/2026-09-18-simplificacao-extrema-estoque.md` |

## Instruções para Conferência na IDE

1. **Título da Barra Superior**: Navegue para Estoque e veja o título "Estoque". Entre em um produto e veja o título mudar para o **Nome do Produto**.
2. **Listagem de Estoque**: Verifique se a busca e os filtros de modo sumiram no modo Estoque, deixando o visual limpo.
3. **Popup de Ações**: Na tela de detalhes, clique em um estágio (ex: Crua ou Madura) e verifique se o popup de ações aparece com as opções de Soltar, Corrigir e Perda.
4. **Seleção de Lote**: Se um estágio tiver mais de um lote, verifique se a seleção de lote aparece dentro do popup.
