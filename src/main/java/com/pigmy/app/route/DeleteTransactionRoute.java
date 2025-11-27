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
                .log(LoggingLevel.INFO,"delete User request: ${body}")
                .bean("transactionService","deleteTransaction");
    }
}
