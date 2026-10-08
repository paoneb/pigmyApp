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
                .log(LoggingLevel.INFO, "fetch agent request ${header.bankCode}, ${header.agentCode}")
                .bean("agentService","fetchAgent")
                .removeProperties(".*")
                .removeHeaders(".*");

        from("direct:fetchPastDeposits")
                .routeId("fetchPastDepositsId")
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO,"fetch banksoft past deposits request ${header.bankCode}, ${header.agentCode}, ${header.from}, ${header.to}")
                .bean("agentService","fetchPastDeposits")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO,"fetch peocit past deposits request ${header.bankCode}, ${header.agentCode}, ${header.from}, ${header.to}")
                .bean("agentService","fetchPastDepositsPeocit")
                .when(header("bankType").isEqualTo("sledger"))
                .log(LoggingLevel.INFO,"fetch sledger past deposits request ${header.bankCode}, ${header.agentCode}, ${header.from}, ${header.to}")
                .bean("agentService","fetchPastDepositsSledger")
                .end()
                .removeProperties(".*")
                .removeHeaders(".*");

    }
}
