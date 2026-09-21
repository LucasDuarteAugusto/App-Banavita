package br.com.banavita.app

import android.content.Context
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource
import br.com.banavita.app.data.remote.groq.GroqConfig
import br.com.banavita.app.data.repository.AppSettingsRepository
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.DocumentoItemFactory
import br.com.banavita.app.data.repository.EstoqueValidator
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.data.repository.VendaRepository
import javax.inject.Provider

class TestRepositories(
    context: Context,
    private val database: BanavitaDatabase,
) {
    val settings = AppSettingsRepository(
        AppSettingsLocalDataSource(context),
        object : GroqConfig {
            override val apiKey: String = ""
            override val modelId: String = "test"
        },
    )

    val loteRepository = LoteRepository(
        Provider { database },
        Provider { database.loteDao() },
        Provider { database.produtoDao() },
        Provider { database.perdaDao() },
        Provider { database.precoQueimaDao() },
        settings,
    )

    val documentoItemFactory = DocumentoItemFactory(
        Provider { database.produtoDao() },
        loteRepository,
    )

    val estoqueValidator = EstoqueValidator(
        Provider { database.estoqueDao() },
        Provider { database.produtoDao() },
        Provider { database.loteDao() },
    )

    val compraRepository = CompraRepository(
        Provider { database },
        Provider { database.compraDao() },
        Provider { database.fornecedorDao() },
        documentoItemFactory,
        Provider { database.pagamentoDao() },
        loteRepository,
    )

    val vendaRepository = VendaRepository(
        Provider { database },
        Provider { database.vendaDao() },
        Provider { database.clienteDao() },
        documentoItemFactory,
        estoqueValidator,
        Provider { database.recebimentoDao() },
        loteRepository,
        Provider { database.produtoDao() },
    )

    val eventoRepository = br.com.banavita.app.data.repository.EventoRepository(
        Provider { database.eventoDao() },
        Provider { database.clienteDao() },
        Provider { database.fornecedorDao() },
        object : br.com.banavita.app.domain.reminder.ReminderScheduler {
            override fun schedule(evento: br.com.banavita.app.domain.model.Evento) {}
            override fun cancel(eventoId: java.util.UUID) {}
            override fun rescheduleAll(eventos: List<br.com.banavita.app.domain.model.Evento>) {}
        }
    )
}
