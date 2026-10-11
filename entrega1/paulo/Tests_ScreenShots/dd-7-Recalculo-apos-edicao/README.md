# dd-7 — Recálculo após edição do principal

Resultado: **Passou**. Execução assistida pela interface pelo Codex, sob solicitação de Paulo; não é uma execução humana independente. Ambiente e horário: [registro completo](../execucao.json).

## Passo 1

Ação: Em Accounts, localizar Emprestimo Paulo A e abrir a ação de edição. Aguardar o carregamento dos parâmetros de empréstimo.

Esperado: Os parâmetros atuais são principal 1200, juros 0%, início 01/11/2026 e término 01/11/2027; a parcela mensal permanece não informada.

Observado: A edição da conta A carregou principal 1200, juros zero, datas originais e parcela mensal vazia.

Resultado: **Passou**.

- [passo-01-edicao-valores-originais.jpg](passo-01-edicao-valores-originais.jpg)
- [passo-01-principal-1200.jpg](passo-01-principal-1200.jpg)

## Passo 2

Ação: Alterar apenas o principal/borrowed amount para 2400, manter a parcela mensal em branco e salvar.

Esperado: A edição é aceita sem criar uma nova conta.

Observado: Somente Montant emprunté foi alterado para 2400; mensalidade permaneceu vazia. Salvamento aceito na conta original.

Resultado: **Passou**.

- [passo-02-principal-2400-parcela-em-branco.jpg](passo-02-principal-2400-parcela-em-branco.jpg)

## Passo 3

Ação: Abrir ou retornar ao detalhe e aguardar a atualização do resumo.

Esperado: Parcela e capital mensal 200; custo total 2400; saldo atual 2400; zero parcelas pagas e 12 restantes; juros, seguro e tarifa zero.

Observado: Detalhe da mesma conta /accounts/5 mostrou parcela e capital 200, custo e saldo 2400, juros/seguro/tarifa zero, 0 parcelas pagas e 12 restantes.

Resultado: **Passou**.

- [passo-03-custo-total-2400.jpg](passo-03-custo-total-2400.jpg)
- [passo-03-resumo-recalculado.jpg](passo-03-resumo-recalculado.jpg)

## Passo 4

Ação: Recarregar a página e conferir o resumo; reabrir a edição para conferir o principal salvo.

Esperado: Principal 2400 persistido, parcela automática não informada e resumo calculado em 200 por mês. Não reaparecem os valores antigos de 1200/100.

Observado: Após recarga, resumo permaneceu em 2400/200. Reabertura da edição confirmou principal 2400 e mensalidade vazia.

Resultado: **Passou**.

- [passo-04-principal-persistido.jpg](passo-04-principal-persistido.jpg)
- [passo-04-resumo-apos-recarregar.jpg](passo-04-resumo-apos-recarregar.jpg)

## Passo 5

Ação: Voltar a Accounts, conferir que existe uma única conta Emprestimo Paulo A e guardar as capturas de antes/depois.

Esperado: A alteração pertence à conta original, sem duplicidade; valores persistem após navegação e recarga.

Observado: Após cancelar a consulta da edição e recarregar Accounts, havia exatamente uma conta A, com principal 2400; nenhuma conta adicional foi criada.

Resultado: **Passou**.

- [passo-05-conta-original-sem-duplicata.jpg](passo-05-conta-original-sem-duplicata.jpg)
