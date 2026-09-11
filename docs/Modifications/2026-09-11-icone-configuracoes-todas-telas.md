# Ícone de configurações em todas as telas

O botão da engrenagem, que só existia no Início, passa a aparecer na top bar de todas as telas do app ativado. A tela de Liberação continua sem o ícone. Na própria tela de Configurações o ícone fica oculto (já está nela).

## Criado

| Caminho |
| ------- |
| `app/src/main/res/drawable/ic_settings.xml` |

## Alterado

| Caminho |
| ------- |
| `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| `docs/ui/README.md` |
| `docs/ui/mockups/*.png` (todas, exceto `activation.png` e `settings.png`) |

## Removido

Nenhum.

## O que conferir na IDE

1. Confirmar que `ic_settings.xml` resolve `R.drawable.ic_settings` em `BanavitaIcons.Settings` (Sync se o ícone ainda aparecer vermelho).
2. `MainAppShell.kt` — a engrenagem deve aparecer na top bar de Início, Vendas, Compras, Cadastros, listas, formulários e consultas.
3. Tocar a engrenagem em qualquer dessas telas deve abrir Configurações; Voltar retorna à tela de origem.
4. Liberação do aplicativo: sem engrenagem. Configurações: sem engrenagem (só Voltar).
