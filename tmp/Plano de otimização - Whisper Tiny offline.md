# Plano de otimização — Whisper Tiny offline no Android

## Objetivo

Reduzir o tempo de transcrição de comandos curtos em português brasileiro, mantendo execução 100% offline e precisão suficiente para operações como cadastro de clientes, telefones, quantidades e endereços.

O cenário atual é um Galaxy A17 executando Whisper Tiny 5.1: um áudio de aproximadamente 10 segundos levou cerca de 50 segundos para transcrever e apresentou erros em palavras e números. A meta inicial é reduzir o tempo para menos de 15 segundos em aparelhos de produção mais fortes e melhorar a interpretação de dados estruturados.

## Princípios de arquitetura

- Manter a interface responsiva: a inferência jamais deve rodar na thread principal.
- Separar claramente captura, pré-processamento, transcrição, pós-processamento e persistência.
- Priorizar modelos e runtimes quantizados, com execução nativa.
- Medir cada etapa individualmente; não medir apenas o tempo total percebido na tela.
- Tratar números, telefone e campos de negócio por pós-processamento determinístico, sem depender apenas do Whisper.
- Permitir troca remota/local de configuração de modelo, sem exigir reescrever o aplicativo.



## Metas mensuráveis


| Métrica                                         | Situação inicial | Meta mínima                      | Meta desejável no Moto G86                               |
| ------------------------------------------------ | ------------------ | --------------------------------- | --------------------------------------------------------- |
| Áudio de 10 segundos                            | ~50 segundos       | Menos de 25 segundos              | 8 a 15 segundos                                           |
| RTF (tempo de inferência / duração do áudio) | ~5,0               | Até 2,5                          | Até 1,5                                                  |
| Erros em telefone ditado                         | Frequentes         | Detectar e pedir confirmação    | Extrair 11 dígitos corretamente em comandos padronizados |
| Travamento da UI                                 | Não aceitável    | Zero                              | Zero                                                      |
| Memória adicional em uso                        | Medir              | Dentro da RAM física disponível | Estável após múltiplas transcrições                  |


RTF é o principal indicador: por exemplo, RTF 1,0 significa que 10 segundos de áudio são transcritos em aproximadamente 10 segundos.

## Pipeline recomendado

```text
AudioRecord
  -> áudio PCM mono, 16 kHz, 16-bit
  -> VAD / remoção de silêncio
  -> normalização e segmentação
  -> Whisper Tiny quantizado via runtime nativo
  -> texto bruto
  -> normalização de português e números
  -> extração de intenção e entidades
  -> validação e confirmação na interface
  -> banco de dados local
```

Cada bloco deve registrar duração, consumo aproximado de memória e resultado para diagnóstico local.

## Fase 1 — Medir a implementação atual

Antes de substituir modelo ou celular, instrumentar o aplicativo para descobrir onde está o gargalo.

- Registrar `t_capture`, `t_preprocessamento`, `t_carregamento_modelo`, `t_inferencia`, `t_pos_processamento` e `t_total`.
- Registrar duração do áudio, taxa de amostragem, número de canais, tamanho do arquivo e quantidade de silêncio removido.
- Registrar versão do modelo, formato, quantização, backend, número de threads e aparelho.
- Rodar cada caso no mínimo cinco vezes, descartando a primeira execução se ela incluir carregamento frio do modelo.
- Medir também aquecimento e bateria em uma sequência de pelo menos 20 comandos.



### Formato de log sugerido

```json
{
  "device": "SM-A17",
  "model": "whisper-tiny",
  "quantization": "fp16",
  "backend": "atual",
  "threads": 4,
  "audio_seconds": 10.0,
  "speech_seconds": 7.8,
  "load_model_ms": 0,
  "preprocess_ms": 120,
  "inference_ms": 49800,
  "postprocess_ms": 35,
  "total_ms": 50020,
  "result": "..."
}
```

Não registrar áudio ou telefone completo em logs de produção sem consentimento. Para logs, mascarar dados pessoais, por exemplo `11987654321` como `119****4321`.

## Fase 2 — Padronizar a entrada de áudio

O Whisper tende a perder precisão se recebe áudio ruim, estéreo, com taxa inadequada ou muito silêncio.

