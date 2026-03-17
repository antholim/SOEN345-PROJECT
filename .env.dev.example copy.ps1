# Copy this file to `.env.dev.ps1` and fill in the real values before running `.\start-dev.ps1`.

$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://your-host:6543/postgres?prepareThreshold=0'
$env:SPRING_DATASOURCE_USERNAME = 'your-database-username'
$env:SPRING_DATASOURCE_PASSWORD = 'your-database-password'
