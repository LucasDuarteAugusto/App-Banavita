# Android Studio — ícones Unresolved e warnings

Os `Unresolved reference` de ícones (`People`, `Inventory`, `Mic`, etc.) voltavam porque essas chaves existem só em `material-icons-extended`, que o Android Studio não resolve de forma estável. Passei esses ícones para vetores locais e corrigi os warnings anotados no código.

## Criado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/ui/common/BanavitaIcons.kt` |
| `app/src/main/res/drawable/ic_people.xml` |
| `app/src/main/res/drawable/ic_local_shipping.xml` |
| `app/src/main/res/drawable/ic_shopping_bag.xml` |
| `app/src/main/res/drawable/ic_category.xml` |
| `app/src/main/res/drawable/ic_inventory.xml` |
| `app/src/main/res/drawable/ic_apps.xml` |
| `app/src/main/res/drawable/ic_event.xml` |
| `app/src/main/res/drawable/ic_store.xml` |
| `app/src/main/res/drawable/ic_payments.xml` |
| `app/src/main/res/drawable/ic_mic.xml` |
| `app/src/main/res/drawable/ic_visibility.xml` |
| `app/src/main/res/drawable/ic_visibility_off.xml` |
| `app/src/main/res/drawable/ic_content_copy.xml` |

## Alterado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/ui/cadastros/CadastrosHubScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceFabLayout.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceEnabledScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/activation/ActivationScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/agenda/EventoFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/core/time/PeriodRanges.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/AndroidReminderScheduler.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/FinanceiroRepository.kt` |
| `app/build.gradle.kts` |
| `gradle/libs.versions.toml` |

## Removido

| Caminho |
|---------|
| Dependência `material-icons-extended` (catalog + `app/build.gradle.kts`) |

## O que conferir na IDE

1. **Sync Gradle** (trocou `androidxMaterialIconsExtended` por `androidx.compose.material.icons.core`).
2. `CadastrosHubScreen.kt` — `People` / `LocalShipping` / `ShoppingBag` / `Category` não devem mais aparecer como Unresolved.
3. `MainAppShell.kt`, `HomeScreen.kt`, `VoiceFabLayout.kt`, `ActivationScreen.kt` — mesmo para `Inventory`, `Apps`, `Event`, `Store`, `Payments`, `Mic`, `Visibility`, `ContentCopy`.
4. Telas: Cadastros, Início, abas Compras/Cadastros, FAB de voz, ativação (olho da licença e copiar código).
5. Warnings: `VoiceEnabledScreen` (`modifier` como primeiro opcional), `PeriodRanges` (range check), `AndroidReminderScheduler` (`@param:ApplicationContext`), `EventoFormScreen` (callback de permissão sem `if` vazio).
