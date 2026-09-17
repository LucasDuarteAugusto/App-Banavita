# Checklist de Validação - App-Banavita

## 🎙️ Funcionalidades de Voz e ASR (Whisper Local / Groq)
- [x] Transcrição offline via modelo **Whisper Base** integrado (precisão e tempo estáveis de ~3s).
- [x] Extração inteligente de dados fonéticos técnicos da Banavita (`Nanica Graúda`, `Prata Miúda`, etc.).
- [x] Inserção dinâmica da Chave API do Groq diretamente da tela de Configurações (com máscara de senha).
- [x] Indicação visual dinâmica de disponibilidade do Groq baseada na presença da chave API.
- [x] Ajuste de UX: aumento em 50% dos botões de microfone (FABs de 60.dp) para evitar escapes de toque.
- [x] Correção de UX: dropdowns fluidos Material 3 abrem instantaneamente ao clicar/digitar na tela de confirmação por voz.
- [x] Mitigação de alucinações iniciais e truncamentos fonéticos ("fiat", "40 mil").

## 📦 Gestão de Estoque e Maturação
- [x] Correção visual do botão "Lançar perda" para a linha inferior no card de lote de estoque.
- [ ] Exibição e persistência da Unidade de Medida (`CX MADEIRA`, `KG`, etc.) no fluxo de compras e lotes.
- [ ] Análise do ciclo do relógio de maturação automatizado por etileno.
- [ ] Validação do fracionamento de estados via "Marcar como...".
- [ ] Conversão e consistência matemática de unidades de Lote de Lançamento de Perdas (CX vs KG).
