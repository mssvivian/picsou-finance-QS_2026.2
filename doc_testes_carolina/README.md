# Documentação de Evidências de Testes de Software — COS482

**Disciplina:** Qualidade de Software (COS482 - UFRJ / COPPE)  
**Autora:** Carolina Falcão  
**Projeto Avaliado:** [Picsou Finance](../../README.md) (Sistema de Gestão Financeira Pessoal e Multicontas Familiar)  
**Escopo da Entrega:** Trabalho 1

---

## 1. Visão Geral e Resumo Executivo da Entrega

Este diretório centraliza o **conjunto completo de evidências técnicas, teóricas e práticas**  do Trabalho 1.

```
doc_testes_carolina/
│
├── README.md                           <-- (Este arquivo) Panorama geral e índice unificado
│
├── testes_automatizados/               
│   ├── README.md                       <-- Relatório técnico
│   ├── grafos/                         <-- Diagramas Graphviz do relatório
│   └── relatorio_jacoco/               <-- Relatório de cobertura JaCoCo
│
└── testes_manuais/                     
    ├── README.md                       <-- Roteiro do caso de teste, matriz de passos e auditoria das execuções
    ├── criacao_meta_familiar.xml       <-- Arquivo XML de exportação/importação no TestLink
    ├── prints/                         <-- Galeria de capturas de tela dos testes manuais
    │   ├── teste_1-falha/              <-- Prints da Execução 1 
    │   └── teste_2-sucesso/            <-- Prints da Execução 2 
    ├── teste_1-falha/                  <-- Relatórios oficiais do TestLink da Execução 1
    │   ├── teste_1.pdf
    │   └── teste_1.html
    └── teste_2-sucesso/                <-- Relatórios oficiais do TestLink da Execução 2
        ├── teste_2.pdf
        └── teste_2.html
```

---

## 2. Mapa Rápido das Entregas e Artefatos

| Dimensão de Qualidade | Artefato / Componente | Descrição / Destaques | Link Direto |
| :--- | :--- | :--- | :--- |
| **Classe de Alta Complexidade** | `FamilyViewService.java` | Classe com regra de negócio central de privacidade familiar e segregação de patrimônio. Complexidade Ciclomática máxima $V(G) = 14$ e soma acumulada da classe $V(G) = 24$. | [Visualizar Código](../../backend/src/main/java/com/picsou/service/FamilyViewService.java) |
| **Suíte de Testes Unitários** | `FamilyViewServiceTest.java` | **29 casos de teste unitários** implementados em JUnit 5 e Mockito 5. Cobertura estrita do padrão AAA, análise de valor limite, partições válidas/inválidas e isolamento total via dublês. | [Visualizar Testes](../../backend/src/test/java/com/picsou/service/FamilyViewServiceTest.java) |
| **Documentação Teórica dos Testes** | `testes_automatizados/README.md` | Detalhamento matemático de $V(G)$, **Grafos de Fluxo de Execução (CFG) via Graphviz**, mapeamento dos 14 caminhos básicos e justificativa da técnica RIP. | [Abrir Documento](testes_automatizados/README.md) |
| **Modelagem dos Grafos (Graphviz)** | `testes_automatizados/grafos/` | Modelagem formal em DOT, diagramas com nós em balõezinhos e losangos compilados em PNG e SVG de alta resolução. | [Abrir Pasta](testes_automatizados/grafos/) |
| **Relatório de Cobertura (JaCoCo)** | `testes_automatizados/relatorio_jacoco/` | Relatório HTML oficial do JaCoCo demonstrando **100% de cobertura de instruções (437/437), linhas (103/103) e branches (40/40)** na classe `FamilyViewService`. | [Abrir Relatório](testes_automatizados/relatorio_jacoco/index.html) |
| **Caso de Teste Manual** | `testes_manuais/README.md` | Caso de teste "Criação e compartilhamento de meta familiar". 10 passos detalhados com ações e resultados esperados. | [Abrir Documento](testes_manuais/README.md) |
| **Artefato de Carga (XML)** | `criacao_meta_familiar.xml` | Estrutura interoperável pronta para importação direta em instâncias do TestLink 1.9.20. | [Ver XML](testes_manuais/criacao_meta_familiar.xml) |
| **Prints de Evidência** | `testes_manuais/prints/` | Galeria de capturas de tela organizadas por execução (bloqueio no teste 1 e fluxo completo no teste 2). | [Abrir Pasta](testes_manuais/prints/) |
| **Evidência de Execução 1 (Falha)** | `teste_1.pdf` / `teste_1.html` | Relatório gerado pelo TestLink comprovando execução reprovada (**Failed**) devido a bloqueio de segurança (*Rate Limiting* / *Too many attempts*). | [Abrir Pasta](testes_manuais/teste_1-falha/) |
| **Evidência de Execução 2 (Sucesso)** | `teste_2.pdf` / `teste_2.html` | Relatório oficial do TestLink comprovando execução aprovada (**Passed**) com os 10 passos validados com sucesso. | [Abrir Pasta](testes_manuais/teste_2-sucesso/) |

