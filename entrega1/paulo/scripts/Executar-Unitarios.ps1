[CmdletBinding()]
param(
    [ValidateSet('Classe','Defeitos','Suite')][string]$Modo = 'Classe',
    [string]$Maven = 'mvn',
    [string]$JavaHome = $env:JAVA_HOME
)
$ErrorActionPreference = 'Stop'
$repoPath = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
if ($JavaHome) { $env:JAVA_HOME = $JavaHome }
if (-not (Get-Command $Maven -ErrorAction SilentlyContinue)) {
    $localMaven = Join-Path (Split-Path -Parent $repoPath) '.tools/apache-maven-3.9.16/bin/mvn.cmd'
    if (Test-Path -LiteralPath $localMaven) { $Maven = $localMaven }
    else { throw 'Maven não encontrado. Informe -Maven com o caminho do mvn.cmd e use Java 21.' }
}
$artifactName = switch ($Modo) { 'Classe' {'classe'} 'Defeitos' {'defeitos'} 'Suite' {'suite'} }
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$evidencePath = Join-Path $repoPath "entrega1/paulo/evidencias/unitarios/reexecucao-$artifactName-$timestamp"
New-Item -ItemType Directory -Path $evidencePath -Force | Out-Null
$mavenArguments = @('-B','-f',(Join-Path $repoPath 'backend/pom.xml'),'-Dstyle.color=never')
if ($Modo -eq 'Classe') { $mavenArguments += '-Dtest=LoanAmortizationServiceTest' }
if ($Modo -eq 'Defeitos') {
    $mavenArguments += @('-Dtest=LoanAmortizationServiceDefectTest','-Dpicsou.entrega1.defect-regressions=true')
    Write-Host 'Reprodução de defeitos conhecidos: duas falhas são esperadas até correção da produção.'
}
$mavenArguments += 'test'
& $Maven @mavenArguments *> (Join-Path $evidencePath 'maven-output.txt')
$resultCode = $LASTEXITCODE
if ($Modo -eq 'Suite') {
    $reports = Get-ChildItem (Join-Path $repoPath 'backend/target/surefire-reports/TEST-*.xml')
} else {
    $className = if ($Modo -eq 'Classe') {'LoanAmortizationServiceTest'} else {'LoanAmortizationServiceDefectTest'}
    $reports = Get-ChildItem (Join-Path $repoPath "backend/target/surefire-reports/TEST-com.picsou.service.$className.xml")
}
$reports | Copy-Item -Destination $evidencePath
@{mode=$Modo; exitCode=$resultCode; recordedAt=(Get-Date -Format o); reports=$reports.Name} |
    ConvertTo-Json | Set-Content -LiteralPath (Join-Path $evidencePath 'execucao.json') -Encoding utf8
Get-Content -LiteralPath (Join-Path $evidencePath 'maven-output.txt') -Tail 14
Write-Host "Evidências: $evidencePath"
exit $resultCode
