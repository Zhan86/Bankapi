package ru.urakovzhanat.bankapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urakovzhanat.bankapi.dto.BalanceResponse;
import ru.urakovzhanat.bankapi.dto.ErrorResponse;
import ru.urakovzhanat.bankapi.dto.SuccessResponse;
import ru.urakovzhanat.bankapi.entity.UserBalance;
import ru.urakovzhanat.bankapi.repository.UserBalanceRepository;

@RestController
@RequestMapping("/user")
public class UserBalanceController {
    private final UserBalanceRepository userBalanceRepository;

    @Autowired
    public UserBalanceController(UserBalanceRepository userBalanceRepository) {
        this.userBalanceRepository = userBalanceRepository;
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        try {
            UserBalance userBalance = userBalanceRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok().body(new BalanceResponse(userBalance.getBalance()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to retrieve balance."));
        }
    }

    @PostMapping("/{userId}/put-money")
    public ResponseEntity<?> putMoney(@PathVariable Long userId, @RequestParam double amount) {
        try {
            UserBalance userBalance = userBalanceRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            userBalance.setBalance(userBalance.getBalance() + amount);
            userBalanceRepository.save(userBalance);
            return ResponseEntity.ok().body(new SuccessResponse(1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(0, "Failed to add money."));
        }
    }

    @PostMapping("/{userId}/take-money")
    public ResponseEntity<?> takeMoney(@PathVariable Long userId, @RequestParam double amount) {
        try {
            UserBalance userBalance = userBalanceRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (userBalance.getBalance() < amount) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(0, "Insufficient funds"));
            }

            userBalance.setBalance(userBalance.getBalance() - amount);
            userBalanceRepository.save(userBalance);

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
}
