package ru.urakovzhanat.bankapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.urakovzhanat.bankapi.dto.BalanceResponse;
import ru.urakovzhanat.bankapi.dto.ErrorResponse;
import ru.urakovzhanat.bankapi.dto.SuccessResponse;
import ru.urakovzhanat.bankapi.entity.Operation;
import ru.urakovzhanat.bankapi.entity.UserBalance;
import ru.urakovzhanat.bankapi.repository.OperationRepository;
import ru.urakovzhanat.bankapi.repository.UserBalanceRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserBalanceController {
    private final UserBalanceRepository userBalanceRepository;
    private final OperationRepository operationRepository;

    @Autowired
    public UserBalanceController(UserBalanceRepository userBalanceRepository, OperationRepository operationRepository) {
        this.userBalanceRepository = userBalanceRepository;
        this.operationRepository = operationRepository;
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable long userId) {
        try {
            UserBalance userBalance = userBalanceRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok().body(new BalanceResponse(userBalance.getBalance()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to retrieve balance."));
        }
    }

    @PostMapping("/{userId}/put-money")
    public ResponseEntity<?> putMoney(@PathVariable long userId, @RequestParam double amount) {
        try {
            UserBalance userBalance = userBalanceRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            double newBalance = userBalance.getBalance() + amount;
            userBalance.setBalance(newBalance);

            userBalanceRepository.save(userBalance);

            // Создаем объект Operation
            Operation operation = new Operation();
            operation.setUserId(userId);
            operation.setOperationType(1);
            operation.setAmount(amount);
            operation.setOperationDate(new Date());

            // Сохраняем операцию
            operationRepository.save(operation);


            return ResponseEntity.ok().body(new SuccessResponse(1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to add money."));
        }
    }

    @PostMapping("/{userId}/take-money")
    public ResponseEntity<?> takeMoney(@PathVariable long userId, @RequestParam double amount) {
        try {
            UserBalance userBalance = userBalanceRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (userBalance.getBalance() < amount) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(0, "Insufficient funds"));
            }

            userBalance.setBalance(userBalance.getBalance() - amount);
            userBalanceRepository.save(userBalance);

            Operation operation = new Operation();
            operation.setUserId(userId);
            operation.setOperationType(2);
            operation.setAmount(amount);
            operation.setOperationDate(new Date());

            operationRepository.save(operation);

            return ResponseEntity.ok().body(new SuccessResponse(1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to take money."));
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser() {
        try {
            UserBalance newUser = new UserBalance();
            newUser.setBalance(0);
            UserBalance savedUser = userBalanceRepository.save(newUser);
            return ResponseEntity.ok().body(new SuccessResponse(Math.toIntExact(savedUser.getUserId())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to create user."));
        }
    }

    @PostMapping("/transfer-money")
    public ResponseEntity<?> transferMoney(@RequestParam long senderUserId,
                                           @RequestParam long receiverUserId,
                                           @RequestParam double amount) {
        try {
            UserBalance senderUser = userBalanceRepository.findById(senderUserId)
                    .orElseThrow(() -> new RuntimeException("Sender user not found"));

            UserBalance receiverUser = userBalanceRepository.findById(receiverUserId)
                    .orElseThrow(() -> new RuntimeException("Receiver user not found"));

            if (senderUser.getBalance() < amount) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(0, "Insufficient funds for the transfer"));
            }

            // Вычитаем сумму у отправителя
            senderUser.setBalance(senderUser.getBalance() - amount);
            userBalanceRepository.save(senderUser);

            // Прибавляем сумму получателю
            receiverUser.setBalance(receiverUser.getBalance() + amount);
            userBalanceRepository.save(receiverUser);

            Operation transferOperation = new Operation();
            transferOperation.setUserId(senderUserId);
            transferOperation.setOperationType(3);
            transferOperation.setAmount(amount);
            transferOperation.setOperationDate(new Date());

            // Добавляем операцию в таблицу операций
            operationRepository.save(transferOperation);

            // Добавляем операцию для получателя в таблицу операций
            Operation receiverOperation = new Operation();
            receiverOperation.setUserId(receiverUserId);
            receiverOperation.setOperationType(4);
            receiverOperation.setAmount(amount);
            receiverOperation.setOperationDate(new Date());
            operationRepository.save(receiverOperation);

            return ResponseEntity.ok().body(new SuccessResponse(1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to transfer money."));
        }
    }

    @GetMapping("/{userId}/get-operation-list")
    @Transactional
    public ResponseEntity<?> getOperationList(@PathVariable Long userId,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            UserBalance userBalance = userBalanceRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Operation> operations;

            if (startDate != null && endDate != null) {
                // Получаем список операций в заданном диапазоне дат
                operations = operationRepository.findByUserIdAndOperationDateBetween(userId, startDate, endDate);
            } else {
                // Получаем все операции пользователя
                operations = operationRepository.findByUserId(userId);
            }

            List<String> operationStrings = operations.stream()
                    .map(operation -> formatOperationString(operation, userId))
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(operationStrings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to get operation list."));
        }
    }

    private String formatOperationString(Operation operation, Long userId) {
        String operationType;
        if (operation.getOperationType() == 1) {
            operationType = "Deposit";
        } else if (operation.getOperationType() == 2) {
            operationType = "Withdrawal";
        } else if (operation.getOperationType() == 3) {
            if (operation.getUserId() == userId) {
                operationType = "Transfer to Another User";
            } else {
                operationType = "Transfer from Another User";
            }
        } else if (operation.getOperationType() == 4) {
            if (operation.getUserId() == userId) {
                operationType = "Transfer from Another User";
            } else {
                operationType = "Transfer to Another User";
            }
        } else {
            operationType = "Unknown";
        }

        return String.format("Date: %s, Type: %s, Amount: %s",
                operation.getOperationDate(),
                operationType,
                operation.getAmount());
    }


}
