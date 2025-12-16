package com.pigmy.app.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FetchAgentsRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"An error occured while fetching agent- ${exception.message}")
                .logStackTrace(true);

        from("direct:fetchAgents")
                .routeId(FetchAgentsRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"fetch agent request: ${body}")
                .bean("agentService","fetchAgent");

    }
}
