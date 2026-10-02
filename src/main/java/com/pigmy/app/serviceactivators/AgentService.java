package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.AgentDeposit;
import com.pigmy.app.model.AgentDepositRequest;
import com.pigmy.app.model.AgentNew;
import com.pigmy.app.model.AgentUpdateRequest;
import com.pigmy.app.model.peocit.PeocitAgentDeposit;
import com.pigmy.app.model.response.CreateAgentResponse;
import com.pigmy.app.model.response.FetchPastDepositsResponse;
import com.pigmy.app.model.sledger.SledgerAgentDeposit;
import com.pigmy.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("agentService")
@RequiredArgsConstructor
public class AgentService {


    private final AgentRepo agentRepo;
    private final AgentDepositRepo agentDepositRepo;
    private final PeocitAgentDepositRepo peocitAgentDepositRepo;
    private final SledgerAgentDepositRepo sledgerAgentDepositRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    private final Logger LOGGER = LoggerFactory.getLogger(AgentService.class);

    public CreateAgentResponse saveAgent(@Body AgentNew agentRequestToCreate) {
        AgentNew createdNewAgent = agentRepo.save(agentRequestToCreate);
        CreateAgentResponse createAgentResponse = new CreateAgentResponse();
        if (createdNewAgent.getAgentCode() != null) {

            createAgentResponse.setStatus("200");
            createAgentResponse.setMessage("successfully saved");
            createAgentResponse.setId(createdNewAgent.getId());
            createAgentResponse.setAgentCode(createdNewAgent.getAgentCode());
            createAgentResponse.setName(createdNewAgent.getName());
            createAgentResponse.setBankCode(createdNewAgent.getBankCode());

        } else {
            LOGGER.warn("Not able to create new agent");
        }
        return createAgentResponse;

    }

