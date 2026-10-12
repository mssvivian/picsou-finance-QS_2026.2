# Testes Automatizados e Estruturais (Caixa-Branca / JUnit 5)
**Módulo:** `FinaryImportService.java`  
**Classe de Testes:** [`FinaryImportServiceTest.java`](../../backend/src/test/java/com/picsou/service/FinaryImportServiceTest.java)  
**Total de Testes:** 50 testes (44 de caminhos básicos de McCabe + 6 testes de robustez e defeitos)

---

## Documentos e Recursos

* **[GUIA_GRAFOS_FLUXO_CONTROLE.md](GUIA_GRAFOS_FLUXO_CONTROLE.md):** Manual completo de leitura, fundamentação teórica de McCabe, equações de $V(G)$ e rastreabilidade detalhada de cada aresta com o código.
* **[Pasta grafos/](grafos/):** Imagens de alta resolução (PNG a 150 DPI) e fontes compiláveis em Graphviz (DOT) para os 5 métodos analisados.

---

## Como Executar os Testes no Terminal

A partir do diretório raiz do repositório:

```bash
cd backend

# 1. Executar todos os 47 testes ativos da suite (tempo estimado: ~2.5s)
mvn test -Dtest=FinaryImportServiceTest

# 2. Executar um método específico de teste (exemplo: caminho p06 de executeImport)
mvn test -Dtest=FinaryImportServiceTest#executeImport_p06_accountWithTransactionsReconstructsSnapshotsAndImportsThem

# 3. Executar os testes de defeito conhecidos (forçando a execução de métodos com @Disabled)
mvn test -Dtest=FinaryImportServiceTest#extra_preview_twoAccountsWithSameNameAndCategoryMustGetDistinctIds -DargLine="-Djunit.jupiter.conditions.deactivate=*DisabledCondition"
```

---

## Métricas de Cobertura de McCabe

| Método | Complexidade $V(G)$ | Testes Unitários | Cobertura |
|---|:---:|:---:|:---:|
| `preview` | 2 | `preview_p01` a `p02` | 100% |
| `executeImport` | 10 | `executeImport_p01` a `p10` | 100% |
| `parseXlsx` | 22 | `parseXlsx_p01` a `p22` | 100% |
| `cellString` | 4 | `cellString_p01` a `p04` | 100% |
| `cellNumeric` | 3 | `cellNumeric_p01` a `p03` | 100% |
| `buildParsedAccountKey` | 1 | `buildParsedAccountKey_p01` | 100% |
| `slugify` | 1 | `slugify_p01` | 100% |
| `cleanupExpiredCache` | 1 | `cleanupExpiredCache_p01` | 100% |
| **Total WMC** | **44** | **44 testes** | **100%** |
