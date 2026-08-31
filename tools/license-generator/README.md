# Gerador de licenças Banavita (PC)

Ferramenta Node.js para gerar licenças offline amarradas a um dispositivo.

## Pré-requisitos

- Node.js 18+

## Setup inicial (uma vez)

```bash
cd tools/license-generator
npm run generate-keys
```

Isso cria:

- `keys/private.pem` — **mantenha só no seu PC** (não commitar)
- `keys/public.pem` — referência local
- Atualiza a chave pública em `LicenseConstants.kt` no app

## Gerar licença permanente

```bash
npm run generate -- --request "BAN1-REQ-..." --type permanent
```

## Gerar licença mensal (30 dias após ativação)

```bash
npm run generate -- --request "BAN1-REQ-..." --type monthly
```

## Entrada manual (sem request code)

```bash
npm run generate -- --bfp <hex_base> --kfp <hex_keystore> --type permanent
```

## Salvar em arquivo

```bash
npm run generate -- --request "BAN1-REQ-..." --type monthly --output licenca.txt
```

## Fluxo operacional

1. Usuário instala o APK e abre o app.
2. App mostra código `BAN1-REQ-...` (tela futura).
3. Usuário envia o código para você.
4. Você roda este gerador e devolve `BAN1-LIC-...`.
5. Usuário cola a licença no app.

## Regenerar chaves (produção)

Antes da primeira release pública, rode `npm run generate-keys` novamente em ambiente seguro e distribua um novo APK com a nova chave pública. Licenças antigas deixam de funcionar.
