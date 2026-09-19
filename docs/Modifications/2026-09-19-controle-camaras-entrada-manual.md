# Controle de Câmaras, Entrada Manual e Ajustes de Fluxo

## Resumo
Implementado um controle rigoroso de ocupação de câmaras e fluxo de maturação, além de permitir ajustes manuais de estoque para implantação inicial.

## Mudanças Realizadas
1. **Configurações:** Adicionado campo para configurar a **Capacidade da Câmara Fria**.
2. **Lógica de Câmaras (Pipeline):**
    - O estado **"Em maturação"** agora é vinculado exclusivamente à câmara de **Etileno**.
    - Todos os outros estados (Crua, Verde, Amarela, etc.) são vinculados à câmara **Fria**.
    - Transição automática: Ao sair do estado "Em maturação" (seja por tempo ou correção manual), o lote é movido automaticamente para a câmara Fria para conservação.
3. **Entrada Manual (Saldo Inicial):**
    - Adicionado botão "Entrada manual" na tela de detalhes do estoque.
    - Implementada Popup para inserção direta de saldo com definição de estado, quantidade, unidade e custo unitário.
    - Validação: Impede entrada na Etileno se ultrapassar a capacidade.
4. **Correção Parcial de Estado:**
    - Agora é possível escolher a **quantidade** de caixas ao mudar o estado de um produto ("Marcar como..."), permitindo desmembrar lotes.
5. **Avisos de Capacidade:**
    - **Compra:** Exibe aviso não-impeditivo se a nova compra exceder a capacidade da câmara fria, alertando que o excesso ficará fora da refrigeração.
6. **Tela de Perda:**
    - Adicionado o motivo **"Doença"**.
    - Ajustado o layout dos chips de motivo para evitar que o texto quebre verticalmente.

## Arquivos Alterados
| Ação | Caminho do Arquivo |
| --- | --- |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/LoteRepository.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/domain/model/PereciveisModels.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/compras/CompraFormViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/compras/CompraFormScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/compras/CompraFormUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/pereciveis/perda/PerdaFormScreen.kt` |
| Alterado | `app/src/main/res/values/strings.xml` |

## Instruções para Conferência na IDE
1. Vá em **Configurações** e defina um limite para a **Câmara Fria**.
2. Simule uma **Compra** que ultrapasse esse limite e verifique o aviso de excesso.
3. No **Detalhe de Estoque**, use o novo botão **Entrada Manual** para carregar um saldo inicial.
4. Tente mudar o estado de apenas **algumas caixas** de um lote e verifique se ele se divide corretamente.
5. Na tela de **Lançar Perda**, verifique o novo motivo **Doença** e o layout corrigido.
