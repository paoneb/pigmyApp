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
                .log(LoggingLevel.ERROR, "An error occured while fetching transaction- ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)) // or 500
                .setBody(simple("{\"error\":\"${exception.message}\"}"));


        from("direct:fetchTransaction")
                .routeId(FetchTransactionRoute.class.getSimpleName())
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO, "fetch banksoft transaction request ${header.bankCode}, ${header.agentCode}")
                .bean("transactionService", "fetchTransaction")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO, "fetch peocit transaction request ${header.bankCode}, ${header.agentCode}")
                .bean("transactionService", "fetchTransactionPeocit")
                .when(header("bankType").isEqualTo("sledger"))
                .log(LoggingLevel.INFO, "fetch sledger transaction request ${header.bankCode}, ${header.agentCode}")
                .bean("transactionService", "fetchTransactionSledger")
                .end()
                .removeProperties(".*")
                .removeHeaders(".*");

        from("direct:searchTransaction")
                .routeId("searchTransactionRouteId")
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO, "search banksoft transaction request ${header.bankCode}, ${header.bankType}")
                .bean("transactionService", "searchTransaction")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO, "search peocit transaction request ${header.bankCode}, ${header.bankType}")
                .bean("transactionService", "searchTransactionPeocit")
                .when(header("bankType").isEqualTo("sledger"))
                .log(LoggingLevel.INFO, "search sledger transaction request ${header.bankCode}, ${header.bankType}")
                .bean("transactionService", "searchTransactionSledger")
                .end()
                .removeProperties(".*")
                .removeHeaders(".*");
    }
}
