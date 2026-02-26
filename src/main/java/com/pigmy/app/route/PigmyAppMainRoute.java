package com.pigmy.app.route;

import com.pigmy.app.model.*;
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
                .apiProperty("cors","true")
                .enableCORS(true)
                .corsAllowCredentials(true)
                .corsHeaderProperty("Access-Control-Allow-Origin", "*")
                .corsHeaderProperty("Access-Control-Allow-Methods", "*");


        rest(agentPath).description("creating new agent")
             .consumes("application/json").produces("application/json")
             .post()
             .responseMessage().code(200).message("successfully created agent").responseModel(CreateAgentResponse.class).endResponseMessage()
             .type(AgentNew.class)
             .outType(CreateAgentResponse.class)
             .to("direct:createNewAgent")


             .patch()
             .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
              .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
             .type(AgentNew.class)
             .outType(CreateAgentResponse.class)
             .to("direct:updateAgent")


             .get()
             .description("fetch agents by agentCode and bankCode or all agents")
             .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(false).endParam()
             .param().name("bankCode").type(RestParamType.query).dataType("String").required(false).endParam()
             .type(AgentNew.class)
             .outType(CreateAgentResponse.class)
             .to("direct:fetchAgents");

        rest(transactionPath)
                .consumes("application/json").produces("application/json")
                .get()
                .description("fetch transaction details based on agentCode")
                .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
                .param().name("dateRange").type(RestParamType.query).dataType("LocalDate").required(true).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
                .type(Transaction.class)
                .to("direct:fetchTransaction")

                .post()
                .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
                .param().name("userId").type(RestParamType.query).dataType("Long").required(true).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("depositAmount").type(RestParamType.query).dataType("BigDecimal").required(true).endParam()
                .param().name("depositeDate").type(RestParamType.query).dataType("Date").required(false).endParam()
                .param().name("schemename").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("ledgergroup").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("collectiontype").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("customername").type(RestParamType.query).dataType("String").required(false).endParam()
                .description("add transaction details based on agentCode")
                .type(Transaction.class)
                .to("direct:addDeposit")

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
