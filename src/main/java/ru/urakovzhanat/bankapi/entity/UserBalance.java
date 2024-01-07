package ru.urakovzhanat.bankapi.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_balance")
public class UserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    private double balance;
}
