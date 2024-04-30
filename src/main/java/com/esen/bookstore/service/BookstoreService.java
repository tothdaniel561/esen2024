package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.BookStore;
import com.esen.bookstore.repository.BookRepository;
import com.esen.bookstore.repository.BookStoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookStoreRepository bookStoreRepository;
    private final BookRepository bookRepository;
    @Transactional
    public void removeBookFrom(Book book){
        bookStoreRepository.findAll()
                .forEach(bookStore -> {
                    bookStore.getInventory().remove(book);
                    bookStoreRepository.save(bookStore);
                });
    }

    public List<BookStore> findAll(){
        return bookStoreRepository.findAll();
    }

    public void save(String location, Double priceModifier, Double moneyInCashRegister) {
        bookStoreRepository.save(BookStore.builder()
                .location(location)
                .priceModifier(priceModifier)
                .moneyInCashRegister(moneyInCashRegister).build());
    }

    public void update(Long id, String location, Double priceModifier, Double moneyInCashRegister) {
        if (Stream.of(location, priceModifier, moneyInCashRegister).allMatch(Objects::isNull)) {
            throw new UnsupportedOperationException("There's nothing to update");
        }

        var bookstore = bookStoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find book"));

        if (location != null) {
            bookstore.setLocation(location);
        }

        if (priceModifier != null) {
            bookstore.setPriceModifier(priceModifier);
        }

        if (moneyInCashRegister != null) {
            bookstore.setMoneyInCashRegister(moneyInCashRegister);
        }

        bookStoreRepository.save(bookstore);
    }

    public Map<BookStore, Double> findPrices(Long id) {
        var book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        var bookStores = bookStoreRepository.findAll();

        Map<BookStore, Double> priceMap = new HashMap<>();

        for (var b : bookStores) {
            if (b.getInventory().containsKey(book)){
                Double price = book.getPrice() * b.getPriceModifier();
                priceMap.put(b, price);
            }
        }
        return priceMap;
    }

    public Map<Book, Integer> getStock(Long id){
        var bookstore = bookStoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no such bookstore"));
        return bookstore.getInventory();
    }


    public void changeStock(Long bookStoreId, Long bookId, int amount){
        var bookstore = bookStoreRepository.findById(bookStoreId).orElseThrow(() -> new RuntimeException("no such bookstore"));
        var book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("no such book"));
        if (bookstore.getInventory().containsKey(book)){
            var entry = bookstore.getInventory().get(book);
            if (entry + amount < 0){
                throw new UnsupportedOperationException("Invalid amount");
            }
            bookstore.getInventory().replace(book, entry + amount);
        } else {
            if (amount < 0) {
                bookstore.getInventory().put(book, amount);
            }
        }
        bookStoreRepository.save(bookstore);
    }
}
