package br.com.banavita.app.data.mapper

import br.com.banavita.app.data.local.entity.ClienteEntity
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.entity.FornecedorEntity
import br.com.banavita.app.data.local.entity.GrupoEntity
import br.com.banavita.app.data.local.entity.ProdutoEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity
import br.com.banavita.app.data.local.relation.CompraWithItems
import br.com.banavita.app.data.local.relation.VendaWithItems
import br.com.banavita.app.data.local.view.ProdutoEstoqueView
import br.com.banavita.app.domain.model.Cliente
import br.com.banavita.app.domain.model.Compra
import br.com.banavita.app.domain.model.CompraItem
import br.com.banavita.app.domain.model.Evento
import br.com.banavita.app.domain.model.Fornecedor
import br.com.banavita.app.domain.model.Grupo
import br.com.banavita.app.domain.model.Pagamento
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.domain.model.ProdutoEstoque
import br.com.banavita.app.domain.model.Recebimento
import br.com.banavita.app.domain.model.Venda
import br.com.banavita.app.domain.model.VendaItem
import br.com.banavita.app.data.local.entity.EventoEntity
import br.com.banavita.app.data.local.entity.PagamentoEntity
import br.com.banavita.app.data.local.entity.RecebimentoEntity

fun GrupoEntity.toDomain(): Grupo =
    Grupo(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        nome = nome,
        observacao = observacao,
    )

fun ProdutoEntity.toDomain(): Produto =
    Produto(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        descricao = descricao,
        precoCentavos = precoCentavos,
        grupoId = grupoId,
        unidade = unidade,
    )

fun ClienteEntity.toDomain(): Cliente =
    Cliente(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        nome = nome,
        telefone = telefone,
    )

fun FornecedorEntity.toDomain(): Fornecedor =
    Fornecedor(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        nome = nome,
        telefone = telefone,
    )

fun VendaItemEntity.toDomain(): VendaItem =
    VendaItem(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        vendaId = vendaId,
        produtoId = produtoId,
        valorUnitarioCentavos = valorUnitarioCentavos,
        quantidade = quantidade,
        valorTotalCentavos = valorTotalCentavos,
    )

fun VendaWithItems.toDomain(): Venda =
    Venda(
        id = venda.id,
        createdAt = venda.createdAt,
        updatedAt = venda.updatedAt,
        data = venda.data,
        clienteId = venda.clienteId,
        valorTotalCentavos = venda.valorTotalCentavos,
        aPrazo = venda.aPrazo,
        itens = itens.map { it.toDomain() },
    )

fun CompraItemEntity.toDomain(): CompraItem =
    CompraItem(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        compraId = compraId,
        produtoId = produtoId,
        valorUnitarioCentavos = valorUnitarioCentavos,
        quantidade = quantidade,
        valorTotalCentavos = valorTotalCentavos,
    )

fun CompraWithItems.toDomain(): Compra =
    Compra(
        id = compra.id,
        createdAt = compra.createdAt,
        updatedAt = compra.updatedAt,
        data = compra.data,
        fornecedorId = compra.fornecedorId,
        valorTotalCentavos = compra.valorTotalCentavos,
        aPrazo = compra.aPrazo,
        itens = itens.map { it.toDomain() },
    )

fun ProdutoEstoqueView.toDomain(): ProdutoEstoque =
    ProdutoEstoque(
        produtoId = produtoId,
        unidade = unidade,
        saldo = saldo,
    )

fun RecebimentoEntity.toDomain(): Recebimento =
    Recebimento(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        vendaId = vendaId,
        data = data,
        valorCentavos = valorCentavos,
    )

fun PagamentoEntity.toDomain(): Pagamento =
    Pagamento(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        compraId = compraId,
        data = data,
        valorCentavos = valorCentavos,
    )

fun EventoEntity.toDomain(): Evento =
    Evento(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        titulo = titulo,
        dataHora = dataHora,
        observacao = observacao,
        clienteId = clienteId,
        fornecedorId = fornecedorId,
        notificar = notificar,
        antecipacaoMinutos = antecipacaoMinutos,
        notifTitulo = notifTitulo,
        notifCorpo = notifCorpo,
    )

