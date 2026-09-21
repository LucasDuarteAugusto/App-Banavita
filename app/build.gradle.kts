plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.androidx.room)
}

fun loadDotEnv(file: File): Map<String, String> {
    if (!file.exists()) return emptyMap()
    return file.readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
        .associate { line ->
            val idx = line.indexOf('=')
            line.substring(0, idx).trim() to line.substring(idx + 1).trim()
        }
}

val rootEnv = loadDotEnv(rootProject.file(".env"))
val groqApiKey = rootEnv["GROQ_API_KEY"]
    ?: rootEnv["GROK_API_KEY"]
    ?: ""

val whisperTinyConfig = mapOf(
    "id" to "ggml-tiny-q5_1",
    "file" to "ggml-tiny-q5_1.bin",
    "sha256" to "818710568da3ca15689e31a743197b520007872ff9576237bda97bd1b469c3d7",
    "profile" to "MINIMUM",
    "size" to "32"
)
val whisperBaseConfig = mapOf(
    "id" to "ggml-base-q5_1",
    "file" to "ggml-base-q5_1.bin",
    "sha256" to "422f1ae452ade6f30a004d7e5c6a43195e4433bc370bf23fac9cc591f01a8898",
    "profile" to "MINIMUM",
    "size" to "57"
)

android {
    namespace = "br.com.banavita.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.banavita.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "WHISPER_TINY_ID", "\"${whisperTinyConfig["id"]}\"")
        buildConfigField("String", "WHISPER_TINY_FILE", "\"${whisperTinyConfig["file"]}\"")
        buildConfigField("String", "WHISPER_TINY_SHA256", "\"${whisperTinyConfig["sha256"]}\"")
        buildConfigField("String", "WHISPER_TINY_PROFILE", "\"${whisperTinyConfig["profile"]}\"")
        buildConfigField("int", "WHISPER_TINY_SIZE_MB", "${whisperTinyConfig["size"]}")

        buildConfigField("String", "WHISPER_BASE_ID", "\"${whisperBaseConfig["id"]}\"")
        buildConfigField("String", "WHISPER_BASE_FILE", "\"${whisperBaseConfig["file"]}\"")
        buildConfigField("String", "WHISPER_BASE_SHA256", "\"${whisperBaseConfig["sha256"]}\"")
        buildConfigField("String", "WHISPER_BASE_PROFILE", "\"${whisperBaseConfig["profile"]}\"")
        buildConfigField("int", "WHISPER_BASE_SIZE_MB", "${whisperBaseConfig["size"]}")

        ndk {
            abiFilters += "arm64-v8a"
        }

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments += listOf(
                    "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON",
                    "-DGGML_OPENMP=OFF",
                    "-DGGML_LLAMAFILE=OFF",
                    "-DGGML_NATIVE=OFF",
                    "-DWHISPER_BUILD_EXAMPLES=OFF",
                    "-DWHISPER_BUILD_TESTS=OFF",
                    "-DBUILD_SHARED_LIBS=OFF",
                    "-DCMAKE_C_FLAGS_DEBUG=-O3",
                    "-DCMAKE_CXX_FLAGS_DEBUG=-O3",
                )
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        disable += "ChromeOsAbiSupport"
        disable += "UnusedResources"
        disable += "PluralsCandidate"
        disable += "IconLauncherShape"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

val groqBuildFieldsFile = layout.projectDirectory.file(
    "src/main/java/br/com/banavita/app/GroqBuildFields.kt",
)

tasks.register("generateGroqBuildFields") {
    group = "banavita"
    description = "Gera GroqBuildFields.kt a partir do .env (não versionar o arquivo gerado)"
    inputs.property("groqApiKey", groqApiKey)
    outputs.file(groqBuildFieldsFile)
    doLast {
        val escapedKey = groqApiKey
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("$", "\\$")
        val content = """
            package br.com.banavita.app

            /**
             * Valores da Groq injetados a partir do `.env` (tarefa Gradle `generateGroqBuildFields`).
             * Arquivo gerado — não versionar; ver `GroqBuildFields.kt.example`.
             */
            object GroqBuildFields {
                const val API_KEY: String = "$escapedKey"
                const val WHISPER_MODEL: String = "whisper-large-v3-turbo"
            }

        """.trimIndent() + "\n"
        groqBuildFieldsFile.asFile.parentFile.mkdirs()
        groqBuildFieldsFile.asFile.writeText(content)
    }
}

tasks.named("preBuild") {
    dependsOn("generateGroqBuildFields")
    dependsOn("verifySpeechModelForBuild")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.core)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidxNavigationCompose)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.security.crypto)
    implementation(libs.okhttp)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    debugImplementation(libs.androidx.ui.tooling)
}

tasks.register("downloadWhisperTiny", Exec::class) {
    group = "banavita"
    description = "Baixa o modelo Whisper Tiny via Node.js"
    commandLine("npm", "--prefix", "../tools/speech-model", "run", "download", "--", "tiny")
}

tasks.register("downloadWhisperBase", Exec::class) {
    group = "banavita"
    description = "Baixa o modelo Whisper Base via Node.js"
    commandLine("npm", "--prefix", "../tools/speech-model", "run", "download", "--", "base")
}

val speechModelAssetsDir = layout.projectDirectory.dir("src/main/assets/models")

tasks.register<Copy>("copySpeechModelToAssets") {
    group = "banavita"
    description = "Copia os modelos GGML baixados para assets/ antes do build"
    doFirst {
        delete(speechModelAssetsDir)
    }
    from(rootProject.file("tools/speech-model/models")) {
        include(whisperBaseConfig["file"]!!)
    }
    into(speechModelAssetsDir)
}

tasks.register("verifySpeechModelForBuild") {
    group = "banavita"
    description = "Garante que os modelos ASR estão em assets/ (use -PskipSpeechModel para pular)"
    dependsOn("copySpeechModelToAssets")
    doLast {
        if (project.hasProperty("skipSpeechModel")) {
            logger.warn("skipSpeechModel: APK será gerado sem modelo ASR embutido")
            return@doLast
        }
        val baseFile = speechModelAssetsDir.file(whisperBaseConfig["file"]!!).asFile
        
        if (!baseFile.exists()) {
            throw GradleException("Modelo Base ausente. Execute: ./gradlew downloadWhisperBase")
        }
    }
}
