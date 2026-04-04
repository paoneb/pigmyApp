package com.pigmy.app.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Table(name = "adminWeb")
@Entity
public class AdminLogin{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private String password;
    private String bankCode;
    private String bankName;

}
