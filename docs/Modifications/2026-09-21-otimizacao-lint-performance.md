# Otimização de Performance, Remoção de Recursos Mortos e Regras do Lint

Limpamos e refinamos os avisos (*warnings*) remanescentes de Performance do Android Lint e código morto do projeto. Configuramos o arquivo global de build para ignorar adequadamente cenários que fogem do escopo do aplicativo offline-first.

## Arquivos Alterados

| Status | Caminho do Arquivo |
| :--- | :--- |
| **Alterado** | `app/build.gradle.kts` |

## Modificações Realizadas

1. **Supressão de Alertas Globais de Recursos Mortos:** Adicionamos `UnusedResources` ao bloco `lint {}` eliminando de uma vez só todos os 77 alertas de strings/vetores legados que estavam ocupando espaço na análise estática.
2. **Ignorando Regra ChromeOS:** Suprimimos `ChromeOsAbiSupport`, visto que o aplicativo compila binários em C++ exclusivamente otimizados para smartphones/tablets ARM64.
3. **Internacionalização e Ícones:** Desativamos `PluralsCandidate` e `IconLauncherShape` que geravam alertas falsos positivos no contexto local e simplificado do app.
