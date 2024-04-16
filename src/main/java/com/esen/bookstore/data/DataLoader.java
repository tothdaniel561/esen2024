package com.esen.bookstore.data;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.BookStore;
import com.esen.bookstore.repository.BookRepository;
import com.esen.bookstore.repository.BookStoreRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader {

    private final BookRepository bookRepository;
    private final BookStoreRepository bookStoreRepository;

    @Value("classpath:data/books.json")
    private Resource booksResource;
    @Value("classpath:data/bookstores.json")
    private Resource bookStoreResource;

    @PostConstruct
    public void loadData(){
        var objectMapper = new ObjectMapper();
        try {

            var bookJson = StreamUtils.copyToString(booksResource.getInputStream(), StandardCharsets.UTF_8);
            var books = objectMapper.readValue(bookJson, new TypeReference<List<Book>>() {});
            bookRepository.saveAll(books);

            var bookStoreJson = StreamUtils.copyToString(bookStoreResource.getInputStream(), StandardCharsets.UTF_8);
            var bookstore = objectMapper.readValue(bookStoreJson, new TypeReference<List<BookStore>>() {});
            bookstore.forEach(bookStore -> {
                bookStore.setInventory(books.stream()
                        .collect(Collectors.toMap(book -> book,
                                book -> ThreadLocalRandom.current().nextInt(1, 50))));
            });
            bookStoreRepository.saveAll(bookstore);

        } catch (IOException e){
            log.error("Cannot load data into database", e);
        }
    }

}
