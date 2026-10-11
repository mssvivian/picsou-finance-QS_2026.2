# dd-6 — Composição com seguro e tarifa

Resultado: **Passou**. Execução assistida pela interface pelo Codex, sob solicitação de Paulo; não é uma execução humana independente. Ambiente e horário: [registro completo](../execucao.json).

## Passo 1

Ação: Abrir Add account e escolher criação manual, tipo Loan, nome Emprestimo Paulo B e moeda EUR.

Esperado: O formulário apresenta os parâmetros de empréstimo.

Observado: Formulário Manuel aberto com nome Emprestimo Paulo B, tipo Prêt e EUR.

Resultado: **Passou**.

- [passo-01-formulario-emprestimo.jpg](passo-01-formulario-emprestimo.jpg)

## Passo 2

Ação: Preencher principal 1200, juros 0%, parcela mensal explícita 110, seguro mensal 10, tarifa/file fees 50, início 01/11/2026 e término 01/11/2027; salvar.

Esperado: O cadastro é aceito sem erro e os parâmetros informados são salvos.

Observado: Cadastro aceitou principal e saldo 1200, juros 0%, parcela 110, seguro 10, tarifa 50 e datas 01/11/2026 a 01/11/2027.

Resultado: **Passou**.

- [passo-02-conta-criada.jpg](passo-02-conta-criada.jpg)
- [passo-02-parametros.jpg](passo-02-parametros.jpg)

## Passo 3

Ação: Abrir o detalhe do empréstimo e conferir a composição mensal e o progresso.

Esperado: Parcela total 110; capital 100; juros 0; seguro 10. Zero parcelas pagas, 12 restantes e saldo atual 1200.

Observado: Detalhe mostrou parcela 110, capital 100, juros zero, seguro 10, 0 parcelas pagas, 12 restantes e saldo 1200.

Resultado: **Passou**.

- [passo-03-composicao-mensal.jpg](passo-03-composicao-mensal.jpg)
- [passo-03-progresso.jpg](passo-03-progresso.jpg)

## Passo 4

Ação: Conferir o resumo de custos e o ponto final do gráfico de amortização.

Esperado: Capital total 1200; juros e seguro combinados 120; tarifa 50; custo total 1370. Cronograma encerra com saldo zero.

Observado: Síntese mostrou capital 1200, juros/seguro 120, tarifa 50 e custo total 1370. Tooltip final mostrou saldo 0,00 EUR.

Resultado: **Passou**.

- [passo-04-custos-legiveis.jpg](passo-04-custos-legiveis.jpg)
- [passo-04-resumo-custos.jpg](passo-04-resumo-custos.jpg)
- [passo-04-saldo-final-zero.jpg](passo-04-saldo-final-zero.jpg)

## Passo 5

Ação: Recarregar o detalhe e registrar capturas dos parâmetros e resultados.

Esperado: Seguro, tarifa, parcela e custo total permanecem iguais após a recarga.

Observado: Após recarregar, parcela 110, seguro 10, tarifa 50 e custo 1370 permaneceram.

Resultado: **Passou**.

- [passo-05-custos-persistidos.jpg](passo-05-custos-persistidos.jpg)
- [passo-05-resumo-persistido.jpg](passo-05-resumo-persistido.jpg)
