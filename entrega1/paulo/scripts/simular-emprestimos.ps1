param(
    [string]$BaseUrl = 'http://localhost:8082',
    [string]$CredentialsPath = '.local/entrega1/picsou-teste-credenciais.json'
)
$ErrorActionPreference = 'Stop'
$credentials = Get-Content -LiteralPath $CredentialsPath -Raw | ConvertFrom-Json
Invoke-RestMethod "$BaseUrl/api/auth/login" -Method Post -ContentType application/json -Body ($credentials | ConvertTo-Json) -SessionVariable session | Out-Null
function Api($path, $method = 'Get', $body = $null) {
    $args = @{Uri="$BaseUrl/api/$path";Method=$method;WebSession=$session}
    if ($null -ne $body) { $args.ContentType='application/json'; $args.Body=$body | ConvertTo-Json -Depth 8 }
    Invoke-RestMethod @args
}
function Check($actual, $expected, $label) {
    if ($actual -ne $expected) { throw "$label esperado=$expected observado=$actual" }
}
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$debt = @{borrowedAmount=1200;interestRate=0;monthlyPayment=$null;insuranceMonthly=0;fileFees=0;startDate='2026-11-01';endDate='2027-11-01'}
$a = Api 'accounts' Post @{name="Simulacao Paulo A $stamp";type='LOAN';currency='EUR';currentBalance=1200;isManual=$true;color='#336699'}
Api "accounts/$($a.id)/debt" Put $debt | Out-Null
$r1 = Api "accounts/$($a.id)/loan-summary"
Check $r1.summary.monthlyPayment 100 'Parcela CT-PCV-01'
Check $r1.summary.totalCost 1200 'Custo CT-PCV-01'
Check $r1.summary.remainingBalance 1200 'Saldo atual CT-PCV-01'
Check $r1.summary.paidInstallments 0 'Parcelas pagas CT-PCV-01'
Check $r1.schedule.Count 12 'Quantidade de parcelas CT-PCV-01'
foreach ($item in $r1.schedule) { Check $item.capital 100 'Capital por parcela CT-PCV-01'; Check $item.totalPayment 100 'Valor por parcela CT-PCV-01' }
Check $r1.schedule[-1].remainingBalance 0 'Saldo final CT-PCV-01'
$b = Api 'accounts' Post @{name="Simulacao Paulo B $stamp";type='LOAN';currency='EUR';currentBalance=1200;isManual=$true;color='#663399'}
$debt.monthlyPayment=110; $debt.insuranceMonthly=10; $debt.fileFees=50
Api "accounts/$($b.id)/debt" Put $debt | Out-Null
$r2 = Api "accounts/$($b.id)/loan-summary"
Check $r2.summary.monthlyPayment 110 'Parcela CT-PCV-02'
Check $r2.summary.monthlyCapital 100 'Capital CT-PCV-02'
Check $r2.summary.totalInsuranceCost 120 'Seguro CT-PCV-02'
Check $r2.summary.totalCost 1370 'Custo CT-PCV-02'
Check $r2.schedule[-1].remainingBalance 0 'Saldo final CT-PCV-02'
$debt.borrowedAmount=2400; $debt.monthlyPayment=$null; $debt.insuranceMonthly=0; $debt.fileFees=0
Api "accounts/$($a.id)/debt" Put $debt | Out-Null
$r3 = Api "accounts/$($a.id)/loan-summary"
Check $r3.summary.monthlyPayment 200 'Parcela CT-PCV-03'
Check $r3.summary.totalCost 2400 'Custo CT-PCV-03'
Check $r3.summary.remainingBalance 2400 'Saldo atual CT-PCV-03'
$persisted = (Api "accounts/$($a.id)").debt
Check $persisted.borrowedAmount 2400 'Principal persistido CT-PCV-03'
Check $persisted.monthlyPayment $null 'Parcela automatica persistida CT-PCV-03'
$reloaded = Api "accounts/$($a.id)/loan-summary"
Check $reloaded.summary.monthlyPayment 200 'Reconsulta CT-PCV-03'
$output = Join-Path $PSScriptRoot '../evidencias/simulacao-api'
New-Item -ItemType Directory -Force -Path $output | Out-Null
@{type='Simulacao automatizada via HTTP; nao comprova execucao manual da interface';time=(Get-Date).ToString('o');baseUrl=$BaseUrl;commit='58c71b93544c27ff6f9b032dfe2c4df69fe01aa0';image='picsou-paulo:entrega1-58c71b9-lf';runtimeAdaptation='entrypoint CRLF normalizado para LF';accounts=@($a.id,$b.id);case1=$r1;case2=$r2;case3=$r3;persistedDebt=$persisted;result='Todas as verificacoes da simulacao passaram'} | ConvertTo-Json -Depth 12 | Set-Content (Join-Path $output 'resultado.json') -Encoding utf8
Write-Output "Simulacao API aprovada: 3 cenarios. Contas $($a.id) e $($b.id). Interface manual ainda nao executada."
