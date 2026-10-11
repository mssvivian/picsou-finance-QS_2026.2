# Ambiente local — simulação de empréstimos

Em 11/10/2026, imagem construída a partir do commit `58c71b93544c27ff6f9b032dfe2c4df69fe01aa0`, branch `feature/paulo-entrega1-amortizacao`, sem alterações de regras de negócio.

- Versão: `qs-entrega1-58c71b9`; frontend construído com `VITE_DEMO_MODE=false`.
- Imagem executada: `picsou-paulo:entrega1-58c71b9-lf`.
- Image ID: `sha256:2a8dbe31e1e0f08048a735313b35c05ddf57ac736e633bb29957099fc79417e5`.
- Adaptação de execução: normalização CRLF → LF somente em `/app/entrypoint.sh`, dentro da imagem derivada. Necessária porque o script copiado do checkout Windows não iniciou no Linux. Código de produção não editado.
- Aplicação de teste: http://localhost:8082, backend real, PostgreSQL 16, banco e volumes próprios `picsou-paulo-entrega1_paulo_pgdata` e `picsou-paulo-entrega1_paulo_data`.
- Conta: `paulo-teste`. Credenciais e Compose com segredos estão na pasta ignorada `.local/entrega1/`; não entram no Git.
- Ambiente HTTP de teste em loopback: cookies Secure desativados e origem permitida `http://localhost:8082`, somente nessa instalação.

A instalação anterior continua em http://localhost:8080, também usando a imagem local. Antes da troca, foi feito backup do banco em `.local/entrega1/banco-antes-imagem-local.dump`. Os volumes anteriores foram preservados.

## Reprodução

Na raiz do repositório, executar `docker compose -p picsou-paulo-entrega1 -f .local/entrega1/compose-teste.yml up -d`. Depois executar `./entrega1/paulo/scripts/simular-emprestimos.ps1` em PowerShell. A instalação e credenciais locais precisam existir; arquivos com segredos não são transferidos para o repositório.

[Resultado da simulação HTTP](evidencias/simulacao-api/resultado.json): três cenários aprovados, contas 3 e 4. As contas 1 e 2 pertencem a uma tentativa preparatória anterior que consultou uma rota inexistente; não são defeitos do produto. O script corrigido usa a resposta de `GET /api/accounts/{id}` para conferir os parâmetros persistidos.

As contas de simulação têm nomes diferentes dos casos manuais. CT-PCV-01/02/03 permanecem sem execução pela interface. Para finalizar, seguir os casos manualmente e anexar capturas no TestLink.
