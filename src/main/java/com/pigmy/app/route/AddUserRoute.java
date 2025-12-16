package com.pigmy.app.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class AddUserRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .log(LoggingLevel.ERROR, "An error occurred while adding users - ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("${exception.message}"));




        from("direct:addCustomers")
                .routeId(AddUserRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"Add User request: ${body}")
                .bean("addUserService","saveUsers");

        from("direct:fetchCustomers")
                .routeId("fetchCustomersRouteId")
                .log(LoggingLevel.INFO,"fetch User request: ${body}")
                .bean("addUserService","fetchCustomers");

    }
}
