# Testes Manuais Funcionais (Caixa-Preta / TestLink)
**Testadora:** Vivian Souza  
**Ferramenta:** TestLink  
**Módulo Testado:** Importação de Arquivo Finary (`FinaryImportService`)  
**Caso de Teste:** `dd-3` — Pré-visualização e importação com criação de novas contas (`CREATE_NEW`)

---

## Contexto e Ciclo de Vida do Teste

Nesta etapa de testes funcionais (caixa-preta), foi planejado e executado um **único caso de teste abrangente de ponta a ponta** no TestLink. 

O fluxo executado demonstrou na prática o **ciclo completo de gestão de qualidade e controle de defeitos**:
1. **1ª Execução (Falha Bloqueante):** Durante a primeira tentativa de execução, a submissão da planilha foi rejeitada com erro HTTP, impedindo a continuidade do fluxo.
2. **Abertura da Issue no GitHub:** O defeito foi formalmente registrado como [Issue #2 no GitHub](https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/2).
3. **Mitigação do Código:** O código foi corrigido no commit `9231231dca0377d56dee877f89765371202b83d5`.
4. **2ª Execução (Re-teste / Sucesso):** Com o bug corrigido, o mesmo caso de teste foi re-executado em nova build, sendo **100% aprovado** em todos os 9 passos.

```
[Planejamento no TestLink]
       │
       ▼
[1ª Execução] ──► ❌ Falha no Passo 3 (HTTP 415)
                         │
                         ▼
             [Abertura da Issue #2 no GitHub]
                         │
                         ▼
             [Mitigação: Commit 9231231dca]
                         │
                         ▼
[2ª Execução (Re-teste)] ──► ✅ Sucesso (9/9 passos aprovados)
```

---

## Arquivos Desta Pasta

| Arquivo / Diretório | Descrição |
|---|---|
| [`finary_testlink_dummy.xlsx`](finary_testlink_dummy.xlsx) | Planilha de dados de entrada com 7 contas em categorias diversas e 5 transações em EUR. |
| [`Vivian - Pré-visualização...xml`](Vivian%20-%20Pr%C3%A9-visualiza%C3%A7%C3%A3o%20e%20importa%C3%A7%C3%A3o%20com%20cria%C3%A7%C3%A3o%20de%20novas%20contas%20%28CREATE_NEW%29.xml) | Arquivo oficial de exportação XML do caso de teste criado e mantido no TestLink. |
| [`finary-test-falha/`](finary-test-falha/) | **1ª Execução:** Evidências da falha bloqueante no passo 3, relatório HTML do TestLink e prints do erro. |
| [`finary-teste-sucesso/`](finary-teste-sucesso/) | **2ª Execução (Pós-Mitigação):** Evidências da aprovação completa, relatório HTML do TestLink e prints de todos os passos. |

---

## Detalhamento das Execuções

### 1ª Execução: Falha Bloqueante (Bug Reportado)
* **Status:** ❌ **Failed (Bloqueante no Passo 3)**
* **Relatório Oficial:** [`finary-test-falha/ExecuçãoFalhaFinary.html`](finary-test-falha/ExecuçãoFalhaFinary.html)
* **Descrição da Falha:**
  * No **Passo 3**, ao submeter o arquivo `finary_testlink_dummy.xlsx` na interface de importação do Finary, a requisição de upload foi rejeitada pelo servidor com o erro:
    > *"Request failed with status code 415 (Unsupported Media Type)"*
  * A aplicação não avançou para a etapa *2 Mapping*, tornando impossível a conclusão dos passos subsequentes (passos 4 a 9 marcados como *Blocked*).
* **Ação Corretiva Tomada:**
  * Abertura imediata de defeito no GitHub: [Issue #2](https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/2).
* **Evidências Visuais:**
  * Modal do Finary aberto: [`finary-test-falha/botao_finary.png`](finary-test-falha/botao_finary.png)
  * Erro de upload bloqueante: [`finary-test-falha/erro_upload.png`](finary-test-falha/erro_upload.png)

---

### Mitigação do Defeito
* **Commit de Correção:** `9231231dca0377d56dee877f89765371202b83d5`
* **Resolução:** Ajuste no tratamento de *media type* / cabeçalhos multipart no endpoint de upload, permitindo que arquivos `.xlsx` sejam consumidos corretamente pelo backend.

---

### 2ª Execução: Re-teste Pós-Mitigação (Aprovação Plena)
* **Status:** ✅ **Passed (Aprovado em todos os 9 passos)**
* **Relatório Oficial:** [`finary-teste-sucesso/ExecuçãoSucessoFinary.html`](finary-teste-sucesso/ExecuçãoSucessoFinary.html)
* **Notas de Execução no TestLink:**
  > *"Após o commit: 9231231dca0377d56dee877f89765371202b83d5, o erro anterior passou a ser mitigado, agora temos a issue: https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/2 mitigada, possibilitando o funcionamento correto do sistema."*
* **Passos Aprovados:**
  1. Acesso à tela *Accounts* e clique em `+ Add account`.
  2. Seleção do conector `Finary`.
  3. Upload com sucesso de `finary_testlink_dummy.xlsx` e avanço automático para o Mapping.
  4. Validação do primeiro cartão ("Conta Corrente Principal", €3,500.50, BNP Paribas, 3 transações).
  5. Validação dos campos derivados automaticamente da planilha.
  6. Validação dos demais 6 cartões (Savings, Investments, Real Estate, Cryptos).
  7. Confirmação do botão `Import ->` e avanço para *Results*.
  8. Verificação da gravação das 7 contas na tela *Accounts*.
  9. Abertura do extrato da conta com exibição das transações importadas.
* **Evidências Visuais Principais:**
  * Tela de Preview com totalizadores: [`finary-teste-sucesso/preview_xlsx.png`](finary-teste-sucesso/preview_xlsx.png)
  * Detalhes das contas mapeadas: [`finary-teste-sucesso/detalhe_finary.png`](finary-teste-sucesso/detalhe_finary.png)
  * Resultado pós-importação: [`finary-teste-sucesso/pos_import.png`](finary-teste-sucesso/pos_import.png)
  * Contas e saldos persistidos no banco: [`finary-teste-sucesso/account_resultado.png`](finary-teste-sucesso/account_resultado.png)
