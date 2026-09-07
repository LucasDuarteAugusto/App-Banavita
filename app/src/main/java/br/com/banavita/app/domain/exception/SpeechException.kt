package br.com.banavita.app.domain.exception

open class SpeechException(message: String) : BanavitaDomainException(message)

class SpeechModelException(message: String) : SpeechException(message)

class SpeechEngineException(message: String) : SpeechException(message)

class SpeechAudioException(message: String) : SpeechException(message)

class SpeechRemoteException(message: String) : SpeechException(message)
