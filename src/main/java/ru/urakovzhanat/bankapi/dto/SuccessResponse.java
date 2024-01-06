package ru.urakovzhanat.bankapi.dto;

import lombok.Data;

@Data
public class SuccessResponse {
    private final int value;

    public SuccessResponse(int value) {
        this.value = value;
    }

}