package ru.urakovzhanat.bankapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "operations")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private long operationId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "operation_type")
    private int operationType;

    @Column(name = "amount")
    private double amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "operation_date")
    private Date operationDate;


}
