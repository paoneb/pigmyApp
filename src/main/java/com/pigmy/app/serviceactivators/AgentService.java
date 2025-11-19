package com.pigmy.app.serviceactivators;



import com.pigmy.app.model.AgentNew;
import com.pigmy.app.model.response.CreateAgentResponse;
import com.pigmy.app.repository.AgentRepo;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("agentService")
public class AgentService {

    @Autowired
    private AgentRepo agentRepo;

    public CreateAgentResponse saveAgent(@Body AgentNew ag)
    {
        AgentNew created=  agentRepo.save(ag);
        CreateAgentResponse createAgentResponse=new CreateAgentResponse();
        if(created.getAgentCode() != null)
        {

            createAgentResponse.setStatus("200");
            createAgentResponse.setMessage("successfully saved");
            createAgentResponse.setAgentCode(created.getAgentCode());
            createAgentResponse.setName(created.getName());
            createAgentResponse.setBankCode(created.getBankCode());

        }
        return createAgentResponse;

    }

    public ResponseEntity<?> updateAgent(@Body AgentNew updateAgent, @Header("agentCode") final Integer agCode) throws Exception {
        AgentNew existingAgent=agentRepo.findById(agCode).orElseThrow(()-> new RuntimeException("Agent not found"));

        BeanUtils.copyProperties(updateAgent,existingAgent,"agentCode");

        AgentNew updated=agentRepo.save(existingAgent);

        return ResponseEntity.ok(
               Map.of(
                       "status", "success",
                       "message", "Agent updated successfully"
               )
       );


    }

    public void fetchAgent(@Header("agentCode") final Integer agCode,final Exchange e)
    {
        if (agCode != null ) {

            AgentNew singleAgent=agentRepo.findById(agCode).orElseThrow(()-> new RuntimeException("Agent not found"));
            e.getIn().setBody(singleAgent);
        }
        else {
            List<AgentNew> multipleAgents=agentRepo.findAll();
            e.getIn().setBody(multipleAgents);
        }

    }

}
