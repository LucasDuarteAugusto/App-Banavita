# Integração de Encomenda com Agenda e Ajustes de Preço

## Resumo
Implementada a integração automática entre Encomendas e a Agenda (Eventos). Agora, ao salvar uma encomenda, um evento correspondente é criado/atualizado na agenda com lembrete configurável. Também foi adicionado o campo de hora de entrega e habilitada a edição de preço unitário nos itens da encomenda.

## Mudanças Realizadas
1. **Banco de Dados (Versão 8):** Adicionada a coluna `ENC_EVENTO` na tabela `encomenda` para vincular a encomenda ao seu evento na agenda.
2. **EncomendaRepository:**
    - Agora injeta `EventoRepository` para gerenciar a vida útil do evento vinculado.
    - O título do evento é gerado como `Encomenda - {{nomeCliente}}`.
    - A observação do evento lista detalhadamente os itens, estados e quantidades encomendadas.
    - Ao excluir uma encomenda, o evento vinculado também é removido.
3. **EncomendaForm (UI & ViewModel):**
    - Adicionado campo para informar a **Hora de Entrega**.
    - Adicionada seção de **Lembrete** com chips para habilitar notificação e escolher a antecipação (15m, 1h, 1d).
    - Habilitada a edição do campo de **Preço Unitário** em cada item da encomenda.
    - Integrado o fluxo de permissão de notificação do Android 13+.

## Arquivos Alterados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/entity/EncomendaEntity.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabase.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaMigrations.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabaseHolder.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/domain/model/PereciveisModels.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/EncomendaRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormScreen.kt` |

## Instruções para Conferência na IDE
1. Abra o formulário de **Nova Encomenda**.
2. Verifique se agora é possível definir a **Hora** além da data.
3. Configure um **Lembrete** (Ex: 1 hora antes).
4. Digite um **Preço Unitário** personalizado para um item.
5. Salve a encomenda e vá até a tela de **Agenda** para confirmar se o evento `Encomenda - [Cliente]` foi criado com a lista de produtos na observação.
6. Tente editar ou excluir a encomenda e verifique se o evento na agenda reflete a mudança ou é removido.
