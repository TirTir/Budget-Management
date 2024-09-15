package backend.budget.common.config;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0")
                .title("Budget-Management RestAPI")
                .description("예산 관리 어플리케이션 API 문서");

        return new OpenAPI()
                .addServersItem(new Server().url("/").description("HTTPS 설정"))
                .components(new Components())
                .info(info);
    }

}
