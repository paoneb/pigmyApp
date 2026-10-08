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
                .log(LoggingLevel.ERROR, "An error occured while add/fetch/upload user- ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)) // or 500
                .setBody(simple("{\"error\":\"${exception.message}\"}"));


        from("direct:addCustomers")
                .routeId(AddUserRoute.class.getSimpleName())
                .log(LoggingLevel.INFO, "Add User Banksoft request: ${body}")
                .bean("addUserService", "saveUsers");


        from("direct:addCustomersPeocit")
                .routeId("addCustomersPeocitRouteId")
                .log(LoggingLevel.INFO, "Add User Peocit request: ${body}")
                .bean("addUserService", "saveUsersPeocit");

        from("direct:addCustomersSledger")
                .routeId("addCustomersSledgerRouteId")
                .log(LoggingLevel.INFO, "Add User Sledger request: ${body}")
                .bean("addUserService", "saveUsersSledger");


        from("direct:fetchCustomers")
                .routeId("fetchCustomersRouteId")
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO, "fetch banksoft User request ${header.bankCode}, ${header.agentCode}")
                .bean("addUserService", "fetchCustomers")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO, "fetch User Peocit request ${header.bankCode}, ${header.agentCode}")
                .bean("addUserService", "fetchCustomersPeocit")
                .when(header("bankType").isEqualTo("sledger"))
                .log(LoggingLevel.INFO, "fetch User Sledger request ${header.bankCode}, ${header.agentCode}")
                .bean("addUserService", "fetchCustomersSledger");

        from("direct:addCustomersMobileNumber")
                .routeId("addCustomersMobileNumberRouteId")
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO, "addCustomers MobileNumber request ${header.bankType}")
                .bean("addUserService", "addMobileNumberService")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO, "addCustomers Peocit MobileNumber request ${header.bankType}")
                .bean("addUserService", "addPeocitMobileNumberService")
                .when(header("bankType").isEqualTo("sledger"))
                .log(LoggingLevel.INFO, "addCustomers Sledger MobileNumber request ${header.bankType}")
                .bean("addUserService", "addSledgerMobileNumberService");

        from("direct:updateCustomersMobileNumber")
                .routeId("updateCustomersMobileNumberRouteId")
                .choice()
                .when(header("bankType").isEqualTo("banksoft"))
                .log(LoggingLevel.INFO, "update banksoft Customers MobileNumber request ${header.mobilenumber}")
                .bean("addUserService", "updateCustomersMobileNumber")
                .when(header("bankType").isEqualTo("peocit"))
                .log(LoggingLevel.INFO, "update Customers Peocit MobileNumber request ${header.mobilenumber}")
                .bean("addUserService", "updateCustomersPeocitMobileNumber")
                .when(header("bankType").isEqualTo("sledger"))
                .log(LoggingLevel.INFO, "update Customers Sledger MobileNumber request ${header.mobilenumber}")
                .bean("addUserService", "updateCustomersSledgerMobileNumber");

    }
}
