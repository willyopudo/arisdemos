package com.arisdemos.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class Application {
//    private final BookRepository bookRepository;
//    private final PublisherRepository publisherRepository;
//    private final BookPublisherRepository bookPublisherRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Override
//    @Transactional
//    public void run(String... strings) throws Exception {
//        // Create a couple of Book, Publisher and BookPublisher
//        Publisher publisherA = new Publisher("Publisher A");
//        Publisher publisherB = new Publisher("Publisher B");
//        publisherRepository.saveAll(Arrays.asList(publisherA, publisherB));
//
//        bookRepository.save(new Book("Book 1", new BookPublisher(publisherA, new Date()), new BookPublisher(publisherB, new Date())));
//        bookRepository.save(new Book("Book 2", new BookPublisher(publisherA, new Date())));
//    }
}
