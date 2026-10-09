package br.com.banavita.app.data.mapper

import br.com.banavita.app.data.local.entity.ClienteEntity
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.entity.FornecedorEntity
import br.com.banavita.app.data.local.entity.GrupoEntity
import br.com.banavita.app.data.local.entity.LoteEntity
import br.com.banavita.app.data.local.entity.PerdaEntity
import br.com.banavita.app.data.local.entity.ProdutoEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity
import br.com.banavita.app.data.local.relation.CompraWithItems
import br.com.banavita.app.data.local.relation.VendaWithItems
import br.com.banavita.app.data.local.view.EstoqueLinhaView
import br.com.banavita.app.data.local.view.ProdutoEstoqueView
import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.Cliente
import br.com.banavita.app.domain.model.Compra
import br.com.banavita.app.domain.model.CompraItem
import br.com.banavita.app.domain.model.EstoqueLinha
import br.com.banavita.app.domain.model.Evento
import br.com.banavita.app.domain.model.Fornecedor
import br.com.banavita.app.domain.model.Grupo
import br.com.banavita.app.domain.model.Lote
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.Pagamento
import br.com.banavita.app.domain.model.Perda
import br.com.banavita.app.domain.model.PerdaMotivo
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
        perecivel = perecivel,
        curva = br.com.banavita.app.domain.model.RipeningCurve(
            diasEmMaturacao = diasEmMaturacao,
            diasVerde = diasVerde,
            diasVerdolenga = diasVerdolenga,
            diasAmarela = diasAmarela,
            diasPintada = diasPintada,
        ),
        precosEstado = br.com.banavita.app.domain.model.PrecosPorEstado(
            cruaCentavos = precoCruaCentavos,
            emMaturacaoCentavos = precoEmMaturacaoCentavos,
            verdeCentavos = precoVerdeCentavos,
            verdolengaCentavos = precoVerdolengaCentavos,
            amarelaCentavos = precoAmarelaCentavos,
            pintadaCentavos = precoPintadaCentavos,
            podreCentavos = precoPodreCentavos,
        ),
    )

fun ClienteEntity.toDomain(): Cliente =
    Cliente(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        nome = nome,
        telefone = telefone,
        pontoPreferido = MaturacaoEstado.fromStored(pontoPreferido),
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
        estado = MaturacaoEstado.fromStored(estado),
        unidade = unidade,
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
        vencimento = venda.vencimento,
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
        unidade = unidade,
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
        vencimento = compra.vencimento,
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

fun LoteEntity.toDomain(): Lote =
    Lote(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        produtoId = produtoId,
        compraId = compraId,
        compraItemId = compraItemId,
        origemId = origemId,
        dataEntrada = dataEntrada,
        quantidadeInicial = quantidadeInicial,
        quantidadeRestante = quantidadeRestante,
        camara = Camara.valueOf(camara),
        estado = MaturacaoEstado.valueOf(estado),
        etapaInicioEpochDay = etapaInicioEpochDay,
        custoUnitarioCentavos = custoUnitarioCentavos,
        rotulo = rotulo,
        unidade = unidade,
    )

fun EstoqueLinhaView.toDomain(): EstoqueLinha =
    EstoqueLinha(
        produtoId = produtoId,
        descricao = descricao,
        unidade = unidade,
        estado = MaturacaoEstado.fromStored(estado),
        camara = camara?.let { runCatching { Camara.valueOf(it) }.getOrNull() },
        quantidade = quantidade,
        perecivel = perecivel == 1,
    )

fun PerdaEntity.toDomain(): Perda =
    Perda(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        data = data,
        produtoId = produtoId,
        loteId = loteId,
        estado = MaturacaoEstado.valueOf(estado),
        quantidade = quantidade,
        motivo = PerdaMotivo.valueOf(motivo),
        valorCentavos = valorCentavos,
        unidade = unidade,
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
        tipo = br.com.banavita.app.domain.model.EventoTipo.valueOf(tipo),
        status = br.com.banavita.app.domain.model.EventoStatus.valueOf(status),
        prioridade = br.com.banavita.app.domain.model.Prioridade.valueOf(prioridade),
    )

