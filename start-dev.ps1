param(
    [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

$repoRoot = $PSScriptRoot
$backendDir = Join-Path $repoRoot 'backend'
$frontendDir = Join-Path $repoRoot 'frontend'
$envScript = Join-Path $repoRoot '.env.dev.ps1'

if (Test-Path $envScript) {
    . $envScript
}

$requiredEnvVars = @(
    'SPRING_DATASOURCE_URL',
    'SPRING_DATASOURCE_USERNAME',
    'SPRING_DATASOURCE_PASSWORD'
)

$missingEnvVars = $requiredEnvVars | Where-Object {
    -not (Get-Item -Path "Env:$_" -ErrorAction SilentlyContinue)
}

if ($missingEnvVars.Count -gt 0) {
    throw @"
Missing required environment variables: $($missingEnvVars -join ', ')

Create `.env.dev.ps1` from `.env.dev.example.ps1`, then run this script again:
    Copy-Item .env.dev.example.ps1 .env.dev.ps1
"@
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'Java is required but was not found on PATH.'
}

if (-not (Get-Command 'npm.cmd' -ErrorAction SilentlyContinue)) {
    throw 'Node.js and npm are required but were not found on PATH.'
}

$backendCommand = @"
Set-Location '$backendDir'
`$Host.UI.RawUI.WindowTitle = 'SOEN345 Backend'
`$env:GRADLE_USER_HOME = Join-Path (Get-Location) '.gradle-user-home'
.\gradlew.bat bootRun
"@

$frontendCommand = @"
Set-Location '$frontendDir'
`$Host.UI.RawUI.WindowTitle = 'SOEN345 Frontend'
if (-not (Test-Path 'node_modules')) {
    npm.cmd install
}
npm.cmd run dev -- --host 127.0.0.1 --port 5173
"@

if ($DryRun) {
    Write-Host 'Backend command:'
    Write-Host $backendCommand
    Write-Host ''
    Write-Host 'Frontend command:'
    Write-Host $frontendCommand
    exit 0
}

$processArgs = @('-NoExit', '-ExecutionPolicy', 'Bypass', '-Command')

Start-Process powershell.exe -ArgumentList ($processArgs + $backendCommand) | Out-Null
Start-Process powershell.exe -ArgumentList ($processArgs + $frontendCommand) | Out-Null

Write-Host 'Started backend and frontend in separate PowerShell windows.'
Write-Host 'Frontend: http://127.0.0.1:5173'
Write-Host 'Backend:  http://127.0.0.1:8081'
