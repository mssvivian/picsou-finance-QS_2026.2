# Justificativa da classe e complexidade ciclomática

## Escolha

`LoanAmortizationService` calcula amortização, juros, seguro, custo total e saldo em uma data de referência. Não é um CRUD: contém laço sobre parcelas, fórmula matemática, tratamento de datas, decisões de arredondamento e acumulação de valores. É determinística quando se usa `compute(Debt, LocalDate)`, sem banco, Spring ou serviços externos no teste.

## Convenção e reprodução

Para cada método explícito: **V(G) = 1 + número de decisões**. Contar `if`, `for`, `while`, `case`, `catch`, operador ternário `?` e cada `&&`/`||` por causa do curto-circuito. Não contar `else`, operadores de comparação, chamadas `Math.min/max`, comentários ou literais. Records e construtor implícito não entram na soma. A soma de métodos é uma agregação de complexidade da classe, não a complexidade de um único grafo conectado.

Executar da raiz: `node entrega1/paulo/scripts/medir-complexidade.cjs`. O [relatório JSON](evidencias/complexidade/resultado.json) lista todos os tokens contados, suas linhas e SHA-256 do arquivo. O script é específico desta classe: não substitui um analisador Java completo. A contagem abaixo foi conferida diretamente no código original, commit `6b020c76cf093b8995a68df12a32e8c5b21e9144`.

| Método | Decisões | V(G) |
|---|---:|---:|
| compute(Debt) | 0 | 1 |
| compute(Debt, LocalDate) | 13 | **14** |
| computeRemainingBalance | 0 | 1 |
| nz | 1 | 2 |
| computeTotalInstallments | 1 if + 2 operadores OR | 4 |
| computePaidInstallments | 2 if + 2 operadores OR | 5 |
| computeMonthlyPayment | 2 if | 3 |
| **Soma dos métodos explícitos** | | **30** |

No método principal: 1 laço, 4 `if` e 8 ternários. Sem contar `&&`/`||`, o máximo continua sendo 14 e a soma passa a 26. Portanto a escolha atende ao mínimo 10 mesmo sob essa convenção alternativa. A lista preliminar anotava 14 + 4 + 5 + 3; as diferenças com a soma completa decorrem dos métodos auxiliares de complexidade 1/2.

## Limitações relevantes para explicar os testes

- Parcelas pagas são estimadas por meses de calendário; não são pagamentos bancários efetivamente registrados.
- A primeira parcela ocorre um mês após o início; a última encerra o contrato.
- A documentação prevê ajuste de arredondamento na última parcela.
- Datas ausentes/inválidas resultam em zero parcelas no serviço. Isso não significa que toda entrada seja aceita pela interface.
- O ramo de geração de datas com início nulo não produz parcelas, pois o cálculo de duração retorna zero antes. Não prometer cobertura total de todos os ramos.
- Nesta entrega não foi medida cobertura de arestas nem escore de mutação. Esses critérios pertencem à Entrega 2.