---

## 3. Síntese dos Testes Automatizados 

A classe sob teste selecionada foi [`FamilyViewService.java`](../../backend/src/main/java/com/picsou/service/FamilyViewService.java), responsável por agregar e projetar a visão compartilhada do patrimônio familiar (*Family Dashboard*) e os aportes das metas compartilhadas, garantindo isolamento estrito entre membros da família.

### Métricas de Complexidade Aferidas
* **Método `getFamilyDashboard(Long)`**: $P = 13$ pontos de decisão $\rightarrow$ **$V(G) = 14$** (cumpre a exigência $V(G) \ge 10$ da Etapa 4).
* **Método `getGoalContributions(Long, Long, List)`**: $P = 4$ $\rightarrow$ **$V(G) = 5$**.
* **Método `isGoalVisibleTo(Goal, Long)`**: $P = 3$ $\rightarrow$ **$V(G) = 4$**.
* **Complexidade Ciclomática Total da Classe ($WMC$):** **$24$**.

### Resultados da Execução
Todos os **29 casos de teste** foram executados e validados contra o build do Maven com Java 21:
```text
[INFO] Running com.picsou.service.FamilyViewServiceTest
[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.109 s
[INFO] BUILD SUCCESS
```

Para conferir o detalhamento teórico de Caixa-Preta (Partição de Equivalência e Valor Limite) e Caixa-Branca (Grafo de Fluxo e Caminhos Básicos), acesse o documento dedicado:  
👉 **[Dossiê Completo de Testes Automatizados](testes_automatizados/README.md)**

---

## 4. Síntese dos Testes Manuais 
Para validar uma funcionalidade de ponta a ponta, foi planejado e executado um cenário completo de teste no **TestLink**:

* **Nome do Teste:** `Criação e compartilhamento de meta familiar` (ID Externo: `dd-4` / `PICSOU-01`)
* **Suíte:** `Gestão e Compartilhamento de Metas Familiares`
* **Objetivo:** Garantir que uma nova meta cadastrada pelo membro administrador, ao receber a configuração de compartilhamento familiar `ALL`, torne-se imediatamente visível para outro membro da família ao acessar o painel compartilhado (`/family`), preservando a consistência dos dados (saldo ponderado e percentual alcançado).

### Histórico de Auditoria no TestLink
1. **Execução 1 — Resultado: FAILED**:  
   Ao submeter as credenciais no Passo 1, o sistema disparou a proteção de *Rate Limiting* do *Bucket4j* retornando a mensagem *"Too many attempts. Please wait a moment and try again."*, bloqueando os passos subsequentes. A anomalia foi devidamente registrada e documentada como evidência em PDF/HTML.
2. **Execução 2 — Resultado: PASSED**:  
   Após o reset de janela de segurança, os 10 passos foram percorridos com êxito, atestando a criação da meta *"Reserva de Emergência Familiar"*, a configuração `ALL`, o logout/login do segundo membro e a correta renderização do card e barra de progresso no *Family Dashboard*.

Para visualizar a matriz passo a passo, os relatórios completos e as capturas de tela:  
👉 **[Dossiê Completo de Testes Manuais](testes_manuais/README.md)**

---

## 5. Como Reproduzir os Resultados

### Execução dos Testes Automatizados
```bash
# Estando na raiz do repositório picsou-finance-QS_2026.2:
docker run --rm -v "$PWD:/app" -w /app/backend -v "$HOME/.m2:/root/.m2" \
  maven:3.9-eclipse-temurin-21-alpine mvn test -Dtest=FamilyViewServiceTest
```

### Importação da Suíte no TestLink
1. Acesse o TestLink na seção **Test Specification**.
2. Selecione a raiz do projeto e clique no ícone **Actions (⚙️)**.
3. Clique em **Import Test Suite** e selecione o arquivo [`testes_manuais/criacao_meta_familiar.xml`](testes_manuais/criacao_meta_familiar.xml).
