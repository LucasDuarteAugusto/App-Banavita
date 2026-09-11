package br.com.banavita.app.di

import br.com.banavita.app.core.speech.WhisperCppEngine
import br.com.banavita.app.core.speech.WhisperEngine
import br.com.banavita.app.data.remote.groq.EnvGroqConfig
import br.com.banavita.app.data.remote.groq.GroqConfig
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SpeechModule {
    @Binds
    @Singleton
    abstract fun bindWhisperEngine(impl: WhisperCppEngine): WhisperEngine

    @Binds
    @Singleton
    abstract fun bindGroqConfig(impl: EnvGroqConfig): GroqConfig
}
