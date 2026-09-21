package com.pigmy.app.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class DeleteTransactionRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"An error occured while delete transaction- ${exception.message}")
                .logStackTrace(true);

        from("direct:deleteTransaction")
                .routeId(DeleteTransactionRoute.class.getSimpleName())
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO,"delete banksoft transaction request: ${body}")
                .bean("transactionService","deleteTransaction")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO,"delete peocit transaction request: ${body}")
                .bean("transactionService","deleteTransactionPeocit");
    }
}