- Usar `AudioRecord` em PCM 16-bit, mono, 16 kHz.
- Desabilitar processamento agressivo que distorça a fala; testar separadamente `NoiseSuppressor`, `AcousticEchoCanceler` e `AutomaticGainControl` quando aplicável.
- Normalizar volume com cuidado para evitar clipping.
- Cortar silêncio inicial e final; não enviar 30 segundos de buffer para um comando de 3 segundos.
- Usar VAD (Voice Activity Detection) para manter apenas fala e criar segmentos coerentes.
- Para o caso de distribuição, preferir gravação por botão: pressionar, falar, soltar. Isso reduz ruído e delimita melhor o comando.



### Critério de segmentação

- Iniciar transcrição após fala detectada.
- Encerrar o segmento após 600 a 1.000 ms de silêncio.
- Limitar cada segmento a aproximadamente 15 a 20 segundos.
- Para áudios mais longos, processar sequencialmente e unir resultados, mantendo o contexto apenas quando necessário.



## Fase 3 — Migrar para inferência nativa

Evitar execução interpretada ou pontes de alto overhead no caminho crítico. A recomendação principal é usar `whisper.cpp` compilado para Android via NDK/JNI.

### Opção preferida: whisper.cpp via JNI

- Compilar `whisper.cpp` com Android NDK para `arm64-v8a`.
- Usar bibliotecas nativas carregadas uma vez durante a inicialização do módulo de voz.
- Manter o contexto/modelo carregado entre transcrições; não recarregar o arquivo a cada comando.
- Executar a inferência em executor dedicado, com prioridade apropriada e cancelamento controlado.
- Testar número de threads igual a 2, 4 e ao número de núcleos de desempenho; escolher por benchmark, não por suposição.
- Usar build otimizado de produção (`Release`, otimizações do compilador e símbolos reduzidos).



### Alternativas a avaliar

- ONNX Runtime Mobile com modelo exportado e quantizado para INT8, testando CPU e NNAPI/GPU quando o delegate for estável.
- ExecuTorch, se houver modelo e backend maduros para a arquitetura escolhida.

Não manter dois runtimes em produção inicialmente. Escolher um após benchmark com os mesmos áudios, mesma configuração e mesmos dispositivos.

## Fase 4 — Quantização e escolha do modelo

A primeira mudança deve ser trocar a variante atual por uma versão quantizada do Whisper Tiny compatível com o runtime escolhido.

### Ordem recomendada de testes


| Prioridade | Variante         | Objetivo                                                              | Risco                                  |
| ---------- | ---------------- | --------------------------------------------------------------------- | -------------------------------------- |
| 1          | Tiny INT8 / Q8_0 | Reduzir memória e melhorar velocidade com perda pequena de precisão | Baixo                                  |
| 2          | Tiny Q5          | Ganhar velocidade adicional preservando boa qualidade                 | Médio                                 |
| 3          | Tiny Q4_0 / Q4_K | Máxima velocidade e menor RAM                                        | Maior perda em fala ruidosa e números |
| 4          | Base INT8        | Melhorar qualidade se Tiny não atingir precisão                     | Mais lento e maior consumo             |




### Regra de decisão

- Começar por **Tiny INT8/Q8_0**.
- Se tempo ainda estiver acima da meta, comparar **Tiny Q5** e **Tiny Q4**.
- Só migrar para **Base INT8** se, com áudio bem capturado e pós-processamento, o Tiny continuar errando nomes e intenções em uma taxa incompatível com o uso.
- Não trocar para Base apenas para resolver telefones: reconhecimento de dígitos deve ser reforçado por regras, confirmação e eventualmente gramática/vocabulário controlado.



## Fase 5 — Configurar a decodificação

Usar parâmetros restritivos para comandos curtos e conhecidos. O objetivo não é transcrever uma entrevista; é reconhecer uma instrução operacional.

- Fixar idioma em `pt` ou `pt-BR` se o runtime permitir, evitando autodetecção.
- Desativar tradução; executar somente transcrição.
- Usar `greedy decoding` inicialmente (`beam_size = 1`), pois comandos são curtos e a prioridade é velocidade.
- Desabilitar timestamps se eles não forem usados na aplicação.
- Desabilitar saídas detalhadas como token-level timestamps e segmentos excessivos.
- Avaliar `no_speech_threshold` para reduzir alucinações em silêncio ou ruído.
- Passar um `initial prompt` curto, sem informações pessoais, contendo vocabulário do domínio, por exemplo: `Cadastro de cliente, nome, telefone, pedido, banana, caixa, entrega.`

