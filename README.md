# Bankapi
1)DB to initiate:
  CREATE TABLE user_balance (
      user_id SERIAL PRIMARY KEY,
      balance DECIMAL(10, 2) NOT NULL
  );

  CREATE TABLE operations (
    operation_id SERIAL PRIMARY KEY,
    user_id BIGINT,
    operation_type INT,
    amount DOUBLE PRECISION,
    operation_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_balance(user_id)
);

2)server.port=8080
3) Endpoint Post "/user/create-user" (creates user with 0 balance)
4) Endpoint Get "user/{userId}/balance" (shows balance)
5) Endpoint Post "user/{userId}/put-money" (puts money, increases balance)
6) Endpoint Post "user/{userId}/take-money" (takes money, reduses balance)
7) Endpoint Get "user/{userId}/get-operation-list") (shows operations. for example http://localhost:8080/user/1/get-operation-list?startDate=2024-01-01&endDate=2024-01-06)
8) Endpoint Post "user/transfer-money") (transfers money. for example http://localhost:8080/user/transfer-money?senderUserId=2&receiverUserId=1&amount=1000)
