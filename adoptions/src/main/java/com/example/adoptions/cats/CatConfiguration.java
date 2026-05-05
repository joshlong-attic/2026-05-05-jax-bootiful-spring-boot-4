package com.example.adoptions.cats;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(CatFactsClient.class)
class CatConfiguration {
}
