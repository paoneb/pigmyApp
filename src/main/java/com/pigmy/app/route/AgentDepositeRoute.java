package com.pigmy.app.route;

import jakarta.persistence.Column;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class AgentDepositeRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "An error occured while updating agent- ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)) // or 500
                .setBody(simple("{\"error\":\"${exception.message}\"}"));


        from("direct:agentMultipleDeposit")
                .routeId("agentMultipleDepositRouteID")
                .log(LoggingLevel.INFO, "banksoft agent deposit multiple request: ${body}")
                .setProperty("AgentDepositMultipleDatesRequest", body())
                .bean("transactionService", "validateDepositingAmountMultipleDate")
                .transacted()
                .bean("agentService", "agentMultipleDeposit")
                .choice()
                .when(simple("${exchangeProperty.agentDepositedMultipleDateSuccess} != null"))
                .bean("transactionService", "agentDepositingWithMultipleDate")
                .choice()
                .when(simple("${exchangeProperty.saveTotransaction} == true"))
                .bean("agentService", "updateStatus");


        from("direct:agentMultipleDepositPeocit")
                .routeId("agentMultipleDepositPeocitRouteID")
                .log(LoggingLevel.INFO, "peocit agent deposit multiple request: ${body}")
                .setProperty("PeocitAgentDepositMultipleDatesRequest", body())
                .bean("transactionService", "validatePeocitDepositingAmountMultipleDate")
                .transacted()
                .bean("agentService", "agentMultipleDepositPeocit")
                .choice()
                .when(simple("${exchangeProperty.PeocitAgentDepositedMultipleDateSuccess} != null"))
                .bean("transactionService", "PeocitAgentDepositingWithMultipleDate")
                .choice()
                .when(simple("${exchangeProperty.saveToPeocitTransaction} == true"))
                .bean("agentService", "updatePeocitStatus");


        from("direct:exportDeposits")
                .routeId("exportDepositsRouteID")
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO, "export deposit request: ${body}")
                .bean("transactionService", "fetchPastTransaction")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO, "export peocit deposit request: ${body}")
                .bean("transactionService", "fetchPeocitPastTransaction");

    }
}