O prompt deve ser testado. Um prompt muito longo pode aumentar custo e não substitui validação de entidades.

## Fase 6 — Pós-processamento orientado ao negócio

Não usar o texto bruto do Whisper diretamente para criar cadastros. Transformar a saída em dados validados.

### Normalização textual

- Converter para minúsculas e normalizar espaços.
- Remover pontuação irrelevante sem apagar sinais úteis.
- Corrigir variações frequentes identificadas em testes, por exemplo termos do negócio, bairros, produtos e nomes recorrentes.
- Manter dicionário local de clientes e endereços conhecidos para sugestão, nunca para substituir silenciosamente um dado novo.



### Extração de telefone

O comando pode ser estruturado como: `cadastrar cliente [nome] telefone [número]`.

- Detectar palavras-gatilho: `telefone`, `celular`, `fone`, `número`.
- Aceitar números falados isoladamente, com ou sem separadores.
- Converter palavras de dígitos para algarismos: `zero` a `nove`.
- Remover caracteres não numéricos após a conversão.
- Validar comprimento de 10 ou 11 dígitos para telefone brasileiro; tratar DDD e nono dígito.
- Quando houver 11 dígitos, formatar como `(11) 98765-4321` apenas para exibição.
- Se houver mais de uma sequência candidata ou comprimento inválido, pedir confirmação verbal ou edição manual.



### Exemplo de fluxo seguro

Entrada falada: `cadastrar cliente Alfredo telefone onze nove oito sete seis cinco quatro três dois um`.

Resultado intermediário: nome candidato `Alfredo`; telefone candidato `11987654321`.

Interface: `Cadastrar cliente Alfredo com telefone (11) 98765-4321?` com botões **Confirmar**, **Editar** e **Cancelar**.

Nunca gravar definitivamente o cadastro sem confirmação quando houver telefone, valor, quantidade ou endereço novo.

## Fase 7 — Testes de precisão

Criar um conjunto de testes realista, gravado no ambiente de trabalho. Não medir apenas frases limpas e curtas dentro de casa.

### Conjunto mínimo

- 20 comandos de cadastro de cliente, incluindo nomes comuns e incomuns.
- 20 comandos com telefones de 10 e 11 dígitos.
- 20 comandos de pedido/quantidade, incluindo números por extenso.
- 10 comandos com ruído de rua, veículo, galpão ou conversa ao fundo.
- 10 comandos pronunciados por cada perfil de usuário relevante.
- Áudios de 3 s, 5 s, 10 s e 15 s.



### Critérios de aceitação


| Item                                  | Critério mínimo                          |
| ------------------------------------- | ------------------------------------------ |
| Intenção correta                    | 95% ou mais                                |
| Nome capturado de forma utilizável   | 90% ou mais, com possibilidade de edição |
| Telefone válido após confirmação  | 99% ou mais                                |
| Tempo em Moto G86 para áudio de 10 s | Até 15 s como meta desejável             |
| Tempo em Moto G56 para áudio de 10 s | Até 25 s como meta desejável             |
| Falha/travamento em 100 execuções   | Zero                                       |


Registrar também WER (Word Error Rate) se houver transcrição literal de referência, mas priorizar métricas de negócio: intenção correta, telefone validado, nome adequado e necessidade de edição.

## Fase 8 — Comparativo nos aparelhos

Executar exatamente o mesmo APK, modelo, arquivo de áudio e parâmetros nos aparelhos disponíveis.


| Dispositivo                   | Papel no teste                       | Configuração mínima                              |
| ----------------------------- | ------------------------------------ | --------------------------------------------------- |
| Galaxy A17                    | Base de comparação e pior cenário | Medir versão de RAM, Android e armazenamento livre |
| Moto G56 8/256 GB             | Opção robusta de custo-benefício  | Repetir 5 vezes por áudio, com e sem aquecimento   |
| Moto G86 8/256 GB ou superior | Candidato de melhor desempenho       | Repetir os mesmos testes e medir sustentação      |




