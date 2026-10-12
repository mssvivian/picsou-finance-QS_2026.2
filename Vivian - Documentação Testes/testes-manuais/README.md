# Testes Manuais Funcionais (Caixa-Preta / TestLink)
**Testadora:** Vivian Souza  
**Ferramenta:** TestLink  
**Módulo Testado:** Importação de Arquivo Finary (`FinaryImportService`)

---

## Objetivo dos Testes Manuais

Validar a interface e as regras de negócio de ponta a ponta (E2E funcional) sob a ótica do usuário final, cobrindo:
1. O fluxo feliz de upload de planilha válida com múltiplos tipos de contas e transações, seleção de mapeamento padrão `CREATE_NEW`, importação para o banco de dados e reflexão imediata nas telas de *Accounts* e *Dashboard*.
2. O fluxo de exceção (tratamento de falha) ao tentar realizar o upload de arquivos inválidos ou corrompidos, garantindo que o sistema rejeite a operação sem corromper o estado do banco.

---

## Conteúdo desta Pasta

| Arquivo / Diretório | Descrição |
|---|---|
| [`finary_testlink_dummy.xlsx`](finary_testlink_dummy.xlsx) | Planilha de dados de entrada com 7 contas em categorias diversas e 5 transações em EUR. |
| [`Vivian - Pré-visualização...xml`](Vivian%20-%20Pr%C3%A9-visualiza%C3%A7%C3%A3o%20e%20importa%C3%A7%C3%A3o%20com%20cria%C3%A7%C3%A3o%20de%20novas%20contas%20%28CREATE_NEW%29.xml) | Arquivo oficial de exportação XML do caso de teste criado e mantido no TestLink. |
| [`finary-teste-sucesso/`](finary-teste-sucesso/) | Evidências da execução aprovada com sucesso (relatório HTML do TestLink e capturas de tela passo a passo). |
| [`finary-test-falha/`](finary-test-falha/) | Evidências da execução de teste com erro de upload (relatório HTML do TestLink e capturas de tela do erro). |

---

## Casos de Teste e Execuções

### 1. Caso de Sucesso: Pré-visualização e importação com `CREATE_NEW`
* **Identificador no TestLink:** Internal ID 48 / External ID 3
* **Status da Execução:** ✅ **Aprovado (Passed)**
* **Relatório Oficial:** [`finary-teste-sucesso/ExecuçãoSucessoFinary.html`](finary-teste-sucesso/ExecuçãoSucessoFinary.html)
* **Resumo do Cenário:**
  * **Pré-condições:** Usuário logado no Picsou, sem contas pré-existentes homônimas na tela *Accounts*.
  * **Passos executados:**
    1. Acessar tela *Accounts* e clicar no botão `+ Add account`.
    2. Selecionar o conector `Finary`.
    3. Fazer upload do arquivo `finary_testlink_dummy.xlsx`.
    4. Conferir a tela de pré-visualização (verificando que as 7 contas e o totalizador de 5 transações foram identificados).
    5. Manter a ação padrão `CREATE_NEW` e confirmar a importação (`Import accounts`).
    6. Conferir se todas as 7 contas e seus respectivos saldos foram gravados e exibidos na tela *Accounts* e no *Dashboard*.
* **Evidências Visuais Principais:**
  * Seleção do arquivo: [`finary-teste-sucesso/selecionar_arquivo.png`](finary-teste-sucesso/selecionar_arquivo.png)
  * Tela de Preview com totalizadores: [`finary-teste-sucesso/preview_xlsx.png`](finary-teste-sucesso/preview_xlsx.png)
  * Detalhes das contas mapeadas: [`finary-teste-sucesso/detalhe_finary.png`](finary-teste-sucesso/detalhe_finary.png)
  * Resultado pós-importação: [`finary-teste-sucesso/pos_import.png`](finary-teste-sucesso/pos_import.png)
  * Saldos persistidos em Accounts: [`finary-teste-sucesso/account_resultado.png`](finary-teste-sucesso/account_resultado.png)

---

### 2. Caso de Falha: Upload de Arquivo Inválido / Rejeição
* **Status da Execução:** ⚠️ **Falha / Tratamento de Erro Validado**
* **Relatório Oficial:** [`finary-test-falha/ExecuçãoFalhaFinary.html`](finary-test-falha/ExecuçãoFalhaFinary.html)
* **Resumo do Cenário:**
  * Tentativa de envio de arquivo inválido ou formato incompatível para verificar o comportamento defensivo do sistema.
  * Validação da mensagem de erro e bloqueio da importação sem efeito colateral no banco de dados.
* **Evidências Visuais:**
  * Modal do Finary: [`finary-test-falha/botao_finary.png`](finary-test-falha/botao_finary.png)
  * Mensagem de erro de upload: [`finary-test-falha/erro_upload.png`](finary-test-falha/erro_upload.png)
  * Integridade da listagem de contas preservada: [`finary-test-falha/resultado_account.png`](finary-test-falha/resultado_account.png)
