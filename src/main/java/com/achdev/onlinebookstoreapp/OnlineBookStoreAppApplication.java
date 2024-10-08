package com.achdev.onlinebookstoreapp;

import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.BookRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreAppApplication {
    @Autowired
    private BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Book Title");
            book.setIsbn("1234567890");
            book.setPrice(BigDecimal.valueOf(10.00));
            book.setDescription("Book Description");
            book.setCoverImage("Book Cover Image");

            bookRepository.save(book);

            bookRepository.findAll().forEach(System.out::println);
        };
    }

}
