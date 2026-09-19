package br.com.banavita.app.ui.compras

import br.com.banavita.app.domain.model.Fornecedor
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.DocumentLineItemFormState
import br.com.banavita.app.ui.common.computeDocumentTotalCentavos
import java.util.UUID

data class CompraFormUiState(
    val dataInput: String = DateInputParser.todayInput(),
    val fornecedorId: UUID? = null,
    val fornecedores: List<Fornecedor> = emptyList(),
    val produtos: List<Produto> = emptyList(),
    val itens: List<DocumentLineItemFormState> = listOf(DocumentLineItemFormState()),
    val aPrazo: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showCapacidadeWarning: Boolean = false,
    val capacidadeWarningMessage: String = "",
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
) {
    val valorTotalCentavos: Long
        get() = computeDocumentTotalCentavos(itens, produtos, precoObrigatorio = true)

    val pago: Boolean
        get() = !aPrazo

    val fornecedorOptions: List<Pair<UUID, String>>
        get() = fornecedores.map { it.id to it.nome }
}
