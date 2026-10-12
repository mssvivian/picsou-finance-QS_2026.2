# Especificação de Testes Unitários: `TransactionRowMapper`

Este documento formaliza a cobertura estrutural da classe `TransactionRowMapper`, detalhando a **Complexidade Ciclomática ($V(G)$)** de cada método e o comportamento verificado por cada um dos 39 testes unitários da suíte JUnit 5.

---

## 1. Resumo da Complexidade Ciclomática por Método

A Complexidade Ciclomática de McCabe é calculada através de:
$$V(G) = P + 1$$
Onde $P$ representa a quantidade de predicados de decisão (`if`, operadores lógicos `&&` / `||`, operadores ternários `? :` e cláusulas `catch`).

| Método | Responsabilidade | Predicados ($P$) | Complexidade $V(G)$ | Testes Associados |
| :--- | :--- | :---: | :---: | :--- |
| **`map(...)`** | Orquestração principal de mapeamento da linha CSV | 8 | **9** | Testes 01 ao 09 |
| **`resolveSide(...)`** | Inferência da operação (`BUY`/`SELL`) por texto, mapa ou montante | 14 | **15** | Testes 10 ao 24 |
| **`resolvePrice(...)`** | Extração direta ou dedução de preço unitário e taxas | 5 | **6** | Testes 25 ao 30 |
| **`cell(...)`** | Extração segura de células por índice com tratamento de limites | 4 | **5** | Testes 31 ao 35 |
| **`parseDecimal(...)`** e **`parseDate(...)`** | Conversão tipada com tratamento de exceções de formato | 3 | **4** | Testes 36 ao 39 |
| **Total Global** | — | **34** | **39** | **39 Testes Unitários** |

---

## 2. Detalhamento dos Métodos e Casos de Teste

### 2.1 Método: `map(...)` — $V(G) = 9$

O método principal coordena a resolução de cada campo e aplica validações de integridade estrutural (data ausente, quantidade nula ou negativa, taxas negativas, ticker nulo).

* **Test 01 (`test01_map_success_allFieldsValid`):**  
  * **O que avalia:** Caminho feliz completo. Garante que todos os campos válidos (`date`, `side`, `ticker`, `quantity`, `pricePerUnit`, `fees`, `nativeCurrency`) são atribuídos corretamente ao objeto `Transaction`.
* **Test 02 (`test02_map_dateNull_throwsException`):**  
  * **O que avalia:** Validação do predicado `date == null`. Lança `IllegalArgumentException("Missing date")`.
* **Test 03 (`test03_map_quantityNull_throwsException`):**  
  * **O que avalia:** Validação de ausência de quantidade (`qty == null`). Lança `IllegalArgumentException("Missing or non-positive quantity")`.
* **Test 04 (`test04_map_quantityZero_throwsException`):**  
  * **O que avalia:** Validação de fronteira para quantidade zero (`qty.compareTo(BigDecimal.ZERO) == 0`). Lança `IllegalArgumentException`.
* **Test 05 (`test05_map_quantityNegative_throwsException`):**  
  * **O que avalia:** Validação de fronteira para quantidade negativa (`qty.compareTo(BigDecimal.ZERO) < 0`). Lança `IllegalArgumentException`.
* **Test 06 (`test06_map_feesNull_defaultsToZero`):**  
  * **O que avalia:** Comportamento de fallback quando a célula de taxa é nula ou vazia. O campo `fees` é inicializado como `BigDecimal.ZERO`.
* **Test 07 (`test07_map_feesNegative_throwsException`):**  
  * **O que avalia:** Validação de taxas negativas (`fees.compareTo(BigDecimal.ZERO) < 0`). Lança `IllegalArgumentException("Fees cannot be negative")`.
* **Test 08 (`test08_map_currencyBlank_defaultsToAccountCurrency`):**  
  * **O que avalia:** Fallback de moeda nativa. Quando a coluna de moeda está em branco, adota a moeda configurada na conta (`account.getCurrency()`).
