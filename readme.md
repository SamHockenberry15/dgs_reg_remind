# Email Instructions

## Application.properties Configuration
Add the following properties to application.properties
```
spring.mail.host=<host>
spring.mail.port=<port>
spring.mail.username=<username>
spring.mail.password=<password>
```

Host and port are specific to the email service being used.

Use the following for Gmail
```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
```