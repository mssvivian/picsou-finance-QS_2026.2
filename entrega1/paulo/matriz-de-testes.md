# Matriz de rastreabilidade — Paulo

Origem dos requisitos: `docs/features/loans.md`, ADR `docs/decisions/2026-04-26-loan-amortization-on-the-fly.md` e cálculos independentes. São identificadores locais de rastreabilidade, não requisitos já cadastrados pelo grupo no TestLink.

| ID | Regra / cenário | Método de teste acrescentado | Execuções | Caso manual / defeito |
|---|---|---|---:|---|
| R-PCV-01 | Parcela automática e cronograma de 12 meses sem juros | automaticZeroRate_allInstallmentsAndTotalsMatchContract | 1 | CT-PCV-01 |
| R-PCV-02 | Última parcela absorve um centavo quando parcela explícita é 333,33 | explicitRoundedPayment_lastInstallmentAbsorbsOneCent | 1 | BUG-PCV-01, caso automático diferente |
| R-PCV-03 | Parcela acima do saldo não produz capital ou saldo negativo | paymentAbovePrincipal_capsCapitalWithoutNegativeBalance | 1 | Unitário |
| R-PCV-04 | Seguro, tarifa, custo e amortização parcial reconciliam | insuranceAndFees_fullScheduleAndPartialRepaymentReconcile | 1 | CT-PCV-02 |
| R-PCV-05 | Principal zero não provoca divisão por zero | zeroPrincipal_percentageDoesNotDivideByZero | 1 | Unitário |
| R-PCV-06 | Valores opcionais ausentes equivalem a zero | optionalAmountsAbsent_defaultToZeroAndAutomaticPayment | 1 | CT-PCV-01 |
| R-PCV-07 | Datas ausentes, iguais, invertidas ou duração inferior a um mês | invalidOrIncompleteTerm_producesEmptySchedule | 6 | Unitário |
| R-PCV-08 | Referência antes/no início, durante, no fim e após o contrato | fixedReferenceDate_repaymentAndRemainingBalanceMatchElapsedMonths | 7 | Unitário |
| R-PCV-09 | Referência nula conserva parcelas como não pagas | nullReferenceDate_withValidTermTreatsAllInstallmentsAsUnpaid | 1 | Unitário |
| R-PCV-10 | Edição do principal recalcula sem alterar resposta anterior | changedPrincipal_recomputesWithoutMutatingEarlierResponse | 1 | CT-PCV-03 |
| R-PCV-11 | Juros de 12% ao ano em um mês: 1000 + 10 = 1010 | positiveRate_singleInstallmentHasExactInterest | 1 | Unitário |
| D-PCV-01 | Capitais exibidos de parcela automática somam o principal | automaticRoundedPayment_displayedCapitalMustSumToPrincipal | 1 opt-in | BUG-PCV-01 |
| D-PCV-02 | Parcela automática deve incluir seguro mensal | automaticPaymentWithInsurance_mustIncludeInsuranceInMonthlyTotal | 1 opt-in | BUG-PCV-02 |

11 métodos próprios normais produzem **22 execuções adicionais**. Somados aos 9 testes herdados, a classe principal executa **31** casos. Os 2 testes opt-in dos defeitos ficam em uma classe separada e não contam como aprovados. Os casos manuais somente serão considerados executados após registro real no ambiente e na ferramenta.
