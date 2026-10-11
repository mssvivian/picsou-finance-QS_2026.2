# Roteiro individual — Paulo (3 a 4 minutos)

1. **Escopo (30s):** “Testei o cálculo de amortização do Picsou. A classe recebe parâmetros de uma dívida e calcula o cronograma e o resumo sob demanda. Não testei sincronização de wallets nem substituí a contribuição dos colegas.”
2. **Algoritmo (45s):** juros mensais = taxa anual decimal / 12; parcela automática usa a fórmula de amortização e, sem juros, P/n. Cada parcela separa capital, juros e seguro, reduz o saldo e acumula valores. Datas fixas tornam os testes reproduzíveis. Parcelas pagas são estimadas por meses de calendário.
3. **Complexidade (30s):** “O método principal tem V(G)=14: base 1, um laço, quatro if e oito ternários. O relatório inclui os tokens e as linhas. A soma dos métodos explícitos é 30.”
4. **Contribuição própria (45s):** “O projeto já tinha nove testes. Preservei esses testes e acrescentei 22 execuções, incluindo entradas de fronteira, valores opcionais, composição de custos e recálculo. Todos os 31 casos normais passaram.” Mostrar diff/commit e um oráculo simples: 1200/12=100; com seguro 10 e tarifa 50, custo total 1370.
5. **Defeitos (45s):** “Encontrei cronograma automático que soma 999,99 para principal 1000 e parcela automática que omite seguro. Os testes de reprodução têm esperado independente e falham. Não mudei a produção nem ajustei o esperado para acomodar a falha.” Mostrar XML/saída e issues #4/#5.
6. **Manual e conclusão factual (30s):** apresentar dd-5/6/7 Passed, 15 passos aprovados e 30 capturas; informar execução assistida pelo Codex e build testado. A suíte atual após integrar a main tem 31 aprovados e dois ignorados; os resultados amplos anteriores são históricos.

## Perguntas para ensaiar

- O que distingue unitário de manual? Unitário chama a classe com dados controlados; manual verifica fluxo real de interface, persistência e backend.
- Por que não usar Mockito aqui? Não há repositórios nem portas externas na classe. Mockar BigDecimal/Debt apenas enfraqueceria o teste.
- De onde vêm os esperados? Regras documentadas, contas independentes e datas fixas; não copiar a fórmula da implementação para obter o esperado.
- O que está no fork original? Nove testes herdados. O diff e a matriz identificam o que Paulo acrescentou.
- 31 aprovados significam sistema aprovado? Não: a suíte ampla tem erros/ignorados; existem dois defeitos confirmados e os três casos pela interface foram aprovados, mas não exercitam os dados específicos dos defeitos.
- Como a autoria será comprovada? Commits de Paulo, branch publicada no GitHub e histórico de edição no Google Docs da própria conta.
- Onde estão 80% de arestas e mutação? São metas da Entrega 2; ainda não foram medidas nesta contribuição.
