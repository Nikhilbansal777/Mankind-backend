# Verify compile + unit tests for mankind_services (requires JDK 17+ on PATH)
$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Error "Maven not found. Install JDK 17+ and Maven, or run: docker compose up --build"
}

Write-Host "Running tests..."
mvn clean test -pl mankind_services -DskipITs
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Packaging jar..."
mvn package -pl mankind_services -DskipTests
exit $LASTEXITCODE
