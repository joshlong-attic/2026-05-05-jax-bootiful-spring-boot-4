package com.example.adoptions;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.Map;

//@Import(MyBeanRegisrar.class)
@EnableResilientMethods
@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

    @Bean
    JdbcPostgresDialect jdbcPostgresDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }

//    @Bean
//    ApplicationRunner runner(IncompleteEventPublications eventPublications) {
//        return e -> eventPublications
//                .resubmitIncompletePublications((evt) ->evt.);
//    }
}

@Controller
@ResponseBody
class HelloController {

    @GetMapping("/")
    Map<String, String> me(Principal p) {
        return Map.of("name", p.getName());
    }
}

@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.PASSWORD_AUTHORITY,
        FactorGrantedAuthority.OTT_AUTHORITY
})
@Configuration
class SecurityConfiguration {

    @Bean
    Customizer<HttpSecurity> httpSecurityCustomizer() {
        return http -> http
                .webAuthn(a -> a
                        .allowedOrigins("http://localhost:8080")
                        .rpName("jax")
                        .rpId("localhost")
                )
                .oneTimeTokenLogin(ott ->
                        ott.tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {

                            response.getWriter()
                                    .println("you've got console mail!");
                            response.setContentType(MediaType.TEXT_PLAIN_VALUE);

                            IO.println("please go to http://localhost:8080/login/ott?token=" +
                                    oneTimeToken.getTokenValue());
                        }));
    }

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        var u = new JdbcUserDetailsManager(dataSource);
        u.setEnableUpdatePassword(true);
        return u;
    }
}


class MyBeanRegisrar implements BeanRegistrar {

    @Override
    public void register(@NonNull BeanRegistry registry, @NonNull Environment env) {
        for (var i = 0; i < 5; i++) {
            var name = "Mainz #" + i;
            registry.registerBean(MyRunner.class, a -> a.supplier(
                    supplierContext -> new MyRunner(
                            supplierContext.bean(DataSource.class), name)));
        }
    }
}

class MyRunner implements ApplicationRunner {

    private final DataSource dataSource;

    private final String name;

    MyRunner(DataSource dataSource, String name) {
        this.dataSource = dataSource;
        this.name = name;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IO.println("hello " + this.name);
    }
}


/*
@Component
class CatFactsClient {

    private final RestClient http;

    CatFactsClient(RestClient.Builder http) {
        this.http = http.build();
    }

    CatFacts facts() {
        return this.http
                .get()
                .uri("https://www.catfacts.net/api")
                .retrieve()
                .body(CatFacts.class);
    }
}*/

