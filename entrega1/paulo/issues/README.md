# Registro de problemas — LoanAmortizationService

Responsável: Paulo Cesar Vaz. Atualizado em 11/10/2026. Dois defeitos confirmados por testes unitários executados sobre o código original, sem alterações nas regras de negócio.

| Identificador | Problema | Esperado | Observado | Estado |
|---|---|---|---|---|
| [BUG-PCV-01](BUG-PCV-01.md) | Arredondamento do capital na parcela automática | Principal 1000 em três parcelas: soma dos capitais 1000,00 | Soma 999,99 | Reproduzido; publicação pendente |
| [BUG-PCV-02](BUG-PCV-02.md) | Seguro omitido do total da parcela automática | Principal 1200 em 12 meses, seguro mensal 10: parcela 110,00 | Parcela 100,00 | Reproduzido; publicação pendente |

Cada relato contém dados de entrada, passos para reprodução, resultado esperado e observado, ambiente, commit, severidade proposta e links para o teste e a saída real do Maven. Os identificadores BUG-PCV são locais; ainda não são números de issues no GitHub.

## Relação com a entrega 1

A especificação da disciplina exige “Issues reportadas para os problemas encontrados, em um bug tracker à escolha (GitHub Issues, Mantis, Bugzilla)”. Os relatos estão preparados aqui, mas precisam ser publicados no tracker do grupo e seus links acrescentados ao README para concluir esse requisito. TestLink guarda os casos e resultados; o cadastro dos casos não substitui o registro dos defeitos no tracker.

A conta conectada de Paulo não possui permissão de escrita no fork. Um integrante com acesso pode publicar o conteúdo dos dois relatos em https://github.com/mssvivian/picsou-finance-QS_2026.2/issues, identificando Paulo como responsável pela descoberta e anexando os links das evidências após integração na main. Registrar aqui os números e URLs atribuídos pelo GitHub quando a publicação ocorrer.

## Evidências e interpretação

- [Teste de reprodução dos dois defeitos](../../../backend/src/test/java/com/picsou/service/LoanAmortizationServiceDefectTest.java).
- [Saída Maven](../evidencias/unitarios/defeitos/maven-output.txt) e [relatório Surefire](../evidencias/unitarios/defeitos/TEST-com.picsou.service.LoanAmortizationServiceDefectTest.xml): duas falhas de asserção, nenhuma falha de infraestrutura nessa execução.
- [Resultados gerais](../resultados.md) e [execução dos casos pela interface](../Tests_ScreenShots/README.md).

Os três casos manuais dd-5/6/7 passaram. Eles não reproduzem estes defeitos: dd-5 usa 1200/12, divisão exata; dd-6 informa explicitamente parcela 110; dd-7 usa 2400/12, também divisão exata. Portanto, o sucesso desses casos é compatível com os dois problemas encontrados nos testes unitários. Não há evidência de execução pela interface dos cenários específicos dos defeitos.

Para reproduzir as duas falhas na pasta backend:

```shell
mvn -Dtest=LoanAmortizationServiceDefectTest -Dpicsou.entrega1.defect-regressions=true test
```

As regressões exigem ativação explícita; o resultado esperado da reprodução no código atual é falha. A branch desta entrega registra os problemas e não os corrige. Os 20 erros de infraestrutura da suíte ampla são documentados separadamente e não são atribuídos a esta classe como defeitos funcionais.
