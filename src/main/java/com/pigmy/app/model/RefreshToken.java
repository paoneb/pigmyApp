package com.pigmy.app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The actual token string
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    // Link to the user (you can replace with your User entity relation)
    @Column(nullable = false)
    private String mobileNumber;

    // Expiry date/time
    @Column(nullable = false)
    private Instant expiryDate;

    @Column
    private String bankCode;

    @Column
    private Integer agentCode;

    @Column
    private String bankName;
}
