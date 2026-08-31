package br.com.banavita.app.data.cache

import br.com.banavita.app.domain.model.extraction.ExtractionDraft
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory draft/transcript holder (mobile Redis equivalent).
 * Never persists to Room/SQLite.
 */
@Singleton
class ExtractionSessionCache @Inject constructor() {
    private val current = AtomicReference<ExtractionDraft?>(null)

    fun put(draft: ExtractionDraft) {
        current.set(draft)
    }

    fun get(): ExtractionDraft? = current.get()

    fun clear() {
        current.set(null)
    }

    fun getIfId(draftId: java.util.UUID): ExtractionDraft? {
        val draft = current.get() ?: return null
        return draft.takeIf { it.id == draftId }
    }
}
