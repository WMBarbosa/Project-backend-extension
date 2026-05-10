package com.barbosa.extension_project.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
            .info(new Info()
                .title("Raízes do Nordeste — API Back-end")
                .description("""
                    API REST para o sistema de gerenciamento da rede de lanchonetes 
                    **Raízes do Nordeste**.
                    
                    ## Funcionalidades
                    - Autenticação JWT com controle de perfis (ADMIN, GERENTE, ATENDENTE, COZINHA, CLIENTE)
                    - Gestão de unidades da rede
                    - Cardápio por unidade
                    - Pedidos com multicanalidade (APP, TOTEM, BALCÃO, PICKUP, WEB)
                    - Controle de estoque por unidade
                    - Programa de fidelização com pontos
                    - Pagamento simulado (mock)
                    
                    ## Segurança / LGPD
                    Senhas armazenadas com BCrypt. Dados pessoais tratados conforme LGPD.
                    Logs de auditoria para ações sensíveis.
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Projeto Multidisciplinar — UNINTER")
                    .email("projeto@raizesnordeste.com")))
            .addSecurityItem(new SecurityRequirement().addList(schemeName))
            .components(new Components()
                .addSecuritySchemes(schemeName, new SecurityScheme()
                    .name(schemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
