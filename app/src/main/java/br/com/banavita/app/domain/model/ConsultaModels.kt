package br.com.banavita.app.domain.model

enum class ConsultaModo(val route: String) {
    ESTOQUE("estoque"),
    VENDAS("vendas"),
    COMPRAS("compras"),
    RECEBER("receber"),
    PAGAR("pagar"),
    ;

    companion object {
        fun fromRoute(value: String?): ConsultaModo =
            entries.firstOrNull { it.route.equals(value, ignoreCase = true) } ?: ESTOQUE
    }
}

enum class PeriodoPreset {
    HOJE,
    SEMANA,
    MES,
    INTERVALO,
}

enum class ConsultaVisao {
    DOCUMENTOS,
    POR_PARCEIRO,
    POR_PRODUTO,
}

enum class AgendaRecorte {
    HOJE,
    PROXIMOS_7_DIAS,
    TODOS,
}

enum class EstoqueOrdenacao {
    SALDO_ASC,
    NOME_ASC,
}

enum class EstoqueMovimentoTipo {
    ENTRADA,
    SAIDA,
}
