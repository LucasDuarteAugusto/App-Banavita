package br.com.banavita.app.domain.exception

open class ExtractionException(message: String) : BanavitaDomainException(message)

class ExtractionParseException(message: String) : ExtractionException(message)

class ExtractionConfirmException(message: String) : ExtractionException(message)

class ExtractionDraftNotFoundException(message: String) : ExtractionException(message)
