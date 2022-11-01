package com.alkemy.wallet.model;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    private Integer transactionID;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private TransactionType type;

    @Column(name = "DESCRIPTION")
    private String description;

    // TODO: Create foreign key userId
    // --- Foreign Key userID

    // TODO: Create foreign key accountId
    // --- Foreign Key userID

    // TODO: Check timestamps format and auditing
    private Timestamp transactionDate;
}
