# Validação de Disponibilidade e Alocação de Encomendas

## Resumo
Implementada a lógica de reserva lógica de estoque para encomendas em aberto. Agora, a tela de Encomendas exibe a disponibilidade preditiva baseada na data de entrega (simulando a maturação dos lotes na etileno). Além disso, ao realizar uma venda que consuma bananas reservadas para encomendas futuras, o sistema exibe um aviso (Popup) permitindo ao usuário cancelar ou prosseguir com a venda mesmo assim.

## Arquivos Criados / Alterados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/dao/PerecivelDaos.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/LoteRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/EncomendaRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormScreen.kt` |

## Instruções para Conferência na IDE
1. Na tela de **Encomenda**, ao selecionar um produto, estado (ex: Amarela) e data de entrega, observe que o campo "Estoque" agora mostra a previsão de quantas caixas estarão naquele estágio na data escolhida.
2. Crie uma encomenda de teste para uma data futura.
3. Tente registrar uma **Venda** do mesmo produto e estado que consuma o saldo físico atual, deixando-o abaixo da quantidade total encomendada.
4. Valide se a Popup "Aviso de Encomenda" aparece, e teste as opções "Vender assim mesmo" e "Cancelar".
