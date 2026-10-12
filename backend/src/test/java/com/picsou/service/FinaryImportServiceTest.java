/*
 * ============================================================================
 *  COS482 - Qualidade de Software | Trabalho 1
 *  Testes Unitários - critério do caminho base (um teste por unidade de CC)
 * ----------------------------------------------------------------------------
 *  Autora:            Vivian Souza
 *  Classe sob teste:  backend/src/main/java/com/picsou/service/FinaryImportService.java
 * ----------------------------------------------------------------------------
 *
 *  Método                                              V(G)  Testes
 *  --------------------------------------------------  ----  ------
 *  preview(MultipartFile, Long)                           2       2
 *  executeImport(FinaryImportRequest, Long)              10      10
 *  parseXlsx(Workbook)  (privado)                        22      22
 *  cellString(Row, int)  (privado)                        4       4
 *  buildParsedAccountKey(String, String)  (privado)       1       1
 *  cellNumeric(Row, int)  (privado)                       3       3
 *  slugify(String)  (privado)                             1       1
 *  cleanupExpiredCache()                                  1       1
 *  --------------------------------------------------  ----  ------
 *  Total (contagem manual)                               44      44
 *
 *  + 6 testes extras (prefixo extra_), fora da contagem de CC:
 *    - 3 testes documentam defeitos conhecidos marcados com @Disabled
 *      (Issues #1, #2 e #3 do GitHub);
 *    - 3 testes cobrem robustez adicional de tokens e validações de membros.
 *
 *  Convenção de nomes: <metodo>_pNN_<caminho coberto>
 *  Os métodos privados são exercitados por reflexão (helper call()).
 * ============================================================================
 */

package com.picsou.service;

