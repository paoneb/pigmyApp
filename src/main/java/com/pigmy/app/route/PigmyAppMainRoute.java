package com.pigmy.app.route;

import com.pigmy.app.model.*;
import com.pigmy.app.model.response.AgentDepositResponse;
import com.pigmy.app.model.response.CreateAgentResponse;
import com.pigmy.app.model.response.SearchTransactionResponse;
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


    @Value("${dashboard.path}")
    private String dashboard;

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
             .param().name("from").type(RestParamType.query).dataType("String").required(true).endParam()
             .param().name("to").type(RestParamType.query).dataType("String").required(true).endParam()
             .to("direct:fetchPastDeposits")

             .get("/export")
                .param().name("depositId").type(RestParamType.query).dataType("long").required(true).endParam()
                .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("date").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("depositedAmount").type(RestParamType.query).dataType("double").required(true).endParam()
                .to("direct:exportDeposits")

             .delete("/revoke")
                .param().name("mobileNumber").type(RestParamType.query).dataType("String").required(true).endParam()
                .description("revoke agent access")
                .to("direct:revokeAgentAccess");

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
                .to("direct:deleteTransaction")


                .get("/search")
                //.param().name("agentCode").type(RestParamType.query).dataType("Integer").required(true).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("from").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("to").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("agent").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("schemeType").type(RestParamType.query).dataType("String").required(true).endParam()
                .param().name("collectionStatus").type(RestParamType.query).dataType("String").required(true).endParam()
                .type(SearchTransactionResponse.class)
                .to("direct:searchTransaction");

        rest(userPath)
                .consumes("application/json").produces("application/json")
                .post()
                .type(UserData.class)
                .to("direct:addCustomers")


                .get()
                .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(false).endParam()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(false).endParam()
                .type(UserData.class)
                .to("direct:fetchCustomers")


                .post("/upload/mobilenumbers")
                .type(UploadMobileNumberRequest.class)
                .to("direct:addCustomersMobileNumber")


                .patch("/updateMobileNumber")
                .param().name("userId").type(RestParamType.query).dataType("long").required(true).endParam()
                .param().name("mobilenumber").type(RestParamType.query).dataType("String").required(true).endParam()
                .to("direct:updateCustomersMobileNumber");

        rest(adminLogin)
                .consumes("application/json").produces("application/json")
                .post()
                .type(LoginRequest.class)
                .to("direct:loginAdmin");


        rest(dashboard)
                .consumes("application/json").produces("application/json")
                .get()
                .param().name("bankCode").type(RestParamType.query).dataType("String").required(true).endParam()
                .to("direct:fetchDashboardData");


    }
}
