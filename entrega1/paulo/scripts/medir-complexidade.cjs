// Auditor específico de LoanAmortizationService; não é um parser Java geral.
// Conta decisões no corpo dos sete métodos explícitos, sem comentários/literais.
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const sourcePath = path.resolve(__dirname, '../../../backend/src/main/java/com/picsou/service/LoanAmortizationService.java');
const original = fs.readFileSync(sourcePath, 'utf8');
const stripped = original.replace(/\/\*[\s\S]*?\*\/|\/\/[^\r\n]*|"(?:\\.|[^"\\])*"|'(?:\\.|[^'\\])*'/g,
  match => match.replace(/[^\r\n]/g, ' '));
const signatures = /(?:public|private)\s+(?:static\s+)?(?!record\b)[\w<>]+\s+(\w+)\s*\(([^)]*)\)\s*\{/g;
const methods = [];
for (const match of stripped.matchAll(signatures)) {
  const start = match.index + match[0].length;
  let depth = 1, end = start;
  while (depth && end < stripped.length) {
    if (stripped[end] === '{') depth++;
    if (stripped[end] === '}') depth--;
    end++;
  }
  if (depth) throw new Error('Corpo de método não fechado');
  const decisions = [];
  for (const decision of stripped.slice(start, end - 1).matchAll(/\b(?:if|for|while|case|catch)\b|\?|&&|\|\|/g)) {
    const index = start + decision.index;
    decisions.push({ token: decision[0], line: original.slice(0, index).split('\n').length });
  }
  methods.push({ method: `${match[1]}(${match[2].replace(/\s+/g, ' ').trim()})`,
    line: original.slice(0, match.index).split('\n').length,
    decisions, complexity: 1 + decisions.length });
}
if (methods.length !== 7) throw new Error(`Esperados 7 métodos explícitos, encontrados ${methods.length}`);
process.stdout.write(JSON.stringify({
  source: 'backend/src/main/java/com/picsou/service/LoanAmortizationService.java',
  sha256: crypto.createHash('sha256').update(original).digest('hex'),
  convention: '1 + if/for/while/case/catch + ternários + operadores curtos &&/||; exclui código gerado de records e construtor implícito',
  methods, sumOfMethodComplexities: methods.reduce((sum, method) => sum + method.complexity, 0),
  maximumMethodComplexity: Math.max(...methods.map(method => method.complexity))
}, null, 2) + '\n');
