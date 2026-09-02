# Rodar o Banavita no Android Studio Quail

Este guia usa os menus do **Android Studio Quail** (Windows). O objetivo é:

1. Abrir o app no **emulador**
2. Gerar o **APK** e instalar no **celular**

O Banavita só inclui biblioteca nativa **arm64-v8a** (Whisper). Celular real de 64 bits é o caminho mais confiável. Emulador x86_64 padrão costuma falhar na instalação.

## Antes de tudo

### 1. Abrir o projeto

1. Abra o Android Studio Quail.
2. **File → Open** e escolha a pasta `App-Banavita` (a que contém `settings.gradle.kts`).
3. Espere o Gradle terminar (**File → Sync Project with Gradle Files** se o sync não começar sozinho).
4. Confirme o JDK 17 da IDE: **File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**. Deixe o **jbr-17** (JDK embutido do Android Studio).

### 2. Instalar SDK, NDK e CMake

1. **File → Settings → Languages & Frameworks → Android SDK**.
2. Aba **SDK Platforms**: marque **Android 15.0 (API 35)** (ou a plataforma 35 que aparecer).
3. Aba **SDK Tools**, marque:
   - **Android SDK Build-Tools**
   - **NDK (Side by side)**
   - **CMake** (o projeto pede **3.22.1**)
   - **Android Emulator** (se for usar emulador)
   - **Android SDK Platform-Tools**
4. **Apply → OK** e espere baixar.

### 3. Baixar o modelo de voz (obrigatório no APK com voz)

No Terminal do Android Studio (**View → Tool Windows → Terminal**):

```bash
cd tools/speech-model
npm run download
```

Sem esse arquivo, o **Make Project** / **Run** falha no `preBuild` pedindo o modelo. Detalhes: [tools/speech-model/README.md](../../tools/speech-model/README.md).

---

## Parte 1 — Emulador

O app filtra ABI para **arm64-v8a**. AVD com imagem **x86_64** (a mais comum no Windows) tende a recusar o APK (`INSTALL_FAILED_NO_MATCHING_ABIS`) ou crashar no Whisper.

Use emulador só se você conseguir uma imagem **ARM 64 v8a**. No PC Windows x86, isso pode ser lento ou indisponível. Para testar de verdade (voz, microfone, desempenho), prefira o celular.

### Criar o AVD

1. **View → Tool Windows → Device Manager**.
2. Aba **Virtual**.
3. **+** → **Create Virtual Device**.
4. Hardware: **Pixel 7** ou **Medium Phone** → **Next**.
5. System image:
   - API **35** (ou próxima disponível ≥ 26).
   - Na coluna **ABI**, escolha **arm64-v8a** (nome típico: *Google APIs ARM 64 v8a*).
   - Se a imagem não estiver baixada, clique **Download**.
6. **Next** → **Finish**.

RAM do AVD: nas configurações avançadas do dispositivo, use **8 GB** se o PC aguentar. O ASR do Banavita não é suportado em aparelhos de 4 GB.

### Ligar o emulador

No Device Manager, no AVD criado, clique no **Play** (Launch).

Espere a tela inicial do Android aparecer.

### Rodar o app

1. Na barra superior, no seletor de dispositivo, escolha o emulador ligado.
2. Confirme que o módulo é **app**.
3. Clique no **Run** (triângulo verde) ou **Run → Run 'app'**.
4. Na primeira vez, o Android Studio instala o APK e abre o Banavita.

Microfone no emulador: **⋮** (Extended controls) → **Microphone** → permita o host. Ainda assim a voz pode ser pior que no celular.

Se a instalação falhar com ABI, o emulador não é arm64. Use um celular.

---

## Parte 2 — Gerar o APK e passar para o celular

Há dois jeitos na IDE. O que **gera um arquivo** para copiar é o **Generate APKs**. O **Run** instala direto pelo cabo, mas o APK do Run é `testOnly` e **não** instala por arquivo no celular.

### A. Instalar pelo cabo (mais simples no dia a dia)

#### No celular

1. **Ajustes → Sobre o telefone** → toque ~7 vezes em **Número da versão** (ou **Versão do software**) até aparecer “você é um desenvolvedor”.
2. **Ajustes → Sistema → Opções do desenvolvedor** (o caminho varia por marca).
3. Ative **Depuração USB**.
4. Ligue o cabo USB no PC. No celular, autorize o computador.
5. Modo USB: **Transferência de arquivos** (MTP), se o Windows não reconhecer.

