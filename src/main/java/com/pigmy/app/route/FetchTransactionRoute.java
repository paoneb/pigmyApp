package com.pigmy.app.route;

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
                .logStackTrace(true);


        from("direct:fetchTransaction")
                .routeId(FetchTransactionRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"fetch transaction request: ${body}")
                .bean("transactionService","fetchTransaction");
    }
}
