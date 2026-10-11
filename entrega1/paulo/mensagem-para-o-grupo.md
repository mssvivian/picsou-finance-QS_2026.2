# Mensagem atual de Paulo para enviar ao grupo

Pessoal, publiquei minha contribuição na branch `feat/teste_LoanAmortizationService_paulo`, incorporando a main atual e preservando minha autoria. A classe é LoanAmortizationService, com complexidade 14 no método principal. Mantive nove testes herdados identificados e acrescentei 22 execuções próprias: 31 aprovados. A suíte atual tem 33 casos, dois ignorados por padrão; as duas reproduções de defeitos ativadas falham conforme esperado.

Os casos dd-5/6/7 passaram nos 15 passos pela interface, em execução assistida pelo Codex e identificada nas notas. Há 30 capturas por teste e passo. Issues abertas: https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/4 e https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/5. Não corrigi produção nesta contribuição.

Os testes e documentos estão no README da branch e em entrega1/paulo/. Precisamos revisar o PR, integrar na main e consolidar o Plano IEEE 829 conforme o template da aula. O relatório de três páginas está em https://docs.google.com/document/d/1a1vX4Uais7X7nwQXB1aPTmWpnuptHEkYsVlMXl88kIw/edit.

Esta mensagem foi preparada para Paulo enviar pessoalmente; não foi enviada automaticamente.

---

# Mensagem inicial de 10/10 preservada como histórico

Pessoal, vou trabalhar na Entrega 1 com a classe **LoanAmortizationService**, responsável por calcular o cronograma e o resumo de amortização dos empréstimos. Meu cenário manual será a criação de empréstimo com parcela automática, composição com seguro/tarifa e recálculo após edição.

Confirmei complexidade ciclomática **14 no método principal**, incluindo ternários e decisões de curto-circuito. Já executei os 9 testes originais e acrescentei 22 execuções próprias: os 31 casos passaram. Também reproduzi dois problemas, com relatos e testes separados: o cronograma automático de 1000 em três parcelas exibe capitais que somam 999,99; a parcela automática não inclui o seguro mensal.

Estou na branch `feature/paulo-entrega1-amortizacao`, baseada em `6b020c76cf093b8995a68df12a32e8c5b21e9144`. Vou disponibilizar os commits ou patches preservando minha autoria para integração à `main`. Meus artefatos estão em `entrega1/paulo/`, com índice e links no README.

Preciso combinar com vocês:

1. Alguém já reservou essa classe/funcionalidade? As anotações de quem contou a complexidade não foram tratadas como reservas.
2. Qual é o endereço do TestLink? É uma instância individual ou compartilhada? Preparei três casos e um XML de importação; seus códigos são CT-PCV-01 a CT-PCV-03.
3. Podem enviar o template IEEE 829 da aula e o Google Docs em que vamos consolidar o Plano de Teste?
4. Preciso do convite para o GitHub do grupo: minha conta tem leitura, mas não push; a página de issues limita criação a colaboradores. Quem fará a revisão/integração dos PRs e quem será responsável pela submissão no Moodle?
5. Quem consegue fornecer um Picsou operacional baseado no commit do fork para executar os casos manuais? Meu Docker falha ao iniciar e a criação de um PostgreSQL isolado foi bloqueada pelo Controle de Aplicativo do Windows. Os testes unitários rodam normalmente.

Sugiro integrar tudo até **11/10 às 20h**, deixando tempo para conferir a `main`, os links do README e a submissão antes das **23h59**. Cada integrante precisa editar os documentos com a própria conta; a participação individual será avaliada no histórico e no GitHub.

Minha contribuição no Google Docs: https://docs.google.com/document/d/1244Hgfv4YOyM4nBO8PSXadMBxtFPmg2J8so5xoWCy_o/edit?usp=drivesdk

Esta mensagem foi preparada para Paulo enviar pessoalmente; não foi enviada automaticamente ao grupo.
