# Casos manuais — Paulo

Projeto: Grupo DinDin. Suíte: **Paulo - Amortização de empréstimos**. Responsável previsto: Paulo Cesar Vaz. Tipo: Manual. Importância: High. Estado de projeto: Draft; execução ainda não registrada.

Fonte canônica: [casos.json](testlink/casos.json). Gerar Markdown e XML com `node entrega1/paulo/scripts/gerar-casos.cjs`. O XML é um arquivo de **projeto de casos**, não uma exportação de execução real do TestLink.

## CT-PCV-01 — Parcela automática sem juros

Responsável: Paulo Cesar Vaz. Classe relacionada: LoanAmortizationService. Código local: CT-PCV-01. Verificar cronograma e resumo de um empréstimo de EUR 1200 em 12 meses, com juros zero e parcela automática. Oráculo: 1200/12=100; custo total 1200; zero parcelas pagas antes do início.

### Precondições

1. Picsou do fork em execução, backend real e modo demo desativado; usuário de teste logado.
2. Registrar commit/build, navegador, sistema operacional e data de execução. Usar moeda EUR e dados fictícios.
3. Executar antes de 01/11/2026 para conferir zero parcelas pagas nos contratos futuros deste conjunto.
4. A conta Emprestimo Paulo A não deve existir antes deste caso; usar usuário de teste sem dados de terceiros.

### Passos

| # | Ação | Resultado esperado |
|---|---|---|
| 1 | Abrir Accounts, clicar Add account e escolher a opção de criação manual. | O formulário de criação manual de conta é exibido. |
| 2 | Informar nome Emprestimo Paulo A, moeda EUR e tipo Loan; preencher principal/borrowed amount 1200, juros 0%, início 01/11/2026, término 01/11/2027. Deixar parcela mensal em branco; seguro e tarifa em branco ou zero. Salvar. | A conta é criada sem erro. Parcela automática é solicitada mantendo o campo mensal vazio, sem inserir zero. |
| 3 | Abrir o detalhe da conta Emprestimo Paulo A e aguardar o carregamento do resumo de empréstimo. | Parcela mensal 100, capital mensal 100, juros 0, seguro 0. Progresso: 0 parcelas pagas e 12 restantes. Saldo atual 1200. |
| 4 | Conferir o resumo de custos e o gráfico de amortização; inspecionar o ponto final do cronograma. | Custo total 1200; capital total 1200; juros/seguro e tarifas zero. O cronograma termina com saldo zero. Datas são apresentadas no formato configurado na aplicação. |
| 5 | Recarregar a página e conferir novamente os valores; capturar resumo, progresso e gráfico. | Os mesmos parâmetros e valores permanecem. Nenhuma duplicata de Emprestimo Paulo A é criada. |

### Registro real — preencher após executar

| Campo | Valor |
|---|---|
| Testador real | Codex, execução assistida solicitada por Paulo |
| Data/hora e fuso | 2026-10-11T19:44:43.142Z; America/Sao_Paulo |
| Commit/build | 58c71b93544c27ff6f9b032dfe2c4df69fe01aa0 / Paulo - Entrega1 - 58c71b9-lf |
| Navegador/versão e SO | Codex In-app Browser (versão não exposta), Windows, idioma francês |
| ID nativo no TestLink / Plano / Build | dd-5 / Plano de Testes Manuais (46) / build 5 |
| Situação atual | Executado pela interface com assistência do Codex |
| Resultado final | Passou |

