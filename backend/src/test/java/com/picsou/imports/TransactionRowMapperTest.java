// COMPLEXIDADE CICLOMÁTICA TOTAL: 39

package com.picsou.imports;

import com.picsou.dto.ColumnMappingDto;
import com.picsou.imports.csv.CsvDialect;
import com.picsou.imports.csv.DecimalStyle;
import com.picsou.model.Account;
import com.picsou.model.Transaction;
import com.picsou.model.TransactionType;
import com.picsou.service.InstrumentFieldResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRowMapperTest {

    @Mock
    private InstrumentFieldResolver instrumentFieldResolver;

    @InjectMocks
    private TransactionRowMapper mapper;

    private Account defaultAccount;
    private CsvDialect defaultDialect;

    @BeforeEach
    void setUp() {
        defaultAccount = new Account();
        defaultAccount.setCurrency("EUR");

        defaultDialect = new CsvDialect(',', DecimalStyle.DOT, "yyyy-MM-dd");
    }

    private ColumnMappingDto defaultMapping() {
        return new ColumnMappingDto(0, 1, 2, 3, 4, 5, 6, 7, 8);
    }

    private void mockResolvedInstrument() {
        InstrumentFieldResolver.ResolvedInstrument inst =
            new InstrumentFieldResolver.ResolvedInstrument("AAPL", "Apple Inc.", "Apple Inc. (AAPL)");
        when(instrumentFieldResolver.resolve(anyString(), anyString())).thenReturn(inst);
    }

  // GRUPO DE TESTE 1: MÉTODO map(...)
    
    @Test
    @DisplayName("01. map: Caminho Feliz com todos os campos válidos")
    void test01_map_success_allFieldsValid() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "BUY", "AAPL", "Apple Inc.", "10", "150.00", "5.00", "USD", "-1505.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertNotNull(tx);
        assertEquals(LocalDate.of(2026, 3, 15), tx.getDate());
        assertEquals(TransactionType.BUY, tx.getTxType());
        assertEquals("USD", tx.getNativeCurrency());
        assertEquals(new BigDecimal("10"), tx.getQuantity());
        assertEquals(new BigDecimal("5.00"), tx.getFees());
    }
    
    @Test
    @DisplayName("02. map: date == null deve lançar IllegalArgumentException")
    void test02_map_dateNull_throwsException() {
        List<String> row = Arrays.asList("", "BUY", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "-1500.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Missing date", ex.getMessage());
    }
    
    @Test
    @DisplayName("03. map: quantity == null deve lançar IllegalArgumentException")
    void test03_map_quantityNull_throwsException() {
        List<String> row = Arrays.asList("2026-03-15", "BUY", "AAPL", "Apple Inc.", "", "150.00", "0", "USD", "-1500.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Missing or non-positive quantity", ex.getMessage());
    }
    
    @Test
    @DisplayName("04. map: quantity <= 0 (zero) deve lançar IllegalArgumentException")
    void test04_map_quantityZero_throwsException() {
        List<String> row = Arrays.asList("2026-03-15", "BUY", "AAPL", "Apple Inc.", "0", "150.00", "0", "USD", "-1500.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Missing or non-positive quantity", ex.getMessage());
    }
    
    @Test
    @DisplayName("05. map: quantity <= 0 (negativo) deve lançar IllegalArgumentException")
    void test05_map_quantityNegative_throwsException() {
        List<String> row = Arrays.asList("2026-03-15", "BUY", "AAPL", "Apple Inc.", "-5", "150.00", "0", "USD", "-1500.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Missing or non-positive quantity", ex.getMessage());
    }
    
    @Test
    @DisplayName("06. map: fees == null deve assumir BigDecimal.ZERO")
    void test06_map_feesNull_defaultsToZero() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "BUY", "AAPL", "Apple Inc.", "10", "150.00", "", "USD", "-1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(BigDecimal.ZERO, tx.getFees());
    }
    
    @Test
    @DisplayName("07. map: fees < 0 deve lançar IllegalArgumentException")
    void test07_map_feesNegative_throwsException() {
        List<String> row = Arrays.asList("2026-03-15", "BUY", "AAPL", "Apple Inc.", "10", "150.00", "-2.00", "USD", "-1500.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Fees cannot be negative", ex.getMessage());
    }

    @Test
    @DisplayName("08. map: currency vazia deve herdar moeda da conta")
    void test08_map_currencyBlank_defaultsToAccountCurrency() {
        mockResolvedInstrument();
        // Define o valor diretamente no objeto real em vez de usar when(...)
        defaultAccount.setCurrency("EUR");

        List<String> row = Arrays.asList(
            "2026-03-15", "BUY", "AAPL", "Apple Inc.", "10", "150.00", "0", "   ", "-1500.00"
        );

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertNotNull(tx);
        assertEquals("EUR", tx.getNativeCurrency());
    }
    
    @Test
    @DisplayName("09. map: instrument == null deve lançar IllegalArgumentException")
    void test09_map_instrumentNull_throwsException() {
        when(instrumentFieldResolver.resolve(anyString(), anyString())).thenReturn(null);
        List<String> row = Arrays.asList("2026-03-15", "BUY", "UNKNOWN", "Unknown", "10", "150.00", "0", "USD", "-1500.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Missing ticker/ISIN", ex.getMessage());
    }

    // GRUPO DE TESTE 2: método resolveSide()
    
    @Test
    @DisplayName("10. resolveSide: sideCell preenchido encontra correspondência no sideValueMap")
    void test10_resolveSide_sideValueMap_hit() {
        mockResolvedInstrument();
        Map<String, String> map = Collections.singletonMap("ACHAT_PERSO", "BUY");
        List<String> row = Arrays.asList("2026-03-15", "ACHAT_PERSO", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "-1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, map, false, defaultAccount);

        assertEquals(TransactionType.BUY, tx.getTxType());
    }

    @Test
    @DisplayName("11. resolveSide: sideValueMap não é nulo mas não contém chave correspondente")
    void test11_resolveSide_sideValueMap_miss_fallsThrough() {
        mockResolvedInstrument();
        Map<String, String> map = Collections.singletonMap("OUTRO", "BUY");
        List<String> row = Arrays.asList("2026-03-15", "sell", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, map, false, defaultAccount);

        assertEquals(TransactionType.SELL, tx.getTxType());
    }
    
    @Test
    @DisplayName("12. resolveSide: lower.equals('s') resulta em SELL")
    void test12_resolveSide_tokenExactS_returnsSell() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "s", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.SELL, tx.getTxType());
    }

    @Test
    @DisplayName("13. resolveSide: lower contém 'vent' resulta em SELL")
    void test13_resolveSide_tokenContainsVent_returnsSell() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "Vente de titres", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.SELL, tx.getTxType());
    }

    @Test
    @DisplayName("14. resolveSide: lower contém 'sell' resulta em SELL")
    void test14_resolveSide_tokenContainsSell_returnsSell() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "Market Sell", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.SELL, tx.getTxType());
    }

    @Test
    @DisplayName("15. resolveSide: lower contém 'sale' resulta em SELL")
    void test15_resolveSide_tokenContainsSale_returnsSell() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "Direct Sale", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.SELL, tx.getTxType());
    }

    @Test
    @DisplayName("16. resolveSide: lower.equals('b') resulta em BUY")
    void test16_resolveSide_tokenExactB_returnsBuy() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "b", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "-1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.BUY, tx.getTxType());
    }

    @Test
    @DisplayName("17. resolveSide: lower contém 'ach' resulta em BUY")
    void test17_resolveSide_tokenContainsAch_returnsBuy() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "Achat d'actions", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "-1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.BUY, tx.getTxType());
    }
    
    @Test
    @DisplayName("18. resolveSide: lower contém 'buy' resulta em BUY")
    void test18_resolveSide_tokenContainsBuy_returnsBuy() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "Limit Buy", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "-1500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.BUY, tx.getTxType());
    }

    @Test
    @DisplayName("19. resolveSide: sideCell presente mas desconhecido, recorre a amount negativo -> BUY")
    void test19_resolveSide_unknownSideCell_fallbackNegativeAmount_returnsBuy() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "Souscription", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "-500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.BUY, tx.getTxType());
    }

    @Test
    @DisplayName("20. resolveSide: sideCell presente mas desconhecido, recorre a amount positivo -> SELL")
    void test20_resolveSide_unknownSideCell_fallbackPositiveAmount_returnsSell() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "Redemption", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "500.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.SELL, tx.getTxType());
    }

    @Test
    @DisplayName("21. resolveSide: sideCell nulo/em branco e amount negativo -> BUY")
    void test21_resolveSide_blankSideCell_negativeAmount_returnsBuy() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", "   ", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "-1200.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.BUY, tx.getTxType());
    }

    @Test
    @DisplayName("22. resolveSide: sideCell nulo/em branco e amount positivo -> SELL")
    void test22_resolveSide_blankSideCell_positiveAmount_returnsSell() {
        mockResolvedInstrument();
        List<String> row = Arrays.asList("2026-03-15", null, "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "1200.00");

        Transaction tx = mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount);

        assertEquals(TransactionType.SELL, tx.getTxType());
    }

    @Test
    @DisplayName("23. resolveSide: sideCell nulo e amount nulo lança IllegalArgumentException")
    void test23_resolveSide_blankSideCell_nullAmount_throwsException() {
        List<String> row = Arrays.asList("2026-03-15", "", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Cannot determine BUY/SELL", ex.getMessage());
    }

    @Test
    @DisplayName("24. resolveSide: sideCell com texto irrelevante e amount nulo lança IllegalArgumentException")
    void test24_resolveSide_unrecognizedSideCell_nullAmount_throwsException() {
        List<String> row = Arrays.asList("2026-03-15", "Transfer", "AAPL", "Apple Inc.", "10", "150.00", "0", "USD", "");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            mapper.map(row, defaultMapping(), defaultDialect, null, false, defaultAccount)
        );
        assertEquals("Cannot determine BUY/SELL", ex.getMessage());
    }

    

}
