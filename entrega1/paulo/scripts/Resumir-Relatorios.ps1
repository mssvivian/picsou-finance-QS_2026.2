[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$Pasta)
$ErrorActionPreference = 'Stop'
$reports = @(Get-ChildItem -LiteralPath $Pasta -Filter 'TEST-*.xml' | Sort-Object Name)
if ($reports.Count -eq 0) { throw 'Não há relatórios Surefire nesta pasta.' }
$totals = @{tests=0;failures=0;errors=0;skipped=0}
$classes = @()
foreach ($file in $reports) {
    $report = [xml](Get-Content -LiteralPath $file.FullName -Raw)
    $suite = $report.testsuite
    foreach ($key in @('tests','failures','errors','skipped')) { $totals[$key] += [int]$suite.$key }
    $casesWithProblems = @($suite.testcase | Where-Object { $_.failure -or $_.error } | ForEach-Object {
        @{test=$_.name; failure=$_.failure.message; error=$_.error.message}
    })
    $skippedCases = @($suite.testcase | Where-Object { $_.skipped } | ForEach-Object {
        @{test=$_.name; reason=$_.skipped.message}
    })
    $classes += @{name=$suite.name;tests=[int]$suite.tests;failures=[int]$suite.failures;
        errors=[int]$suite.errors;skipped=[int]$suite.skipped;problems=$casesWithProblems;skippedCases=$skippedCases}
}
@{totals=$totals;classes=$classes;reportCount=$reports.Count} | ConvertTo-Json -Depth 8 |
    Set-Content -LiteralPath (Join-Path $Pasta 'resumo.json') -Encoding utf8
Compress-Archive -LiteralPath $reports.FullName -DestinationPath (Join-Path $Pasta 'surefire-reports.zip') -Force
$totals | ConvertTo-Json
