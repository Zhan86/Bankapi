# Bankapi
1)DB to initiate:
  CREATE TABLE user_balance (
      user_id SERIAL PRIMARY KEY,
      balance DECIMAL(10, 2) NOT NULL
  );
2)server.port=8080
3) Endpoint Post "/user/create-user" (creates user with 0 balance)
4) Endpoint Get "/{userId}/balance" (shows balance)
5) Endpoint Post "/{userId}/put-money" (puts money, increases balance)
6) Endpoint Post "/{userId}/take-money" (takes money, reduses balance)
