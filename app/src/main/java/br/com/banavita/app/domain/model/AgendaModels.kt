package br.com.banavita.app.domain.model

enum class EventoTipo(val label: String) {
    ENTREGA("Entrega"), REUNIAO("Reunião"), TAREFA("Tarefa"), LEMBRETE("Lembrete")
}

enum class EventoStatus(val label: String) {
    PENDENTE("Pendente"), EM_ANDAMENTO("Em andamento"), CONCLUIDO("Concluído")
}

enum class Prioridade(val label: String) {
    BAIXA("Baixa"), MEDIA("Média"), ALTA("Alta")
}
