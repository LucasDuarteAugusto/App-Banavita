import crypto from 'node:crypto';

const LICENSE_VERSION = 1;
const REQUEST_PREFIX = 'BAN1-REQ-';
const LICENSE_PREFIX = 'BAN1-LIC-';

export function buildCanonicalMessage({ bfp, kfp, type }) {
  return `v${LICENSE_VERSION}|bfp=${bfp}|kfp=${kfp}|type=${type}`;
}

export function parseRequestCode(requestCode) {
  if (!requestCode.startsWith(REQUEST_PREFIX)) {
    throw new Error('Código de solicitação inválido. Deve iniciar com BAN1-REQ-.');
  }

  const encoded = requestCode.slice(REQUEST_PREFIX.length);
  const json = Buffer.from(encoded, 'base64url').toString('utf8');
  const payload = JSON.parse(json);

  if (!payload.bfp || !payload.kfp) {
    throw new Error('Código de solicitação incompleto (bfp/kfp ausentes).');
  }

  return {
    bfp: payload.bfp,
    kfp: payload.kfp,
    hardwareBacked: Boolean(payload.hw),
  };
}

export function buildLicenseToken({ bfp, kfp, type }, privateKeyPem) {
  const canonical = buildCanonicalMessage({ bfp, kfp, type });
  const signature = signCanonicalMessage(canonical, privateKeyPem);
  const payloadPart = Buffer.from(canonical, 'utf8').toString('base64url');
  const signaturePart = signature.toString('base64url');
  return `${LICENSE_PREFIX}${payloadPart}.${signaturePart}`;
}

function signCanonicalMessage(message, privateKeyPem) {
  const signer = crypto.createSign('RSA-SHA256');
  signer.update(message);
  signer.end();
  return signer.sign(privateKeyPem);
}

export { LICENSE_PREFIX, REQUEST_PREFIX };
