package com.pigmy.app.model.peocit;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pigmy.app.model.UserDetails;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PeocitUploadMobileNumberRequest {
    private String bankCode;
    private List<PeocitUserDetails> userDetailsList;

}
