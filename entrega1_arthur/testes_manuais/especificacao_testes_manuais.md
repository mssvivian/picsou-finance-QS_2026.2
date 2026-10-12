# Especificação de Testes Manuais: Mapeamento de Transações CSV (`TransactionRowMapper`)

Este documento formaliza os casos de teste manuais para o módulo de importação de arquivos CSV do sistema **Picsou Finance**. Os casos aqui descritos cobrem os cenários de negócio, regras de integridade e tratamentos de exceção implementados na classe `TransactionRowMapper`.

---

## Tabela Resumo dos Casos de Teste

| ID TestLink | Nome do Caso de Teste | Importância | Resultado |
| :--- | :--- | :---: | :---: |
| **TC-01** | TC-01: Importação com Mapeamento Completo e Valores Válidos | Alta | Aprovado |
| **TC-02** | TC-02: Rejeição de Quantidade Inválida (zero ou negativa) | Alta | Aprovado |
| **TC-03** | TC-03: Dedução de BUY/SELL por Sinal do Montante | Média | Aprovado |
| **TC-04** | TC-04: Cálculo Reverso de Preço com Taxas Embutidas | Média | Aprovado |
| **TC-05** | TC-05: Conversão de Moeda para a Moeda da Conta | Média | Aprovado |

---

## Detalhamento dos Casos de Teste

### TC-01: Importação com Mapeamento Completo e Valores Válidos

* **Identificador Externo:** `TC-01`
* **Ordem de Execução:** 1
* **Versão:** 1.0
* **Importância:** Alta

#### Descrição
Validar o mapeamento completo e o processamento de ponta a ponta de uma linha de CSV que possui todos os atributos preenchidos com valores válidos.

#### Pré-condições
1. Usuário autenticado na aplicação Picsou Finance.
2. Pelo menos uma conta manual de investimentos cadastrada com moeda padrão `EUR`.
3. Ficheiro CSV de teste disponível contendo dados estruturados no formato padrão internacional.

#### Etapas de Execução

1. **Etapa 1:**
   * **Ação:** Fazer o upload do arquivo contendo a linha: `2026-03-15,BUY,AAPL,Apple Inc.,10,150.00,5.00,USD,-1505.00`.
   * **Resultado Esperado da Etapa:** Arquivo carregado com sucesso no assistente de importação e tabela de pré-visualização preenchida.

2. **Etapa 2:**
   * **Ação:** Configurar o dialeto (delimitador `,`, ponto decimal `DOT`, formato de data `yyyy-MM-dd`) e confirmar o mapeamento de cada uma das colunas (Data, Operação, Ticker, Nome, Quantidade, Preço Unitário, Taxas, Moeda e Montante).
   * **Resultado Esperado da Etapa:** Mapeamento validado e aceito pelo assistente sem mensagens de inconsistência.

3. **Etapa 3:**
   * **Ação:** Confirmar e executar a importação do lote.
   * **Resultado Esperado da Etapa:** Notificação de sucesso indicando que o registro foi importado.

#### Resultado Final Obtido
A transação é criada e persistida no banco de dados com os seguintes dados:
* **Data:** `2026-03-15`
* **Operação:** `BUY`
* **Ticker:** `AAPL`
* **Nome do Ativo:** `Apple Inc.`
* **Quantidade:** `10`
* **Preço Unitário:** `150.00`
* **Taxas:** `5.00`
* **Moeda Nativa:** `USD`

---

### TC-02: Rejeição de Quantidade Inválida (zero ou negativa)

* **Identificador Externo:** `TC-02`
* **Ordem de Execução:** 2
* **Versão:** 1.0
* **Importância:** Alta

#### Descrição
Verificar se linhas de transação com quantidade vazia, igual a zero ou com valor negativo são devidamente identificadas e rejeitadas pela validação de integridade.

#### Pré-condições
1. Assistente de importação CSV aberto em uma conta de investimentos.

#### Etapas de Execução

1. **Etapa 1:**
   * **Ação:** Selecionar e carregar um arquivo contendo uma linha com quantidade `0` ou `-5` (ex.: `2026-03-15,BUY,AAPL,Apple Inc.,0,150.00,0,USD,-1500.00`).
   * **Resultado Esperado da Etapa:** Linha lida pelo assistente e destacada com aviso de inconsistência ou pronta para submissão de validação.

