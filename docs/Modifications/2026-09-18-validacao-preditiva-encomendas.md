# Preço em Encomendas e Validação Preditiva de Conflitos

## Resumo
Implementadas melhorias significativas no fluxo de encomendas e vendas:
1. **Preço em Encomendas:** Adicionado suporte para entrada manual de preço unitário em cada item da encomenda. O banco de dados foi migrado (Versão 7) para armazenar este valor.
2. **Validação Preditiva Avançada:** Aprimorada a lógica de interceptação de vendas. Agora, o sistema analisa todo o "pipeline" de maturação dos lotes físicos. Se uma venda de banana verde hoje comprometer uma encomenda de banana amarela para daqui a 3 dias, o sistema emitirá o aviso de conflito, informando a data e a quantidade que faltará para cumprir o compromisso futuro.

## Arquivos Criados / Alterados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/entity/EncomendaEntity.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabase.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaMigrations.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabaseHolder.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/domain/model/PereciveisModels.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/LoteRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/EncomendaRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormUiState.kt` |

## Instruções para Conferência na IDE
1. Abra a tela de **Encomenda** e verifique se o campo de preço unitário está habilitado para edição em cada item.
2. Realize o teste do fluxo de maturação:
   - Cadastre uma encomenda de banana Amarela para daqui a 2 dias.
   - Tente vender banana Verde hoje em uma quantidade que deixe o estoque remanescente insuficiente para suprir a encomenda após o amadurecimento.
   - Valide se a Popup de aviso detalha corretamente a data e a falta de produto projetada.
