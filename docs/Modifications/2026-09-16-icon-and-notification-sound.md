# Novo Ícone e Som de Notificação Personalizado

Substituição do ícone do aplicativo e implementação de som personalizado para lembretes, com suporte a escolha de toques do sistema nas configurações.

## Arquivos Criados
| Arquivo | Ação |
| :--- | :--- |
| `app/src/main/res/raw/olha_a_banana.m4a` | Criado (movido de assets) |
| `app/src/main/res/mipmap/ic_launcher_banavita.png` | Criado (renomeado) |
| `docs/Modifications/2026-09-16-icon-and-notification-sound.md` | Criado |

## Arquivos Alterados
| Arquivo | Ação |
| :--- | :--- |
| `app/src/main/AndroidManifest.xml` | Alterado |
| `app/src/main/java/br/com/banavita/app/BanavitaApplication.kt` | Alterado |
| `app/src/main/java/br/com/banavita/app/core/reminder/AgendaAlarmReceiver.kt` | Alterado |
| `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisAlarmReceiver.kt` | Alterado |
| `app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt` | Alterado |
| `app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt` | Alterado |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` | Alterado |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` | Alterado |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` | Alterado |
| `app/src/main/res/values/strings.xml` | Alterado |

## Arquivos Removidos
| Arquivo | Ação |
| :--- | :--- |
| `app/src/main/assets/sons/Olha a banana.m4a` | Removido (movido) |
| `app/src/main/res/mipmap/Icone Banavita.png` | Removido (renomeado) |

## O que conferir na IDE
1. Verifique se o ícone do app agora é a imagem do Banavita (gaveta de apps).
2. Vá em **Configurações > Notificações** e teste a alteração do som de aviso.
3. Certifique-se de que, ao receber um lembrete (Agenda ou Perecíveis), o áudio "Olha a banana" toca por padrão.
