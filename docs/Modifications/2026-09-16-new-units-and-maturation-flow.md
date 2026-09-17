# Novas Unidades e Fluxo de Maturação Ampliado

Implementação das novas unidades de medida para caixas e expansão do ciclo de vida dos perecíveis para incluir os estados "Crua" e "Em Maturação".

## Arquivos Alterados
| Arquivo | Ação |
| :--- | :--- |
| `app/src/main/java/br/com/banavita/app/domain/model/PereciveisModels.kt` | Atualizado enum e modelos de curva/preço |
| `app/src/main/java/br/com/banavita/app/data/local/entity/ProdutoEntity.kt` | Adicionadas colunas de dias e preços |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabase.kt` | Incremento da versão (v5) |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaMigrations.kt` | Criada `MIGRATION_4_5` |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabaseHolder.kt` | Registrada nova migração |
| `app/src/main/java/br/com/banavita/app/data/mapper/EntityMappers.kt` | Atualizado mapeamento de produto |
| `app/src/main/java/br/com/banavita/app/data/repository/LoteRepository.kt` | Ajustado fluxo inicial e transferência |
| `app/src/main/java/br/com/banavita/app/data/repository/ProdutoRepository.kt` | Atualizados métodos de criação/edição |
| `app/src/main/java/br/com/banavita/app/core/extraction/UnitDictionary.kt` | Unidades `CX MADEIRA` e `CX PLASTICA` |
| `app/src/main/java/br/com/banavita/app/core/extraction/RoleKeywords.kt` | Adicionados novos estados às stop words |
| `app/src/main/java/br/com/banavita/app/ui/common/PereciveisUi.kt` | Labels para novos estados |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormViewModel.kt` | Lógica de formulário atualizada |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormUiState.kt` | Novos campos de estado |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormScreen.kt` | UI para novos campos |
| `app/src/main/res/values/strings.xml` | Strings para novos estados e unidades |

## O que conferir na IDE
1. No cadastro de produtos, verifique se as unidades agora mostram `CX MADEIRA` e `CX PLASTICA`.
2. Verifique se apareceu o campo "Dias em maturação (gás)" na curva de perecíveis.
3. Ao registrar uma compra de banana, confirme se o lote entra no estado `crua` (câmara fria).
4. Ao soltar para etileno, o estado deve mudar para `em maturação`.
