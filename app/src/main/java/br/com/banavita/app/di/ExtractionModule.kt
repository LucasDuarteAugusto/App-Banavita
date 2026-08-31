package br.com.banavita.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Extraction components are concrete @Inject/@Singleton classes;
 * this module exists for future binds (e.g. MediaPipe embedder).
 */
@Module
@InstallIn(SingletonComponent::class)
object ExtractionModule
