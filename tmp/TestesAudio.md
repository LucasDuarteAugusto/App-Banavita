# Testes de transcrição de áudio

## Vendas

Fiz os testes com a internet desligada para o modelos offline e fui descartando a cada novo teste, creio que isso não deve influenciar o modelo acho bom informar.

Os testes foram realizados com o seguinte áudio:
```text
2 caixas plástica nanica graúda amarela pra Lucas 40 reais fiado
```
### Teste 1
| Modelo       | Texto extraído                                                                | Tempo [s] |
|--------------|-------------------------------------------------------------------------------|-----------|
| Local (Tiny) | Dúas caixas plástica na Nica grauda, a amarela, pra Lucas, 40 reais           | 4         |
| Local (Tiny) | Dúas caixas plástica na Nica grauda, amarela, para lucas 40 reais fiado       | 5         |
| Local (Tiny) | Estão                                                                         | 9         |
| Local (Tiny) | Estão com 2 caixas                                                            | 18        |
| Local (Tiny) | Duáz, caiça, esplaz                                                           | 17        |
| Local (Base) | Duas caixas plásticas, cana nica grauda, amarela, pra lucas, 40 reais fiado   | 3         |
| Local (Base) | Duas caixas plástica, na Nica Grauda, amarela pra lucas, 40 reais fiada       | 3         |
| Local (Base) | Tuas caixas plásticas, na Nica Grauda, amarela para lucas, 40 reais           | 3         |
| Local (Base) | Duas caixas plásticas, na Nica Grauda, amarela, para alucas, 40 reais, fiar   | 4         |
| Local (Base) | Duas caixas plásticas na Nica Grauda, a Morela, para alucas, R$ 40,000, fiado | 3         |
| Groq         | Duas caixas plásticas, nanca graúda, amarela, pra lucas, R$  40,00 fiel       | 2         |

### Teste 2
| Modelo       | Texto extraído                                                              | Tempo [s] |
|--------------|-----------------------------------------------------------------------------|-----------|
| Local (Tiny) | Dúas caixas plástica, na Nica grauda, a amarela, pra Lucas, 40 reais, fiat  | 2         |
| Local (Base) | Duas caixas plástica, na Nica Grauda, amarela, para lucas, 40 reais, fiado. | 3         |
| Local (Base) | Plástica, nanica graúda, amarela, para lucas, 40 reais, 40 mil              | 3         |
| Local (Base) | Duas caixas plástica, nanica graúda, amarela, pra lucas, 40 reais, fiado.   | 3         |

## Compras

Os testes foram realizados com o seguinte áudio:
```text
90 caixas plástica nanica graúda do João 30 reais a prazo
```

| Modelo       | Texto extraído                                                    | Tempo [s] |
|--------------|-------------------------------------------------------------------|-----------|
| Local (Tiny) | 90 caixas plástica, na Nica Grauda, do João, 30 reais a prazo     | 2         |
| Local (Tiny) | 90 caixas plástica, na Níca Graluda, do João, 30 reais a prazo    | 2         |
| Local (Tiny) | 90 caixa explástica, na Nica grauda, do juguão, 30 reais a prazo  | 2         |
| Local (Tiny) | 90 caixa, Plástica, na Nica, grauda, do João                      | 2         |
| Local (Tiny) | Na 90 caixa explástica, na Nica grauda, do João, 30 reais a prazo | 2         |
| Local (Base) | 90 caixas plática, na Nica Agraruda do Júlia.                     | 2         |
| Local (Base) | Cacha-Cacha-Splástica na Nica Grauda, do João, 30 reais           | 2         |
| Local (Base) | 90 caixas plástica, na Nica Grauda do João, 30 reais a prazer     | 3         |
| Local (Base) | 90 caixas plásticas, na Nica Grauda, do João, 30 reais a prazo    | 3         |
| Local (Base) | 90 caixas plástica, na Nica Grauda, do João, R$ 30 a prazo        | 3         |
| Groq         | 90 caixas plástica, Nanica Graúda, do João, R$ 30,00 a prazo      | 2         |

# Chave API Groq
GROQ_API_KEY=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
