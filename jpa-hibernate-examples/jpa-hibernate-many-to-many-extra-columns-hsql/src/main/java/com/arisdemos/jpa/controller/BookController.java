package com.arisdemos.jpa.controller;

import com.arisdemos.jpa.dto.BookDto;
import com.arisdemos.jpa.model.Book;
import com.arisdemos.jpa.model.BookPublisher;
import com.arisdemos.jpa.model.Publisher;
import com.arisdemos.jpa.repository.BookPublisherRepository;
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
    private final BookPublisherRepository bpRepository;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Integer id) {
        Book book = bookRepository.findById(id).orElse(null);
        if(book == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookToBookDto(book));
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

            //existingBook.getBookPublishers().removeIf(bp -> !bookDto.getPublishers().contains(bp.getPublisher().getId()));
            Iterator<BookPublisher> bps = existingBook.getBookPublishers().iterator();
            while(bps.hasNext()){
                BookPublisher bp = bps.next();
                if(!bookDto.getPublishers().contains(bp.getPublisher().getId())){
                    bps.remove();
                    bpRepository.delete(bp);
                }
            }

            savedBook =  bookRepository.save(existingBook);
            return ResponseEntity.ok(bookToBookDto(savedBook));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            if(bookRepository.findById(id.intValue()).isPresent())
                return ResponseEntity.ok().body("{\"status\" : 200, \"message\" : \"Book deleted\"}");
            else return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("{\"status\" : 500, \"message\" : \"Book could not be deleted\"}");
        }
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
