package com.pigmy.app.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@Table(name="customers_details")
public class UploadMobileNumber {

    @Id
    @GeneratedValue(generator = "customerdetails_seq")
    @GenericGenerator(
            name = "customerdetails_seq",
            type = org.hibernate.id.enhanced.SequenceStyleGenerator.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "customerdetails_seq"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "100"), // match batch size
                    @org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo")
            }
    )
    private Long id;

    @Column(name = "bank_code")
    private String bankCode;

    private String mobilenumber;

    @Column(name = "account_number")
    private String accountNumber;

}
