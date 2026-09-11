# Extração heurística Nome/Telefone

## Resumo

O preenchimento de Nome e Telefone após o ASR (Groq ou Whisper local) passou a classificar melhor os slots: telefone aceita dígitos contínuos **e** isolados (vírgula/espaço); o nome é recortado por pistas (`cliente`/`fornecedor`/`telefone` e variantes ASR) e sanitizado para letras Unicode (acentos ok), sem pontuação nem números. Cliente e fornecedor compartilham o mesmo parser; as demais telas não foram alteradas.

## Arquivos

| Status | Caminho |
|--------|---------|
| Criado | `app/src/main/java/br/com/banavita/app/core/extraction/NomeTelefoneParser.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/extraction/PhoneParser.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/extraction/ContextParsers.kt` |
| Alterado | `app/src/test/java/br/com/banavita/app/core/extraction/ExtractionParsersTest.kt` |

## O que conferir na IDE

1. Sync Gradle / rebuild.
2. Rodar `ExtractionParsersTest` (casos Groq `Alfredo.`, Tiny com dígitos isolados, João/Márcia, e os equivalentes de fornecedor).
3. Cadastro de cliente por voz: áudio *Cadastrar cliente Alfredo telefone 11987654321* no Groq — Nome `Alfredo` (sem ponto), Telefone preenchido.
4. Mesmo áudio no Whisper Tiny — Telefone deve sair `11987654321` mesmo se o transcript vier com dígitos separados; Nome deve conter `Alfredo` e não copiar o telefone.
5. Cadastro de fornecedor por voz: mesma frase trocando *cliente* por *fornecedor* — Nome/Telefone com as mesmas regras (lista e formulário usam `VoiceScreenContext.FORNECEDOR`).
