package com.pigmy.app.route;

import com.pigmy.app.model.AgentNew;
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


             .patch("/{agentCode}")
             .type(AgentNew.class)
             .outType(CreateAgentResponse.class)
             .to("direct:updateAgent")


             .get()
             .description("fetch agents by agentCode or all agents")
             .param().name("agentCode").type(RestParamType.query).dataType("Integer").required(false).endParam()
             .type(AgentNew.class)
             .outType(CreateAgentResponse.class)
             .to("direct:fetchAgents");

    }
}
