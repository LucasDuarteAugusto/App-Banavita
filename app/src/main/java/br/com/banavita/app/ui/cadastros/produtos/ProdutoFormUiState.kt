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
