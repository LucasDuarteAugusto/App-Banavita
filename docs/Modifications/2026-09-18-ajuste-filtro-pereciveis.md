# Ajuste de Saldo no Filtro de Perecíveis

## Resumo
Correção na listagem e somatório da tela de Perecíveis (ativada com o parâmetro `somenteCriticos`). O cálculo do saldo total por variedade agora considera estritamente apenas a soma dos estoques nos estágios `PINTADA` e `PODRE` quando o filtro de perecíveis estiver ativado, omitindo volumes totais ou em estágios de maturação anteriores (como caixas verdes ou cruas) para garantir consistência perfeita com as variedades prontas exibidas na tela de perecíveis do dia.

## Arquivos Modificados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasViewModel.kt` |

## Instruções para Conferência na IDE
1. Acesse o card/botão **Perecíveis** na tela inicial.
2. Na listagem de variedades com estado perecível crítico, valide se os volumes e saldos indicados (ex: `Nanica graúda`) correspondem unicamente às quantidades em estágio Pintada ou Podre (ex: 34 caixas em vez do total geral de 184 caixas que inclui estágios anteriores).
