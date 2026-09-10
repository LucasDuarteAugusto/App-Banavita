package br.com.banavita.app.core.speech

import android.util.Log
import java.util.Locale

object SttDiagnostics {
    private const val TAG = "BanavitaStt"

    fun maskSensitive(text: String): String =
        text.replace(Regex("""\d{8,}""")) { match ->
            val digits = match.value
            if (digits.length >= 10) {
                digits.take(3) + "****" + digits.takeLast(4)
            } else {
                "****"
            }
        }

    fun logTiming(
        deviceModel: String? = android.os.Build.MODEL,
        modelId: String,
        threads: Int,
        audioSeconds: Double,
        speechSeconds: Double,
        loadMs: Long,
        preprocessMs: Long,
        inferenceMs: Long,
        postprocessMs: Long,
        totalMs: Long,
        maskedResult: String,
    ) {
        val rtf = if (speechSeconds > 0.0) inferenceMs / (speechSeconds * 1000.0) else 0.0
        val json = buildString {
            append('{')
            append("\"device\":\"").append(escape(deviceModel.orEmpty())).append("\",")
            append("\"model\":\"").append(escape(modelId)).append("\",")
            append("\"threads\":").append(threads).append(',')
            append("\"audio_seconds\":").append("%.3f".format(Locale.US, audioSeconds)).append(',')
            append("\"speech_seconds\":").append("%.3f".format(Locale.US, speechSeconds)).append(',')
            append("\"load_model_ms\":").append(loadMs).append(',')
            append("\"preprocess_ms\":").append(preprocessMs).append(',')
            append("\"inference_ms\":").append(inferenceMs).append(',')
            append("\"postprocess_ms\":").append(postprocessMs).append(',')
            append("\"total_ms\":").append(totalMs).append(',')
            append("\"rtf\":").append("%.3f".format(Locale.US, rtf)).append(',')
            append("\"result\":\"").append(escape(maskedResult)).append('"')
            append('}')
        }
        Log.i(TAG, json)
    }

    private fun escape(value: String): String =
        value.replace("\\", "\\\\").replace("\"", "\\\"")
}