| Passo | Resultado real | Passou / Falhou / Bloqueado | Evidência |
|---|---|---|---|
| 1 | O formulário Manuel foi exibido após Ajouter un compte > Manuel. | Passou | [Capturas do passo 1](Tests_ScreenShots/dd-5-Parcela-automatica/README.md#passo-1) |
| 2 | Emprestimo Paulo A foi criada em EUR, tipo Prêt, principal e saldo informado 1200, juros 0%, datas 01/11/2026 e 01/11/2027. Mensualité, seguro e tarifa deixados vazios. | Passou | [Capturas do passo 2](Tests_ScreenShots/dd-5-Parcela-automatica/README.md#passo-2) |
| 3 | Detalhe mostrou parcela 100, capital 100, juros e seguro zero, 0 parcelas pagas, 12 restantes e saldo atual 1200. | Passou | [Capturas do passo 3](Tests_ScreenShots/dd-5-Parcela-automatica/README.md#passo-3) |
| 4 | Síntese mostrou custo e capital total 1200, juros/seguro e tarifa zero. Tooltip do último ponto (outubro/2027) mostrou saldo 0,00 EUR. | Passou | [Capturas do passo 4](Tests_ScreenShots/dd-5-Parcela-automatica/README.md#passo-4) |
| 5 | Após recarga, valores permaneceram 1200/100. A lista apresentou exatamente uma conta Emprestimo Paulo A. | Passou | [Capturas do passo 5](Tests_ScreenShots/dd-5-Parcela-automatica/README.md#passo-5) |

Issue relacionada: nenhuma falha comprovada nestes três casos. [Capturas e resultados](Tests_ScreenShots/dd-5-Parcela-automatica/README.md).

## CT-PCV-02 — Composição com seguro e tarifa

Responsável: Paulo Cesar Vaz. Classe relacionada: LoanAmortizationService. Código local: CT-PCV-02. Verificar capital, seguro e custo total de um contrato com parcela explícita. Oráculo: 110-10=100 de capital por mês; seguro total 12*10=120; custo total 1200+120+50=1370.

### Precondições

1. Picsou do fork em execução, backend real e modo demo desativado; usuário de teste logado.
2. Registrar commit/build, navegador, sistema operacional e data de execução. Usar moeda EUR e dados fictícios.
3. Executar antes de 01/11/2026 para conferir zero parcelas pagas nos contratos futuros deste conjunto.
4. A conta Emprestimo Paulo B não deve existir antes deste caso; usar usuário de teste sem dados de terceiros.

### Passos

| # | Ação | Resultado esperado |
|---|---|---|
| 1 | Abrir Add account e escolher criação manual, tipo Loan, nome Emprestimo Paulo B e moeda EUR. | O formulário apresenta os parâmetros de empréstimo. |
| 2 | Preencher principal 1200, juros 0%, parcela mensal explícita 110, seguro mensal 10, tarifa/file fees 50, início 01/11/2026 e término 01/11/2027; salvar. | O cadastro é aceito sem erro e os parâmetros informados são salvos. |
| 3 | Abrir o detalhe do empréstimo e conferir a composição mensal e o progresso. | Parcela total 110; capital 100; juros 0; seguro 10. Zero parcelas pagas, 12 restantes e saldo atual 1200. |
| 4 | Conferir o resumo de custos e o ponto final do gráfico de amortização. | Capital total 1200; juros e seguro combinados 120; tarifa 50; custo total 1370. Cronograma encerra com saldo zero. |
| 5 | Recarregar o detalhe e registrar capturas dos parâmetros e resultados. | Seguro, tarifa, parcela e custo total permanecem iguais após a recarga. |

### Registro real — preencher após executar

| Campo | Valor |
|---|---|
| Testador real | Codex, execução assistida solicitada por Paulo |
| Data/hora e fuso | 2026-10-11T19:44:43.142Z; America/Sao_Paulo |
| Commit/build | 58c71b93544c27ff6f9b032dfe2c4df69fe01aa0 / Paulo - Entrega1 - 58c71b9-lf |
| Navegador/versão e SO | Codex In-app Browser (versão não exposta), Windows, idioma francês |
| ID nativo no TestLink / Plano / Build | dd-6 / Plano de Testes Manuais (46) / build 5 |
| Situação atual | Executado pela interface com assistência do Codex |
| Resultado final | Passou |

| Passo | Resultado real | Passou / Falhou / Bloqueado | Evidência |
|---|---|---|---|
| 1 | Formulário Manuel aberto com nome Emprestimo Paulo B, tipo Prêt e EUR. | Passou | [Capturas do passo 1](Tests_ScreenShots/dd-6-Seguro-e-tarifa/README.md#passo-1) |
| 2 | Cadastro aceitou principal e saldo 1200, juros 0%, parcela 110, seguro 10, tarifa 50 e datas 01/11/2026 a 01/11/2027. | Passou | [Capturas do passo 2](Tests_ScreenShots/dd-6-Seguro-e-tarifa/README.md#passo-2) |
| 3 | Detalhe mostrou parcela 110, capital 100, juros zero, seguro 10, 0 parcelas pagas, 12 restantes e saldo 1200. | Passou | [Capturas do passo 3](Tests_ScreenShots/dd-6-Seguro-e-tarifa/README.md#passo-3) |
| 4 | Síntese mostrou capital 1200, juros/seguro 120, tarifa 50 e custo total 1370. Tooltip final mostrou saldo 0,00 EUR. | Passou | [Capturas do passo 4](Tests_ScreenShots/dd-6-Seguro-e-tarifa/README.md#passo-4) |
| 5 | Após recarregar, parcela 110, seguro 10, tarifa 50 e custo 1370 permaneceram. | Passou | [Capturas do passo 5](Tests_ScreenShots/dd-6-Seguro-e-tarifa/README.md#passo-5) |

Issue relacionada: nenhuma falha comprovada nestes três casos. [Capturas e resultados](Tests_ScreenShots/dd-6-Seguro-e-tarifa/README.md).

## CT-PCV-03 — Recálculo após edição do principal

Responsável: Paulo Cesar Vaz. Classe relacionada: LoanAmortizationService. Código local: CT-PCV-03. Verificar que alterar o principal de 1200 para 2400 em uma conta existente atualiza o resumo e persiste os novos parâmetros. Oráculo: 2400/12=200; custo total 2400.

### Precondições

1. Picsou do fork em execução, backend real e modo demo desativado; usuário de teste logado.
2. Registrar commit/build, navegador, sistema operacional e data de execução. Usar moeda EUR e dados fictícios.
3. Executar antes de 01/11/2026 para conferir zero parcelas pagas nos contratos futuros deste conjunto.
4. CT-PCV-01 executado com sucesso e Emprestimo Paulo A existente, com parcela automática, sem juros, seguro ou tarifa.

### Passos

| # | Ação | Resultado esperado |
|---|---|---|
| 1 | Em Accounts, localizar Emprestimo Paulo A e abrir a ação de edição. Aguardar o carregamento dos parâmetros de empréstimo. | Os parâmetros atuais são principal 1200, juros 0%, início 01/11/2026 e término 01/11/2027; a parcela mensal permanece não informada. |
| 2 | Alterar apenas o principal/borrowed amount para 2400, manter a parcela mensal em branco e salvar. | A edição é aceita sem criar uma nova conta. |
| 3 | Abrir ou retornar ao detalhe e aguardar a atualização do resumo. | Parcela e capital mensal 200; custo total 2400; saldo atual 2400; zero parcelas pagas e 12 restantes; juros, seguro e tarifa zero. |
| 4 | Recarregar a página e conferir o resumo; reabrir a edição para conferir o principal salvo. | Principal 2400 persistido, parcela automática não informada e resumo calculado em 200 por mês. Não reaparecem os valores antigos de 1200/100. |
| 5 | Voltar a Accounts, conferir que existe uma única conta Emprestimo Paulo A e guardar as capturas de antes/depois. | A alteração pertence à conta original, sem duplicidade; valores persistem após navegação e recarga. |

### Registro real — preencher após executar

| Campo | Valor |
|---|---|
| Testador real | Codex, execução assistida solicitada por Paulo |
| Data/hora e fuso | 2026-10-11T19:44:43.142Z; America/Sao_Paulo |
| Commit/build | 58c71b93544c27ff6f9b032dfe2c4df69fe01aa0 / Paulo - Entrega1 - 58c71b9-lf |
| Navegador/versão e SO | Codex In-app Browser (versão não exposta), Windows, idioma francês |
| ID nativo no TestLink / Plano / Build | dd-7 / Plano de Testes Manuais (46) / build 5 |
| Situação atual | Executado pela interface com assistência do Codex |
| Resultado final | Passou |

| Passo | Resultado real | Passou / Falhou / Bloqueado | Evidência |
|---|---|---|---|
| 1 | A edição da conta A carregou principal 1200, juros zero, datas originais e parcela mensal vazia. | Passou | [Capturas do passo 1](Tests_ScreenShots/dd-7-Recalculo-apos-edicao/README.md#passo-1) |
| 2 | Somente Montant emprunté foi alterado para 2400; mensalidade permaneceu vazia. Salvamento aceito na conta original. | Passou | [Capturas do passo 2](Tests_ScreenShots/dd-7-Recalculo-apos-edicao/README.md#passo-2) |
| 3 | Detalhe da mesma conta /accounts/5 mostrou parcela e capital 200, custo e saldo 2400, juros/seguro/tarifa zero, 0 parcelas pagas e 12 restantes. | Passou | [Capturas do passo 3](Tests_ScreenShots/dd-7-Recalculo-apos-edicao/README.md#passo-3) |
| 4 | Após recarga, resumo permaneceu em 2400/200. Reabertura da edição confirmou principal 2400 e mensalidade vazia. | Passou | [Capturas do passo 4](Tests_ScreenShots/dd-7-Recalculo-apos-edicao/README.md#passo-4) |
| 5 | Após cancelar a consulta da edição e recarregar Accounts, havia exatamente uma conta A, com principal 2400; nenhuma conta adicional foi criada. | Passou | [Capturas do passo 5](Tests_ScreenShots/dd-7-Recalculo-apos-edicao/README.md#passo-5) |

Issue relacionada: nenhuma falha comprovada nestes três casos. [Capturas e resultados](Tests_ScreenShots/dd-7-Recalculo-apos-edicao/README.md).
