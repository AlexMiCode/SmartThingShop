$ErrorActionPreference = "Stop"

Write-Host "Checking gateway health..."
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" | Out-Null

Write-Host "Checking product list through gateway..."
$products = Invoke-RestMethod -Uri "http://localhost:8080/api/products"
if ($products.Count -lt 1) { throw "Products list is empty" }

Write-Host "Creating user through gateway..."
$userBody = @{ fullName = "Smoke User"; email = "smoke.user@mail.com"; role = "CUSTOMER" } | ConvertTo-Json
$user = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/users" -Body $userBody -ContentType "application/json"

Write-Host "Creating order through gateway..."
$orderBody = @{ userId = $user.id; productId = $products[0].id; quantity = 1 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/orders" -Body $orderBody -ContentType "application/json" | Out-Null

Write-Host "Checking notifications through gateway..."
$notifications = Invoke-RestMethod -Uri "http://localhost:8080/api/notifications"
if ($notifications.Count -lt 1) { throw "Notifications not created" }

Write-Host "Smoke test passed"