2. **Etapa 2:**
   * **Ação:** Submeter o lote de importação.
   * **Resultado Esperado da Etapa:** O sistema interrompe o processamento da linha incorreta e sinaliza o erro na interface.

#### Resultado Final Obtido
Exibição visível da mensagem de erro de validação: `"Missing or non-positive quantity"`. Nenhuma transação com quantidade inválida é salva na base de dados.

---

### TC-03: Dedução de BUY/SELL via Sinal do Montante

* **Identificador Externo:** `TC-03`
* **Ordem de Execução:** 3
* **Versão:** 1.0
* **Importância:** Média

#### Descrição
Validar a heurística que infere automaticamente o tipo de operação (compra ou venda) a partir do sinal numérico do montante total quando a coluna `side` estiver vazia ou omitida.

#### Pré-condições
1. Arquivo CSV configurado com a coluna de operação (`side`) em branco e com linhas alternando montantes negativos e positivos.

#### Etapas de Execução

1. **Etapa 1:**
   * **Ação:** Processar linha onde `side` está em branco e o montante é negativo: `2026-03-15,,AAPL,Apple Inc.,10,150.00,0,USD,-1500.00`.
   * **Resultado Esperado da Etapa:** O sistema infere e classifica a operação como `BUY`.

2. **Etapa 2:**
   * **Ação:** Processar linha onde `side` está em branco e o montante é positivo: `2026-03-15,,AAPL,Apple Inc.,10,150.00,0,USD,1500.00`.
   * **Resultado Esperado da Etapa:** O sistema infere e classifica a operação como `SELL`.

#### Resultado Final Obtido
Ambas as transações são criadas com sucesso na tabela de transações, registrando `tx_type = BUY` para o montante negativo (saída de caixa) e `tx_type = SELL` para o montante positivo (entrada de caixa).

---

### TC-04: Cálculo Reverso de Preço com Taxas Embutidas

* **Identificador Externo:** `TC-04`
* **Ordem de Execução:** 4
* **Versão:** 1.0
* **Importância:** Média

#### Descrição
Validar o cálculo matemático do preço unitário a partir do valor bruto ajustado pelas taxas quando a coluna `unitPrice` não for fornecida e a flag `feesIncluded` estiver habilitada.

#### Pré-condições
1. Arquivo de importação contendo transação sem coluna de preço unitário mapeada.
2. Opção "Taxas já incluídas no total" (`feesIncluded = true`) disponível no assistente.

#### Etapas de Execução

1. **Etapa 1:**
   * **Ação:** Importar uma operação de compra de `10` ações com montante de `-105.00` e taxa de `5.00`, mantendo o campo de preço unitário não mapeado e marcando a caixa de seleção de taxas incluídas.
   * **Resultado Esperado da Etapa:** O sistema aplica a fórmula $(\text{Montante Bruto} - \text{Taxas}) / \text{Quantidade}$, ou seja, $(105.00 - 5.00) / 10 = 10.00$.

#### Resultado Final Obtido
A transação é registrada no sistema com o campo `price_per_unit` calculado exatamente como `10.00`.

---

### TC-05: Conversão de Moeda para a Moeda da Conta

* **Identificador Externo:** `TC-05`
* **Ordem de Execução:** 5
* **Versão:** 1.0
* **Importância:** Média

#### Descrição
Garantir que, na ausência de informação explícita de moeda na linha do arquivo CSV, a transação herde a moeda padrão configurada na conta bancária/corretora associada.

#### Pré-condições
1. Conta de destino no sistema previamente criada com moeda nativa configurada como `EUR`.

#### Etapas de Execução

1. **Etapa 1:**
   * **Ação:** Importar uma linha de transação válida onde a coluna correspondente à moeda está preenchida com espaços em branco (`"   "`).
   * **Resultado Esperado da Etapa:** A linha é processada sem erro de campo nulo ou vazio.

#### Resultado Final Obtido
A transação é persistida na base com o campo `native_currency` preenchido com o valor `EUR` (herdado da conta).
