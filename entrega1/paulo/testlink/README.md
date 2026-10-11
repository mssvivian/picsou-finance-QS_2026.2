# TestLink — Paulo: cadastro e execução

Instância: http://147.15.86.250:8080/index.php. Projeto **Grupo DinDin** (20), suíte **Paulo - Amortização de empréstimos** (73). Cadastro e leitura de conferência realizados em 11/10/2026. Cada caso tem cinco passos, importância High, execução Manual e estado Draft.

| Código local | ID TestLink | Caso | Resultado manual |
|---|---|---|---|
| CT-PCV-01 | dd-5 | Parcela automática sem juros | Não executado |
| CT-PCV-02 | dd-6 | Composição com seguro e tarifa | Não executado |
| CT-PCV-03 | dd-7 | Recálculo após edição do principal | Não executado |

[Exportação real da especificação](../evidencias/testlink/especificacao-exportada.xml) e [conferência do cadastro](../evidencias/testlink/cadastro-verificado.json). Essa exportação comprova o cadastro dos casos, não sua execução. Não reimportar: a suíte já existe.

## Próxima execução manual

1. Abrir http://localhost:8082 e entrar com o usuário de teste. As credenciais estão somente no arquivo local ignorado `.local/entrega1/picsou-teste-credenciais.json`; não publicá-las.
2. No TestLink, associar dd-5/6/7 ao plano de testes manuais do grupo e criar/selecionar uma build identificando `qs-entrega1-58c71b9-lf`. Registrar commit completo, navegador, SO, data e testador.
3. Seguir [os passos](../casos-manuais.md) pela interface: criar Emprestimo Paulo A, criar Emprestimo Paulo B e editar A. As contas com prefixo Simulacao são preparação por API e não substituem esses passos.
4. Guardar resultado real e capturas de cada caso em `../evidencias/manuais/`. Marcar Passou/Falhou/Bloqueado de acordo com o observado; Draft/Final é o estado da especificação.
5. Exportar o relatório de execução disponível e atualizar os registros locais. Não regenerar os casos depois de preencher resultados sem preservar esses registros.

## Simulação preparatória

Os três cenários passaram no backend real por HTTP, inclusive persistência após nova consulta. [Resultado completo](../evidencias/simulacao-api/resultado.json). Isso não valida os cliques, rótulos, gráficos, recarga do navegador nem a interface. A automação do Firefox foi bloqueada por ausência de suporte à política de URLs do Computer Use.

[Ambiente e reprodução](../ambiente-local.md). Nenhum resultado de execução manual foi atribuído no TestLink.
