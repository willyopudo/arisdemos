package com.arisdemos.jpa.controller;

import com.arisdemos.jpa.dto.BookDto;
import com.arisdemos.jpa.model.Book;
import com.arisdemos.jpa.model.BookPublisher;
import com.arisdemos.jpa.model.Publisher;
import com.arisdemos.jpa.repository.BookRepository;
import com.arisdemos.jpa.repository.PublisherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    @PostMapping
    public BookDto createBook(@RequestBody BookDto bookDto) {
        Set<BookPublisher> bookPubs = new HashSet<>();
        Book newBook =  new Book();
        newBook.setName(bookDto.getName());

        for(Integer pubId : bookDto.getPublishers()){
            Publisher p = publisherRepository.findById(pubId).orElse(null);
            BookPublisher bp = new BookPublisher(p, new Date());
            bp.setBook(newBook);
            bookPubs.add(bp);
        }

        newBook.setBookPublishers(bookPubs);
        Book savedBook =  bookRepository.save(newBook);
        return bookToBookDto(savedBook);
    }
    @GetMapping
    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        return bookListToBookDtoList(books);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyBook(@PathVariable Long id, @RequestBody BookDto bookDto){
        Book savedBook = new Book();
        if(bookDto.getId() != null && bookDto.getId().intValue() != id){
            return ResponseEntity.badRequest().body("{\"status\" : 400, \"message\" : \"URI ID differs from Book Id\"}");
        }
        Book existingBook = bookRepository.findById(id.intValue()).orElse(null);
        if(existingBook != null){
            existingBook.setName(bookDto.getName());

            for(Integer pubId : bookDto.getPublishers()){
                Publisher p = publisherRepository.findById(pubId).orElse(null);
                BookPublisher bp = new BookPublisher(p, new Date());
                bp.setBook(existingBook);
                if(existingBook.getBookPublishers().stream().noneMatch((e) -> e.getBook().getId() == id && e.getPublisher().getId() == pubId))
                    existingBook.getBookPublishers().add(bp);
            }

            existingBook.getBookPublishers().removeIf(bp -> !bookDto.getPublishers().contains(bp.getPublisher().getId()));

            //existingBook.setBookPublishers(bookPubs);
            savedBook =  bookRepository.save(existingBook);
            bookRepository.flush();
            return ResponseEntity.ok(bookToBookDto(savedBook));
        }
        return ResponseEntity.notFound().build();
    }

    private BookDto bookToBookDto(Book book){
        BookDto bookDto = new BookDto(book.getId(), book.getName(), null);
        List<Integer> bookPubs = new ArrayList<>();
        if(!book.getBookPublishers().isEmpty()){
            for(BookPublisher bp : book.getBookPublishers()){
                bookPubs.add(bp.getPublisher().getId());
            }
        }
        bookDto.setPublishers(bookPubs);
        return bookDto;
    }
    private List<BookDto> bookListToBookDtoList(List<Book> books){
        return books.stream().map(
                        this::bookToBookDto)
                .collect(Collectors.toList());
    }
}
