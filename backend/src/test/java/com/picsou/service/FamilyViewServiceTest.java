package com.picsou.service;

/*
 * ============================================================================
 *  COS482 - Qualidade de Software | Trabalho 1 
 *  Testes Unitários 
 * ----------------------------------------------------------------------------
 *  Autora:            Carolina Falcão
 *  Classe sob teste:  backend/src/main/java/com/picsou/service/FamilyViewService.java
 * ----------------------------------------------------------------------------
 *  Complexidade Ciclomática 
 *
 *  Método                                              P    V(G)
 *  --------------------------------------------------  ---  ----
 *  FamilyViewService(...)  (construtor)                 0     1
 *  getFamilyDashboard(Long)                            13    14
 *  getGoalContributions(Long, Long, List)               4     5
 *  isGoalVisibleTo(Goal, Long)  (privado)               3     4
 *  --------------------------------------------------  ---  ----
 *  Total da classe                                           24
 * ============================================================================
 */

import com.picsou.dto.ContributionBreakdownResponse;
import com.picsou.dto.FamilyDashboardResponse;
import com.picsou.dto.FamilyDashboardResponse.ContributionInfo;
import com.picsou.dto.FamilyDashboardResponse.SharedAccountInfo;
import com.picsou.dto.FamilyDashboardResponse.SharedGoalInfo;
import com.picsou.exception.ResourceNotFoundException;
import com.picsou.model.Account;
import com.picsou.model.AccountType;
import com.picsou.model.FamilyMember;
import com.picsou.model.Goal;
import com.picsou.model.GoalManualContribution;
import com.picsou.model.SharedResource;
import com.picsou.model.SharingLevel;
import com.picsou.model.SharingSettings;
import com.picsou.repository.AccountRepository;
import com.picsou.repository.FamilyMemberRepository;
import com.picsou.repository.GoalManualContributionRepository;
import com.picsou.repository.GoalRepository;
import com.picsou.repository.SharedResourceRepository;
import com.picsou.repository.SharingSettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyViewServiceTest {

    private static final Long VIEWER_ID = 1L;
    private static final Long ANA_ID = 2L;
    private static final Long BRUNO_ID = 3L;
    private static final Long META_ID = 20L;

    private final FamilyMember viewer = membro(VIEWER_ID, "Carolina"); // quem esta vendo o painel
    private final FamilyMember ana = membro(ANA_ID, "Ana");
    private final FamilyMember bruno = membro(BRUNO_ID, "Bruno");

    // stubs das dependencias (banco e serviços)
    @Mock FamilyMemberRepository memberRepository;
    @Mock SharingSettingsRepository sharingSettingsRepository;
    @Mock SharedResourceRepository sharedResourceRepository;
    @Mock AccountRepository accountRepository;
    @Mock GoalRepository goalRepository;
    @Mock AccountService accountService;
    @Mock GoalManualContributionRepository contributionRepository;
    @Mock AccountAccessResolver accessResolver;

    @InjectMocks FamilyViewService service; // recebe os mocks pelo construtor

    //  getFamilyDashboard

    // T01 - sem membros na família -> painel vazio e patrimonio zero
    @Test
    void dashboard_semMembros_retornaVazioETotalZero() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of());

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertTrue(painel.sharedAccounts().isEmpty());   // nenhuma conta
        assertTrue(painel.sharedGoals().isEmpty());      // nenhuma meta
        assertValor("0", painel.totalSharedNetWorth());  // patrimonio = 0
        verifyNoInteractions(sharingSettingsRepository); // nem consultou compartilhamento
    }

    // T02 - so existe o proprio visualizador -> ele é ignorado
    @Test
    void dashboard_apenasVisualizador_ignoraOProprioMembro() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer));

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertTrue(painel.sharedAccounts().isEmpty());
        assertTrue(painel.sharedGoals().isEmpty());
        verifyNoInteractions(sharingSettingsRepository); // pulou o visualizador
    }

    // T03 - membro sem configuração de compartilhamento -> nada aparece
    @Test
    void dashboard_membroSemConfiguracao_naoCompartilhaNada() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, null, null);

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertTrue(painel.sharedAccounts().isEmpty());
        assertTrue(painel.sharedGoals().isEmpty());
        verifyNoInteractions(accountRepository, goalRepository); // não buscou contas nem metas
    }

    // T04 - contas com nivel none -> contas não são buscadas
    @Test
    void dashboard_contasComNivelNone_naoBuscaContas() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.NONE, null);

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertTrue(painel.sharedAccounts().isEmpty());
        verifyNoInteractions(accountRepository); // não buscou contas
    }

    // T05 - nivel all mas o membro não tem contas -> patrimonio zero
    @Test
    void dashboard_contasNivelAllListaVazia_totalZero() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.ALL, null);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of());

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertTrue(painel.sharedAccounts().isEmpty());
        assertValor("0", painel.totalSharedNetWorth()); // patrimonio = 0
    }

    // T06 - nivel all -> retorna todas as contas do membro
    @Test
    void dashboard_contasNivelAll_retornaTodasAsContas() {
        Account corrente = conta(10L, AccountType.CHECKING);
        Account poupanca = conta(11L, AccountType.SAVINGS);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.ALL, null);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(corrente, poupanca));
        when(accessResolver.sharesFor(any(), eq(ANA_ID))).thenReturn(Map.of(10L, cota("100"), 11L, cota("100")));
        when(accountService.signedLiveBalanceEur(corrente)).thenReturn(new BigDecimal("1000"));
        when(accountService.signedLiveBalanceEur(poupanca)).thenReturn(new BigDecimal("500"));

        List<SharedAccountInfo> contas = service.getFamilyDashboard(VIEWER_ID).sharedAccounts();

        assertEquals(2, contas.size());                       // as 2 contas
        assertEquals("Ana", contas.get(0).ownerName());       // dona correta
        assertEquals("CHECKING", contas.get(0).type());       // tipo correto
        assertValor("1000", contas.get(0).balanceEur());      // saldo da 1
        assertValor("500", contas.get(1).balanceEur());       // saldo da 2
        verify(sharedResourceRepository, never()).findAllByOwnerMemberIdAndResourceType(any(), any()); // não usou lista manual
    }

    // T07 - nivel manual -> retorna so as contas marcadas como compartilhadas
    @Test
    void dashboard_contasNivelManual_retornaSoAsCompartilhadas() {
        Account compartilhada = conta(10L, AccountType.CHECKING);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.MANUAL, null);
        when(sharedResourceRepository.findAllByOwnerMemberIdAndResourceType(ANA_ID, "ACCOUNT"))
            .thenReturn(List.of(recursoCompartilhado(10L)));
        when(accountRepository.findByIdInAndMemberId(List.of(10L), ANA_ID)).thenReturn(List.of(compartilhada));

        List<SharedAccountInfo> contas = service.getFamilyDashboard(VIEWER_ID).sharedAccounts();

        assertEquals(1, contas.size());           // só 1 conta
        assertEquals(10L, contas.get(0).id());    // a compartilhada
        verify(accountRepository, never()).findAllByMemberIdOrderByCreatedAtAsc(any()); // não buscou todas
    }

    // T08 - conta com cota de 50% -> conta so metade do saldo
    @Test
    void dashboard_contaComCotaDe50_pesaMetadeDoSaldo() {
        Account imovel = conta(10L, AccountType.REAL_ESTATE);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.ALL, null);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(imovel));
        when(accessResolver.sharesFor(any(), eq(ANA_ID))).thenReturn(Map.of(10L, cota("50")));
        when(accountService.signedLiveBalanceEur(imovel)).thenReturn(new BigDecimal("1000"));

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertValor("500", painel.sharedAccounts().get(0).balanceEur()); // 50% de 1000
    }

    // T09 - conta sem cota definida -> saldo vira zero
    @Test
    void dashboard_contaSemCotaNoMapa_saldoZero() {
        Account corrente = conta(10L, AccountType.CHECKING);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.ALL, null);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(corrente));
        when(accessResolver.sharesFor(any(), eq(ANA_ID))).thenReturn(Map.of()); // sem cota
        when(accountService.signedLiveBalanceEur(corrente)).thenReturn(new BigDecimal("1000"));

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertValor("0", painel.sharedAccounts().get(0).balanceEur()); // saldo = 0
    }

    // T10 - loan diminui o patrimonio
    @Test
    void dashboard_contaLoan_reduzPatrimonio() {
        Account corrente = conta(10L, AccountType.CHECKING);
        Account emprestimo = conta(11L, AccountType.LOAN);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.ALL, null);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(corrente, emprestimo));
        when(accessResolver.sharesFor(any(), eq(ANA_ID))).thenReturn(Map.of(10L, cota("100"), 11L, cota("100")));
        when(accountService.signedLiveBalanceEur(corrente)).thenReturn(new BigDecimal("1000"));
        when(accountService.signedLiveBalanceEur(emprestimo)).thenReturn(new BigDecimal("-300"));

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertValor("700", painel.totalSharedNetWorth()); // 1000 - 300
    }

    // T11 - metas com nivel none -> metas não são buscadas
    @Test
    void dashboard_metasComNivelNone_naoBuscaMetas() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, null, SharingLevel.NONE);

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertTrue(painel.sharedGoals().isEmpty());
        verifyNoInteractions(goalRepository); // não buscou metas
    }

    // T12 - metas com nivel all mas o membro não tem metas
    @Test
    void dashboard_metasNivelAllListaVazia_semMetas() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, null, SharingLevel.ALL);
        when(goalRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of());

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertTrue(painel.sharedGoals().isEmpty());
        verifyNoInteractions(contributionRepository); // não buscou aportes
    }

    // T13 - meta all -> soma o saldo das contas da meta e lista os aportes
    @Test
    void dashboard_metaNivelAll_somaSaldoDasContasEAportes() {
        Account corrente = conta(10L, AccountType.CHECKING);
        Account poupanca = conta(11L, AccountType.SAVINGS);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, null, SharingLevel.ALL);
        when(goalRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID))
            .thenReturn(List.of(meta(META_ID, ana, corrente, poupanca)));
        when(accessResolver.sharesFor(any(), eq(ANA_ID))).thenReturn(Map.of(10L, cota("100"), 11L, cota("100")));
        when(accountService.signedLiveBalanceEur(corrente)).thenReturn(new BigDecimal("1000"));
        when(accountService.signedLiveBalanceEur(poupanca)).thenReturn(new BigDecimal("500"));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, VIEWER_ID)).thenReturn(List.of());
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, ANA_ID)).thenReturn(List.of(aporte("200")));

        SharedGoalInfo meta = service.getFamilyDashboard(VIEWER_ID).sharedGoals().get(0);

        assertEquals("Ana", meta.ownerName());                           // dona correta
        assertValor("1500", meta.currentTotal());                        // 1000 + 500
        assertEquals(1, meta.contributions().size());                    // só quem aportou
        assertEquals("Ana", meta.contributions().get(0).memberName());
        assertValor("200", meta.contributions().get(0).amount());
    }

    // T14 - metas com nivel manual -> busca só as metas compartilhadas
    @Test
    void dashboard_metaNivelManual_buscaPorIds() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, null, SharingLevel.MANUAL);
        when(sharedResourceRepository.findAllByOwnerMemberIdAndResourceType(ANA_ID, "GOAL"))
            .thenReturn(List.of(recursoCompartilhado(META_ID)));
        when(goalRepository.findByIdInAndMemberId(List.of(META_ID), ANA_ID)).thenReturn(List.of(meta(META_ID, ana)));

        List<SharedGoalInfo> metas = service.getFamilyDashboard(VIEWER_ID).sharedGoals();

        assertEquals(1, metas.size());          // só 1 meta
        assertEquals(META_ID, metas.get(0).id()); // a compartilhada
        verify(goalRepository, never()).findAllByMemberIdOrderByCreatedAtAsc(any()); // não buscou todas
    }

    // T15 - aportes no limite (-0.01, 0.00, 0.01) -> só o positivo aparece
    @Test
    void dashboard_aporteNoLimite_soPositivoEntra() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana, bruno));
        compartilhamento(ANA_ID, null, SharingLevel.ALL);
        compartilhamento(BRUNO_ID, null, null);
        when(goalRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(meta(META_ID, ana)));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, VIEWER_ID)).thenReturn(List.of(aporte("-0.01")));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, ANA_ID)).thenReturn(List.of(aporte("0.00")));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, BRUNO_ID)).thenReturn(List.of(aporte("0.01")));

        List<ContributionInfo> aportes = service.getFamilyDashboard(VIEWER_ID).sharedGoals().get(0).contributions();

        assertEquals(1, aportes.size());                     // só 1 entrou
        assertEquals("Bruno", aportes.get(0).memberName());  // o de 0.01
    }

    // T16 - meta sem contas vinculadas -> total atual zero
    @Test
    void dashboard_metaSemContas_currentTotalZero() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, null, SharingLevel.ALL);
        when(goalRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(meta(META_ID, ana)));

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertValor("0", painel.sharedGoals().get(0).currentTotal()); // total = 0
    }

    // T17 - varios membros -> mantem a ordem e ignora o visualizador
    @Test
    void dashboard_variosMembros_respeitaOrdemEIgnoraVisualizador() {
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana, bruno));
        compartilhamento(ANA_ID, SharingLevel.ALL, null);
        compartilhamento(BRUNO_ID, SharingLevel.ALL, null);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID))
            .thenReturn(List.of(conta(10L, AccountType.CHECKING)));
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(BRUNO_ID))
            .thenReturn(List.of(conta(30L, AccountType.CHECKING)));

        List<String> donos = service.getFamilyDashboard(VIEWER_ID).sharedAccounts().stream()
            .map(SharedAccountInfo::ownerName).toList();

        assertEquals(List.of("Ana", "Bruno"), donos); // ordem certa, sem "Carolina"
    }

    // T18 - metas não entram no patrimonio total
    @Test
    void dashboard_metasNaoEntramNoPatrimonio() {
        Account corrente = conta(10L, AccountType.CHECKING);
        Account poupanca = conta(11L, AccountType.SAVINGS);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.ALL, SharingLevel.ALL);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(corrente));
        when(goalRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(meta(META_ID, ana, poupanca)));
        when(accessResolver.sharesFor(any(), eq(ANA_ID))).thenReturn(Map.of(10L, cota("100"), 11L, cota("100")));
        when(accountService.signedLiveBalanceEur(corrente)).thenReturn(new BigDecimal("100"));
        when(accountService.signedLiveBalanceEur(poupanca)).thenReturn(new BigDecimal("500"));

        FamilyDashboardResponse painel = service.getFamilyDashboard(VIEWER_ID);

        assertValor("100", painel.totalSharedNetWorth()); // só a conta, sem os 500 da meta
    }

    // T19 - cotas buscadas 1 vez para contas e 1 vez para metas (não por conta)
    @Test
    void dashboard_sharesForChamadoUmaVezPorMembro() {
        Account c1 = conta(10L, AccountType.CHECKING);
        Account c2 = conta(11L, AccountType.SAVINGS);
        Account c3 = conta(12L, AccountType.PEA);
        when(memberRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(viewer, ana));
        compartilhamento(ANA_ID, SharingLevel.ALL, SharingLevel.ALL);
        when(accountRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID)).thenReturn(List.of(c1, c2, c3));
        when(goalRepository.findAllByMemberIdOrderByCreatedAtAsc(ANA_ID))
            .thenReturn(List.of(meta(20L, ana, c1, c2), meta(21L, ana, c2, c3)));

        service.getFamilyDashboard(VIEWER_ID);

        verify(accessResolver, times(2)).sharesFor(any(), eq(ANA_ID)); // 2 chamadas no total
    }

    // getGoalContributions

    // T20 - meta que não existe -> ResourceNotFoundException
    @Test
    void contribuicoes_metaInexistente_lancaResourceNotFound() {
        when(goalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> service.getGoalContributions(99L, VIEWER_ID, List.of(viewer)));
    }

    // T21 - visualizador é o dono -> nao checa compartilhamento
    @Test
    void contribuicoes_visualizadorEhDono_naoConsultaCompartilhamento() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, ANA_ID)).thenReturn(List.of(aporte("100")));

        List<ContributionBreakdownResponse> aportes = service.getGoalContributions(META_ID, ANA_ID, List.of(ana));

        assertEquals(List.of(contribuicao("Ana", "100")), aportes);
        verifyNoInteractions(sharingSettingsRepository); // não checou permissão
    }

    // T22 - não é o dono e não há configuração -> acesso negado
    @Test
    void contribuicoes_naoDonoSemConfiguracao_lancaAccessDenied() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(sharingSettingsRepository.findByMemberIdAndResourceType(ANA_ID, "GOAL")).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
            () -> service.getGoalContributions(META_ID, VIEWER_ID, List.of(viewer, ana)));
        verifyNoInteractions(contributionRepository); // parou antes de buscar aportes
    }

    // T23 - não é o dono e o nivel é none -> acesso negado
    @Test
    void contribuicoes_naoDonoNivelNone_lancaAccessDenied() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(sharingSettingsRepository.findByMemberIdAndResourceType(ANA_ID, "GOAL"))
            .thenReturn(Optional.of(config(SharingLevel.NONE)));

        assertThrows(AccessDeniedException.class,
            () -> service.getGoalContributions(META_ID, VIEWER_ID, List.of(viewer, ana)));
    }

    // T24 - não é o dono e o nivel é all -> acesso liberado
    @Test
    void contribuicoes_naoDonoNivelAll_retornaLista() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(sharingSettingsRepository.findByMemberIdAndResourceType(ANA_ID, "GOAL"))
            .thenReturn(Optional.of(config(SharingLevel.ALL)));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, ANA_ID)).thenReturn(List.of(aporte("100")));

        List<ContributionBreakdownResponse> aportes = service.getGoalContributions(META_ID, VIEWER_ID, List.of(ana));

        assertEquals(List.of(contribuicao("Ana", "100")), aportes);
        verifyNoInteractions(sharedResourceRepository); // ALL não precisa da lista manual
    }

    // T25 - nivel manual e a meta está compartilhada -> acesso liberado
    @Test
    void contribuicoes_naoDonoManualCompartilhada_retornaLista() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(sharingSettingsRepository.findByMemberIdAndResourceType(ANA_ID, "GOAL"))
            .thenReturn(Optional.of(config(SharingLevel.MANUAL)));
        when(sharedResourceRepository.existsByOwnerMemberIdAndResourceTypeAndResourceId(ANA_ID, "GOAL", META_ID))
            .thenReturn(true);
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, ANA_ID)).thenReturn(List.of(aporte("100")));

        List<ContributionBreakdownResponse> aportes = service.getGoalContributions(META_ID, VIEWER_ID, List.of(ana));

        assertEquals(List.of(contribuicao("Ana", "100")), aportes);
    }

    // T26 - nivel manual e a meta não está compartilhada -> acesso negado
    @Test
    void contribuicoes_naoDonoManualNaoCompartilhada_lancaAccessDenied() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(sharingSettingsRepository.findByMemberIdAndResourceType(ANA_ID, "GOAL"))
            .thenReturn(Optional.of(config(SharingLevel.MANUAL)));
        when(sharedResourceRepository.existsByOwnerMemberIdAndResourceTypeAndResourceId(ANA_ID, "GOAL", META_ID))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class,
            () -> service.getGoalContributions(META_ID, VIEWER_ID, List.of(viewer, ana)));
    }

    // T27 - lista de membros vazia -> retorna lista vazia
    @Test
    void contribuicoes_listaDeMembrosVazia_retornaVazio() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));

        List<ContributionBreakdownResponse> aportes = service.getGoalContributions(META_ID, ANA_ID, List.of());

        assertTrue(aportes.isEmpty()); // nenhum aporte
    }

    // T28 - somas no limite (-0.01, 0.00, 0.01) -> só positivo aparece
    @Test
    void contribuicoes_somaNoLimite_soPositivoEntra() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, VIEWER_ID)).thenReturn(List.of(aporte("-0.01")));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, ANA_ID)).thenReturn(List.of(aporte("0.00")));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, BRUNO_ID)).thenReturn(List.of(aporte("0.01")));

        List<ContributionBreakdownResponse> aportes =
            service.getGoalContributions(META_ID, ANA_ID, List.of(viewer, ana, bruno));

        assertEquals(List.of(contribuicao("Bruno", "0.01")), aportes); // só o de 0.01
    }

    // T29 - vários aportes do mesmo membro são somados
    @Test
    void contribuicoes_variosAportes_somaPorMembro() {
        when(goalRepository.findById(META_ID)).thenReturn(Optional.of(meta(META_ID, ana)));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, ANA_ID))
            .thenReturn(List.of(aporte("100"), aporte("50")));
        when(contributionRepository.findByGoalIdAndMemberId(META_ID, BRUNO_ID)).thenReturn(List.of(aporte("30")));

        List<ContributionBreakdownResponse> aportes =
            service.getGoalContributions(META_ID, ANA_ID, List.of(ana, bruno));

        assertEquals(List.of(contribuicao("Ana", "150"), contribuicao("Bruno", "30")), aportes); // 100 + 50
    }

    // auxiliares - construção de objetos que sao repetidamente utilizados nos testes 

    // define o nivel de compartilhamento de contas e metas (null = sem configuração)
    private void compartilhamento(Long memberId, SharingLevel contas, SharingLevel metas) {
        when(sharingSettingsRepository.findByMemberIdAndResourceType(memberId, "ACCOUNT"))
            .thenReturn(Optional.ofNullable(contas).map(FamilyViewServiceTest::config));
        when(sharingSettingsRepository.findByMemberIdAndResourceType(memberId, "GOAL"))
            .thenReturn(Optional.ofNullable(metas).map(FamilyViewServiceTest::config));
    }

    // compara BigDecimal pelo valor (500 == 500.00)
    private static void assertValor(String esperado, BigDecimal obtido) {
        assertEquals(new BigDecimal(esperado).stripTrailingZeros(), obtido.stripTrailingZeros());
    }

    private static FamilyMember membro(Long id, String nome) {
        return FamilyMember.builder().id(id).displayName(nome).build();
    }

    private static Account conta(Long id, AccountType tipo) {
        return Account.builder().id(id).name("Conta " + id).type(tipo).currency("EUR").build();
    }

    private static Goal meta(Long id, FamilyMember dono, Account... contas) {
        return Goal.builder().id(id).member(dono).name("Meta " + id)
            .targetAmount(new BigDecimal("5000")).accounts(new ArrayList<>(List.of(contas))).build();
    }

    private static SharingSettings config(SharingLevel nivel) {
        return SharingSettings.builder().sharingLevel(nivel).build();
    }

    private static SharedResource recursoCompartilhado(Long resourceId) {
        return SharedResource.builder().resourceId(resourceId).build();
    }

    private static GoalManualContribution aporte(String valor) {
        GoalManualContribution aporte = new GoalManualContribution();
        aporte.setAmount(new BigDecimal(valor));
        return aporte;
    }

    private static ContributionBreakdownResponse contribuicao(String nome, String valor) {
        return new ContributionBreakdownResponse(nome, new BigDecimal(valor));
    }

    private static BigDecimal cota(String percentual) {
        return new BigDecimal(percentual);
    }
}
