#!/usr/bin/env node
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import https from 'node:https';
import http from 'node:http';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, '..');
const MODELS_DIR = path.join(ROOT, 'models');
const ASSETS_DIR = path.join(ROOT, '..', '..', 'app', 'src', 'main', 'assets', 'models');
const CHECKSUMS_PATH = path.join(ROOT, 'checksums.json');

const MODEL = {
  id: 'ggml-base-q5_1',
  fileName: 'ggml-base-q5_1.bin',
  url: 'https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base-q5_1.bin',
  expectedSha256: '422f1ae452ade6f30a004d7e5c6a43195e4433bc370bf23fac9cc591f01a8898',
};

function download(url, dest) {
  return new Promise((resolve, reject) => {
    const file = fs.createWriteStream(dest);
    const client = url.startsWith('https') ? https : http;

    const request = client.get(url, (response) => {
      if (response.statusCode >= 300 && response.statusCode < 400 && response.headers.location) {
        file.close();
        fs.unlinkSync(dest);
        download(response.headers.location, dest).then(resolve).catch(reject);
        return;
      }
      if (response.statusCode !== 200) {
        reject(new Error(`HTTP ${response.statusCode} ao baixar ${url}`));
        return;
      }
      response.pipe(file);
      file.on('finish', () => file.close(() => resolve()));
    });
    request.on('error', reject);
  });
}

function sha256File(filePath) {
  const hash = crypto.createHash('sha256');
  const data = fs.readFileSync(filePath);
  hash.update(data);
  return hash.digest('hex');
}

async function main() {
  fs.mkdirSync(MODELS_DIR, { recursive: true });
  const dest = path.join(MODELS_DIR, MODEL.fileName);

  if (fs.existsSync(dest)) {
    const existing = sha256File(dest);
    if (existing === MODEL.expectedSha256) {
      console.log(`Modelo já presente e válido:\n${dest}`);
      writeChecksums(existing, dest);
      copyToAssets(dest);
      printBuildInstructions(dest);
      return;
    }
    console.log('Arquivo existente com checksum diferente — baixando novamente...');
  }

  console.log(`Baixando ${MODEL.fileName} ...`);
  await download(MODEL.url, dest);
  const actual = sha256File(dest);
  if (actual !== MODEL.expectedSha256) {
    throw new Error(
      `SHA-256 inválido.\nEsperado: ${MODEL.expectedSha256}\nObtido:   ${actual}`,
    );
  }

  writeChecksums(actual, dest);
  console.log(`OK: ${dest}`);
  console.log(`SHA-256: ${actual}`);
  copyToAssets(dest);
  printBuildInstructions(dest);
}

function copyToAssets(localPath) {
  fs.mkdirSync(ASSETS_DIR, { recursive: true });
  const assetDest = path.join(ASSETS_DIR, MODEL.fileName);
  fs.copyFileSync(localPath, assetDest);
  console.log(`Copiado para assets do APK:\n${assetDest}`);
}

function writeChecksums(sha256, filePath) {
  const payload = {
    models: [
      {
        id: MODEL.id,
        fileName: MODEL.fileName,
        sha256,
        url: MODEL.url,
        localPath: filePath,
        updatedAt: new Date().toISOString(),
      },
    ],
  };
  fs.writeFileSync(CHECKSUMS_PATH, `${JSON.stringify(payload, null, 2)}\n`, 'utf8');
  console.log(`Checksums gravados em ${CHECKSUMS_PATH}`);
}

function printBuildInstructions(localPath) {
  console.log(`
Próximos passos (modelo embutido no APK):

1. O arquivo já foi copiado para app/src/main/assets/models/.
2. Gere o APK no Android Studio ou via Gradle:

   ./gradlew assembleDebug

3. Na primeira execução, o app extrai o modelo para:

   files/models/${MODEL.fileName}

Build sem modelo (só UI, sem voz):

   ./gradlew assembleDebug -PskipSpeechModel

Sidecar manual (legado / debug avançado):

   adb push "${localPath}" /data/local/tmp/${MODEL.fileName}
   adb shell run-as br.com.banavita.app mkdir -p files/models
   adb shell run-as br.com.banavita.app cp /data/local/tmp/${MODEL.fileName} files/models/${MODEL.fileName}
`);
}

main().catch((error) => {
  console.error(`Erro: ${error.message}`);
  process.exit(1);
});
