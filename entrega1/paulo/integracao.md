# Integração e fechamento da Entrega 1

## Material local já preparado

- [x] Branch individual, commit-base e identidade Git de Paulo verificados.
- [x] Complexidade confirmada, script e evidência.
- [x] Baseline de nove testes e 22 execuções novas aprovadas.
- [x] Suíte ampla executada; limitações registradas sem declarar sucesso global.
- [x] Dois defeitos reproduzidos, relatos e regressões opt-in.
- [x] Três casos manuais projetados, XML de importação e guia de registro.
- [x] Contribuição ao Plano de Teste e roteiro individual.
- [x] Frontend/backend compilados e manifesto com hashes.
- [x] Contribuição no Google Docs criada pela conta de Paulo e conferida, sem alterar compartilhamento.
- [x] Mesmos erros da suíte ampla reproduzidos no código original isolado.

## Dependências reais antes da entrega

- [ ] Paulo enviar a mensagem ao grupo e confirmar reserva da classe.
- [ ] Grupo definir revisores, consolidador e responsável pelo Moodle.
- [ ] Obter o template da aula e adaptar/consolidar o Plano de Teste.
- [ ] Compartilhar o Google Docs da contribuição/consolidação; cada integrante editar na própria conta.
- [ ] Obter a instância TestLink; importar e revisar os três casos.
- [ ] Construir/subir um ambiente do fork com backend real e registrar commit, navegador e SO.
- [ ] Executar os casos manuais e preencher resultados/evidências; exportar a execução real do TestLink.
- [ ] Publicar BUG-PCV-01 e BUG-PCV-02 e inserir links reais no índice.
- [ ] Integrar os commits à `main` do repositório do grupo, conferir links do README e autoria no GitHub.
- [ ] Verificar entrega no Moodle até 11/10/2026 às 23h59 (Brasília).
- [ ] Definir dia de apresentação (13 ou 15/10) e enviar material antes da aula.

Meta interna de integração: **11/10 às 20h**. Não configurar reset do Docker nem apagar dados para acelerar a preparação.

## Aplicação dos patches se Paulo continuar sem permissão

Os patches serão entregues na pasta irmã `../patches-paulo-entrega1/`, fora do repositório, evitando inclusão recursiva no próprio commit. O colega deve começar com checkout limpo da `main`, criar uma branch de integração e aplicar os patches com `git am <arquivo.patch>` na ordem numérica. Isso preserva autor e email de Paulo; não copiar arquivos e reatribuir toda a contribuição ao integrador.

Executar os testes normais da classe, revisar artefatos e abrir PR. Durante a revisão, informar que regressões dos defeitos falham quando ativadas e que a suíte ampla está afetada pelo ambiente. Após merge, confirmar que `entrega1/paulo/` e o índice do README estão na `main`. Se surgir conflito, preservar alterações dos colegas e resolver pela revisão do diff; não usar reset/force push.

Descrição sugerida do PR: “Acrescenta 22 execuções unitárias de amortização, três casos manuais para TestLink e contribuição documental de Paulo. Preserva código de produção e testes herdados. Registra dois defeitos reproduzidos em regressões opt-in. Execução manual e consolidação do grupo ainda dependem dos acessos/ambiente indicados no relatório.”
