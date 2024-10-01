package com.arisdemos.jpa.controller;

import com.arisdemos.jpa.model.Publisher;
import com.arisdemos.jpa.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/publisher")
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherRepository publisherRepository;

    @PostMapping
    public Publisher createBook(@RequestBody Publisher pub) {
        return publisherRepository.save(pub);
    }
}
