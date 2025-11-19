package com.pigmy.app.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class UpdateAgentRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"An error occured while updating agent- ${exception.message}")
                .logStackTrace(true);

        from("direct:updateAgent")
                .routeId(UpdateAgentRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"update agent request: ${body}")
                .bean("agentService","updateAgent");

    }
}
