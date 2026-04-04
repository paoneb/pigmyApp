package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.AgentDeposit;
import com.pigmy.app.model.AgentDepositRequest;
import com.pigmy.app.model.AgentNew;
import com.pigmy.app.model.AgentUpdateRequest;
import com.pigmy.app.model.response.AgentDepositResponse;
import com.pigmy.app.model.response.CreateAgentResponse;
import com.pigmy.app.model.response.UserCollection;
import com.pigmy.app.repository.AgentDepositRepo;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.TransactionRepo;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("agentService")
public class AgentService {

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private AgentDepositRepo agentDepositRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    private final Logger LOGGER= LoggerFactory.getLogger(AgentService.class);

    public CreateAgentResponse saveAgent(@Body AgentNew agentRequestToCreate)
    {
        AgentNew createdNewAgent=  agentRepo.save(agentRequestToCreate);
        CreateAgentResponse createAgentResponse=new CreateAgentResponse();
        if(createdNewAgent.getAgentCode() != null)
        {

            createAgentResponse.setStatus("200");
            createAgentResponse.setMessage("successfully saved");
            createAgentResponse.setId(createdNewAgent.getId());
            createAgentResponse.setAgentCode(createdNewAgent.getAgentCode());
            createAgentResponse.setName(createdNewAgent.getName());
            createAgentResponse.setBankCode(createdNewAgent.getBankCode());

        }
        else
        {
            LOGGER.warn("Not able to create new agent");
        }
        return createAgentResponse;

    }

    public ResponseEntity<?> updateAgent(@Body AgentUpdateRequest updateAgent) throws Exception {
        AgentNew existingAgent = agentRepo.findById(updateAgent.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // Update only the fields you want
        LOGGER.info("updating agent with agentCode: {}, bankCode: {}",updateAgent.getAgentCode(),updateAgent.getBankCode());
        existingAgent.setName(updateAgent.getName());
        existingAgent.setAddress(updateAgent.getAddress());
        existingAgent.setPhone(updateAgent.getPhone());
        existingAgent.setEmail(updateAgent.getEmail());
        existingAgent.setLimitAmount(updateAgent.getLimitAmount());
        existingAgent.setType(updateAgent.getType());
        existingAgent.setAgentCode(updateAgent.getAgentCode());
        existingAgent.setBankCode(updateAgent.getBankCode());
        existingAgent.setPassword(updateAgent.getPassword());
        existingAgent.setStatus(updateAgent.getStatus());

        agentRepo.save(existingAgent);

        return ResponseEntity.ok(
               Map.of(
                       "status", "success",
                       "message", "Agent updated successfully"
               )
       );


    }

    public void fetchAgent(@Header("agentCode") final Integer agCode,@Header("bankCode") final String bankCode,final Exchange e)
    {
        if (agCode != null ) {

            AgentNew singleAgent=agentRepo.findByAgentCodeAndBankCode(agCode,bankCode)
                    .orElseThrow(()-> new RuntimeException("Agent not found"));
            e.getIn().setBody(singleAgent);
        }
        else {
            List<AgentNew> multipleAgents=agentRepo.findAllAgentByBankCode(bankCode);
            e.getIn().setBody(multipleAgents);
        }

    }

    public void agentDeposit(@Body AgentDepositRequest agentDepositrequest,final Exchange e)
    {
        AgentNew agent = agentRepo.findByAgentCodeAndBankCode(agentDepositrequest.getAgentCode(),agentDepositrequest.getBankCode())
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        LOGGER.info("agent deposit request received agentCode: {}, bankCode: {}",agentDepositrequest.getAgentCode(),agentDepositrequest.getBankCode());
        AgentDeposit agd=new AgentDeposit();

        agd.setAgentCode(agentDepositrequest.getAgentCode());
        agd.setBankCode(agentDepositrequest.getBankCode());
        agd.setDepositingAmount(agentDepositrequest.getDepositingAmount());
        agd.setVoucherId(agentDepositrequest.getVoucherId());
        agd.setDepositDate(LocalDate.now());
        agd.setDateOfCollectedAmount(agentDepositrequest.getDateOfCollectedAmount());
        agd.setDepositStatus("Progressing");
        agd.setAgentName(agentDepositrequest.getName());

        AgentDeposit agentDepositedOK= agentDepositRepo.save(agd);
        e.setProperty("agentDepositedSuccess",agentDepositedOK);
        LOGGER.info("updated agent deposit details",agentDepositedOK.getId());
    }

    public void agentMultipleDeposit(@Body AgentDepositRequest agentDepositrequest,final Exchange e)
    {
        AgentNew agent = agentRepo.findByAgentCodeAndBankCode(agentDepositrequest.getAgentCode(),agentDepositrequest.getBankCode())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        LOGGER.info("agent deposit multiple date request received agentCode: {}, bankCode: {}",agentDepositrequest.getAgentCode(),agentDepositrequest.getBankCode());


        AgentDeposit agd=new AgentDeposit();

        agd.setAgentCode(agentDepositrequest.getAgentCode());
        agd.setBankCode(agentDepositrequest.getBankCode());
        agd.setDepositingAmount(agentDepositrequest.getDepositingAmount());
        agd.setVoucherId(agentDepositrequest.getVoucherId());
        agd.setDepositDate(LocalDate.now());
        agd.setDateOfCollectedAmount(agentDepositrequest.getDateOfCollectedAmount());
        agd.setDepositStatus("Progressing");
        agd.setAgentName(agentDepositrequest.getName());

        AgentDeposit agentDepositedMultipleDateOK= agentDepositRepo.save(agd);
        e.setProperty("agentDepositedMultipleDateSuccess",agentDepositedMultipleDateOK);
        LOGGER.info("updated agent deposit details",agentDepositedMultipleDateOK.getId());
    }



    public void updateStatus(final Exchange e)
    {
        final AgentDeposit agentDeposit = e.getProperty("agentDepositedSuccess", AgentDeposit.class);
        agentDepositRepo.updateAgentDesositStatus(agentDeposit.getId());
    }

    public void fetchPastDeposits(@Header("agentCode") final Integer agCode,@Header("bankCode") final String bankCode, @Header("dateRange") String dateRange,final Exchange e)
    {
        String[] dates = dateRange.split(" to ");
        LocalDate start = LocalDate.parse(dates[0].trim());
        LocalDate end = LocalDate.parse(dates[1].trim());

        List<AgentDeposit> agentDeposit=agentDepositRepo.findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(agCode,bankCode,start,end);

        if(!agentDeposit.isEmpty())
        {
            List<AgentDepositResponse> agentDepositResponseList = agentDeposit.stream().map(tr -> {
                AgentDepositResponse agentDepositResponse=new AgentDepositResponse();
                agentDepositResponse.setAgentCode(tr.getAgentCode());
                agentDepositResponse.setBankCode(tr.getBankCode());
                agentDepositResponse.setDepositedDate(tr.getDepositDate().toString());
                agentDepositResponse.setTotalCollectedAmount(BigDecimal.valueOf(tr.getDepositingAmount()));
                        return agentDepositResponse;
                    })
                    .collect(Collectors.toList());

            e.getIn().setBody(agentDepositResponseList);
        }
    }



      public void fetchPastAgentDeposit(@Header("agentCode") final Integer agCode,@Header("bankCode") final String bankCode, @Header("dateRange") String dateRange,final Exchange e)
    {

        AgentDeposit agentDeposit=agentDepositRepo.findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(agCode,bankCode,LocalDate.parse(dateRange));

            e.setProperty("pastDeposit",agentDeposit);
        }
    }

