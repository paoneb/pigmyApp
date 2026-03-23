package com.pigmy.app.route;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.pigmy.app.model.AgentNew;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;


@Component
public class CreateAgentRoute extends RouteBuilder {

    protected final JacksonDataFormat successAdd=new JacksonDataFormat();
    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "An error occured while creating agent- ${exception.message}")
                .logStackTrace(true);


        from("direct:createNewAgent")
                .routeId(CreateAgentRoute.class.getSimpleName())
                .log(LoggingLevel.INFO, "create new agent request: ${body}")
                .bean("agentService", "saveAgent");


    }

}
