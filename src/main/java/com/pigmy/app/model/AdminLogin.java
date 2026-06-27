package com.pigmy.app.model;

import jakarta.persistence.*;
import lombok.Data;


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

}
