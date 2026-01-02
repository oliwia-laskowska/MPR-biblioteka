package pl.s32832.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracja OpenAPI / Swagger.
 *
 * Dzięki temu po uruchomieniu aplikacji dostępna jest dokumentacja endpointów REST:
 * - Swagger UI
 * - OpenAPI JSON
 *
 * Wymaganie: "Aplikacja będzie udostępniać dokumentację endpointów w postaci swaggera."
 */
@Configuration
public class OpenApiConfig {

    private static final String API_VERSION = "1.0.0";
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library API")
                        .version(API_VERSION)
                        .description("REST API for Library application (Books, Authors, Users, Profiles, Loans)."));
    }
}
