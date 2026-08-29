package com.pigmy.app.route;


import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class CreateAgentRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "An error occured while creating agent- ${exception.message}")
                .logStackTrace(true);


        from("direct:createNewAgent")
                .routeId(CreateAgentRoute.class.getSimpleName())
                .log(LoggingLevel.INFO, "create new agent request: ${body}")
                .bean("agentService", "saveAgent");

         from("direct:revokeAgentAccess")
                .routeId("revokeAgentAccessRouteId")
                .log(LoggingLevel.INFO, "Revoke agent access request: ${body}")
                .bean("agentService", "revokeAgentAccess");

         from("direct:fetchDashboardData")
                .routeId("fetchDashboardDataRouteId")
                .log(LoggingLevel.INFO, "Fetch dashboard data request: ${body}")
                .bean("authAdminService", "fetchDashboardData");
    }

}
