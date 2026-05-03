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
                .handled(true)
                .log(LoggingLevel.ERROR,"An error occured while add/fetch/upload user- ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)) // or 500
                .setBody(simple("{\"error\":\"${exception.message}\"}"));




        from("direct:addCustomers")
                .routeId(AddUserRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"Add User request: ${body}")
                .bean("addUserService","saveUsers");

        from("direct:fetchCustomers")
                .routeId("fetchCustomersRouteId")
                .log(LoggingLevel.INFO,"fetch User request: ${body}")
                .bean("addUserService","fetchCustomers");

        from("direct:addCustomersMobileNumber")
                .routeId("addCustomersMobileNumberRouteId")
                .log(LoggingLevel.INFO,"addCustomers MobileNumber request: ${body}")
                .bean("addUserService","addMobileNumberService");

        from("direct:updateCustomersMobileNumber")
                .routeId("updateCustomersMobileNumberRouteId")
                .log(LoggingLevel.INFO,"update Customers MobileNumber request: ${body}")
                .bean("addUserService","updateCustomersMobileNumber");

    }
}
