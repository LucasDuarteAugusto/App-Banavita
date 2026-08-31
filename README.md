# App-Banavita

Aplicativo para controle offline do negócio Banavita.

## Stack

- Kotlin + Jetpack Compose (UI placeholder)
- Room (SQLite) com KSP
- Hilt + Coroutines/Flow
- Arquitetura MVVM/Clean (camada de dados implementada)

## Requisitos de build

- JDK 17+
- Android SDK (API 35)
- Android Studio Ladybug ou superior (recomendado)

## Estrutura

```
app/src/main/java/br/com/banavita/app/
  data/local/          # Room: entities, DAOs, database, view de estoque
  data/repository/     # Repositórios transacionais
  domain/              # Modelos e exceções de domínio
  core/                # Utilitários (Money, tempo, segurança/ativação)
docs/database/         # Documentação do schema SQLite
docs/activation/       # Documentação do controle de acesso offline
tools/license-generator/  # Gerador Node.js de licenças (PC)
```

## Banco de dados

Ver [docs/database/README.md](docs/database/README.md) para tabelas, FKs, view de estoque e regras de negócio.

## Ativação por dispositivo

Ver [docs/activation/README.md](docs/activation/README.md) e [tools/license-generator/README.md](tools/license-generator/README.md).

## Etapas do projeto

- [x] Banco de dados (Room + repositórios)
- [x] Controle de acesso offline (backend + gerador Node.js)
- [ ] IA local para transcrever áudios em português
- [ ] IA local para classificar e extrair informações
- [ ] Tela de cadastro de produtos
- [ ] Tela de cadastro de clientes
- [ ] Tela de cadastro vendas
- [ ] Tela de cadastro de compras
- [ ] Tela de acompanhamento de vendas
- [ ] Tela de acompanhamento de compras

## Testes

```bash
./gradlew test
```

Testes de integração Room usam Robolectric + banco in-memory.
