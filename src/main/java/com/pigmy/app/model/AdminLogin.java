package com.pigmy.app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@Table(name = "admin_web")
@Entity
public class AdminLogin{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private String password;
    private String bankCode;
    private String bankName;
    private boolean isMainBranch;
    private String parentId;
    private String city;
    private String bankType;
    private LocalDate purchaseDate;
    private int graceDays;
}
