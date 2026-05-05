package com.example.adoptions.dogs;

import org.springframework.modulith.events.Externalized;

//@Externalized ("kafkaDestination")
public record DogAdoptedEvent(int dogId) {
}
