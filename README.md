# messanger-system


## Run Spring Boot application
```
mvn clean install
mvn spring-boot:run
```

## Run following SQL insert statements
```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

## Postman screenshots

![](Screenshot 2023-08-20 at 19.02.12.png)
![](Screenshot 2023-08-20 at 19.02.22.png)
![](Screenshot 2023-08-20 at 19.02.25.png)
![](Screenshot 2023-08-20 at 19.02.27.png)
![](Screenshot 2023-08-20 at 19.02.29.png)
![](Screenshot 2023-08-20 at 19.02.32.png)

## CURLS for the same

### /send/text/user
```
curl --location --request POST 'localhost:8080/api/auth/create/user' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "sanky2",
    "password": "test@123",
    "role": [
        "ROLE_USER",
        "ROLE_ADMIN"
    ]
}'
```

###/login
```aidl
curl --location --request POST 'localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "sanky2",
    "password": "test@123"
}'
```

###/get/user
```aidl
curl --location --request GET 'localhost:8080/api/messenger/get/users' \
--header 'Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW5reTIiLCJpYXQiOjE2OTI1MjgwMTEsImV4cCI6MTY5MjYxNDQxMX0.6GeQq_Php48Skts8jk3h8wng7fTwfngC4I4tdsTjm_U'
```

###/get/history/{chatId}
```aidl
curl --location --request GET 'localhost:8080/api/messenger/get/history/1/sanky1' \
--header 'Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW5reTIiLCJpYXQiOjE2OTI1MjgwMTEsImV4cCI6MTY5MjYxNDQxMX0.6GeQq_Php48Skts8jk3h8wng7fTwfngC4I4tdsTjm_U'
```

###/get/unread/{userName}
```aidl
curl --location --request GET 'localhost:8080/api/messenger/get/unread/sanky1' \
--header 'Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW5reTIiLCJpYXQiOjE2OTI1MjgwMTEsImV4cCI6MTY5MjYxNDQxMX0.6GeQq_Php48Skts8jk3h8wng7fTwfngC4I4tdsTjm_U'
```

###/send/text/user
```aidl
curl --location --request POST 'localhost:8080/api/messenger/send/text/user' \
--header 'Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW5reTIiLCJpYXQiOjE2OTI1MjgwMTEsImV4cCI6MTY5MjYxNDQxMX0.6GeQq_Php48Skts8jk3h8wng7fTwfngC4I4tdsTjm_U' \
--header 'Content-Type: application/json' \
--data-raw '{
  "fromUserName": "sanky",
  "toUserName": "sanky1",
  "chatId": 1,
  "text": "Four message"
}'
```

