# Agenda: chip "1 dia antes" na linha seguinte

## Resumo

No formulário de evento (`Novo evento` / editar), o chip de antecipação **1 dia antes** ficava espremido na mesma `Row` dos demais e o texto saía na vertical. O chip foi movido para a linha de baixo, com texto horizontal.

## Arquivos

| Status | Caminho |
|--------|---------|
| Alterado | `app/src/main/java/br/com/banavita/app/ui/agenda/EventoFormScreen.kt` |
| Criado | `docs/Modifications/2026-09-08-agenda-chip-1-dia-antes.md` |

## O que conferir na IDE

1. Abrir **Agenda → Novo evento** (ou editar com lembrete ligado).
2. Ativar **Com lembrete**.
3. Conferir chips: primeira linha `Na hora` / `15 min antes` / `1 h antes`; segunda linha `1 dia antes` com texto na horizontal.
4. Selecionar cada opção e salvar normalmente.
