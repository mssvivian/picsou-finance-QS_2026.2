# BUG-PCV-01 — Capitais exibidos não somam o principal na parcela automática

**Estado:** reproduzido e publicado em [GitHub #4](https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/4); aberto, sem correção. **Severidade proposta:** média (inconsistência de um centavo nos valores apresentados). **Autor:** Paulo. **Código:** `6b020c76cf093b8995a68df12a32e8c5b21e9144`, classe original sem alteração.

## Reprodução

1. Criar Debt com principal 1000, juros 0, parcela mensal nula, início 01/01/2026 e término 01/04/2026.
2. Calcular `compute(debt, LocalDate.of(2026, 4, 1))`.
3. Somar o capital das três parcelas retornadas, já com duas casas decimais.

**Esperado:** soma 1000,00. Uma distribuição monetária consistente é 333,33 + 333,33 + 333,34; a documentação afirma que a última parcela absorve o resíduo.

**Observado:** soma **999,99**. A precisão interna é maior que dois decimais; os capitais são arredondados individualmente na resposta. O resumo pode indicar 1000,00 quitados e saldo zero enquanto o cronograma retornado perde um centavo. O teste confirma a soma divergente; não foi executado um caso de interface para este defeito.

## Evidência e comando

[Teste de reprodução](../../../backend/src/test/java/com/picsou/service/LoanAmortizationServiceDefectTest.java), método `automaticRoundedPayment_displayedCapitalMustSumToPrincipal`.

Da pasta backend: `mvn -Dtest=LoanAmortizationServiceDefectTest -Dpicsou.entrega1.defect-regressions=true test`.

[Saída Maven](../evidencias/unitarios/defeitos/maven-output.txt) e [XML Surefire](../evidencias/unitarios/defeitos/TEST-com.picsou.service.LoanAmortizationServiceDefectTest.xml): expected 1000.00, actual 999.99. Execução em 10/10/2026, 20h03, Brasília, Windows 11, Java 21, Maven 3.9.16.

## Limites e próximo passo

Não alterar o oráculo para 999,99. Acompanhar a issue publicada e manter a regressão opt-in até a correção ser autorizada em outra contribuição. O teste de parcela **explícita** 333,33 passa com última parcela 333,34: o problema é reproduzido no caminho **automático**.

## Conferência após integrar a main

Reproduzido em 11/10 no commit `4d7a2d362cc12bd24e0d6352beceaac7fc0ec6c6`. [Resultado das duas reproduções](../evidencias/unitarios/integracao-defeitos/execucao.json): duas falhas de asserção, nenhum erro de infraestrutura.
