# TestLink — Paulo: cadastro e execução

Instância: http://147.15.86.250:8080/index.php. Projeto **Grupo DinDin** (20), suíte **Paulo - Amortização de empréstimos** (73). Plano **Plano de Testes Manuais** (46), build **Paulo - Entrega1 - 58c71b9-lf** (5).

| Código local | ID TestLink | Caso | Resultado | Execução |
|---|---|---|---|---|
| CT-PCV-01 | dd-5 | Parcela automática sem juros | Passed | 8 |
| CT-PCV-02 | dd-6 | Composição com seguro e tarifa | Passed | 9 |
| CT-PCV-03 | dd-7 | Recálculo após edição do principal | Passed | 10 |

Cinco passos aprovados em cada caso, em 11/10/2026. Execução pela interface assistida pelo Codex, solicitada por Paulo Cesar Vaz; não representa execução humana independente. A conta compartilhada admin consta como testador no TestLink, e as notas identificam responsável e operador. Especificação em Draft para revisão, importância High, tipo Manual.

[Resultados conferidos no servidor](../evidencias/testlink/execucoes-verificadas.json), com relatórios [dd-5](../evidencias/testlink/execucao-dd-5.txt), [dd-6](../evidencias/testlink/execucao-dd-6.txt) e [dd-7](../evidencias/testlink/execucao-dd-7.txt). Chaves dos links de exportação foram removidas dos relatórios locais.

## Evidências

[30 capturas originais por caso e passo](../Tests_ScreenShots/README.md), [resultados e hashes](../Tests_ScreenShots/execucao.json) e [casos com observações reais](../casos-manuais.md). Algumas imagens foram aceitas pelo TestLink, mas parte dos anexos foi rejeitada com “File Name does not pass REGEXP checkings”. Não se afirma que as 30 imagens estejam anexadas no servidor; o conjunto completo está no diretório local.

Ambiente: http://localhost:8082, backend real, demo desativado, Windows, Codex In-app Browser (versão não exposta), idioma francês. Commit 58c71b93544c27ff6f9b032dfe2c4df69fe01aa0; imagem picsou-paulo:entrega1-58c71b9-lf. [Ambiente e reprodução](../ambiente-local.md).

## Especificação e preparação

[Exportação real da especificação](../evidencias/testlink/especificacao-exportada.xml) e [conferência do cadastro](../evidencias/testlink/cadastro-verificado.json). Não reimportar: a suíte já existe. Não regenerar os casos depois de preencher os resultados sem preservar os registros.

[Simulação HTTP preparatória](../evidencias/simulacao-api/resultado.json), anterior à execução pela interface; as contas Simulacao são independentes das contas Emprestimo Paulo A e B usadas nos casos.