### Procedimento de benchmark

1. Reiniciar o aparelho ou fechar processos não essenciais.
2. Garantir bateria acima de 60%, modo de economia desligado e temperatura ambiente semelhante.
3. Carregar o modelo uma vez e registrar o tempo de carregamento frio separadamente.
4. Transcrever cada áudio cinco vezes com o modelo já carregado.
5. Registrar mediana, mínimo, máximo, RTF e temperatura ao início/fim.
6. Rodar uma sequência de 20 comandos para avaliar thermal throttling.
7. Repetir para Tiny Q8, Tiny Q5, Tiny Q4 e, apenas se necessário, Base INT8.

A métrica de decisão deve ser a mediana do tempo de inferência quente, não a melhor execução isolada.

## Fase 9 — Uso de memória e armazenamento

- Priorizar aparelhos com 8 GB de RAM física ou mais.
- Não considerar RAM virtual como equivalente a RAM física para inferência.
- Manter pelo menos 15 a 25 GB livres no armazenamento interno.
- Armazenar o modelo ativo no armazenamento interno; cartão microSD pode guardar backups, áudios arquivados e modelos não ativos.
- Limitar cache de áudio e implementar política de retenção/remoção após sincronização ou confirmação do lançamento.
- Liberar buffers PCM, tensores e recursos JNI ao final de cada trabalho, sem destruir o contexto do modelo a cada comando.



## Fase 10 — Experiência do usuário

Para o pai do usuário, a percepção de velocidade e segurança importa tanto quanto o benchmark.

- Ao iniciar, mostrar estado claro: `Ouvindo`, `Processando` e `Confirme os dados`.
- Mostrar transcrição parcial apenas se ela for confiável e não atrapalhar a interface.
- Permitir cancelar a transcrição em andamento.
- Nunca bloquear o botão de voltar nem a tela inteira.
- Oferecer correção rápida por teclado e botão para gravar novamente.
- Para comandos longos, avisar que o processamento continuará em segundo plano e exibir resultado quando finalizado.



## Configuração inicial recomendada

Esta deve ser a primeira configuração a implementar e validar:

```text
Runtime: whisper.cpp compilado para arm64-v8a via NDK/JNI
Modelo: Whisper Tiny em INT8/Q8_0
Idioma: português (pt/pt-BR)
Modo: transcrição, sem tradução
Decodificação: greedy, beam size 1
Áudio: PCM 16-bit, mono, 16 kHz
Threads: testar 2, 4 e núcleos de desempenho; usar a melhor mediana
VAD: habilitado, silêncio de encerramento entre 600 e 1.000 ms
Timestamps: desabilitados
Modelo: carregado uma vez e mantido em memória
Execução: worker/executor dedicado fora da UI
Pós-processamento: normalização + parser de intenção + validador de telefone
Confirmação: obrigatória para cadastro, telefone, quantidade, valor e endereço
```



## Ordem de implementação

1. Instrumentar tempos e criar o conjunto de áudios de teste.
2. Padronizar captura em PCM mono 16 kHz e adicionar VAD.
3. Integrar whisper.cpp via JNI e manter modelo carregado.
4. Testar Tiny Q8/INT8 com decodificação greedy e idioma fixo.
5. Implementar parser e validação de telefone/números com tela de confirmação.
6. Benchmark no Galaxy A17, Moto G56 e Moto G86.
7. Testar Tiny Q5 e Q4 se o tempo ainda for inadequado.
8. Considerar Base INT8 somente se a métrica de precisão de negócio não for atendida.
9. Rodar teste de estresse de 100 comandos e corrigir vazamentos, aquecimento e falhas.
10. Congelar a configuração vencedora e documentar resultados por versão do aplicativo.



## Resultado esperado

O maior ganho deve vir da combinação de runtime nativo, modelo Tiny quantizado, áudio limpo/segmentado e parâmetros de decodificação adequados. A troca do Galaxy A17 para Moto G56 ou Moto G86 ajuda, mas não deve substituir essas otimizações.

O objetivo do produto não é obter uma transcrição perfeita de texto livre: é capturar comandos de trabalho com rapidez, extrair dados confiáveis e solicitar confirmação quando um campo crítico estiver incerto.