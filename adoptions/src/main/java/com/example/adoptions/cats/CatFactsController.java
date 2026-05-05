package com.example.adoptions.cats;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
@ResponseBody
class CatFactsController {

    private final CatFactsClient client;
    private final AtomicInteger counter = new AtomicInteger(0);

    CatFactsController(CatFactsClient client) {
        this.client = client;
    }


    //    @ConcurrencyLimit(10)
    @Retryable(maxRetries = 5, includes = IllegalStateException.class)
    @GetMapping("/cats")
    CatFacts facts() {
        if (this.counter.getAndIncrement() < 5) {
            IO.println("oops!");
            throw new IllegalStateException("Boom!");
        }
        IO.println("facts!");
        return client.facts();
    }
}