import com.picsou.dto.FinaryAccountMapping;
import com.picsou.dto.FinaryAccountPreview;
import com.picsou.dto.FinaryImportRequest;
import com.picsou.dto.FinaryImportResultResponse;
import com.picsou.dto.FinaryMappingAction;
import com.picsou.dto.FinaryPreviewResponse;
import com.picsou.dto.ImportedAccountSummary;
import com.picsou.dto.NewAccountDetails;
import com.picsou.exception.ResourceNotFoundException;
import com.picsou.finary.FinaryPersistenceHelper;
import com.picsou.finary.FinaryPersistenceHelper.ParsedFinaryAccount;
import com.picsou.finary.FinaryPersistenceHelper.ParsedFinaryTransaction;
import com.picsou.model.Account;
import com.picsou.model.AccountType;
import com.picsou.model.FamilyMember;
import com.picsou.repository.AccountRepository;
import com.picsou.repository.BalanceSnapshotRepository;
import com.picsou.repository.FamilyMemberRepository;
import com.picsou.repository.TransactionRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FinaryImportServiceTest {

    private static final Long MEMBER_ID = 1L;
    private static final String XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final List<String> KNOWN_CATEGORIES = List.of(
        "Checkings", "Savings", "Investments", "Real Estate", "Cryptos",
        "Fonds Euro", "Commodities", "Credits", "Other Assets", "Startups"
    );

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BalanceSnapshotRepository balanceSnapshotRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private FamilyMemberRepository familyMemberRepository;
    @Mock
    private FinaryPersistenceHelper persistenceHelper;

    private FinaryImportService service;
    private FamilyMember member;

    @BeforeEach
    void setUp() {
        service = new FinaryImportService(
            accountRepository,
            balanceSnapshotRepository,
            transactionRepository,
            familyMemberRepository,
            persistenceHelper
        );
        member = FamilyMember.builder().id(MEMBER_ID).displayName("Vivian").build();
        when(familyMemberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(100L);
            return a;
        });
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(MEMBER_ID)).thenReturn(List.of());
    }

    // ───────────────────────── helpers ─────────────────────────

    /** Invoca método privado (ou de pacote) pelo nome e quantidade de argumentos. */
    @SuppressWarnings("unchecked")
    private <T> T call(String name, Object... args) {
        Method method = Arrays.stream(FinaryImportService.class.getDeclaredMethods())
            .filter(m -> m.getName().equals(name) && m.getParameterCount() == args.length)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Method not found: " + name));
        method.setAccessible(true);
        try {
            return (T) method.invoke(service, args);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException re) throw re;
            throw new AssertionError(ex.getCause());
        } catch (IllegalAccessException ex) {
            throw new AssertionError(ex);
        }
    }

    private FinaryImportService.ParsedFinaryData parse(Workbook workbook) {
        return call("parseXlsx", workbook);
    }

    /** Escreve uma linha; valor null deixa a célula inexistente, como numa planilha com campo vazio. */
    private static void put(Sheet sheet, int rowIndex, Object... values) {
        Row row = sheet.createRow(rowIndex);
        for (int c = 0; c < values.length; c++) {
            Object v = values[c];
            if (v == null) continue;
            Cell cell = row.createCell(c);
            if (v instanceof Number n) {
                cell.setCellValue(n.doubleValue());
            } else {
                cell.setCellValue(v.toString());
            }
        }
    }

    private static Sheet accountSheet(Workbook wb, String category) {
        Sheet sheet = wb.createSheet(category);
        put(sheet, 0, "Name", "Institution", "Balance", "Currency");
        return sheet;
    }

    private static Sheet transactionSheet(Workbook wb) {
        Sheet sheet = wb.createSheet("Transactions");
        put(sheet, 0, "Category", "Date", "Name", "Amount", "Type", "Account", "Institution", "Currency");
        return sheet;
    }

    private static byte[] xlsx(Consumer<Workbook> fill) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            fill.accept(wb);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static MultipartFile multipart(byte[] bytes) {
        return new MockMultipartFile("file", "finary.xlsx", XLSX_TYPE, bytes);
    }

    /** Planilha mínima: uma conta "Conta Corrente" (1500.50 EUR) em Checkings e nenhuma transação. */
    private static byte[] singleCheckingAccountFile() {
        return xlsx(wb -> {
            Sheet s = accountSheet(wb, "Checkings");
            put(s, 1, "Conta Corrente", "Banco X", 1500.50, "EUR");
        });
    }

    private String previewToken(byte[] file) {
        return service.preview(multipart(file), MEMBER_ID).fileToken();
    }

    private static NewAccountDetails details(AccountType type, String color) {
        return new NewAccountDetails("Minha conta", type, "Banco X", "EUR", color);
    }

    private static FinaryAccountMapping create(String finaryId, String finaryName, String category, NewAccountDetails det) {
        return new FinaryAccountMapping(finaryId, finaryName, category, FinaryMappingAction.CREATE_NEW, null, det);
    }

    private static final String CHECKING_ID = "checkings_conta_corrente";

    // ═════════════ preview — V(G) = 2 ═════════════

    @Test
    void preview_p01_validFileReturnsPreviewWithTokenAccountsAndExistingAccounts() {
        byte[] file = xlsx(wb -> {
            Sheet s = accountSheet(wb, "Checkings");
            put(s, 1, "Conta Corrente", "Banco X", 1500.50, "EUR");
            Sheet t = transactionSheet(wb);
            put(t, 1, "Checkings", "2024-01-15", "Salario", 2500.0, "Income", "Conta Corrente", "Banco X", "EUR");
            put(t, 2, "Checkings", "2024-01-20", "Mercado", -80.0, "Expense", "CONTA CORRENTE", "Banco X", "EUR");
            put(t, 3, "Checkings", "2024-01-21", "Outra", -10.0, "Expense", "Outra conta", "Banco X", "EUR");
        });
        Account existing = Account.builder().id(5L).name("Existente").type(AccountType.CHECKING)
            .currentBalance(BigDecimal.TEN).build();
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(MEMBER_ID)).thenReturn(List.of(existing));

        FinaryPreviewResponse preview = service.preview(multipart(file), MEMBER_ID);

        assertThat(preview.fileToken()).isNotBlank();
        assertThat(preview.autoMapped()).isFalse();
        assertThat(preview.suggestedMappings()).isEmpty();
        assertThat(preview.totalTransactionCount()).isEqualTo(3);
        assertThat(preview.existingPicsouAccounts()).hasSize(1);
        assertThat(preview.existingPicsouAccounts().get(0).id()).isEqualTo(5L);
        assertThat(preview.accounts()).hasSize(1);

        FinaryAccountPreview acc = preview.accounts().get(0);

        assertThat(acc.finaryId()).isEqualTo(CHECKING_ID);
        assertThat(acc.finaryName()).isEqualTo("Conta Corrente");
        assertThat(acc.finaryInstitution()).isEqualTo("Banco X");
        assertThat(acc.finaryCategory()).isEqualTo("Checkings");
        assertThat(acc.suggestedType()).isEqualTo(AccountType.CHECKING);
        assertThat(acc.currentBalance()).isEqualTo(1500.50);
        assertThat(acc.nativeCurrency()).isEqualTo("EUR");
        assertThat(acc.transactionCount()).isEqualTo(2); 
    }

    @Test
    void preview_p02_unreadableFileThrowsIllegalArgumentException() throws IOException {
        MultipartFile broken = mock(MultipartFile.class);
        when(broken.getInputStream()).thenThrow(new IOException("disco"));

        assertThatThrownBy(() -> service.preview(broken, MEMBER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("valid Excel spreadsheet");
    }

    // ═════════════ executeImport — V(G) = 10 ═════════════

    @Test
    void executeImport_p01_unknownTokenThrowsPreviewExpired() {
        FinaryImportRequest req = new FinaryImportRequest(List.of(), "token-inexistente");

        assertThatThrownBy(() -> service.executeImport(req, MEMBER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Preview expired");
    }

    @Test
    void executeImport_p02_emptyMappingsImportsNothingAndConsumesTheToken() {
        String token = previewToken(singleCheckingAccountFile());
        FinaryImportRequest req = new FinaryImportRequest(List.of(), token);

        FinaryImportResultResponse result = service.executeImport(req, MEMBER_ID);

        assertThat(result.accountsCreated()).isZero();
        assertThat(result.accountsMapped()).isZero();
        assertThat(result.accountsSkipped()).isZero();
        assertThat(result.importedAccounts()).isEmpty();
        verify(accountRepository, never()).save(any());
        // efeito colateral: o token foi removido do cache: reutilizá-lo falha
        assertThatThrownBy(() -> service.executeImport(req, MEMBER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Preview expired");
    }

    @Test
    void executeImport_p03_mappingWithFinaryIdResolvesTheAccountById() {
        String token = previewToken(singleCheckingAccountFile());
        // categoria e nome apontam para outra coisa: só o finaryId pode ter localizado a conta
        FinaryAccountMapping mapping = create(CHECKING_ID, "outro nome", "Savings", details(AccountType.CHECKING, "#112233"));

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        assertThat(result.accountsCreated()).isEqualTo(1);
        assertThat(result.accountsMapped()).isZero();
        assertThat(result.accountsSkipped()).isZero();
        assertThat(result.importedAccounts()).hasSize(1);
        assertThat(result.importedAccounts().get(0).name()).isEqualTo("Minha conta");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void executeImport_p04_mappingWithoutFinaryIdBuildsTheKeyFromCategoryAndName() {
        String token = previewToken(singleCheckingAccountFile());
        FinaryAccountMapping mapping = create(null, "Conta Corrente", "Checkings", details(AccountType.CHECKING, "#112233"));

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        assertThat(result.accountsCreated()).isEqualTo(1);
        assertThat(result.accountsMapped()).isZero();
        assertThat(result.accountsSkipped()).isZero();
        assertThat(result.importedAccounts()).hasSize(1);
        assertThat(result.importedAccounts().get(0).name()).isEqualTo("Minha conta");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void executeImport_p05_mappingThatMatchesNoParsedAccountIsIgnored() {
        String token = previewToken(singleCheckingAccountFile());
        FinaryAccountMapping mapping = create("nao_existe", "x", "y", details(AccountType.CHECKING, "#112233"));

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        assertThat(result.accountsCreated()).isZero();
        assertThat(result.accountsMapped()).isZero();
        assertThat(result.accountsSkipped()).isZero();
        assertThat(result.importedAccounts()).isEmpty();
        verify(accountRepository, never()).save(any());
        verify(persistenceHelper, never()).reconstructSnapshots(any(), any(), any());
    }

    @Test
    void executeImport_p06_skipActionCountsTheAccountAndPersistsNothing() {
        String token = previewToken(singleCheckingAccountFile());
        FinaryAccountMapping mapping = new FinaryAccountMapping(
            CHECKING_ID, "Conta Corrente", "Checkings", FinaryMappingAction.SKIP, null, null);

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        assertThat(result.accountsSkipped()).isEqualTo(1);
        assertThat(result.accountsCreated()).isZero();
        assertThat(result.accountsMapped()).isZero();
        assertThat(result.importedAccounts()).isEmpty();
        verify(accountRepository, never()).save(any());
        verify(persistenceHelper, never()).importTransactions(any(), any(), any());
    }

    @Test
    void executeImport_p07_mapExistingUsesTheMembersAccountWithoutCreatingOne() {
        String token = previewToken(singleCheckingAccountFile());
        Account existing = Account.builder().id(7L).name("Existente").type(AccountType.CHECKING)
            .currentBalance(BigDecimal.TEN).build();
        when(accountRepository.findByIdAndMemberId(7L, MEMBER_ID)).thenReturn(Optional.of(existing));
        FinaryAccountMapping mapping = new FinaryAccountMapping(
            CHECKING_ID, "Conta Corrente", "Checkings", FinaryMappingAction.MAP_EXISTING, 7L, null);

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        assertThat(result.accountsMapped()).isEqualTo(1);
        assertThat(result.accountsCreated()).isZero();
        assertThat(result.accountsSkipped()).isZero();
        verify(accountRepository, never()).save(any());
        verify(persistenceHelper).reconstructSnapshots(eq(existing), any(), any());
        verify(persistenceHelper).importTransactions(eq(existing), any(), any());
        assertThat(result.importedAccounts()).extracting(ImportedAccountSummary::id).containsExactly(7L);
    }

    @Test
    void executeImport_p08_createNewBuildsTheAccountFromTheFileAndAggregatesTheCounts() {
        String token = previewToken(singleCheckingAccountFile());
        when(persistenceHelper.reconstructSnapshots(any(), any(), any())).thenReturn(3);
        when(persistenceHelper.importTransactions(any(), any(), any())).thenReturn(5);
        FinaryAccountMapping mapping = create(CHECKING_ID, "Conta Corrente", "Checkings",
            new NewAccountDetails("Minha conta", AccountType.CHECKING, "Banco X", "EUR", "#112233"));

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        ArgumentCaptor<Account> saved = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(saved.capture());
        Account created = saved.getValue();
        assertThat(created.getName()).isEqualTo("Minha conta");
        assertThat(created.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(created.getProvider()).isEqualTo("Banco X");
        assertThat(created.getCurrency()).isEqualTo("EUR");
        assertThat(created.getCurrentBalance()).isEqualByComparingTo("1500.50"); // saldo vem do arquivo, não do mapeamento
        assertThat(created.isManual()).isTrue();
        assertThat(created.getColor()).isEqualTo("#112233");
        assertThat(created.getExternalAccountId()).isEqualTo("finary_Checkings_conta_corrente");
        assertThat(created.getMember()).isSameAs(member);

        assertThat(result.accountsCreated()).isEqualTo(1);
        assertThat(result.snapshotsCreated()).isEqualTo(3);
        assertThat(result.transactionsImported()).isEqualTo(5);
        assertThat(result.importedAccounts()).hasSize(1);
        assertThat(result.importedAccounts().get(0).id()).isEqualTo(100L);
        assertThat(result.importedAccounts().get(0).name()).isEqualTo("Minha conta");
        assertThat(result.importedAccounts().get(0).currentBalance()).isEqualTo(1500.50);
    }

    @Test
    void executeImport_p09_createNewWithoutColorUsesTheDefaultColorOfTheType() {
        String token = previewToken(singleCheckingAccountFile());
        FinaryAccountMapping mapping = create(CHECKING_ID, "Conta Corrente", "Checkings",
            details(AccountType.SAVINGS, null));

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        ArgumentCaptor<Account> saved = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(saved.capture());
        assertThat(saved.getValue().getColor())
            .isEqualTo(FinaryPersistenceHelper.defaultColorForType(AccountType.SAVINGS));
        assertThat(result.accountsCreated()).isEqualTo(1);
    }

    @Test
    void executeImport_p10_mappingWithoutActionImportsNothing() {
        String token = previewToken(singleCheckingAccountFile());
        FinaryAccountMapping mapping = new FinaryAccountMapping(
            CHECKING_ID, "Conta Corrente", "Checkings", null, null, null);

        FinaryImportResultResponse result = service.executeImport(
            new FinaryImportRequest(List.of(mapping), token), MEMBER_ID);

        assertThat(result.accountsCreated()).isZero();
        assertThat(result.accountsMapped()).isZero();
        assertThat(result.accountsSkipped()).isZero();
        assertThat(result.importedAccounts()).isEmpty();
        verify(accountRepository, never()).save(any());
        verify(persistenceHelper, never()).reconstructSnapshots(any(), any(), any());
    }

    // ═════════════ parseXlsx — V(G) = 22 ═════════════

    @Test
    void parseXlsx_p01_validAccountAndTransactionRowsAreParsedAndTrimmed() {
        Workbook wb = new XSSFWorkbook();
        put(accountSheet(wb, "Checkings"), 1, " Conta Corrente ", " Banco X ", 1500.50, " USD ");
        put(transactionSheet(wb), 1, " Checkings ", " 2024-01-15 ", " Salario ", 2500.0, " Income ",
            " Conta Corrente ", "Banco X", " USD ");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).hasSize(1);

        ParsedFinaryAccount acc = parsed.accounts().get(0);

        assertThat(acc.name()).isEqualTo("Conta Corrente");
        assertThat(acc.institution()).isEqualTo("Banco X");
        assertThat(acc.category()).isEqualTo("Checkings");
        assertThat(acc.balance()).isEqualByComparingTo("1500.50");
        assertThat(acc.currency()).isEqualTo("USD");
        assertThat(parsed.transactions()).hasSize(1);

        ParsedFinaryTransaction tx = parsed.transactions().get(0);
        
        assertThat(tx.accountName()).isEqualTo("Conta Corrente");
        assertThat(tx.date()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(tx.description()).isEqualTo("Salario");
        assertThat(tx.amount()).isEqualByComparingTo("2500");
        assertThat(tx.type()).isEqualTo("Income");
        assertThat(tx.category()).isEqualTo("Checkings");
        assertThat(tx.currency()).isEqualTo("USD");
        assertThat(parsed.parsedAt()).isNotNull();
    }

    @Test
    void parseXlsx_p02_missingAccountSheetsAreSkippedButTransactionsStillParse() {
        Workbook wb = new XSSFWorkbook();
        put(transactionSheet(wb), 1, "Checkings", "2024-01-15", "Salario", 2500.0, "Income", "Conta", "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).hasSize(1);
        assertThat(parsed.transactions().get(0).accountName()).isEqualTo("Conta");
        assertThat(parsed.transactions().get(0).description()).isEqualTo("Salario");
        assertThat(parsed.transactions().get(0).amount()).isEqualByComparingTo("2500");
        assertThat(parsed.parsedAt()).isNotNull();
    }

    @Test
    void parseXlsx_p03_accountSheetWithOnlyTheHeaderYieldsNoAccounts() {
        Workbook wb = new XSSFWorkbook();
        accountSheet(wb, "Checkings");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
        assertThat(parsed.parsedAt()).isNotNull();
    }

    @Test
    void parseXlsx_p04_missingRowInTheMiddleOfAnAccountSheetIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        Sheet s = accountSheet(wb, "Checkings");
        put(s, 1, "Primeira", "Banco", 10.0, "EUR");
        // linha 2 nunca criada: sheet.getRow(2) devolve null
        put(s, 3, "Terceira", "Banco", 30.0, "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).extracting(ParsedFinaryAccount::name)
            .containsExactly("Primeira", "Terceira");
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p05_accountRowWithoutNameIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        put(accountSheet(wb, "Checkings"), 1, null, "Banco", 10.0, "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p06_accountRowWithBlankNameIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        put(accountSheet(wb, "Checkings"), 1, "   ", "Banco", 10.0, "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p07_accountRowWithNonNumericBalanceIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        put(accountSheet(wb, "Checkings"), 1, "Conta", "Banco", "abc", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p08_accountRowWithoutInstitutionGetsAnEmptyInstitution() {
        Workbook wb = new XSSFWorkbook();
        put(accountSheet(wb, "Checkings"), 1, "Conta", null, 10.0, "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).hasSize(1);
        ParsedFinaryAccount acc = parsed.accounts().get(0);
        assertThat(acc.name()).isEqualTo("Conta");
        assertThat(acc.institution()).isEmpty();
        assertThat(acc.balance()).isEqualByComparingTo("10.0");
        assertThat(acc.currency()).isEqualTo("EUR");
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p09_accountRowWithoutCurrencyDefaultsToEur() {
        Workbook wb = new XSSFWorkbook();
        put(accountSheet(wb, "Checkings"), 1, "Conta", "Banco", 10.0, null);

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).hasSize(1);
        ParsedFinaryAccount acc = parsed.accounts().get(0);
        assertThat(acc.name()).isEqualTo("Conta");
        assertThat(acc.currency()).isEqualTo("EUR");
        assertThat(acc.balance()).isEqualByComparingTo("10.0");
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p10_malformedAccountRowIsSkipped() throws IOException {
        Workbook wb = mock(Workbook.class);
        Sheet sheet = mock(Sheet.class);
        Row row = mock(Row.class);

        when(wb.getSheet("Checkings")).thenReturn(sheet);
        when(sheet.getLastRowNum()).thenReturn(1);
        when(sheet.getRow(1)).thenReturn(row);
        when(row.getCell(anyInt())).thenThrow(new RuntimeException("célula corrompida"));

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
        verify(wb).close();
    }

    @Test
    void parseXlsx_p11_workbookWithoutTransactionsSheetYieldsNoTransactions() {
        Workbook wb = new XSSFWorkbook();
        put(accountSheet(wb, "Checkings"), 1, "Conta", "Banco", 10.0, "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).hasSize(1);
        assertThat(parsed.accounts().get(0).name()).isEqualTo("Conta");
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p12_transactionsSheetWithOnlyTheHeaderYieldsNoTransactions() {
        Workbook wb = new XSSFWorkbook();
        transactionSheet(wb);

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
        assertThat(parsed.parsedAt()).isNotNull();
    }

    @Test
    void parseXlsx_p13_missingRowInTheTransactionsSheetIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        Sheet t = transactionSheet(wb);
        put(t, 1, "Checkings", "2024-01-15", "A", 1.0, "Income", "Conta", "Banco", "EUR");
        // linha 2 nunca criada
        put(t, 3, "Checkings", "2024-01-17", "C", 3.0, "Income", "Conta", "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).extracting(ParsedFinaryTransaction::description)
            .containsExactly("A", "C");
    }

    @Test
    void parseXlsx_p14_transactionWithoutDateIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        put(transactionSheet(wb), 1, "Checkings", null, "A", 1.0, "Income", "Conta", "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p15_transactionWithNonNumericAmountIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        put(transactionSheet(wb), 1, "Checkings", "2024-01-15", "A", "abc", "Income", "Conta", "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p16_transactionWithoutAccountNameIsSkipped() {
        Workbook wb = new XSSFWorkbook();
        put(transactionSheet(wb), 1, "Checkings", "2024-01-15", "A", 1.0, "Income", null, "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p17_transactionWithoutNameGetsAnEmptyDescription() {
        Workbook wb = new XSSFWorkbook();
        put(transactionSheet(wb), 1, "Checkings", "2024-01-15", null, 1.0, "Income", "Conta", "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.transactions()).hasSize(1);
        ParsedFinaryTransaction tx = parsed.transactions().get(0);
        assertThat(tx.description()).isEmpty();
        assertThat(tx.accountName()).isEqualTo("Conta");
        assertThat(tx.amount()).isEqualByComparingTo("1.0");
        assertThat(parsed.accounts()).isEmpty();
    }

    @Test
    void parseXlsx_p18_transactionWithoutCategoryGetsAnEmptyCategory() {
        Workbook wb = new XSSFWorkbook();
        put(transactionSheet(wb), 1, null, "2024-01-15", "A", 1.0, "Income", "Conta", "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.transactions()).hasSize(1);
        ParsedFinaryTransaction tx = parsed.transactions().get(0);
        assertThat(tx.category()).isEmpty();
        assertThat(tx.description()).isEqualTo("A");
        assertThat(tx.accountName()).isEqualTo("Conta");
        assertThat(parsed.accounts()).isEmpty();
    }

    @Test
    void parseXlsx_p19_transactionWithoutCurrencyDefaultsToEur() {
        Workbook wb = new XSSFWorkbook();
        put(transactionSheet(wb), 1, "Checkings", "2024-01-15", "A", 1.0, "Income", "Conta", "Banco");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.transactions()).hasSize(1);
        ParsedFinaryTransaction tx = parsed.transactions().get(0);
        assertThat(tx.currency()).isEqualTo("EUR");
        assertThat(tx.description()).isEqualTo("A");
        assertThat(tx.accountName()).isEqualTo("Conta");
        assertThat(parsed.accounts()).isEmpty();
    }

    @Test
    void parseXlsx_p20_transactionWithUnparseableDateIsSkippedAndTheOthersSurvive() {
        Workbook wb = new XSSFWorkbook();
        Sheet t = transactionSheet(wb);
        put(t, 1, "Checkings", "31/12/2024", "ruim", 1.0, "Income", "Conta", "Banco", "EUR");
        put(t, 2, "Checkings", "2024-12-31", "boa", 2.0, "Income", "Conta", "Banco", "EUR");

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.transactions()).hasSize(1);
        ParsedFinaryTransaction tx = parsed.transactions().get(0);
        assertThat(tx.description()).isEqualTo("boa");
        assertThat(tx.amount()).isEqualByComparingTo("2.0");
        assertThat(tx.date()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(parsed.accounts()).isEmpty();
    }

    @Test
    void parseXlsx_p21_transactionRowThatThrowsIsSkipped() {
        Workbook wb = mock(Workbook.class);
        Sheet sheet = mock(Sheet.class);
        Row row = mock(Row.class);
        when(wb.getSheet("Transactions")).thenReturn(sheet);
        when(sheet.getLastRowNum()).thenReturn(1);
        when(sheet.getRow(1)).thenReturn(row);
        when(row.getCell(anyInt())).thenThrow(new RuntimeException("célula corrompida"));

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).isEmpty();
        assertThat(parsed.transactions()).isEmpty();
    }

    @Test
    void parseXlsx_p22_everyKnownCategorySheetIsReadInTheKnownOrder() {
        Workbook wb = new XSSFWorkbook();
        // criadas em ordem inversa para provar que a ordem de leitura vem da lista de categorias
        for (int i = KNOWN_CATEGORIES.size() - 1; i >= 0; i--) {
            put(accountSheet(wb, KNOWN_CATEGORIES.get(i)), 1, "Conta " + i, "Banco", 10.0 * (i + 1), "EUR");
        }

        FinaryImportService.ParsedFinaryData parsed = parse(wb);

        assertThat(parsed.accounts()).hasSize(KNOWN_CATEGORIES.size());
        assertThat(parsed.accounts()).extracting(ParsedFinaryAccount::category)
            .containsExactlyElementsOf(KNOWN_CATEGORIES);
        assertThat(parsed.transactions()).isEmpty();
    }

    // ═════════════ cellString — V(G) = 4 ═════════════

    @Test
    void cellString_p01_missingCellReturnsNull() {
        Row row = mock(Row.class);
        when(row.getCell(0)).thenReturn(null);

        String value = call("cellString", row, 0);

        assertThat(value).isNull();
    }

    @Test
    void cellString_p02_stringCellReturnsItsText() {
        Row row = mock(Row.class);
        Cell cell = mock(Cell.class);
        when(row.getCell(0)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn("texto");

        String value = call("cellString", row, 0);

        assertThat(value).isEqualTo("texto");
    }

    @Test
    void cellString_p03_numericCellReturnsTheValueTruncatedToAnInteger() {
        Row row = mock(Row.class);
        Cell cell = mock(Cell.class);
        when(row.getCell(0)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(42.9);

        String value = call("cellString", row, 0);

        assertThat(value).isEqualTo("42");
    }

    @Test
    void cellString_p04_otherCellTypesReturnNull() {
        Row row = mock(Row.class);
        Cell cell = mock(Cell.class);
        when(row.getCell(0)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);

        String value = call("cellString", row, 0);

        assertThat(value).isNull();
    }

    // ═════════════ buildParsedAccountKey — V(G) = 1 ═════════════

    @Test
    void buildParsedAccountKey_p01_joinsCategoryAndNameAsASlug() {
        String key = call("buildParsedAccountKey", "Real Estate", "Casa da Praia!");

        assertThat(key).isEqualTo("real_estate_casa_da_praia");
    }

    // ═════════════ cellNumeric — V(G) = 3 ═════════════

    @Test
    void cellNumeric_p01_missingCellReturnsNull() {
        Row row = mock(Row.class);
        when(row.getCell(2)).thenReturn(null);

        BigDecimal value = call("cellNumeric", row, 2);

        assertThat(value).isNull();
    }

    @Test
    void cellNumeric_p02_nonNumericCellReturnsNull() {
        Row row = mock(Row.class);
        Cell cell = mock(Cell.class);
        when(row.getCell(2)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.STRING);

        BigDecimal value = call("cellNumeric", row, 2);

        assertThat(value).isNull();
    }

    @Test
    void cellNumeric_p03_numericCellReturnsItsValueAsBigDecimal() {
        Row row = mock(Row.class);
        Cell cell = mock(Cell.class);
        when(row.getCell(2)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(12.5);

        BigDecimal value = call("cellNumeric", row, 2);

        assertThat(value).isEqualByComparingTo("12.5");
    }

    // ═════════════ slugify — V(G) = 1 ═════════════

    @Test
    void slugify_p01_lowercasesAndCollapsesNonAlphanumericRuns() {
        String slug = call("slugify", "  Livret A -- Épargne  ");

        // acentos viram separador; "_" inicial e final são removidos
        assertThat(slug).isEqualTo("livret_a_pargne");
    }

    // ═════════════ cleanupExpiredCache — V(G) = 1 ═════════════

    @Test
    @SuppressWarnings("unchecked")
    void cleanupExpiredCache_p01_removesEntriesOlderThanThirtyMinutesAndKeepsTheRest() throws Exception {
        Field field = FinaryImportService.class.getDeclaredField("cache");
        field.setAccessible(true);
        ConcurrentHashMap<String, FinaryImportService.ParsedFinaryData> cache =
            (ConcurrentHashMap<String, FinaryImportService.ParsedFinaryData>) field.get(service);
        cache.put("velho", new FinaryImportService.ParsedFinaryData(List.of(), List.of(),
            Instant.now().minusSeconds(31 * 60)));
        cache.put("recente", new FinaryImportService.ParsedFinaryData(List.of(), List.of(),
            Instant.now().minusSeconds(60)));

        service.cleanupExpiredCache();

        assertThat(cache).containsOnlyKeys("recente");
    }

    // ═════════════ EXTRAS (fora da contagem de CC) ═════════════

    // ── Defeito 1 / Issue #1: Colisão de contas com mesmo nome e categoria ──
    @Disabled("Defeito conhecido: ver Issue #1 no GitHub. Contas homônimas na mesma categoria colidem gerando mesmo finaryId e descarte da 2ª conta.")
    @Test
    void extra_preview_twoAccountsWithSameNameAndCategoryMustGetDistinctIds() {
        // Dois "Livret A" em bancos diferentes, na mesma aba. O id é só categoria + nome,
        // e o import junta as chaves repetidas (toMap com (a, b) -> a): a segunda conta
        // não consegue ser mapeada separadamente e herda o saldo da primeira.
        byte[] file = xlsx(wb -> {
            Sheet s = accountSheet(wb, "Savings");
            put(s, 1, "Livret A", "BNP", 100.0, "EUR");
            put(s, 2, "Livret A", "LCL", 200.0, "EUR");
        });

        FinaryPreviewResponse preview = service.preview(multipart(file), MEMBER_ID);

        List<String> ids = preview.accounts().stream().map(FinaryAccountPreview::finaryId).toList();
        assertThat(ids).hasSize(2).doesNotHaveDuplicates();

        // com ids distintos, cada conta deve ser importada com o próprio saldo
        FinaryImportRequest req = new FinaryImportRequest(List.of(
            create(ids.get(0), "Livret A", "Savings", details(AccountType.SAVINGS, null)),
            create(ids.get(1), "Livret A", "Savings", details(AccountType.SAVINGS, null))
        ), preview.fileToken());
        service.executeImport(req, MEMBER_ID);

        ArgumentCaptor<Account> saved = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(2)).save(saved.capture());
        assertThat(saved.getAllValues()).extracting(a -> a.getCurrentBalance().doubleValue())
            .containsExactly(100.0, 200.0);
    }

    // ── Defeito 2 / Issue #2: Descarte silencioso de transações com formato de data não-ISO ──
    @Disabled("Defeito conhecido: ver Issue #2 no GitHub. Transações com formato de data não-ISO (ex: 31/12/2024) são descartadas silenciosamente sem avisar o usuário via warnings.")
    @Test
    void extra_preview_unparseableDateShouldNotBeSilentlyDiscarded() {
        // Transação com formato comum francês/brasileiro (dd/MM/yyyy)
        byte[] file = xlsx(wb -> {
            Sheet s = transactionSheet(wb);
            put(s, 1, "Checkings", "31/12/2024", "Salario", 2500.0, "Income", "Conta", "Banco", "EUR");
        });

        FinaryPreviewResponse preview = service.preview(multipart(file), MEMBER_ID);

        // Comportamento esperado: a transação com data '31/12/2024' deve ser aceita e contabilizada.
        // Comportamento atual: parseXlsx apenas loga em WARN e descarta a transação, resultando em totalTransactionCount == 0.
        assertThat(preview.totalTransactionCount())
            .as("Deveria contabilizar a transação com formato de data '31/12/2024'")
            .isEqualTo(1);
    }

    // ── Defeito 3 / Issue #3: Células do tipo FORMULA caem no switch default e retornam null ──
    @Disabled("Defeito conhecido: ver Issue #3 no GitHub. Células do tipo FORMULA caem no switch default e retornam null em cellString.")
    @Test
    void extra_cellString_formulaCellShouldEvaluateCachedStringResult() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellFormula("\"Salario\"");
        wb.getCreationHelper().createFormulaEvaluator().evaluateFormulaCell(cell);

        // Invocação de cellString via reflexão helper call()
        String result = call("cellString", row, 0);

        // Comportamento esperado: retornar o valor texto calculado pela fórmula ("Salario").
        // Comportamento atual: retorna null porque cell.getCellType() é CellType.FORMULA, caindo no default.
        assertThat(result).isEqualTo("Salario");
    }

    @Test
    void extra_executeImport_throwsNotFoundWhenTheMemberDoesNotExist() {
        String token = previewToken(singleCheckingAccountFile());
        when(familyMemberRepository.findById(MEMBER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.executeImport(new FinaryImportRequest(List.of(), token), MEMBER_ID))
            .isInstanceOf(ResourceNotFoundException.class);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void extra_executeImport_mapExistingToAnAccountOfAnotherMemberThrowsAndCreatesNothing() {
        String token = previewToken(singleCheckingAccountFile());
        when(accountRepository.findByIdAndMemberId(99L, MEMBER_ID)).thenReturn(Optional.empty());
        FinaryAccountMapping mapping = new FinaryAccountMapping(
            CHECKING_ID, "Conta Corrente", "Checkings", FinaryMappingAction.MAP_EXISTING, 99L, null);

        assertThatThrownBy(() -> service.executeImport(new FinaryImportRequest(List.of(mapping), token), MEMBER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Account 99 not found");
        verify(accountRepository, never()).save(any());
        verify(persistenceHelper, never()).importTransactions(any(), any(), any());
    }

    @Test
    void extra_preview_eachCallGetsItsOwnToken() {
        byte[] file = singleCheckingAccountFile();

        String first = previewToken(file);
        String second = previewToken(file);

        assertThat(first).isNotBlank();
        assertThat(second).isNotBlank();
        assertThat(first).isNotEqualTo(second);
    }
}
