package com.example.controller;

import com.example.entity.Book;
import com.example.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
        logger.info("BookController constructor called");
    }

    @PostConstruct
    public void init() {
        logger.info("BookController @PostConstruct - Bean initialized!");
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        logger.info("GET /api/books endpoint called");
        List<Book> books = bookService.getAllBooks();
        logger.info("Found {} books", books.size());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        logger.info("GET /api/books/{} endpoint called", id);
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(book))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        logger.info("POST /api/books endpoint called with book: {}", book.getTitle());
        if (book.getTitle() == null || book.getTitle().trim().isEmpty() ||
                book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        logger.info("PUT /api/books/{} endpoint called", id);
        if (bookDetails.getTitle() == null || bookDetails.getTitle().trim().isEmpty() ||
                bookDetails.getAuthor() == null || bookDetails.getAuthor().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return bookService.updateBook(id, bookDetails)
                .map(book -> ResponseEntity.ok(book))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.info("DELETE /api/books/{} endpoint called", id);
        if (bookService.deleteBook(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
