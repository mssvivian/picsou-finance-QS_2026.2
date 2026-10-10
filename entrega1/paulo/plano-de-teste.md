# PT-PCV-01 — Contribuição individual ao Plano de Teste

**Projeto:** Picsou Finance, fork Grupo DinDin. **Entrega:** COS482 2026.2, primeira entrega. **Autor:** Paulo Cesar Vaz. **Revisão:** 1, 10/10/2026. **Estado:** contribuição preparada; consolidação do grupo pendente. Baseado na estrutura clássica IEEE 829; o template específico da aula ainda não foi fornecido e deve prevalecer na consolidação.

## 1. Identificação e referências

Código-base `6b020c76cf093b8995a68df12a32e8c5b21e9144`, branch `feature/paulo-entrega1-amortizacao`. Referências: especificação da disciplina, documentação de empréstimos, ADR de amortização sob demanda, convenções de testes e [matriz de rastreabilidade](matriz-de-testes.md).

## 2. Introdução e escopo

O Picsou agrega contas, investimentos, dívidas e patrimônio. Esta contribuição verifica o cálculo de empréstimos e sua apresentação na interface: parcelas, capital, juros, seguro, tarifas, saldo e atualização após edição. A relação é Debt → LoanAmortizationService → resumo/cronograma → tela de detalhe da conta LOAN.

## 3. Itens de teste

Classe `LoanAmortizationService`, métodos públicos com data explícita; casos manuais de conta LOAN, formulário de parâmetros e detalhe do empréstimo. O endpoint existente `GET /api/accounts/{id}/loan-summary` dá suporte ao fluxo. Nenhuma API, DTO, entidade, migração ou regra de negócio será modificada nesta contribuição.

## 4. Funcionalidades a testar

Parcela automática sem juros e com juros; cronograma e saldo final; ajuste da última parcela; limite de amortização; seguro/tarifas; datas e valores opcionais; parcelas pagas conforme referência; recálculo após edição. CT-PCV-01/02/03 representam a contribuição manual de Paulo.

## 5. Funcionalidades fora desta contribuição

Sincronização bancária/crypto, exclusão de conexões (cenário informado por Vivian), autenticação como objeto de teste, pré-pagamentos e taxas variáveis. Não abranger Mockito artificialmente: esta classe não tem dependências a isolar. Integração, E2E, 80% de arestas, mutação e inspeção/correções de código pertencem à Entrega 2.

## 6. Abordagem

Testes unitários JUnit 5/AssertJ com datas fixas, valores BigDecimal e oráculos calculados previamente. Preservar os nove testes originais e acrescentar 22 execuções. Particionar entradas por juros zero/positivos, parcela explícita/automática, seguro ausente/presente, duração válida/inválida e situação temporal. Regressões dos defeitos são opt-in e seus resultados vermelhos são evidência, não aprovação. Casos manuais seguem passos e resultados independentes, com backend real fora do demo.

## 7. Critérios de aprovação e falha

Cada teste unitário é aprovado quando todas suas asserções são atendidas. Cada caso manual é aprovado quando todos os passos têm resultado esperado; uma discrepância reproduzível gera falha e issue. Bloqueio de ferramenta/ambiente é registrado como bloqueado, não aprovado. Os 31 casos normais devem passar. Não afirmar aprovação global quando a suíte completa apresenta erros ou quando há casos ignorados.

## 8. Suspensão e retomada

Suspender a execução manual se não houver ambiente operacional, login, versão rastreável ou dados apropriados. Retomar depois da resolução, repetir os passos do caso afetado e preencher resultados reais. Erros de infraestrutura de outros módulos são registrados à parte; não bloquear o avanço dos testes puros de amortização.

## 9. Entregáveis

Testes Java; complexidade com tokens/linhas; matriz; especificações manuais e XML de importação; registros de execução e evidências; relatos dos dois defeitos; contribuição ao Plano de Teste no Google Docs; commits/patches; README com links diretos; roteiro individual de apresentação. O XML projetado não substitui a exportação de execução do TestLink.

## 10. Tarefas

Confirmar reserva da classe; executar baseline; ampliar testes; medir complexidade; executar suíte completa; projetar/importar/executar casos manuais; reportar defeitos; consolidar documentos; revisar e integrar à main; conferir Moodle; preparar apresentação.

## 11. Ambiente e ferramentas

Unitários: Windows 11 amd64, Java Temurin 21.0.12.1, Maven 3.9.16 local, JUnit 5/AssertJ herdados do POM, Surefire 3.5.3. Métrica: Node.js 24.19.0 e script específico auditável. Backend original: Spring Boot 3.4.9. Ambiente manual previsto: frontend e backend construídos do fork, PostgreSQL 16 via Compose, navegador com versão registrada, usuário e dados fictícios. Há PostgreSQL 18 instalado, mas a tentativa de criar cluster isolado foi bloqueada pelo Windows. TestLink: endereço/acesso pendentes. Google Docs: conta de Paulo confirmada pelo conector.

## 12. Responsabilidades

Paulo: esta classe, seus casos/evidências, relatos, contribuição documental e apresentação individual. Vivian: cenário de exclusão da conta/conexão informado por ela; demais responsabilidades devem ser confirmadas. Não atribuir classes a Carol a partir da contagem. O grupo precisa nomear revisores, consolidador dos documentos e responsável pelo Moodle.

## 13. Necessidades de capacitação

Paulo deve dominar o algoritmo, convenção da complexidade, oráculos monetários, diferença entre teste herdado e próprio, registros de execução do TestLink e preservação de autoria no Git. Usar o roteiro individual para preparação das perguntas.

## 14. Cronograma

10/10: testes e documentos locais, alinhamento e resolução de acessos. 11/10 até 20h: execução manual, consolidação e integração à main. 11/10 até 23h59, Brasília: entrega no GitHub e Moodle. Apresentação em 13/10 ou 15/10 conforme turma; material enviado antes da aula.

## 15. Riscos e contingências

Sem permissão GitHub: aplicar patches preservando autoria. Sem TestLink: obter instância do grupo; casos locais não comprovam uso da ferramenta. Docker quebrado: executar em ambiente do colega baseado no mesmo código ou resolver localmente sem reset de dados. Sem template: adaptar esta contribuição ao template quando recebido. Prazo próximo: priorizar requisitos da Entrega 1. Dois defeitos comprovados e erros da suíte ampla precisam ser apresentados com suas limitações, sem correção de produção nesta branch.

## 16. Aprovações

Revisão e aceite do grupo pendentes. Reserva da classe, responsáveis gerais, template, links e registros manuais devem ser confirmados antes da submissão. Este documento não declara cumprimento integral da entrega.
