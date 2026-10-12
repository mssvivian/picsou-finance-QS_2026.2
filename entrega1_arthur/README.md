# Documentação de Testes — Arthur Monteiro Senra
**Disciplina:** Qualidade de Software (COS482) — UFRJ 2026.2  
**Módulo / Classe Analisada:** `TransactionRowMapper` (`picsou-backend`)

---

## Visão Geral

Esta pasta consolida todo o material de garantia da qualidade, projeto de testes e evidências geradas para o serviço de mapeamento de dados de transações por importação de arquivos CSV **(TransactionRowMapper)**. O trabalho engloba tanto a execução de **testes manuais** (caixa-preta, sob a ótica do usuário, projetados no TestLink) quanto a execução de **testes unitários** (caixa-branca, projetados com JUnit 5 e Mockito).

```
entrega1_arthur/
│
├── README.md                                  # Índice geral (este arquivo)
│
├── testes_manuais/                            # Testes Manuais (Caixa-Preta / TestLink)
│   ├── planilhas_teste                        # Planilhas de importação para reprodução dos testes
│   │   ├── tc01_caminho_feliz.csv
│   │   ├── tc02_quantidade_invalida.csv
│   │   ├── tc03_deducao_operacao_por_montante.csv
│   │   ├── tc04_preco_calculado_com_taxas.csv
│   │   └── tc05_moeda_conversao.csv
│   │ 
│   ├── prints                                  # Registros dos testes realizados
│   │   ├── tc01
│   │   │   └── *.png
│   │   ├── tc02
│   │   │   └── *.png
│   │   ├── tc03
│   │   │   └── *.png
│   │   ├── tc04
│   │   │   └── *.png
│   │   └── tc05
│   │        └── *.png
│   │            
│   └── especificação_testes_manuais.md          # Especificação dos testes manuais (pré-condições, resultados, etc.)
│
└── testes_unitários/                            # Testes Unitários (Caixa-Branca / JUnit 5 / Mockito)
    ├── especificacao_testes_unitarios.md        # Especificação dos testes unitários (complexidade ciclomática, cobertura, etc.)                     
    │
    └── log_terminal.txt                         # Log de saída do terminal para a suite de testes
```

---

## Navegação Rápida

1. [Acessar Especificação dos Testes Manuais](testes_manuais/especificacao_testes_manuais.md)
2. [Acessar Especificação dos Testes Unitários](testes_unitários/especificacao_testes_unitarios.md)