    public ResponseEntity<?> updateAgent(@Body AgentUpdateRequest updateAgent) throws Exception {
        AgentNew existingAgent = agentRepo.findById(updateAgent.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // Update only the fields you want
        LOGGER.info("updating agent with agentCode: {}, bankCode: {}", updateAgent.getAgentCode(), updateAgent.getBankCode());
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
        existingAgent.setGraceDays(updateAgent.getGraceDays());

        agentRepo.save(existingAgent);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "Agent updated successfully"
                )
        );


    }

    public void fetchAgent(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, final Exchange e) {
        if (agCode != null) {

            AgentNew singleAgent = agentRepo.findByAgentCodeAndBankCode(agCode, bankCode)
                    .orElseThrow(() -> new RuntimeException("Agent not found"));
            e.getIn().setBody(singleAgent);
        } else {
            List<AgentNew> multipleAgents = agentRepo.findAllAgentByBankCode(bankCode);
            e.getIn().setBody(multipleAgents);
        }

    }

    public void agentMultipleDeposit(@Body AgentDepositRequest agentDepositrequest, final Exchange e) {
        AgentNew agent = agentRepo.findByAgentCodeAndBankCode(agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        LOGGER.info("agent deposit multiple date request received agentCode: {}, bankCode: {}", agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode());

        String collectedDate = String.format("%s to %s", agentDepositrequest.getFrom(), agentDepositrequest.getTo());

        AgentDeposit agd = new AgentDeposit();

        agd.setAgentCode(agentDepositrequest.getAgentCode());
        agd.setBankCode(agentDepositrequest.getBankCode());
        agd.setDepositingAmount(agentDepositrequest.getDepositingAmount());
        agd.setVoucherId(agentDepositrequest.getVoucherId());
        agd.setDepositDate(LocalDate.now());
        agd.setDateOfCollectedAmount(collectedDate);
        agd.setDepositStatus("Progressing");
        agd.setAgentName(agentDepositrequest.getName());

        AgentDeposit agentDepositedMultipleDateOK = agentDepositRepo.save(agd);
        e.setProperty("agentDepositedMultipleDateSuccess", agentDepositedMultipleDateOK);
        LOGGER.info("updated agent deposit details", agentDepositedMultipleDateOK.getId());
    }

    public void agentMultipleDepositPeocit(@Body AgentDepositRequest agentDepositrequest, final Exchange e) {
        AgentNew agent = agentRepo.findByAgentCodeAndBankCode(agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        LOGGER.info("peocit agent deposit multiple date request received agentCode: {}, bankCode: {}", agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode());

        String collectedDate = String.format("%s to %s", agentDepositrequest.getFrom(), agentDepositrequest.getTo());

        PeocitAgentDeposit peocitAgentDeposit = new PeocitAgentDeposit();

        peocitAgentDeposit.setAgentCode(agentDepositrequest.getAgentCode());
        peocitAgentDeposit.setBankCode(agentDepositrequest.getBankCode());
        peocitAgentDeposit.setDepositingAmount(agentDepositrequest.getDepositingAmount());
        peocitAgentDeposit.setVoucherId(agentDepositrequest.getVoucherId());
        peocitAgentDeposit.setDepositDate(LocalDate.now());
        peocitAgentDeposit.setDateOfCollectedAmount(collectedDate);
        peocitAgentDeposit.setDepositStatus("Progressing");
        peocitAgentDeposit.setAgentName(agentDepositrequest.getName());

        PeocitAgentDeposit peocitAgentDepositedMultipleDateOK = peocitAgentDepositRepo.save(peocitAgentDeposit);
        e.setProperty("PeocitAgentDepositedMultipleDateSuccess", peocitAgentDepositedMultipleDateOK);
        LOGGER.info("updated peocit agent deposit details", peocitAgentDepositedMultipleDateOK.getId());
    }


    public void agentMultipleDepositSledger(@Body AgentDepositRequest agentDepositrequest, final Exchange e) {
        AgentNew agent = agentRepo.findByAgentCodeAndBankCode(agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        LOGGER.info("sledger agent deposit multiple date request received agentCode: {}, bankCode: {}", agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode());

        String collectedDate = String.format("%s to %s", agentDepositrequest.getFrom(), agentDepositrequest.getTo());

        SledgerAgentDeposit sledgerAgentDeposit = new SledgerAgentDeposit();

        sledgerAgentDeposit.setAgentCode(agentDepositrequest.getAgentCode());
        sledgerAgentDeposit.setBankCode(agentDepositrequest.getBankCode());
        sledgerAgentDeposit.setDepositingAmount(agentDepositrequest.getDepositingAmount());
        sledgerAgentDeposit.setVoucherId(agentDepositrequest.getVoucherId());
        sledgerAgentDeposit.setDepositDate(LocalDate.now());
        sledgerAgentDeposit.setDateOfCollectedAmount(collectedDate);
        sledgerAgentDeposit.setDepositStatus("Progressing");
        sledgerAgentDeposit.setAgentName(agentDepositrequest.getName());

        SledgerAgentDeposit sledgerAgentDepositedMultipleDateOK = sledgerAgentDepositRepo.save(sledgerAgentDeposit);
        e.setProperty("SledgerAgentDepositedMultipleDateSuccess", sledgerAgentDepositedMultipleDateOK);
        LOGGER.info("updated sledger agent deposit details", sledgerAgentDepositedMultipleDateOK.getId());
    }


    public void updateStatus(final Exchange e) {
        final AgentDeposit agentDeposit = e.getProperty("agentDepositedMultipleDateSuccess", AgentDeposit.class);
        agentDepositRepo.updateAgentDesositStatus(agentDeposit.getId());
    }

    public void updatePeocitStatus(final Exchange e) {
        final PeocitAgentDeposit peocitAgentDeposit = e.getProperty("PeocitAgentDepositedMultipleDateSuccess", PeocitAgentDeposit.class);
        peocitAgentDepositRepo.updatePeocitAgentDesositStatus(peocitAgentDeposit.getId());
    }

    public void updateSledgerStatus(final Exchange e) {
        final SledgerAgentDeposit sledgerAgentDeposit = e.getProperty("SledgerAgentDepositedMultipleDateSuccess", SledgerAgentDeposit.class);
        sledgerAgentDepositRepo.updatePeocitAgentDesositStatus(sledgerAgentDeposit.getId());
    }

    public void fetchPastDeposits(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("from") String start, @Header("to") String end, final Exchange e) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<AgentDeposit> agentDeposit = agentDepositRepo.findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(agCode, bankCode, startDate, endDate);


        if (!agentDeposit.isEmpty()) {
            List<FetchPastDepositsResponse> agentDepositResponseList = agentDeposit.stream().map(tr -> {
                        FetchPastDepositsResponse agentDepositResponse = new FetchPastDepositsResponse();
                        agentDepositResponse.setDepositId(tr.getId());
                        agentDepositResponse.setAgentCode(tr.getAgentCode());
                        agentDepositResponse.setBankCode(tr.getBankCode());
                        agentDepositResponse.setDepositDate(tr.getDepositDate().toString());
                        agentDepositResponse.setTotalDepositedAmount(tr.getDepositingAmount());
                        return agentDepositResponse;
                    })
                    .collect(Collectors.toList());

            e.getIn().setBody(agentDepositResponseList);
        }
    }


    public void fetchPastDepositsPeocit(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("from") String start, @Header("to") String end, final Exchange e) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<PeocitAgentDeposit> peocitAgentDeposit = peocitAgentDepositRepo.findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(agCode, bankCode, startDate, endDate);


        if (!peocitAgentDeposit.isEmpty()) {
            List<FetchPastDepositsResponse> agentDepositResponseList = peocitAgentDeposit.stream().map(tr -> {
                        FetchPastDepositsResponse agentDepositResponse = new FetchPastDepositsResponse();
                        agentDepositResponse.setDepositId(tr.getId());
                        agentDepositResponse.setAgentCode(tr.getAgentCode());
                        agentDepositResponse.setBankCode(tr.getBankCode());
                        agentDepositResponse.setDepositDate(tr.getDepositDate().toString());
                        agentDepositResponse.setTotalDepositedAmount(tr.getDepositingAmount());
                        return agentDepositResponse;
                    })
                    .collect(Collectors.toList());

            e.getIn().setBody(agentDepositResponseList);
        }
    }

    public void fetchPastDepositsSledger(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("from") String start, @Header("to") String end, final Exchange e) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<SledgerAgentDeposit> sledgerAgentDeposit = sledgerAgentDepositRepo.findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(agCode, bankCode, startDate, endDate);

        if (!sledgerAgentDeposit.isEmpty()) {
            List<FetchPastDepositsResponse> agentDepositResponseList = sledgerAgentDeposit.stream().map(tr -> {
                        FetchPastDepositsResponse agentDepositResponse = new FetchPastDepositsResponse();
                        agentDepositResponse.setDepositId(tr.getId());
                        agentDepositResponse.setAgentCode(tr.getAgentCode());
                        agentDepositResponse.setBankCode(tr.getBankCode());
                        agentDepositResponse.setDepositDate(tr.getDepositDate().toString());
                        agentDepositResponse.setTotalDepositedAmount(tr.getDepositingAmount());
                        return agentDepositResponse;
                    })
                    .collect(Collectors.toList());

            e.getIn().setBody(agentDepositResponseList);
        }
    }


    public ResponseEntity<String> revokeAgentAccess(@Header("mobileNumber") final String mobileNumber, final Exchange e) {
        long deletedCount = refreshTokenRepo.deleteByMobileNumber(mobileNumber);

        if (deletedCount > 0) {
            LOGGER.info("Revoked access for mobile number: {}", mobileNumber);
        } else {
            LOGGER.warn("No refresh token found for mobile number: {}", mobileNumber);
            throw new RuntimeException("No refresh token found for mobile number: " + mobileNumber);
        }

        return ResponseEntity.ok("Agent access revoked successfully");
    }
}

