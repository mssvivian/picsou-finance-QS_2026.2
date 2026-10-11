# Registro de problemas — LoanAmortizationService

Responsável: Paulo Cesar Vaz. Atualizado em 11/10/2026. Dois defeitos confirmados por testes unitários executados sobre o código original, sem alterações nas regras de negócio.

| Identificador | Problema | Esperado | Observado | Estado |
|---|---|---|---|---|
| [BUG-PCV-01](BUG-PCV-01.md) | Arredondamento do capital na parcela automática | Principal 1000 em três parcelas: soma dos capitais 1000,00 | Soma 999,99 | Reproduzido; publicado e aberto |
| [BUG-PCV-02](BUG-PCV-02.md) | Seguro omitido do total da parcela automática | Principal 1200 em 12 meses, seguro mensal 10: parcela 110,00 | Parcela 100,00 | Reproduzido; publicado e aberto |

Cada relato contém dados de entrada, passos para reprodução, resultado esperado e observado, ambiente, commit, severidade proposta e links para o teste e a saída real do Maven. BUG-PCV-01 corresponde à [issue #4](https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/4); BUG-PCV-02 corresponde à [issue #5](https://github.com/mssvivian/picsou-finance-QS_2026.2/issues/5). Ambas estão abertas e atribuídas a PaulcVaz.

## Relação com a entrega 1

A especificação da disciplina exige “Issues reportadas para os problemas encontrados, em um bug tracker à escolha (GitHub Issues, Mantis, Bugzilla)”. Os dois relatos foram publicados no GitHub Issues do grupo e seus links constam do README. TestLink guarda os casos e resultados; o cadastro dos casos não substitui o registro dos defeitos no tracker.

Paulo recebeu acesso de escrita em 11/10/2026. Os registros foram publicados por sua conta usando a autenticação Git existente, pois o conector de issues retornou 403. Os testes e evidências estão na branch `feat/teste_LoanAmortizationService_paulo`, pendente de merge na main.

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
