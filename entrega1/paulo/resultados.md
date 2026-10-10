# Resultados da execução — 10/10/2026

Autoria dos testes acrescentados e responsabilidade acadêmica: **Paulo Cesar Vaz**. Execução técnica assistida no workspace de Paulo. Base original `6b020c76cf093b8995a68df12a32e8c5b21e9144`; branch `feature/paulo-entrega1-amortizacao`. Os resultados abaixo vieram de execuções reais, não de estimativas. O código de produção não foi alterado.

## Testes unitários

| Execução | Casos | Falhas de asserção | Erros | Ignorados | Resultado |
|---|---:|---:|---:|---:|---|
| Classe original antes da alteração | 9 | 0 | 0 | 0 | Aprovada |
| Classe ampliada | 31 | 0 | 0 | 0 | Aprovada; 22 execuções próprias adicionais |
| Reproduções de defeitos ativadas explicitamente | 2 | 2 | 0 | 0 | Dois defeitos confirmados; código de saída 1 esperado |
| Suíte completa do código original em cópia isolada | 1247 | 0 | 20 | 33 | Não aprovada globalmente; limitações já presentes antes desta contribuição |
| Suíte completa com a contribuição | 1271 | 0 | 20 | 35 | Mesmos 20 erros; dois ignorados adicionais são as regressões opt-in |
| Diagnóstico da suíte com preferIPv4Stack=true | 1271 | 0 | 20 | 35 | Mesma limitação; não resolveu |

Original: 1194 aprovados, 20 erros, 33 ignorados. Com contribuição: 1216 aprovados, 20 erros, 35 ignorados. Diferença: 22 novos casos aprovados e 2 reproduções opt-in não executadas no modo padrão.

Evidências:

- [Baseline da classe](evidencias/unitarios/baseline/maven-output.txt) e [XML](evidencias/unitarios/baseline/TEST-com.picsou.service.LoanAmortizationServiceTest.xml).
- [Classe ampliada](evidencias/unitarios/ampliados/maven-output.txt) e [XML](evidencias/unitarios/ampliados/TEST-com.picsou.service.LoanAmortizationServiceTest.xml).
- [Defeitos](evidencias/unitarios/defeitos/maven-output.txt) e [XML](evidencias/unitarios/defeitos/TEST-com.picsou.service.LoanAmortizationServiceDefectTest.xml).
- [Suíte original](evidencias/unitarios/baseline-suite/maven-output.txt), [resumo](evidencias/unitarios/baseline-suite/resumo.json) e [relatórios completos ZIP](evidencias/unitarios/baseline-suite/surefire-reports.zip).
- [Suíte com contribuição](evidencias/unitarios/suite-completa/maven-output.txt), [resumo](evidencias/unitarios/suite-completa/resumo.json) e [relatórios completos ZIP](evidencias/unitarios/suite-completa/surefire-reports.zip).
- [Diagnóstico IPv4](evidencias/unitarios/suite-ipv4/maven-output.txt), [resumo](evidencias/unitarios/suite-ipv4/resumo.json) e [relatórios ZIP](evidencias/unitarios/suite-ipv4/surefire-reports.zip).

### Limitações da suíte ampla

Os 20 erros ocorrem nas mesmas quatro classes originais: `MeriaAdapterWiringTest` (1), `TradeRepublicAdapterTest` (10), `ValuationAdapterWiringTest` (1) e `IbkrFlexClientTest` (8). As mensagens incluem `Unable to establish loopback connection`, `failed to create a child event loop` e `SocketException: Invalid argument: connect`. A reprodução no código original confirma que esta contribuição não introduziu esses erros. A causa final da configuração de rede do Windows não foi corrigida nem determinada completamente.

33 casos originais são ignorados por disponibilidade de infraestrutura, principalmente Docker/PostgreSQL real; consultar os motivos individuais no resumo/ZIP. Os 2 adicionais ignorados na suíte padrão são `LoanAmortizationServiceDefectTest`, exigindo `-Dpicsou.entrega1.defect-regressions=true`. Não são testes aprovados e não foram silenciosamente removidos.

