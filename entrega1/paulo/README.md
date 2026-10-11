# COS482 — Entrega 1 — Paulo

Responsável: Paulo Cesar Vaz. Funcionalidade: cálculo e visualização da amortização de empréstimos. Classe: `LoanAmortizationService`. Branch: `feature/paulo-entrega1-amortizacao`. Código-base: `6b020c76cf093b8995a68df12a32e8c5b21e9144`.

## Artefatos

- [Mensagem pronta para o grupo](mensagem-para-o-grupo.md)
- [Contribuição ao Plano de Teste IEEE 829](plano-de-teste.md)
- [Documento de contribuição no Google Docs](https://docs.google.com/document/d/1244Hgfv4YOyM4nBO8PSXadMBxtFPmg2J8so5xoWCy_o/edit?usp=drivesdk)
- [Justificativa e complexidade](complexidade.md)
- [Matriz de rastreabilidade](matriz-de-testes.md)
- [Casos manuais](casos-manuais.md) e [XML para importação no TestLink](testlink/casos-paulo.xml)
- [Guia do TestLink e registro de execução](testlink/README.md)
- [Relatório de execução e limitações](resultados.md)
- [Registro consolidado dos problemas e publicação pendente](issues/README.md)
- [BUG-PCV-01 — arredondamento](issues/BUG-PCV-01.md)
- [BUG-PCV-02 — parcela automática com seguro](issues/BUG-PCV-02.md)
- [Checklist de integração e entrega](integracao.md)
- [Roteiro para apresentação individual](apresentacao.md)
- [Testes ampliados](../../backend/src/test/java/com/picsou/service/LoanAmortizationServiceTest.java)
- [Regressões dos defeitos, ativadas explicitamente](../../backend/src/test/java/com/picsou/service/LoanAmortizationServiceDefectTest.java)
- [Evidências de complexidade](evidencias/complexidade/resultado.json)
- [Evidências originais da classe](evidencias/unitarios/baseline/), [suíte original](evidencias/unitarios/baseline-suite/), [ampliadas](evidencias/unitarios/ampliados/), [defeitos](evidencias/unitarios/defeitos/), [suíte completa](evidencias/unitarios/suite-completa/) e [diagnóstico IPv4](evidencias/unitarios/suite-ipv4/)
- [Scripts de reprodução](scripts/)
- [Ambiente local operacional](ambiente-local.md)
- [Simulação HTTP aprovada](evidencias/simulacao-api/resultado.json)
- [Especificação exportada do TestLink](evidencias/testlink/especificacao-exportada.xml)
- [Capturas por teste e passo](Tests_ScreenShots/README.md) e [registro estruturado](Tests_ScreenShots/execucao.json)
- [Resultados conferidos no TestLink](evidencias/testlink/execucoes-verificadas.json)

## Situação

9 testes originais aprovados; classe ampliada com 22 execuções adicionais, totalizando 31 aprovadas. Dois defeitos reproduzidos em testes separados, opt-in, com duas falhas esperadas. Suíte completa: 1271 casos, 20 erros de infraestrutura/conexão local, 35 ignorados, nenhuma falha de asserção na execução padrão.

Frontend e backend compilados do código local com modo demo desativado. Os 20 erros da suíte ampla também foram reproduzidos no código original; as regressões opt-in explicam os dois ignorados adicionais. A conta de Paulo não tem permissão de push no fork.

Os três casos manuais foram cadastrados no TestLink como dd-5/6/7, na suíte Paulo - Amortização de empréstimos. Os três casos foram executados pela interface, com assistência do Codex solicitada por Paulo: 15 passos aprovados e 30 capturas originais. Os resultados Passed estão registrados no plano 46, build 5. O Plano de Teste é uma contribuição individual, pendente de consolidação pelo grupo e comparação com o template da aula. O material local precisa ser integrado à `main`; esta branch ainda não constitui a entrega final.
