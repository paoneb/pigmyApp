package com.pigmy.app.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@Table(name="customers_details")
public class UploadMobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customerdetails_seq")
    @SequenceGenerator(
            name = "customerdetails_seq",
            sequenceName = "customerdetails_seq",
            allocationSize = 100 // must match batch_size
    )
    private Long id;

    @Column(name = "bank_code")
    private String bankCode;

    private String mobilenumber;

    @Column(name = "account_number")
    private Integer accountNumber;

}