* **Test 09 (`test09_map_instrumentNull_throwsException`):**  
  * **O que avalia:** Validação de resolução do instrumento financeiro. Se `instrumentFieldResolver.resolve(...)` retornar `null`, lança `IllegalArgumentException("Missing ticker/ISIN")`.

---

### 2.2 Método: `resolveSide(...)` — $V(G) = 15$

Responsável pela classificação da operação em `BUY` ou `SELL`. Avalia múltiplos ramos condicionais: dicionário customizado (`sideValueMap`), casamento por substrings multilíngues e fallback heurístico pelo sinal do montante.

* **Test 10 (`test10_resolveSide_sideValueMap_hit`):**  
  * **O que avalia:** Ramo onde o mapa de sinônimos (`sideValueMap`) contém o termo fornecido (ex.: `"ACHAT_PERSO"` mapeado para `"BUY"`).
* **Test 11 (`test11_resolveSide_sideValueMap_miss_fallsThrough`):**  
  * **O que avalia:** Ramo onde `sideValueMap` existe, mas não contém a chave, permitindo que a análise caia nas regras por heurística léxica.
* **Test 12 (`test12_resolveSide_tokenExactS_returnsSell`):**  
  * **O que avalia:** Token exato `"s"` correspondendo a `SELL`.
* **Test 13 (`test13_resolveSide_tokenContainsVent_returnsSell`):**  
  * **O que avalia:** Termo em francês contendo o radical `"vent"` (ex.: `"Vente de titres"`) resultando em `SELL`.
* **Test 14 (`test14_resolveSide_tokenContainsSell_returnsSell`):**  
  * **O que avalia:** Termo em inglês contendo `"sell"` (ex.: `"Market Sell"`) resultando em `SELL`.
* **Test 15 (`test15_resolveSide_tokenContainsSale_returnsSell`):**  
  * **O que avalia:** Termo em inglês contendo `"sale"` (ex.: `"Direct Sale"`) resultando em `SELL`.
* **Test 16 (`test16_resolveSide_tokenExactB_returnsBuy`):**  
  * **O que avalia:** Token exato `"b"` correspondendo a `BUY`.
* **Test 17 (`test17_resolveSide_tokenContainsAch_returnsBuy`):**  
  * **O que avalia:** Termo em francês contendo o radical `"ach"` (ex.: `"Achat d'actions"`) resultando em `BUY`.
* **Test 18 (`test18_resolveSide_tokenContainsBuy_returnsBuy`):**  
  * **O que avalia:** Termo em inglês contendo `"buy"` (ex.: `"Limit Buy"`) resultando em `BUY`.
* **Test 19 (`test19_resolveSide_unknownSideCell_fallbackNegativeAmount_returnsBuy`):**  
  * **O que avalia:** Termo desconhecido na célula de operação que recorre ao fallback de montante negativo, deduzindo `BUY`.
* **Test 20 (`test20_resolveSide_unknownSideCell_fallbackPositiveAmount_returnsSell`):**  
  * **O que avalia:** Termo desconhecido na célula de operação que recorre ao fallback de montante positivo, deduzindo `SELL`.
* **Test 21 (`test21_resolveSide_blankSideCell_negativeAmount_returnsBuy`):**  
  * **O que avalia:** Célula de operação em branco com montante financeiro negativo resultando em `BUY`.
* **Test 22 (`test22_resolveSide_blankSideCell_positiveAmount_returnsSell`):**  
  * **O que avalia:** Célula de operação nula/em branco com montante positivo resultando em `SELL`.
* **Test 23 (`test23_resolveSide_blankSideCell_nullAmount_throwsException`):**  
  * **O que avalia:** Célula de operação e montante ambos nulos. Lança `IllegalArgumentException("Cannot determine BUY/SELL")`.
* **Test 24 (`test24_resolveSide_unrecognizedSideCell_nullAmount_throwsException`):**  
  * **O que avalia:** Célula com texto não mapeado e montante nulo, impedindo dedução. Lança `IllegalArgumentException("Cannot determine BUY/SELL")`.

---

### 2.3 Método: `resolvePrice(...)` — $V(G) = 6$

