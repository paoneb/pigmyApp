package com.pigmy.app.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class FetchTransactionRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"An error occured while fetching transaction- ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)) // or 500
                .setBody(simple("{\"error\":\"${exception.message}\"}"));


        from("direct:fetchTransaction")
                .routeId(FetchTransactionRoute.class.getSimpleName())
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO,"fetch banksoft transaction request: ${body}")
                .bean("transactionService","fetchTransaction")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO,"fetch peocit transaction request: ${body}")
                .bean("transactionService","fetchTransactionPeocit");

        from("direct:searchTransaction")
                .routeId("searchTransactionRouteId")
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO,"search banksoft transaction request: ${body}")
                .bean("transactionService","searchTransaction")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO,"search peocit transaction request: ${body}")
                .bean("transactionService","searchTransactionPeocit");
    }
}
