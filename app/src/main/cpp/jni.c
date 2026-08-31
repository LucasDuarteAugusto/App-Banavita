#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include "whisper.h"

#define TAG "BanavitaWhisper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)

JNIEXPORT jlong JNICALL
Java_br_com_banavita_app_core_speech_WhisperLib_initContext(
        JNIEnv *env,
        jobject thiz,
        jstring model_path_str) {
    (void) thiz;
    const char *model_path = (*env)->GetStringUTFChars(env, model_path_str, NULL);
    struct whisper_context_params cparams = whisper_context_default_params();
    struct whisper_context *context = whisper_init_from_file_with_params(model_path, cparams);
    (*env)->ReleaseStringUTFChars(env, model_path_str, model_path);
    if (context == NULL) {
        LOGW("Failed to init whisper context");
        return 0;
    }
    return (jlong) context;
}

JNIEXPORT void JNICALL
Java_br_com_banavita_app_core_speech_WhisperLib_freeContext(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr) {
    (void) env;
    (void) thiz;
    if (context_ptr == 0) {
        return;
    }
    whisper_free((struct whisper_context *) context_ptr);
}

JNIEXPORT jint JNICALL
Java_br_com_banavita_app_core_speech_WhisperLib_fullTranscribe(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint num_threads,
        jfloatArray audio_data,
        jstring language_str,
        jstring initial_prompt_str) {
    (void) thiz;
    if (context_ptr == 0) {
        return -1;
    }

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    jfloat *audio = (*env)->GetFloatArrayElements(env, audio_data, NULL);
    const jsize audio_length = (*env)->GetArrayLength(env, audio_data);

    const char *language = (*env)->GetStringUTFChars(env, language_str, NULL);
    const char *initial_prompt = NULL;
    if (initial_prompt_str != NULL) {
        initial_prompt = (*env)->GetStringUTFChars(env, initial_prompt_str, NULL);
    }

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.print_realtime = false;
    params.print_progress = false;
    params.print_timestamps = false;
    params.print_special = false;
    params.translate = false;
    params.language = language;
    params.n_threads = num_threads;
    params.offset_ms = 0;
    params.no_context = true;
    params.single_segment = false;
    if (initial_prompt != NULL && strlen(initial_prompt) > 0) {
        params.initial_prompt = initial_prompt;
    }

    LOGI("whisper_full samples=%d threads=%d lang=%s", (int) audio_length, (int) num_threads, language);
    int result = whisper_full(context, params, audio, audio_length);

    (*env)->ReleaseFloatArrayElements(env, audio_data, audio, JNI_ABORT);
    (*env)->ReleaseStringUTFChars(env, language_str, language);
    if (initial_prompt != NULL) {
        (*env)->ReleaseStringUTFChars(env, initial_prompt_str, initial_prompt);
    }

    return result;
}

JNIEXPORT jint JNICALL
Java_br_com_banavita_app_core_speech_WhisperLib_getTextSegmentCount(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr) {
    (void) env;
    (void) thiz;
    if (context_ptr == 0) {
        return 0;
    }
    return whisper_full_n_segments((struct whisper_context *) context_ptr);
}

JNIEXPORT jstring JNICALL
Java_br_com_banavita_app_core_speech_WhisperLib_getTextSegment(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint index) {
    (void) thiz;
    if (context_ptr == 0) {
        return (*env)->NewStringUTF(env, "");
    }
    const char *text = whisper_full_get_segment_text((struct whisper_context *) context_ptr, index);
    return (*env)->NewStringUTF(env, text == NULL ? "" : text);
}
