package com.arisdemos.jpa.controller;

import com.arisdemos.jpa.dto.BookDto;
import com.arisdemos.jpa.dto.BookPublisherDto;
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

        for(BookPublisherDto bpDto : bookDto.getPublishers()){
            Publisher p = publisherRepository.findById(bpDto.getId()).orElse(null);
            BookPublisher bp = new BookPublisher(p, new Date());
            bp.setBook(newBook);
            bp.setDefault(bpDto.isDefault());
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

            for(BookPublisherDto bpDto : bookDto.getPublishers()){
                Publisher p = publisherRepository.findById(bpDto.getId()).orElse(null);
                BookPublisher bp = new BookPublisher(p, new Date());
                bp.setBook(existingBook);
                //Check if publisher in this iteration is not already related to the book we are updating
                if(existingBook.getBookPublishers().stream().noneMatch((e) -> e.getBook().getId() == id && e.getPublisher().getId() == bpDto.getId()))
                    existingBook.getBookPublishers().add(bp);
                else{
                    //If the publisher exists for the book, we'll update only 'isDefault' field and persist later
                    BookPublisher existingBp = existingBook.getBookPublishers().stream().filter((e) -> e.getPublisher().getId() == bpDto.getId()).findFirst().get();
                    existingBp.setDefault(bpDto.isDefault());
                }
            }

            //existingBook.getBookPublishers().removeIf(bp -> !bookDto.getPublishers().contains(bp.getPublisher().getId()));

            //Let's iterate over the BookPublishers for the boom we want to update
            //If a publisher is not in the list submitted in the Dto, we remove it and persist change
            Iterator<BookPublisher> bps = existingBook.getBookPublishers().iterator();
            while(bps.hasNext()){
                BookPublisher bp = bps.next();
                if(bookDto.getPublishers().stream().noneMatch((e) -> e.getId() == bp.getPublisher().getId())){
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
        List<BookPublisherDto> bookPubs = new ArrayList<>();
        if(!book.getBookPublishers().isEmpty()){
            for(BookPublisher bp : book.getBookPublishers()){
                bookPubs.add(new BookPublisherDto(bp.getPublisher().getId(), bp.isDefault()) );
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
