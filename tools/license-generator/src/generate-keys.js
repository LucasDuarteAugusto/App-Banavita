#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { generateKeyPairSync } from 'node:crypto';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, '..');
const KEYS_DIR = path.join(ROOT, 'keys');
const PRIVATE_KEY_PATH = path.join(KEYS_DIR, 'private.pem');
const PUBLIC_KEY_PATH = path.join(KEYS_DIR, 'public.pem');

const ANDROID_CONSTANTS_PATH = path.resolve(
  ROOT,
  '../../app/src/main/java/br/com/banavita/app/core/security/LicenseConstants.kt',
);

function main() {
  fs.mkdirSync(KEYS_DIR, { recursive: true });

  const { publicKey, privateKey } = generateKeyPairSync('rsa', {
    modulusLength: 2048,
    publicKeyEncoding: { type: 'spki', format: 'pem' },
    privateKeyEncoding: { type: 'pkcs8', format: 'pem' },
  });

  fs.writeFileSync(PRIVATE_KEY_PATH, privateKey, 'utf8');
  fs.writeFileSync(PUBLIC_KEY_PATH, publicKey, 'utf8');

  if (fs.existsSync(ANDROID_CONSTANTS_PATH)) {
    const kotlinSource = fs.readFileSync(ANDROID_CONSTANTS_PATH, 'utf8');
    const updated = kotlinSource.replace(
      /const val PUBLIC_KEY_PEM = """[\s\S]*?"""/,
      `const val PUBLIC_KEY_PEM = """\n${publicKey.trim()}\n    """`,
    );
    fs.writeFileSync(ANDROID_CONSTANTS_PATH, updated, 'utf8');
    console.log(`Chave pública sincronizada em:\n${ANDROID_CONSTANTS_PATH}`);
  }

  console.log('Par de chaves RSA gerado:');
  console.log(`- Privada: ${PRIVATE_KEY_PATH}`);
  console.log(`- Pública:  ${PUBLIC_KEY_PATH}`);
  console.log('\nMantenha private.pem apenas no seu PC.');
}

main();
