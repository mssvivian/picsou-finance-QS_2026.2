# TestLink — importação, execução e evidências

## Situação

A URL da instância e seu acesso ainda não foram fornecidos. [casos-paulo.xml](casos-paulo.xml) é XML de importação de especificações; foi verificado como XML bem formado e sua estrutura foi comparada com o [importador oficial do TestLink](https://github.com/TestLinkOpenSourceTRMS/testlink-code/blob/testlink_1_9_20_fixed/lib/testcases/tcImport.php). A importação na instância específica ainda precisa ser validada.

Os códigos CT-PCV-01/02/03 são identificadores locais incluídos no título. O TestLink atribuirá IDs próprios; registrar a relação entre ambos, sem inventar externalid/internalid no XML. As configurações padrão usadas são execução manual=1, importância alta=3 e estado Draft=1; confirmar os estados apresentados na instalação antes de executar.

## Procedimento

1. Acessar o projeto Grupo DinDin existente ou o projeto individual definido pela equipe. Não criar projetos duplicados sem necessidade.
2. Criar a suíte **Paulo — Amortização de empréstimos**. Na especificação de testes, selecionar a suíte e importar `casos-paulo.xml` como casos. Se houver reimportação, verificar os títulos e usar atualização/ignorar duplicados em vez de criar novas cópias.
3. Conferir três casos, cinco passos por caso, precondições, esperado, importância e tipo Manual. Revisar os casos e avançar seu estado conforme o fluxo configurado. Estado de revisão do caso é diferente de resultado de execução.
4. Associar os casos ao plano de execução da Entrega 1, criar/selecionar build com o commit **real** da aplicação e atribuir o testador. Sugerido: `Entrega1-<sha-curto>`; não usar esse nome como prova de build se a aplicação não foi construída dessa revisão.
5. Executar CT-PCV-01, CT-PCV-02 e CT-PCV-03 nessa ordem; o terceiro depende do primeiro. Registrar em cada passo resultado real e anexar capturas. Não marcar como aprovado com base apenas nos unitários.
6. Se não houver aplicação operacional, registrar bloqueio com sua causa. Se um passo falhar, registrar falha, esperado/observado e issue reproduzível. Defeito não confirmado não deve ser reportado como fato.
7. Exportar o relatório disponível na instalação (HTML/PDF/XML, conforme ferramenta) e guardar em `../evidencias/manuais/`. Registrar nome do projeto, suíte, plano, build, IDs dos casos, testador, data/hora, navegador e sistema operacional.
8. Atualizar [casos-manuais.md](../casos-manuais.md) e [resultados.md](../resultados.md) com o resultado real. Após editar resultados no Markdown, não executar novamente o gerador sem antes preservar esses registros: ele regenera a especificação com campos em branco.

## Lista de evidências

| Código local | ID TestLink | Build | Resultado | Evidências reais |
|---|---|---|---|---|
| CT-PCV-01 | Pendente | Pendente | Não executado | Nenhuma |
| CT-PCV-02 | Pendente | Pendente | Não executado | Nenhuma |
| CT-PCV-03 | Pendente | Pendente | Não executado | Nenhuma |

Em instalação individual, compartilhar a exportação e os anexos com o grupo. O material de projeto/importação sozinho não comprova uso do TestLink nem execução manual.
