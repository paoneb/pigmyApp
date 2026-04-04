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
                .log(LoggingLevel.ERROR,"An error occured while updating agent- ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)) // or 500
                .setBody(simple("{\"error\":\"${exception.message}\"}"));


        from("direct:agentDeposit")
                .routeId(AgentDepositeRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"agent deposit request: ${body}")
                .setProperty("AgentDepositRequest",body())
                .bean("transactionService","validateDepositingAmount")
                .bean("agentService","agentDeposit")
                .choice()
                .when(simple("${exchangeProperty.agentDepositedSuccess} != null"))
                .bean("transactionService","agentDepositingWithDate")
                .choice()
                .when(simple("${exchangeProperty.saveTotransaction} == true"))
                .bean("agentService","updateStatus");


        from("direct:agentMultipleDeposit")
                .routeId("agentMultipleDepositRouteID")
                .log(LoggingLevel.INFO,"agent deposit multiple request: ${body}")
                .setProperty("AgentDepositMultipleDatesRequest",body())
                .bean("transactionService","validateDepositingAmountMultipleDate")
                .bean("agentService","agentMultipleDeposit")
                .choice()
                .when(simple("${exchangeProperty.agentDepositedMultipleDateSuccess} != null"))
                .bean("transactionService","agentDepositingWithMultipleDate")
                .choice()
                .when(simple("${exchangeProperty.saveTotransaction} == true"))
                .bean("agentService","updateStatus");


        from("direct:exportDeposits")
                .routeId("exportDepositsRouteID")
                .log(LoggingLevel.INFO,"export deposit request: ${body}")
                .bean("agentService","fetchPastAgentDeposit")
                .bean("transactionService","fetchPastTransaction");

    }
}
