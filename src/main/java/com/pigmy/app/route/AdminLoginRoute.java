package com.pigmy.app.route;

import com.pigmy.app.config.JwtUtil;
import com.pigmy.app.model.LoginRequest;
import com.pigmy.app.model.LoginResponse;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.serviceactivators.AuthAdminService;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AdminLoginRoute extends RouteBuilder {

    @Autowired
    private AuthAdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .log(LoggingLevel.ERROR, "An error occurred while logging - ${exception.message}")
                .logStackTrace(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("${exception.message}"));

        from("direct:loginAdmin")
                .routeId(AdminLoginRoute.class.getSimpleName())
                .log(LoggingLevel.INFO,"login User request: ${body}")
                .process(exchange -> {
                    LoginRequest req = exchange.getIn().getBody(LoginRequest.class);

                    if (adminService.validate(req.getUserName(), req.getPassword(), req.getBankCode())) {
                        String token = jwtUtil.generateToken(req.getUserName(), req.getBankCode());
                        exchange.getMessage().setBody(new LoginResponse(req.getBankCode(), token));
                        System.out.println("logged in");
                    } else {
                        exchange.getMessage().setHeader("CamelHttpResponseCode", 401);
                        exchange.getMessage().setBody("Invalid credentials");
                    }
                });

    }
}
