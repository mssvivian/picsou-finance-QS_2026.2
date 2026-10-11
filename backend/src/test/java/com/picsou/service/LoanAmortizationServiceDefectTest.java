package com.picsou.service;

import com.picsou.model.Debt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Opt-in reproductions of defects reported by Paulo; not production fixes.
 * Run with -Dpicsou.entrega1.defect-regressions=true -Dtest=LoanAmortizationServiceDefectTest.
 * These assertions express the required result and must stay red while the defects exist.
 */
@EnabledIfSystemProperty(named = "picsou.entrega1.defect-regressions", matches = "true")
class LoanAmortizationServiceDefectTest {
    private final LoanAmortizationService service = new LoanAmortizationService();

    @Test
    void automaticRoundedPayment_displayedCapitalMustSumToPrincipal() {
        Debt debt = Debt.builder().borrowedAmount(new BigDecimal("1000"))
            .interestRate(BigDecimal.ZERO).startDate(LocalDate.of(2026, 1, 1))
            .endDate(LocalDate.of(2026, 4, 1)).build();
        var result = service.compute(debt, LocalDate.of(2026, 4, 1));

        assertThat(result.schedule().stream().map(i -> i.capital()).reduce(BigDecimal.ZERO, BigDecimal::add))
            .as("BUG-PCV-01: the three cent-rounded capitals must repay 1000.00")
            .isEqualByComparingTo("1000.00");
    }

    @Test
    void automaticPaymentWithInsurance_mustIncludeInsuranceInMonthlyTotal() {
        Debt debt = Debt.builder().borrowedAmount(new BigDecimal("1200"))
            .interestRate(BigDecimal.ZERO).insuranceMonthly(new BigDecimal("10"))
            .startDate(LocalDate.of(2026, 11, 1)).endDate(LocalDate.of(2027, 11, 1)).build();
        var result = service.compute(debt, LocalDate.of(2026, 10, 10));

        // 1200/12 = 100 capital; total contractual monthly payment includes 10 insurance.
        assertThat(result.summary().monthlyPayment())
            .as("BUG-PCV-02: automatic installment must include the monthly insurance")
            .isEqualByComparingTo("110.00");
    }
}