A baseline completa foi extraída do commit original com `git archive`, sem sobrescrever os testes da branch. Uma tentativa inicial arquivou somente `backend`, causando um skip extra no teste que lê `docker/entrypoint.sh`; essa tentativa foi substituída pela cópia completa do repositório e não é usada na comparação final.

## Defeitos confirmados

**BUG-PCV-01:** na parcela automática, 1000 em três meses resulta em capitais exibidos que somam 999,99; esperado 1000,00. **BUG-PCV-02:** parcela automática para principal 1200, 12 meses, seguro 10 retorna 100,00; esperado total 110,00. As duas asserções foram executadas e falharam em 10/10 às 20h03, Brasília. Relatos completos em [issues](issues/).

As regressões são opt-in para distinguir a suíte normal dos relatos de defeitos, mantendo os resultados vermelhos disponíveis e reproduzíveis. Esta contribuição não corrige produção nem adapta o esperado ao resultado defeituoso.

## Compilação e versão

- Backend: `mvn -f backend/pom.xml package -DskipTests`, compilação aprovada; testes foram executados separadamente.
- Frontend: Bun 1.4.3, `bun install --frozen-lockfile`, `VITE_DEMO_MODE=false`, `VITE_APP_VERSION=qs-entrega1-6b020c7`, `bun run build`, compilação aprovada. Vite emitiu aviso de chunks maiores que 500 kB; não impediu o build.
- [Manifesto com hashes SHA-256](evidencias/build/manifesto.json), [build backend](evidencias/build/backend-build.txt), [instalação frontend](evidencias/build/frontend-install.txt), [build frontend](evidencias/build/frontend-build.txt).
- Artefatos gerados localmente: `backend/target/picsou-backend-1.0.9.jar` e `frontend/dist/`. Ficam ignorados pelo Git; os comandos e hashes permitem reproduzir/verificar. Build aprovado não comprova inicialização ou execução manual.

## Manual, TestLink e acessos

CT-PCV-01/02/03 estão projetados, **não executados**. Não há capturas de aprovação nem exportação de execução do TestLink. A instância não foi fornecida; o XML foi gerado localmente e validado como XML bem formado com três casos e 15 passos, sem importar na ferramenta.

Docker Desktop falha ao iniciar por um socket `sailor-ingest.sock` inacessível. A revisão automática bloqueou a tentativa de encerrar processos à força/remover o socket (motivo informado: bloqueado por política). O encerramento oficial e a tentativa de preservar o socket com outro nome não resolveram. Nenhum reset de fábrica foi feito.

Foi tentada uma alternativa com PostgreSQL isolado: o Windows bloqueou `initdb.exe` por Controle de Aplicativo. O PostgreSQL 18 já instalado exige credenciais não disponíveis. Nenhuma configuração de segurança foi enfraquecida e o banco existente não foi alterado. [Diagnóstico do ambiente](evidencias/ambiente/diagnostico.json).

A conta conectada de Paulo tem `pull=true` e `push=false` no fork, conforme metadados do conector GitHub. A [página de issues do fork](https://github.com/mssvivian/picsou-finance-QS_2026.2/issues) informa que somente colaboradores podem criar issues. Sem publicação de branch/PR, issues ou merge nesta etapa. Os relatos estão prontos localmente. Os patches preservam autoria para um integrante integrar.

O documento de contribuição foi criado pela conta Google conectada `paulc.vaz@gmail.com`, preenchido e conferido por leitura do conector. O compartilhamento não foi alterado. [Google Docs](https://docs.google.com/document/d/1244Hgfv4YOyM4nBO8PSXadMBxtFPmg2J8so5xoWCy_o/edit?usp=drivesdk). Não substitui a colaboração individual dos demais integrantes nem a consolidação conforme o template da aula.

## Fechamento pendente

Resolver o ambiente e executar casos; usar TestLink e exportar registros; confirmar classe e responsabilidades com o grupo; obter template; consolidar Google Docs; publicar issues; integrar à main; verificar README e submissão Moodle. Prazo externo: 11/10/2026 às 23h59, Brasília. [Checklist](integracao.md).
