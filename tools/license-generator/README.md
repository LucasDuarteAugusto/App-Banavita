# Gerador de licenças Banavita (PC)

Ferramenta Node.js para gerar licenças offline amarradas a um dispositivo.

Documentação técnica completa: [docs/activation/README.md](../../docs/activation/README.md)

## Pré-requisitos

- Node.js 14+ (18+ recomendado)

## Setup inicial (uma vez)

```bash
cd tools/license-generator
npm run generate-keys
```

Isso cria:

- `keys/private.pem` — **mantenha só no seu PC** (não commitar; já está no `.gitignore`)
- `keys/public.pem` — referência local (pode commitar)
- Atualiza a chave pública em `app/src/main/java/.../LicenseConstants.kt`

**Importante:** após `generate-keys`, faça um **novo build do APK**. Licenças só funcionam se o app embutir a mesma chave pública do par gerado.

> Se `private.pem` já existir no seu PC, não rode `generate-keys` de novo — use a chave atual para gerar licenças.

## Testar no celular (passo a passo)

1. **Build e instale** o APK no aparelho (Android Studio ou `./gradlew assembleDebug`).
2. Abra o app → tela **Liberação do aplicativo**.
3. Toque em **copiar** no código `BAN1-REQ-...` e envie para o PC.
4. No PC:

```bash
cd tools/license-generator
npm run generate -- --request "BAN1-REQ-COLE_AQUI" --type permanent
```

5. Copie o `BAN1-LIC-...` exibido no terminal.
6. No celular, cole no campo **Licença** e toque **Liberar aplicativo**.

## Gerar licença permanente

```bash
npm run generate -- --request "BAN1-REQ-..." --type permanent
```

Aliases: `--type permanente`

## Gerar licença mensal (30 dias após ativação)

```bash
npm run generate -- --request "BAN1-REQ-..." --type monthly
```

Aliases: `--type mensal`, `--type 1month`

A contagem dos 30 dias começa na **primeira ativação** no celular, não na geração da licença.

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
2. App mostra código `BAN1-REQ-...` (copiar e enviar).
3. Você gera a licença com este script e devolve `BAN1-LIC-...`.
4. Usuário cola a licença e libera o app.

## Regenerar chaves (produção)

Antes da primeira release pública, rode `npm run generate-keys` em ambiente seguro, faça novo build do APK e distribua. Licenças antigas deixam de funcionar.

## Solução de problemas

| Erro no app | O que fazer |
|-------------|-------------|
| Assinatura inválida | APK foi buildado com chave pública diferente da `private.pem` → alinhe com `generate-keys` + rebuild |
| Dispositivo não corresponde | Licença gerada para outro `BAN1-REQ-...` |
| Licença expirada | Tipo `monthly` após 30 dias — gere outra ou use `permanent` em testes |
