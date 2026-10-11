package com.picsou.service;

import com.picsou.model.Debt;
import com.picsou.service.LoanAmortizationService.LoanScheduleResponse;
import com.picsou.service.LoanAmortizationService.LoanSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class LoanAmortizationServiceTest {

    private final LoanAmortizationService service = new LoanAmortizationService();

    @Test
    void zeroRate_splitEvenlyAcrossInstallments() {
        Debt debt = debt("12000", "0", "100", "2025-01-01", "2026-01-01");

        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2025-01-01"));

        assertThat(out.summary().totalInstallments()).isEqualTo(12);
        assertThat(out.summary().monthlyPayment()).isEqualByComparingTo("100.00");
        assertThat(out.summary().totalInterestCost()).isEqualByComparingTo("0.00");
        assertThat(out.schedule()).hasSize(12);
        assertThat(out.schedule().get(0).capital()).isEqualByComparingTo("100.00");
        assertThat(out.schedule().get(11).remainingBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void monthlyPayment_isComputedWhenNotProvided() {
        // 100k borrowed at 3% over 20y → standard French mortgage
        Debt debt = Debt.builder()
            .borrowedAmount(new BigDecimal("100000"))
            .interestRate(new BigDecimal("0.03"))
            .startDate(LocalDate.parse("2025-01-01"))
            .endDate(LocalDate.parse("2045-01-01"))
            .build();

        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2025-01-01"));

        assertThat(out.summary().totalInstallments()).isEqualTo(240);
        assertThat(out.summary().monthlyPayment().doubleValue())
            .isCloseTo(554.60, within(1.0));
    }

    @Test
    void paidInstallments_computedFromAsOfDate() {
        Debt debt = debt("23649", "0.015", null, "2024-01-01", "2029-01-01");

        // 28 months in (2026-05-01) — matches the user mockup
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-05-01"));

        LoanSummary s = out.summary();
        assertThat(s.totalInstallments()).isEqualTo(60);
        assertThat(s.paidInstallments()).isEqualTo(28);
        assertThat(s.remainingInstallments()).isEqualTo(32);
        assertThat(s.endDate()).isEqualTo(LocalDate.parse("2029-01-01"));
    }

    @Test
    void capitalRepaidPct_isProportionalToCapitalPaid() {
        Debt debt = debt("10000", "0", "1000", "2025-01-01", "2025-11-01");

        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2025-06-01"));

        // 5 months paid out of 10 → 50% of capital
        assertThat(out.summary().paidInstallments()).isEqualTo(5);
        assertThat(out.summary().capitalRepaid()).isEqualByComparingTo("5000.00");
        assertThat(out.summary().capitalRepaidPct()).isEqualByComparingTo("50.00");
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("5000.00");
    }

    @Test
    void insurance_isSplitOutOfCapital() {
        Debt debt = Debt.builder()
            .borrowedAmount(new BigDecimal("12000"))
            .interestRate(new BigDecimal("0"))
            .monthlyPayment(new BigDecimal("110"))
            .insuranceMonthly(new BigDecimal("10"))
            .startDate(LocalDate.parse("2025-01-01"))
            .endDate(LocalDate.parse("2026-01-01"))
            .build();

        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2025-01-01"));

        assertThat(out.summary().monthlyPayment()).isEqualByComparingTo("110.00");
        assertThat(out.summary().monthlyInsurance()).isEqualByComparingTo("10.00");
        assertThat(out.summary().totalInsuranceCost()).isEqualByComparingTo("120.00");
        // Capital portion of each installment = 110 - 10 (insurance) - 0 (interest) = 100
        assertThat(out.schedule().get(0).capital()).isEqualByComparingTo("100.00");
    }

    @Test
    void totalCost_includesFileFees() {
        Debt debt = Debt.builder()
            .borrowedAmount(new BigDecimal("12000"))
            .interestRate(new BigDecimal("0"))
            .monthlyPayment(new BigDecimal("100"))
            .fileFees(new BigDecimal("500"))
            .startDate(LocalDate.parse("2025-01-01"))
            .endDate(LocalDate.parse("2026-01-01"))
            .build();

        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2025-01-01"));

        // 12000 + 0 (interest) + 0 (insurance) + 500 (fees) = 12500
        assertThat(out.summary().totalCost()).isEqualByComparingTo("12500.00");
        assertThat(out.summary().fileFees()).isEqualByComparingTo("500.00");
    }

    @Test
    void loanFinished_remainingBalanceIsZero() {
        Debt debt = debt("1200", "0", "100", "2024-01-01", "2025-01-01");

        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2030-01-01"));

        assertThat(out.summary().paidInstallments()).isEqualTo(12);
        assertThat(out.summary().remainingInstallments()).isZero();
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("0.00");
        assertThat(out.summary().capitalRepaidPct()).isEqualByComparingTo("100.00");
    }

    @Test
    void loanNotStartedYet_remainingBalanceIsPrincipal() {
        Debt debt = debt("12000", "0", "100", "2030-01-01", "2031-01-01");

        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-01-01"));

        assertThat(out.summary().paidInstallments()).isZero();
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("12000");
    }

    @Test
    void computeRemainingBalance_returnsPositiveCapitalRemaining() {
        Debt debt = debt("12000", "0", "100", "2025-01-01", "2026-01-01");

        BigDecimal remaining = service.computeRemainingBalance(debt, LocalDate.parse("2025-04-01"));

        // 3 months × 100€ paid → 300 paid, 11700 left
        assertThat(remaining).isEqualByComparingTo("11700.00");
    }

    // COS482 Entrega 1 — Paulo: fixed-date cases with independent monetary oracles.
    @Test
    void automaticZeroRate_allInstallmentsAndTotalsMatchContract() {
        Debt debt = debt("1200", "0", null, "2026-11-01", "2027-11-01");
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-10-10"));

        assertThat(out.schedule()).hasSize(12);
        for (int i = 0; i < 12; i++) {
            var installment = out.schedule().get(i);
            assertThat(installment.number()).isEqualTo(i + 1);
            assertThat(installment.date()).isEqualTo(LocalDate.of(2026, 11, 1).plusMonths(i + 1));
            assertThat(installment.capital()).isEqualByComparingTo("100");
            assertThat(installment.interest()).isZero();
            assertThat(installment.insurance()).isZero();
            assertThat(installment.totalPayment()).isEqualByComparingTo("100");
            assertThat(installment.remainingBalance()).isEqualByComparingTo(BigDecimal.valueOf(1100 - i * 100L));
        }
        assertThat(out.summary().monthlyPayment()).isEqualByComparingTo("100");
        assertThat(out.summary().totalCost()).isEqualByComparingTo("1200");
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("1200");
        assertThat(out.summary().paidInstallments()).isZero();
        assertThat(out.summary().totalRepaid()).isZero();
    }

    @Test
    void explicitRoundedPayment_lastInstallmentAbsorbsOneCent() {
        // 1000 - 333.33 - 333.33 = 333.34, independently of the service formula.
        Debt debt = debt("1000", "0", "333.33", "2026-01-01", "2026-04-01");
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-04-01"));

        assertThat(out.schedule()).extracting(i -> i.capital())
            .containsExactly(new BigDecimal("333.33"), new BigDecimal("333.33"), new BigDecimal("333.34"));
        assertThat(out.schedule().get(2).totalPayment()).isEqualByComparingTo("333.34");
        assertThat(out.summary().capitalRepaid()).isEqualByComparingTo("1000");
        assertThat(out.summary().remainingBalance()).isZero();
        assertThat(out.schedule().stream().map(i -> i.capital()).reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualByComparingTo("1000");
    }

    @Test
    void paymentAbovePrincipal_capsCapitalWithoutNegativeBalance() {
        Debt debt = debt("1200", "0", "1500", "2026-01-01", "2026-04-01");
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-02-01"));

        assertThat(out.schedule()).hasSize(3);
        assertThat(out.schedule()).extracting(i -> i.capital())
            .containsExactly(new BigDecimal("1200.00"), new BigDecimal("0.00"), new BigDecimal("0.00"));
        assertThat(out.schedule()).allSatisfy(i -> {
            assertThat(i.remainingBalance()).isZero();
            assertThat(i.totalPayment()).isNotNegative();
        });
        assertThat(out.summary().remainingBalance()).isZero();
        assertThat(out.summary().capitalRepaidPct()).isEqualByComparingTo("100");
    }

    @Test
    void insuranceAndFees_fullScheduleAndPartialRepaymentReconcile() {
        Debt debt = debt("1200", "0", "110", "2026-11-01", "2027-11-01");
        debt.setInsuranceMonthly(new BigDecimal("10"));
        debt.setFileFees(new BigDecimal("50"));
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2027-02-01"));

        assertThat(out.schedule()).allSatisfy(i -> {
            assertThat(i.capital()).isEqualByComparingTo("100");
            assertThat(i.insurance()).isEqualByComparingTo("10");
            assertThat(i.interest()).isZero();
            assertThat(i.totalPayment()).isEqualByComparingTo("110");
        });
        assertThat(out.summary().totalInsuranceCost()).isEqualByComparingTo("120");
        assertThat(out.summary().fileFees()).isEqualByComparingTo("50");
        assertThat(out.summary().totalCost()).isEqualByComparingTo("1370");
        assertThat(out.summary().paidInstallments()).isEqualTo(3);
        assertThat(out.summary().capitalRepaid()).isEqualByComparingTo("300");
        assertThat(out.summary().insuranceRepaid()).isEqualByComparingTo("30");
        assertThat(out.summary().totalRepaid()).isEqualByComparingTo("330");
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("900");
        assertThat(out.summary().capitalRepaidPct()).isEqualByComparingTo("25");
    }

    @Test
    void zeroPrincipal_percentageDoesNotDivideByZero() {
        Debt debt = debt("0", "0", null, "2026-01-01", "2027-01-01");
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-07-01"));

        assertThat(out.schedule()).hasSize(12).allSatisfy(i -> {
            assertThat(i.capital()).isZero();
            assertThat(i.interest()).isZero();
            assertThat(i.remainingBalance()).isZero();
        });
        assertThat(out.summary().capitalRepaidPct()).isZero();
        assertThat(out.summary().totalCost()).isZero();
    }

    @Test
    void optionalAmountsAbsent_defaultToZeroAndAutomaticPayment() {
        Debt debt = debt("1200", null, null, "2026-01-01", "2027-01-01");
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-07-01"));

        assertThat(out.summary().monthlyPayment()).isEqualByComparingTo("100");
        assertThat(out.summary().monthlyInterest()).isZero();
        assertThat(out.summary().monthlyInsurance()).isZero();
        assertThat(out.summary().fileFees()).isZero();
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("600");
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NULL,2027-01-01", "2026-01-01,NULL", "NULL,NULL",
        "2026-01-01,2026-01-01", "2027-01-01,2026-01-01",
        "2026-01-01,2026-01-31"
    }, nullValues = "NULL")
    void invalidOrIncompleteTerm_producesEmptySchedule(String start, String end) {
        Debt debt = Debt.builder().borrowedAmount(new BigDecimal("1200"))
            .startDate(start == null ? null : LocalDate.parse(start))
            .endDate(end == null ? null : LocalDate.parse(end)).build();
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-07-01"));

        assertThat(out.schedule()).isEmpty();
        assertThat(out.summary().totalInstallments()).isZero();
        assertThat(out.summary().paidInstallments()).isZero();
        assertThat(out.summary().monthlyPayment()).isZero();
        assertThat(out.summary().monthlyCapital()).isZero();
        assertThat(out.summary().monthlyInterest()).isZero();
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("1200");
    }

    @ParameterizedTest
    @CsvSource({
        "2025-12-01,0,1200,0", "2026-01-01,0,1200,0", "2026-01-31,0,1200,0",
        "2026-02-01,1,1100,100", "2026-07-01,6,600,600",
        "2027-01-01,12,0,1200", "2030-01-01,12,0,1200"
    })
    void fixedReferenceDate_repaymentAndRemainingBalanceMatchElapsedMonths(
            String date, int paid, String remaining, String repaid) {
        Debt debt = debt("1200", "0", null, "2026-01-01", "2027-01-01");
        LoanSummary summary = service.compute(debt, LocalDate.parse(date)).summary();

        assertThat(summary.paidInstallments()).isEqualTo(paid);
        assertThat(summary.remainingInstallments()).isEqualTo(12 - paid);
        assertThat(summary.remainingBalance()).isEqualByComparingTo(remaining);
        assertThat(summary.capitalRepaid()).isEqualByComparingTo(repaid);
        assertThat(summary.totalRepaid()).isEqualByComparingTo(repaid);
        assertThat(summary.capitalRepaid().add(summary.remainingBalance())).isEqualByComparingTo("1200");
        assertThat(service.computeRemainingBalance(debt, LocalDate.parse(date))).isEqualByComparingTo(remaining);
    }

    @Test
    void nullReferenceDate_withValidTermTreatsAllInstallmentsAsUnpaid() {
        Debt debt = debt("1200", "0", null, "2026-01-01", "2027-01-01");
        LoanScheduleResponse out = service.compute(debt, null);

        assertThat(out.schedule()).hasSize(12);
        assertThat(out.summary().paidInstallments()).isZero();
        assertThat(out.summary().remainingBalance()).isEqualByComparingTo("1200");
    }

    @Test
    void changedPrincipal_recomputesWithoutMutatingEarlierResponse() {
        Debt debt = debt("1200", "0", null, "2026-11-01", "2027-11-01");
        LoanScheduleResponse before = service.compute(debt, LocalDate.parse("2026-10-10"));
        debt.setBorrowedAmount(new BigDecimal("2400"));
        LoanScheduleResponse after = service.compute(debt, LocalDate.parse("2026-10-10"));

        assertThat(before.summary().monthlyPayment()).isEqualByComparingTo("100");
        assertThat(before.summary().totalCost()).isEqualByComparingTo("1200");
        assertThat(after.summary().monthlyPayment()).isEqualByComparingTo("200");
        assertThat(after.summary().totalCost()).isEqualByComparingTo("2400");
        assertThat(after.schedule()).hasSize(12).allSatisfy(i ->
            assertThat(i.totalPayment()).isEqualByComparingTo("200"));
    }

    @Test
    void positiveRate_singleInstallmentHasExactInterest() {
        // Annual 12% = monthly 1%: 1000 principal + 10 interest = 1010.
        Debt debt = debt("1000", "0.12", null, "2026-01-01", "2026-02-01");
        LoanScheduleResponse out = service.compute(debt, LocalDate.parse("2026-02-01"));

        assertThat(out.schedule()).hasSize(1);
        assertThat(out.summary().monthlyPayment()).isEqualByComparingTo("1010");
        assertThat(out.schedule().getFirst().capital()).isEqualByComparingTo("1000");
        assertThat(out.schedule().getFirst().interest()).isEqualByComparingTo("10");
        assertThat(out.summary().interestRepaid()).isEqualByComparingTo("10");
        assertThat(out.summary().totalCost()).isEqualByComparingTo("1010");
        assertThat(out.summary().totalRepaid()).isEqualByComparingTo("1010");
        assertThat(out.summary().remainingBalance()).isZero();
    }

    private static Debt debt(String borrowed, String rate, String monthly, String start, String end) {
        return Debt.builder()
            .borrowedAmount(new BigDecimal(borrowed))
            .interestRate(rate == null ? null : new BigDecimal(rate))
            .monthlyPayment(monthly == null ? null : new BigDecimal(monthly))
            .startDate(LocalDate.parse(start))
            .endDate(LocalDate.parse(end))
            .build();
    }
}
