package ru.urakovzhanat.bankapi.dto;

import lombok.Data;


@Data
public class BalanceResponse {
    private final double balance;

    public BalanceResponse(double balance) {
        this.balance = balance;
    }

}

