package ru.urakovzhanat.bankapi.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private final int value;
    private final String errorMessage;

    public ErrorResponse(int value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

}
