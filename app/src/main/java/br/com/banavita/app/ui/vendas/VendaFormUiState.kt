package br.com.banavita.app.ui.vendas

import br.com.banavita.app.domain.model.Cliente
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.DocumentLineItemFormState
import br.com.banavita.app.ui.common.computeDocumentTotalCentavos
import java.util.UUID

data class VendaFormUiState(
    val dataInput: String = DateInputParser.todayInput(),
    val clienteId: UUID? = null,
    val clientes: List<Cliente> = emptyList(),
    val produtos: List<Produto> = emptyList(),
    val itens: List<DocumentLineItemFormState> = listOf(DocumentLineItemFormState()),
    val aPrazo: Boolean = false,
    val vencimentoInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showConflitoEncomendaDialog: Boolean = false,
    val conflitoMessage: String = "",
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
) {
    val valorTotalCentavos: Long
        get() = computeDocumentTotalCentavos(itens, produtos, precoObrigatorio = false)

    val pago: Boolean
        get() = !aPrazo

    val clienteOptions: List<Pair<UUID, String>>
        get() = clientes.map { it.id to it.nome }
}
