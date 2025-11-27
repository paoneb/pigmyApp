package com.pigmy.app.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class AddDepositeRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"An error occured while adding transaction- ${exception.message}")
                .logStackTrace(true);

        from("direct:addDeposit")
                .routeId(AddDepositeRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"Add Deposit request: ${body}")
                .bean("transactionService","addDeposit");
    }
}
