# dd-5 — Parcela automática sem juros

Resultado: **Passou**. Execução assistida pela interface pelo Codex, sob solicitação de Paulo; não é uma execução humana independente. Ambiente e horário: [registro completo](../execucao.json).

## Passo 1

Ação: Abrir Accounts, clicar Add account e escolher a opção de criação manual.

Esperado: O formulário de criação manual de conta é exibido.

Observado: O formulário Manuel foi exibido após Ajouter un compte > Manuel.

Resultado: **Passou**.

- [passo-01-formulario-manual.jpg](passo-01-formulario-manual.jpg)

## Passo 2

Ação: Informar nome Emprestimo Paulo A, moeda EUR e tipo Loan; preencher principal/borrowed amount 1200, juros 0%, início 01/11/2026, término 01/11/2027. Deixar parcela mensal em branco; seguro e tarifa em branco ou zero. Salvar.

Esperado: A conta é criada sem erro. Parcela automática é solicitada mantendo o campo mensal vazio, sem inserir zero.

Observado: Emprestimo Paulo A foi criada em EUR, tipo Prêt, principal e saldo informado 1200, juros 0%, datas 01/11/2026 e 01/11/2027. Mensualité, seguro e tarifa deixados vazios.

Resultado: **Passou**.

- [passo-02-conta-criada.jpg](passo-02-conta-criada.jpg)
- [passo-02-parametros.jpg](passo-02-parametros.jpg)

## Passo 3

Ação: Abrir o detalhe da conta Emprestimo Paulo A e aguardar o carregamento do resumo de empréstimo.

Esperado: Parcela mensal 100, capital mensal 100, juros 0, seguro 0. Progresso: 0 parcelas pagas e 12 restantes. Saldo atual 1200.

Observado: Detalhe mostrou parcela 100, capital 100, juros e seguro zero, 0 parcelas pagas, 12 restantes e saldo atual 1200.

Resultado: **Passou**.

- [passo-03-resumo-e-progresso.jpg](passo-03-resumo-e-progresso.jpg)
- [passo-03-resumo-legivel.jpg](passo-03-resumo-legivel.jpg)

## Passo 4

Ação: Conferir o resumo de custos e o gráfico de amortização; inspecionar o ponto final do cronograma.

Esperado: Custo total 1200; capital total 1200; juros/seguro e tarifas zero. O cronograma termina com saldo zero. Datas são apresentadas no formato configurado na aplicação.

Observado: Síntese mostrou custo e capital total 1200, juros/seguro e tarifa zero. Tooltip do último ponto (outubro/2027) mostrou saldo 0,00 EUR.

Resultado: **Passou**.

- [passo-04-custos-e-saldo-final.jpg](passo-04-custos-e-saldo-final.jpg)
- [passo-04-grafico.jpg](passo-04-grafico.jpg)
- [passo-04-saldo-final-zero.jpg](passo-04-saldo-final-zero.jpg)

## Passo 5

Ação: Recarregar a página e conferir novamente os valores; capturar resumo, progresso e gráfico.

Esperado: Os mesmos parâmetros e valores permanecem. Nenhuma duplicata de Emprestimo Paulo A é criada.

Observado: Após recarga, valores permaneceram 1200/100. A lista apresentou exatamente uma conta Emprestimo Paulo A.

Resultado: **Passou**.

- [passo-05-apos-recarregar.jpg](passo-05-apos-recarregar.jpg)
- [passo-05-conta-unica.jpg](passo-05-conta-unica.jpg)
- [passo-05-progresso-e-custos.jpg](passo-05-progresso-e-custos.jpg)
- [passo-05-resumo-persistido.jpg](passo-05-resumo-persistido.jpg)
