package com.soongsil.swcontest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.stereotype.Component;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;

@Component
public class Workaround implements WebMvcOpenApiTransformationFilter {

    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI openApi = context.getSpecification();
        Server server = openApi.getServers().get(0);

        if (server.getUrl().contains("localhost")) {
            return openApi;
        }
        if (server.getUrl().contains(":80")) {
            server.setUrl(server.getUrl().replace(":80",""));
        }
        if (!server.getUrl().contains("https")) {
            server.setUrl(server.getUrl().replace("http","https"));
        }
        return openApi;
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return documentationType.equals(DocumentationType.OAS_30);
    }
}
