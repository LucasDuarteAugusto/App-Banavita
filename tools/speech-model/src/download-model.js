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
      printAdbInstructions(dest);
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
  printAdbInstructions(dest);
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

function printAdbInstructions(localPath) {
  console.log(`
Próximos passos (instalação sidecar no aparelho):

1. Instale o APK do Banavita no celular.
2. Crie a pasta de modelos (troque o userId se necessário):

   adb shell mkdir -p /data/data/br.com.banavita.app/files/models

3. Envie o modelo (requer run-as em debug ou root; alternativa: storage acessível + cópia no app):

   adb push "${localPath}" /data/local/tmp/${MODEL.fileName}
   adb shell run-as br.com.banavita.app cp /data/local/tmp/${MODEL.fileName} files/models/${MODEL.fileName}

4. Confirme:

   adb shell run-as br.com.banavita.app ls -la files/models/
`);
}

main().catch((error) => {
  console.error(`Erro: ${error.message}`);
  process.exit(1);
});
