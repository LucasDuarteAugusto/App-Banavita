package br.com.banavita.app.ui.cadastros.produtos

import br.com.banavita.app.domain.model.Grupo
import java.util.UUID

data class ProdutoFormUiState(
    val descricao: String = "",
    val precoInput: String = "",
    val unidade: String = "UN",
    val grupoId: UUID? = null,
    val grupos: List<Grupo> = emptyList(),
    val unidades: List<String> = UNIDADES,
    val perecivel: Boolean = false,
    val diasVerde: String = "3",
    val diasVerdolenga: String = "2",
    val diasAmarela: String = "2",
    val diasPintada: String = "2",
    val precoVerdeInput: String = "",
    val precoVerdolengaInput: String = "",
    val precoAmarelaInput: String = "",
    val precoPintadaInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
) {
    val grupoNomeSelecionado: String?
        get() = grupos.find { it.id == grupoId }?.nome
}

private val UNIDADES = listOf("UN", "KG", "CX", "DZ")