Marcas Samsung/Xiaomi: ative também **Instalar via USB** / **Depuração USB (configurações de segurança)** se a instalação for bloqueada.

#### No Android Studio

1. Seletor de dispositivo na barra → o nome do celular deve aparecer.
2. **Run → Run 'app'** (triângulo verde).
3. O Studio compila, instala e abre o app.

Se o aparelho não listar: **View → Tool Windows → Device Manager** → aba **Physical**. Confira o cabo, o driver (Windows Update) e a depuração USB.

### B. Gerar o APK e copiar o arquivo (sem depender do Run)

Use este fluxo para mandar o `.apk` por WhatsApp, Drive ou pasta do celular.

1. No seletor **Build Variants** (**View → Tool Windows → Build Variants**), deixe **app** em **debug** para teste.
2. Menu **Build → Generate App Bundles or APKs → Generate APKs**.  
   (Em algumas builds da IDE o item aparece como **Build → Build Bundle(s) / APK(s) → Build APK(s)**.)
3. Espere o **Build** na parte de baixo chegar em sucesso.
4. Na notificação **APK(s) generated successfully**, clique **locate**.

Caminho típico:

```
App-Banavita/app/build/outputs/apk/debug/app-debug.apk
```

Esse APK de **debug** já vem assinado com a chave de debug e **pode ser instalado tocando no arquivo**. Não use o APK gerado só pelo botão Run para esse envio.

#### Passar o arquivo para o celular

**Opção 1 — pasta USB**

1. Cabo USB, modo transferência de arquivos.
2. Copie `app-debug.apk` para **Download** no celular.
3. No celular, abra **Arquivos**, toque no APK.
4. Se pedir, permita instalar apps desta origem (**Instalar apps desconhecidos**).
5. **Instalar**.

**Opção 2 — Drive / WhatsApp**

1. Envie `app-debug.apk` para você mesmo.
2. Abra no celular → **Download** / **Abrir**.
3. Permita a origem se o Android pedir → **Instalar**.

### APK de release (assinado)

Para um APK que não seja debug (distribuir de forma mais “oficial”, ainda fora da Play Store):

1. **Build → Generate Signed App Bundle or APK**.
2. Escolha **APK** → **Next**.
3. Crie um keystore (só na primeira vez):
   - **Create new…**
   - Pasta segura no PC, senhas que você consiga repetir, validade longa.
   - Guarde o `.jks` — sem ele não atualiza o mesmo app no celular.
4. **release** → **Finish**.
5. A notificação aponta o APK. Caminho típico:

```
App-Banavita/app/release/app-release.apk
```

O `build.gradle` atual está com `isMinifyEnabled = false` no release; ainda assim o arquivo precisa dessa assinatura para instalar como release.

Não compartilhe o keystore nem as senhas.

---

## Primeira abertura no aparelho

O app abre na tela **Liberação do aplicativo**. Sem licença não entra nas abas.

Fluxo resumido:

1. Copie o código `BAN1-REQ-…`.
2. No PC, gere `BAN1-LIC-…` com o gerador de licenças.
3. Cole a licença no app → **Liberar aplicativo**.

Passo a passo: [docs/activation/README.md](../activation/README.md).

Na primeira vez que usar o microfone, o Android pede **Gravar áudio**. Aceite.

Celular: **arm64**, **8 GB de RAM** no perfil mínimo atual. Aparelhos de 4 GB não são suportados para ASR.

---

## Problemas comuns

| O que aparece | O que fazer |
|---------------|-------------|
| Gradle pede modelo ASR | `cd tools/speech-model && npm run download` |
| `INSTALL_FAILED_NO_MATCHING_ABIS` | Emulador/celular não é arm64. Troque o AVD ou use um Galaxy/Pixel 64 bits |
| NDK / CMake not found | SDK Tools: NDK + CMake 3.22.1 |
| Celular não aparece no Run | Depuração USB, cabo de dados, autorizar o PC |
| APK não instala pelo arquivo | Use **Generate APKs** (debug), não o artefato do botão Run |
| “App não instalado” ao atualizar | Assinatura diferente (debug vs release, ou outro keystore). Desinstale o Banavita e instale de novo |
| Voz não transcreve | RAM, permissão de microfone, modelo extraído na 1ª execução |

Documentação de voz: [docs/speech/README.md](../speech/README.md).
