# PowerShell script to run ET2FA on multiple DAX workflows
# Collects results for performance reporting

Write-Host "=== ET2FA Batch Testing ===" -ForegroundColor Cyan
Write-Host ""

# Create results directory
if (-not (Test-Path "results")) {
    New-Item -ItemType Directory -Path "results" | Out-Null
}

# Get all DAX files
$daxFiles = Get-ChildItem -Path "workflows\benchmark" -Recurse -Filter "*.dax" -ErrorAction SilentlyContinue

if ($daxFiles.Count -eq 0) {
    Write-Host "ERROR: No DAX files found in workflows\benchmark\" -ForegroundColor Red
    Write-Host "Please run download-workflows.ps1 first" -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
    exit 1
}

Write-Host "Found $($daxFiles.Count) DAX file(s)" -ForegroundColor Green
Write-Host ""

# Deadlines for different workflow sizes (in seconds) - Updated after testing
$deadlines = @{
    "Small" = 3000   # For workflows with < 50 tasks
    "Medium" = 5000  # For workflows with 50-100 tasks
    "Large" = 10000  # For workflows with 500 tasks (replaced 1000 tasks for faster execution)
}

# Results storage
$results = @()

Write-Host "Starting batch tests..." -ForegroundColor Yellow
Write-Host ""

$total = $daxFiles.Count
$current = 0

foreach ($daxFile in $daxFiles) {
    $current++
    $workflowName = $daxFile.BaseName
    $workflowPath = $daxFile.FullName.Replace('\', '/')
    
    # Determine deadline based on workflow name
    $deadline = $deadlines["Medium"]
    if ($workflowName -match "_\d+") {
        $taskCount = [int]($workflowName -replace '.*_(\d+)\.dax', '$1')
        if ($taskCount -lt 50) {
            $deadline = $deadlines["Small"]
        } elseif ($taskCount -ge 500) {
            $deadline = $deadlines["Large"]
        }
        # Special handling for Epigenomics (needs higher deadline)
        if ($workflowName -match "Epigenomics") {
            if ($taskCount -lt 50) {
                $deadline = 5000
            } elseif ($taskCount -lt 100) {
                $deadline = 6000
            } elseif ($taskCount -ge 500) {
                $deadline = 12000
            }
        }
    }
    
    Write-Host "[$current/$total] Testing: $workflowName (deadline: ${deadline}s)..." -ForegroundColor Cyan
    
    # Run ET2FA and capture output
    $outputFile = "results\$workflowName.txt"
    $errorFile = "results\$workflowName.error.txt"
    
    try {
        # Compile first
        Write-Host "  Compiling..." -NoNewline -ForegroundColor Gray
        $compileOutput = mvn clean compile -q 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Host " FAILED" -ForegroundColor Red
            $results += [PSCustomObject]@{
                Workflow = $workflowName
                Status = "Compile Failed"
                Cost = $null
                IdleRate = $null
                MeetsDeadline = $null
                Time = $null
            }
            continue
        }
        Write-Host " OK" -ForegroundColor Green
        
        # Run ET2FA
        Write-Host "  Running ET2FA..." -NoNewline -ForegroundColor Gray
        $startTime = Get-Date
        $runOutput = mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=$workflowPath --deadline=$deadline" 2>&1 | Out-String
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds
        
        # Save output
        $runOutput | Out-File -FilePath $outputFile -Encoding UTF8
        
        # Parse results from output
        $cost = $null
        $idleRate = $null
        $meetsDeadline = $null
        
        if ($runOutput -match "Total Cost:\s*\$?([\d.]+)") {
            $cost = [double]$matches[1]
        }
        if ($runOutput -match "Total Idle Rate:\s*([\d.]+)") {
            $idleRate = [double]$matches[1]
        }
        if ($runOutput -match "Meets Deadline:\s*(Yes|No)") {
            $meetsDeadline = $matches[1] -eq "Yes"
        }
        
        if ($cost -ne $null -or $idleRate -ne $null) {
            Write-Host " OK" -ForegroundColor Green
            Write-Host "    Cost: `$$cost, Idle Rate: $idleRate, Meets Deadline: $meetsDeadline" -ForegroundColor Gray
        } else {
            Write-Host " WARNING (results not parsed)" -ForegroundColor Yellow
        }
        
        $results += [PSCustomObject]@{
            Workflow = $workflowName
            Status = "Success"
            Cost = $cost
            IdleRate = $idleRate
            MeetsDeadline = $meetsDeadline
            Time = [math]::Round($duration, 2)
            Deadline = $deadline
        }
        
    } catch {
        Write-Host " ERROR" -ForegroundColor Red
        $_.Exception.Message | Out-File -FilePath $errorFile -Encoding UTF8
        $results += [PSCustomObject]@{
            Workflow = $workflowName
            Status = "Error: $($_.Exception.Message)"
            Cost = $null
            IdleRate = $null
            MeetsDeadline = $null
            Time = $null
        }
    }
    
    Write-Host ""
}

# Export results to CSV
$csvFile = "results\batch-test-results.csv"
$results | Export-Csv -Path $csvFile -NoTypeInformation -Encoding UTF8

Write-Host "=== Batch Test Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Results saved to: $csvFile" -ForegroundColor Cyan
Write-Host ""

# Summary
$successCount = ($results | Where-Object { $_.Status -eq "Success" }).Count
$failedCount = $total - $successCount

Write-Host "Summary:" -ForegroundColor Yellow
Write-Host "  Total: $total workflows" -ForegroundColor White
Write-Host "  Success: $successCount" -ForegroundColor Green
Write-Host "  Failed: $failedCount" -ForegroundColor Red
Write-Host ""

if ($successCount -gt 0) {
    $avgCost = ($results | Where-Object { $_.Cost -ne $null } | Measure-Object -Property Cost -Average).Average
    $avgIdleRate = ($results | Where-Object { $_.IdleRate -ne $null } | Measure-Object -Property IdleRate -Average).Average
    $avgTime = ($results | Where-Object { $_.Time -ne $null } | Measure-Object -Property Time -Average).Average
    
    Write-Host "Average Metrics:" -ForegroundColor Yellow
    Write-Host "  Cost: `$$([math]::Round($avgCost, 4))" -ForegroundColor White
    Write-Host "  Idle Rate: $([math]::Round($avgIdleRate, 4))" -ForegroundColor White
    Write-Host "  Running Time: $([math]::Round($avgTime, 2))s" -ForegroundColor White
}

Write-Host ""
Read-Host "Press Enter to continue"

