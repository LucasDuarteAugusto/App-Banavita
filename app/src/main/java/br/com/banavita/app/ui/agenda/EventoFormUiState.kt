package br.com.banavita.app.ui.agenda

import br.com.banavita.app.domain.model.Cliente
import br.com.banavita.app.domain.model.Fornecedor
import br.com.banavita.app.ui.common.DateTimeInputParser
import java.util.UUID

data class EventoFormUiState(
    val titulo: String = "",
    val dataInput: String = DateTimeInputParser.todayDateInput(),
    val horaInput: String = DateTimeInputParser.nowTimeInput(),
    val observacao: String = "",
    val clienteId: UUID? = null,
    val fornecedorId: UUID? = null,
    val clientes: List<Cliente> = emptyList(),
    val fornecedores: List<Fornecedor> = emptyList(),
    val notificar: Boolean = true,
    val antecipacaoMinutos: Int? = null,
    val notifTitulo: String = "",
    val notifCorpo: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
) {
    val clienteOptions: List<Pair<UUID, String>>
        get() = clientes.map { it.id to it.nome }
    val fornecedorOptions: List<Pair<UUID, String>>
        get() = fornecedores.map { it.id to it.nome }
}
