# Copy this file to `.env.dev.ps1` and fill in the real values before running `.\start-dev.ps1`.

$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://your-host:6543/postgres?prepareThreshold=0'
$env:SPRING_DATASOURCE_USERNAME = 'your-database-username'
$env:SPRING_DATASOURCE_PASSWORD = 'your-database-password'

# Notification Provider Config (Options: logger, sendgrid, twilio)
$env:NOTIFICATION_EMAIL_PROVIDER = 'sendgrid'
$env:NOTIFICATION_SMS_PROVIDER = 'twilio'

# SendGrid Email (Required if provider is 'sendgrid')
$env:SENDGRID_API_KEY = 'SG.xxxxxxxxxxxxxxxxxxxxxx'
$env:NOTIFICATION_EMAIL_FROM = 'your-email@example.com'

# Twilio SMS (Required if provider is 'twilio')
$env:TWILIO_ACCOUNT_SID = 'ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'
$env:TWILIO_AUTH_TOKEN = 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'
$env:TWILIO_FROM_PHONE = '+1234567890'
