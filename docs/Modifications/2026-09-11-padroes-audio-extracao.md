# Padrões de áudio por tela (extração)

Parsers passam a privilegiar a frase curta da tela (só valores, na ordem combinada). A tela continua sendo a classificação; o áudio só preenche slots. Frases longas antigas seguem aceitas.

## Criado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/core/extraction/SpokenLeadIn.kt` |

## Alterado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/core/extraction/MoneyParser.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/PhoneParser.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/NomeTelefoneParser.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/ContextParsers.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/RoleKeywords.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/V2Parsers.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/ExtractionReviewSheet.kt` |
| `app/src/main/res/values/strings.xml` |
| `app/src/test/java/br/com/banavita/app/core/extraction/ExtractionParsersTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/repository/ExtractionRepositoryTest.kt` |
| `docs/extraction/README.md` |

## Removido

Nenhum.

## O que conferir na IDE

1. Sync Gradle.
2. Rodar `ExtractionParsersTest` e `ExtractionRepositoryTest`.
3. Modal de revisão de voz: uma linha de dica do padrão da tela (ex. clientes: “Fale: nome e telefone”).
4. Mic na lista/novo de cliente: `Armando 14936586721` → nome + telefone no sheet.
5. Vendas e Início: `20 quilos nanica amarela pra padaria 8 reais fiado` → item, estado, cliente, preço, fiado. No Início o sheet deve ser de **venda**, não consulta.
6. Compras: `10 caixas nanica do Sítio Verde 6 reais`.
7. Produto: `Nanica 8 reais quilo` → descrição, R$ 8,00, KG.
8. Frases longas antigas (`cadastrar cliente…`, `vende tres quilos de banana…`) ainda preenchem.
