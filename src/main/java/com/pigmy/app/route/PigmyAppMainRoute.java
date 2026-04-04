package com.pigmy.app.route;

import com.pigmy.app.model.*;
import com.pigmy.app.model.response.AgentDepositResponse;
import com.pigmy.app.model.response.CreateAgentResponse;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PigmyAppMainRoute extends RouteBuilder {

    @Value("${rest.api.base.url}")
    private String restApiBaseUrl;

    @Value("${agent.resource.path}")
    private String agentPath;

    @Value("${transaction.resource.path}")
    private String transactionPath;

    @Value("${user.resource.path}")
    private String userPath;

    @Value("${admin.login.path}")
    private String adminLogin;

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.auto)
                .contextPath(restApiBaseUrl)
                .apiProperty("cors","true");


        rest(agentPath).description("creating new agent")
             .consumes("application/json").produces("application/json")
             .post()
             .responseMessage().code(200).message("successfully created agent").responseModel(CreateAgentResponse.class).endResponseMessage()
             .type(AgentNew.class)
             .outType(CreateAgentResponse.class)
             .to("direct:createNewAgent")

             .post("/deposit/singleDate")
             .description("agent depositing amount")
             .type(AgentDepositRequest.class)
             .outType(AgentDepositResponse.class)
             .to("direct:agentDeposit")

             .post("/deposit/multipleDate")
             .description("agent depositing amount")
             .type(AgentDepositRequest.class)
             .outType(AgentDepositResponse.class)
             .to("direct:agentMultipleDeposit")

             .patch()
             .type(AgentUpdateRequest.class)
             .outType(CreateAgentResponse.class)
             .to("direct:updateAgent")


             .get()
             .description("fetch agents by agentCode and bankCode or all agents")
             .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(false).endParam()
             .param().name("bankCode").type(RestParamType.query).dataType("String").required(false).endParam()
             .type(AgentNew.class)
             .outType(CreateAgentResponse.class)
             .to("direct:fetchAgents")

             .get("/pastDeposits")
             .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
             .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
             .param().name("dateRange").type(RestParamType.query).dataType("String").required(true).endParam()
             .to("direct:fetchPastDeposits")

              .get("/export")
                .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("dateRange").type(RestParamType.query).dataType("String").required(true).endParam()
                .to("direct:exportDeposits");



        rest(transactionPath)
                .consumes("application/json").produces("application/json")
                .get()
                .description("fetch transaction details based on agentCode")
                .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("date").type(RestParamType.query).dataType("LocalDate").required(true).endParam()
                .type(Transaction.class)
                .to("direct:fetchTransaction")

                .delete()
                .param().name("transactionId").type(RestParamType.query).dataType("Long").required(true).endParam()
                .description("delete transaction details based on agentCode")
                .type(Transaction.class)
                .to("direct:deleteTransaction");

        rest(userPath)
                .consumes("application/json").produces("application/json")
                .post()
                .type(UserData.class)
                .to("direct:addCustomers")


                .get()
                .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(false).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(false).endParam()
                .type(UserData.class)
                .to("direct:fetchCustomers");

        rest(adminLogin)
                .consumes("application/json").produces("application/json")
                .post()
                .type(LoginRequest.class)
                .to("direct:loginAdmin");


    }
}
