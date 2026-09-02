package br.com.banavita.app.domain.exception

open class BanavitaDomainException(message: String) : Exception(message)

class ReferencedEntityException(message: String) : BanavitaDomainException(message)

class InsufficientStockException(
    val produtoId: String,
    val disponivel: Double,
    val solicitado: Double,
) : BanavitaDomainException(
    "Estoque insuficiente para o produto $produtoId. Disponível: $disponivel, solicitado: $solicitado.",
)

class InvalidDocumentException(message: String) : BanavitaDomainException(message)

class OverpaymentException(message: String) : BanavitaDomainException(message)
