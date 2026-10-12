# Documentação de Testes — Vivian Souza
**Disciplina:** Qualidade de Software (COS482) — UFRJ 2026.2  
**Módulo / Classe Analisada:** `FinaryImportService` (`picsou-backend`)

---

## Visão Geral

Esta pasta consolida todo o material de garantia da qualidade, projeto de testes e evidências geradas para o serviço de importação de dados do **Finary** (`FinaryImportService`). O trabalho engloba tanto a abordagem funcional de **Caixa-Preta (Testes Manuais via TestLink)** quanto a abordagem estrutural de **Caixa-Branca (Testes de Unidade, McCabe e Grafos de Fluxo de Controle)**.

```
Vivian - Documentação Testes/
│
├── README.md                                  # Índice geral e sumário executivo (este arquivo)
│
├── testes-manuais/                            # Testes Funcionais (Caixa-Preta / TestLink)
│   ├── README.md                              # Documentação do ciclo do teste, issue e mitigação
│   ├── finary_testlink_dummy.xlsx             # Planilha de dados de entrada utilizada nos testes
│   ├── Vivian - Pré-visualização... (CREATE_NEW).xml # Exportação XML oficial do caso dd-3 do TestLink
│   ├── finary-test-falha/                     # 1ª Execução: Falha bloqueante no passo 3 (HTTP 415) -> Issue #2 no GitHub
│   │   ├── ExecuçãoFalhaFinary.html           # Relatório HTML oficial do TestLink
│   │   └── *.png                              # Capturas de tela comprobatórias do erro
│   └── finary-teste-sucesso/                  # 2ª Execução: Re-teste pós-mitigação (Commit 9231231dca) -> 100% Passed
│       ├── ExecuçãoSucessoFinary.html         # Relatório HTML oficial do TestLink
│       └── *.png                              # Capturas de tela comprovando todos os 9 passos
│
└── testes-automatizados/                      # Testes Estruturais (Caixa-Branca / McCabe)
    ├── README.md                              # Guia rápido da suíte JUnit 5 e comandos do terminal
    ├── GUIA_GRAFOS_FLUXO_CONTROLE.md          # Como ler, interpretar e auditar cada gráfico
    └── grafos/                                # Arquivos de imagem (PNG) e código fonte (DOT)
        ├── cfg_parseXlsx.png                  # Grafo do parse XLSX (V(G) = 22)
        ├── cfg_executeImport.png              # Grafo de execução da importação (V(G) = 10)
        ├── cfg_cellString.png                 # Grafo do leitor de células texto (V(G) = 4)
        ├── cfg_cellNumeric.png                # Grafo do leitor de células numéricas (V(G) = 3)
        ├── cfg_preview.png                    # Grafo do endpoint de preview (V(G) = 2)
        └── *.dot                              # Fontes Graphviz compiláveis
```

---

## Resumo Executivo das Métricas

| Dimensão de Qualidade | Métrica / Alvo | Valor Obtido | Status |
|---|---|---|---|
| **Testes Manuais de Caixa-Preta (TestLink)** | Caso de teste E2E ponta a ponta (`dd-3`) | **1 Caso com ciclo completo de defeito** (1ª Execução: Falha / Abertura da Issue #2 / Commit mitigador / 2ª Execução: Sucesso) | ✅ Ciclo de QA concluído |
| **Complexidade Ciclomática Ponderada (WMC)** | $\sum V(G)$ de todos os métodos | **44** | Analisado e coberto |
| **Caminhos Básicos de McCabe Cobertos** | Cobertura de ramos independentes | **44 / 44 (100%)** | ✅ Sucesso total |
| **Testes Unitários Automatizados (JUnit 5)** | Testes de caminhos básicos + extras | **50 testes** | ✅ 47 passando, 3 de defeito |
| **Defeitos Identificados em Caixa-Branca** | Testes de regressão `@Disabled` | **3 Issues documentadas** | ⚠️ Issues documentadas com reprodução |

---

## 🔗 Navegação Rápida

1. [Acessar Documentação de Testes Manuais (TestLink)](testes-manuais/README.md)
2. [Acessar Documentação de Testes Automatizados e Grafos (McCabe)](testes-automatizados/README.md)
3. [Guia de Leitura e Interpretação dos Grafos de Fluxo de Controle](testes-automatizados/GUIA_GRAFOS_FLUXO_CONTROLE.md)
