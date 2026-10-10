const fs = require('node:fs');
const path = require('node:path');
const root = path.resolve(__dirname, '..');
const spec = JSON.parse(fs.readFileSync(path.join(root, 'testlink/casos.json'), 'utf8'));
const escape = text => text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
const cdata = text => '<![CDATA[' + text.replace(/\]\]>/g, ']]]]><![CDATA[>') + ']]>';
let xml = '<?xml version="1.0" encoding="UTF-8"?>\n<testcases>\n';
let md = '# Casos manuais — Paulo\n\nProjeto: ' + spec.project + '. Suíte: **' + spec.suite + '**. Responsável previsto: ' + spec.author + '. Tipo: Manual. Importância: High. Estado de projeto: Draft; execução ainda não registrada.\n\n';
md += 'Fonte canônica: [casos.json](testlink/casos.json). Gerar Markdown e XML com `node entrega1/paulo/scripts/gerar-casos.cjs`. O XML é um arquivo de **projeto de casos**, não uma exportação de execução real do TestLink.\n\n';
for (const tc of spec.cases) {
  if (!tc.steps.length || !tc.id || !tc.summary) throw new Error('Caso incompleto');
  const preconditions = [...spec.preconditions, ...(tc.preconditions || [])];
  xml += '  <testcase name="' + escape(tc.id + ' — ' + tc.title) + '">\n';
  xml += '    <summary>' + cdata(tc.summary) + '</summary>\n';
  xml += '    <preconditions>' + cdata(preconditions.map((p, i) => `${i + 1}. ${p}`).join('<br/>')) + '</preconditions>\n';
  xml += '    <execution_type>1</execution_type>\n    <importance>3</importance>\n    <status>1</status>\n    <steps>\n';
  md += '## ' + tc.id + ' — ' + tc.title + '\n\n' + tc.summary + '\n\n### Precondições\n\n';
  md += preconditions.map((p, i) => `${i + 1}. ${p}`).join('\n') + '\n\n### Passos\n\n| # | Ação | Resultado esperado |\n|---|---|---|\n';
  tc.steps.forEach((s, i) => {
    xml += `      <step><step_number>${i + 1}</step_number><actions>${cdata(s.action)}</actions><expectedresults>${cdata(s.expected)}</expectedresults><execution_type>1</execution_type></step>\n`;
    md += `| ${i + 1} | ${s.action.replace(/\|/g, '\\|')} | ${s.expected.replace(/\|/g, '\\|')} |\n`;
  });
  xml += '    </steps>\n  </testcase>\n';
  md += '\n### Registro real — preencher após executar\n\n';
  md += '| Campo | Valor |\n|---|---|\n| Testador real | A preencher |\n| Data/hora e fuso | A preencher |\n| Commit/build | A preencher |\n| Navegador/versão e SO | A preencher |\n| ID nativo no TestLink / Plano / Build | A preencher |\n| Situação atual | Não executado — ambiente e acesso TestLink pendentes |\n| Resultado final | A preencher: Passou, Falhou ou Bloqueado |\n\n';
  md += '| Passo | Resultado real | Passou / Falhou / Bloqueado | Evidência |\n|---|---|---|---|\n';
  tc.steps.forEach((_, i) => md += `| ${i + 1} | Não executado | — | — |\n`);
  md += '\nIssue relacionada: preencher apenas com problema observado. Capturas previstas: `evidencias/manuais/' + tc.id + '-*.png`. Não existem capturas de aprovação até a execução real.\n\n';
}
xml += '</testcases>\n';
fs.writeFileSync(path.join(root, 'testlink/casos-paulo.xml'), xml);
fs.writeFileSync(path.join(root, 'casos-manuais.md'), md.trimEnd() + '\n');
console.log('Gerados 3 casos com 15 passos; resultados reais permanecem não preenchidos.');
