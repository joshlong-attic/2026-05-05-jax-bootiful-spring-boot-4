package com.example.adoptions.dogs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Map;

@Controller
@ResponseBody
class DogsController {

    private final DogRepository dogRepository;

    DogsController(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @GetMapping(value = "/dogs", version = "1.1")
    Collection<Dog> dogsv11() {
        return dogRepository.findAll();
    }

    @GetMapping(value = "/dogs", version = "1.0")
    Collection<Map<String, Object>> dogsv10() {
        return dogRepository
                .findAll()
                .stream()
                .map(dog -> Map.of("id", (Object) dog.id(), "fullName", dog.name()))
                .toList();
    }

}