Determina o preço unitário da cotação. Pode utilizar o preço explícito ou calcular via montante líquido e quantidade, ajustando de acordo com a inclusão de taxas.

* **Test 25 (`test25_resolvePrice_explicitPositivePrice_returned`):**  
  * **O que avalia:** Preço unitário explícito e positivo fornecido diretamente na coluna, sem necessidade de cálculo derivado.
* **Test 26 (`test26_resolvePrice_negativePrice_throwsException`):**  
  * **O que avalia:** Preço explícito negativo (`price < 0`). Lança `IllegalArgumentException("Unit price cannot be negative")`.
* **Test 27 (`test27_resolvePrice_missingPriceAndAmount_throwsException`):**  
  * **O que avalia:** Cenário de inconsistência em que tanto o preço unitário quanto o montante total estão ausentes. Lança `IllegalArgumentException("Missing unit price (or amount)")`.
* **Test 28 (`test28_resolvePrice_derivedFromAmount_feesNotIncluded`):**  
  * **O que avalia:** Preço derivado de montante quando `feesIncluded = false`, calculando $\text{preço} = |\text{amount}| / \text{quantity}$.
* **Test 29 (`test29_resolvePrice_derivedFromAmount_feesIncluded_buy`):**  
  * **O que avalia:** Preço derivado para operação `BUY` com taxas embutidas (`feesIncluded = true`), calculando $(|\text{amount}| - \text{fees}) / \text{quantity}$.
* **Test 30 (`test30_resolvePrice_derivedFromAmount_feesIncluded_sell`):**  
  * **O que avalia:** Preço derivado para operação `SELL` com taxas embutidas (`feesIncluded = true`), calculando $(|\text{amount}| + \text{fees}) / \text{quantity}$.

---

### 2.4 Método: `cell(...)` — $V(G) = 5$

Utilitário de acesso a colunas que protege a aplicação contra `IndexOutOfBoundsException` e faz sanitização de espaços em branco.

* **Test 31 (`test31_cell_nullIndex_returnsNullSafely`):**  
  * **O que avalia:** Mapeamento de coluna configurado com índice `null`. Retorna `null` com segurança.
* **Test 32 (`test32_cell_negativeIndex_returnsNullSafely`):**  
  * **O que avalia:** Mapeamento de coluna com índice negativo (`idx < 0`). Retorna `null`.
* **Test 33 (`test33_cell_indexOutOfBounds_returnsNullSafely`):**  
  * **O que avalia:** Índice de coluna maior ou igual ao número de elementos da linha (`idx >= row.size()`). Retorna `null`.
* **Test 34 (`test34_cell_nullElementInList_returnsNull`):**  
  * **O que avalia:** Índice válido apontando para uma posição com valor `null` na lista de strings. Retorna `null`.
* **Test 35 (`test35_cell_stringWithWhitespace_isTrimmed`):**  
  * **O que avalia:** Remoção de espaços antes e depois do conteúdo útil (`trim()`).

---

### 2.5 Métodos: `parseDecimal(...)` e `parseDate(...)` — $V(G) = 4$

Camada de conversão que respeita os dialetos regionais configurados (`CsvDialect`) e trata falhas de formatação.

* **Test 36 (`test36_parseDecimal_malformedString_throwsIllegalArgumentException`):**  
  * **O que avalia:** Trata `NumberFormatException` ao converter strings alfanuméricas inválidas (ex.: `"dez_acoes"`), encapsulando em `IllegalArgumentException`.
* **Test 37 (`test37_parseDecimal_validString_parsedCorrectly`):**  
  * **O que avalia:** Conversão bem-sucedida de string numérica em `BigDecimal` respeitando as casas decimais.
* **Test 38 (`test38_parseDate_malformedDate_throwsIllegalArgumentException`):**  
  * **O que avalia:** Trata `DateTimeParseException` para datas em padrão divergente do dialeto, encapsulando em `IllegalArgumentException`.
* **Test 39 (`test39_parseDate_validDate_parsedCorrectly`):**  
  * **O que avalia:** Conversão bem-sucedida de string de data em instância de `LocalDate` conforme o padrão esperado.
