# Testes Automatizados e Estruturais (Caixa-Branca / JUnit 5)
**Módulo:** `FinaryImportService.java`  
**Classe de Testes:** [`FinaryImportServiceTest.java`](../../backend/src/test/java/com/picsou/service/FinaryImportServiceTest.java)  
**Total de Testes:** 50 testes (44 de caminhos básicos de McCabe + 6 testes de robustez e defeitos)


## Documentos e Recursos

* **[GUIA_GRAFOS_FLUXO_CONTROLE.md](GUIA_GRAFOS_FLUXO_CONTROLE.md):** Manual completo de leitura, fundamentação teórica de McCabe, equações de $V(G)$ e rastreabilidade detalhada de cada aresta com o código.
* **[Pasta grafos/](grafos/):** Imagens de alta resolução (PNG a 150 DPI) e fontes compiláveis em Graphviz (DOT) para os 5 métodos analisados.

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

# 4. Executar os testes e gerar relatório de cobertura de código com JaCoCo
mvn org.jacoco:jacoco-maven-plugin:0.8.12:prepare-agent test -Dtest=FinaryImportServiceTest org.jacoco:jacoco-maven-plugin:0.8.12:report
```

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


## Cobertura de Código Estrutural (JaCoCo)

Para verificar empiricamente a eficácia da suíte construída com base nos caminhos de McCabe e assegurar que nenhum fluxo ou condição de borda deixou de ser exercitado, foi executada a análise de cobertura de código utilizando a biblioteca **JaCoCo** (`jacoco-maven-plugin:0.8.12`).

### Comando Executado

```bash
mvn org.jacoco:jacoco-maven-plugin:0.8.12:prepare-agent test -Dtest=FinaryImportServiceTest org.jacoco:jacoco-maven-plugin:0.8.12:report
```

O comando acopla o agente Java do JaCoCo em tempo de execução aos testes unitários e compila o relatório formal de cobertura sem necessidade de alterar o `pom.xml` do projeto.

### Resultados Obtidos em `FinaryImportService`

A execução confirmou **100% de cobertura em todas as dimensões analisadas pelo JaCoCo**:

| Métrica JaCoCo | Total na Classe | Coberto na Suíte | Não Coberto | Taxa de Cobertura |
|---|:---:|:---:|:---:|:---:|
| **Linhas (Lines)** | 163 | 163 | 0 | **100.0%** |
| **Ramos Condicionais (Branches)** | 65 | 65 | 0 | **100.0%** |
| **Instruções Bytecode (Instructions)** | 676 | 676 | 0 | **100.0%** |
| **Métodos e Lambdas Compilados** | 18 | 18 | 0 | **100.0%** |
| **Complexidade Ciclomática (JaCoCo)** | 51 | 51 | 0 | **100.0%** |

*Nota técnica sobre a resolução do merge lambda (linha 120):*
Na linha 120 de `FinaryImportService.java`, a instrução `Collectors.toMap(..., (a, b) -> a)` define uma função de merge defensiva para lidar com contas duplicadas de mesma categoria e nome. O compilador Java (`javac`) gera essa expressão como um método estático privado sintético (`lambda$6`) contendo 2 instruções de bytecode (`aload_0`, `areturn`). Esse trecho é exercitado de forma explícita pelo teste dedicado `extra_executeImport_duplicateAccountsInFileMergeSilentlyKeepsFirst`, garantindo 100% de cobertura também em instruções e métodos compilados sem sobrecarregar os testes da base de McCabe.

### Localização do Relatório após execução

O relatório em formato HTML navegável é gerado em:
- Visão geral: `backend/target/site/jacoco/index.html`
- Detalhamento de `FinaryImportService`: `backend/target/site/jacoco/com.picsou.service/FinaryImportService.html`
- Dados brutos consolidados: `backend/target/site/jacoco/jacoco.csv`
