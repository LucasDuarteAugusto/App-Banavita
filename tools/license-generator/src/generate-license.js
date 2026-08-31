#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { buildLicenseToken, parseRequestCode } from './license-format.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, '..');
const KEYS_DIR = path.join(ROOT, 'keys');
const PRIVATE_KEY_PATH = path.join(KEYS_DIR, 'private.pem');

function printUsage() {
  console.log(`Uso:
  node src/generate-license.js --request "<BAN1-REQ-...>" --type permanent
  node src/generate-license.js --request "<BAN1-REQ-...>" --type monthly
  node src/generate-license.js --bfp <hex> --kfp <hex> --type permanent|monthly

Opções:
  --request   Código enviado pelo app (preferencial)
  --bfp       Fingerprint base (hex) manual
  --kfp       Fingerprint Keystore (hex) manual
  --type      permanent | monthly
  --output    Caminho opcional para salvar a licença em arquivo`);
}

function parseArgs(argv) {
  const args = {};
  for (let i = 0; i < argv.length; i += 1) {
    const key = argv[i];
    if (!key.startsWith('--')) continue;
    const value = argv[i + 1];
    args[key.slice(2)] = value;
    i += 1;
  }
  return args;
}

function normalizeType(rawType) {
  const value = (rawType ?? '').toLowerCase();
  if (value === 'permanent' || value === 'permanente') return 'PERMANENT';
  if (value === 'monthly' || value === 'mensal' || value === '1month') return 'MONTHLY';
  throw new Error('Tipo inválido. Use permanent ou monthly.');
}

function loadPrivateKey() {
  if (!fs.existsSync(PRIVATE_KEY_PATH)) {
    throw new Error(
      `Chave privada não encontrada em ${PRIVATE_KEY_PATH}. Execute: npm run generate-keys`,
    );
  }
  return fs.readFileSync(PRIVATE_KEY_PATH, 'utf8');
}

function resolveFingerprints(args) {
  if (args.request) {
    const parsed = parseRequestCode(args.request.trim());
    return { bfp: parsed.bfp, kfp: parsed.kfp, hardwareBacked: parsed.hardwareBacked };
  }

  if (args.bfp && args.kfp) {
    return { bfp: args.bfp.trim(), kfp: args.kfp.trim(), hardwareBacked: false };
  }

  throw new Error('Informe --request ou o par --bfp/--kfp.');
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  if (args.help || args.h) {
    printUsage();
    return;
  }

  const type = normalizeType(args.type);
  const fingerprints = resolveFingerprints(args);
  const privateKey = loadPrivateKey();
  const licenseToken = buildLicenseToken(
    {
      bfp: fingerprints.bfp,
      kfp: fingerprints.kfp,
      type,
    },
    privateKey,
  );

  console.log('Licença gerada com sucesso:\n');
  console.log(licenseToken);
  console.log('\nResumo:');
  console.log(`- Tipo: ${type}`);
  console.log(`- BFP: ${fingerprints.bfp}`);
  console.log(`- KFP: ${fingerprints.kfp}`);
  console.log(`- Keystore hardware-backed (no aparelho): ${fingerprints.hardwareBacked}`);

  if (type === 'MONTHLY') {
    console.log('- Validade: 30 dias após a ativação no celular');
  } else {
    console.log('- Validade: permanente');
  }

  if (args.output) {
    fs.writeFileSync(path.resolve(args.output), `${licenseToken}\n`, 'utf8');
    console.log(`\nArquivo salvo em: ${path.resolve(args.output)}`);
  }
}

try {
  main();
} catch (error) {
  console.error(`Erro: ${error.message}`);
  printUsage();
  process.exit(1);
}
