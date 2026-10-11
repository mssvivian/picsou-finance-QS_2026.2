# BUG-PCV-02 — Parcela automática não inclui o seguro mensal

**Estado:** reproduzido e publicado em [GitHub #5](https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/5); aberto, sem correção. **Severidade proposta:** alta (parcela e distribuição de capital inconsistentes). **Autor:** Paulo. **Código:** `6b020c76cf093b8995a68df12a32e8c5b21e9144`, classe original sem alteração.

## Reprodução

1. Criar Debt com principal 1200, juros 0, seguro mensal 10, parcela mensal nula, início 01/11/2026 e término 01/11/2027.
2. Calcular `compute(debt, LocalDate.of(2026, 10, 10))`.
3. Conferir a parcela mensal do resumo.

**Esperado:** 1200 / 12 = 100 de capital por mês; com seguro 10, o total mensal deve ser **110,00**. A documentação e o caminho de parcela explícita tratam a parcela como total incluindo o seguro.

**Observado confirmado por asserção:** parcela automática **100,00**. Pela leitura do algoritmo, o seguro é posteriormente subtraído desse total para calcular capital, concentrando o restante na última parcela. A evidência unitária anexada confirma diretamente a parcela incorreta, não uma execução manual da interface.

## Evidência e comando

[Teste de reprodução](../../../backend/src/test/java/com/picsou/service/LoanAmortizationServiceDefectTest.java), método `automaticPaymentWithInsurance_mustIncludeInsuranceInMonthlyTotal`.

Da pasta backend: `mvn -Dtest=LoanAmortizationServiceDefectTest -Dpicsou.entrega1.defect-regressions=true test`.

[Saída Maven](../evidencias/unitarios/defeitos/maven-output.txt) e [XML Surefire](../evidencias/unitarios/defeitos/TEST-com.picsou.service.LoanAmortizationServiceDefectTest.xml): expected 110.00, actual 100.00. Execução em 10/10/2026, 20h03, Brasília, Windows 11, Java 21, Maven 3.9.16.

## Limites e próximo passo

CT-PCV-02 fornece parcela **explícita** 110 e seu oráculo permanece correto. Para reproduzir este defeito na interface, repetir aquele contrato deixando a parcela em branco, em um caso exploratório separado. Não corrigir produção nesta branch e não cadastrar o caso como aprovado.

## Conferência após integrar a main

Reproduzido em 11/10 no commit `4d7a2d362cc12bd24e0d6352beceaac7fc0ec6c6`. [Resultado das duas reproduções](../evidencias/unitarios/integracao-defeitos/execucao.json): duas falhas de asserção, nenhum erro de infraestrutura.
