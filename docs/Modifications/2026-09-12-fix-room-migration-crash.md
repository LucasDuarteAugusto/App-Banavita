# Fix Room Migration Crash

Foi corrigido um erro de inicialização do aplicativo causado por uma falha na validação da migração do Room (versão 3 para 4). O erro `java.lang.IllegalStateException: Migration didn't properly handle: vw_produto_estoque` ocorria devido a uma divergência na formatação do SQL das views entre a definição da classe e o comando manual de migração.

## Arquivos Alterados
| Arquivo | Ação |
| :--- | :--- |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaMigrations.kt` | Alterado |

## O que conferir na IDE
1. Abra o arquivo `BanavitaMigrations.kt` e verifique se o SQL das views `vw_produto_estoque` e `vw_estoque_linha` agora utiliza `AS SELECT` na mesma linha e a indentação correta para bater com o que o Room espera.
2. Execute o aplicativo e confirme que ele abre na tela inicial (Banavita) sem fechar imediatamente.
3. Verifique os logs (Logcat) para garantir que não há erros fatais durante a criação do banco de dados.
