package br.com.banavita.app.core.speech

import br.com.banavita.app.domain.exception.SpeechAudioException
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

object WavReader {
    fun readPcm16MonoAsFloat(file: File): FloatArray {
        val bytes = file.readBytes()
        if (bytes.size < 44) {
            throw SpeechAudioException("Arquivo WAV inválido")
        }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val riff = ByteArray(4).also { buffer.get(it) }.toString(Charsets.US_ASCII)
        buffer.int // chunk size
        val wave = ByteArray(4).also { buffer.get(it) }.toString(Charsets.US_ASCII)
        if (riff != "RIFF" || wave != "WAVE") {
            throw SpeechAudioException("Arquivo não é WAV PCM")
        }

        var audioFormat = 1
        var channels = 1
        var sampleRate = SpeechConstants.SAMPLE_RATE_HZ
        var bitsPerSample = 16
        var dataOffset = -1
        var dataSize = 0

        while (buffer.remaining() >= 8) {
            val chunkId = ByteArray(4).also { buffer.get(it) }.toString(Charsets.US_ASCII)
            val chunkSize = buffer.int
            when (chunkId) {
                "fmt " -> {
                    audioFormat = buffer.short.toInt() and 0xffff
                    channels = buffer.short.toInt() and 0xffff
                    sampleRate = buffer.int
                    buffer.int // byte rate
                    buffer.short // block align
                    bitsPerSample = buffer.short.toInt() and 0xffff
                    if (chunkSize > 16) {
                        buffer.position(buffer.position() + (chunkSize - 16))
                    }
                }
                "data" -> {
                    dataOffset = buffer.position()
                    dataSize = chunkSize
                    break
                }
                else -> buffer.position(buffer.position() + chunkSize)
            }
        }

        if (dataOffset < 0) {
            throw SpeechAudioException("Chunk data não encontrado no WAV")
        }
        if (audioFormat != 1 || bitsPerSample != 16) {
            throw SpeechAudioException("WAV deve ser PCM 16-bit")
        }
        if (channels != 1) {
            throw SpeechAudioException("WAV deve ser mono")
        }
        if (sampleRate != SpeechConstants.SAMPLE_RATE_HZ) {
            throw SpeechAudioException("WAV deve ser ${SpeechConstants.SAMPLE_RATE_HZ} Hz")
        }

        val pcm = bytes.copyOfRange(dataOffset, dataOffset + dataSize)
        return PcmConverter.pcm16LittleEndianToFloat(pcm)
    }
}
