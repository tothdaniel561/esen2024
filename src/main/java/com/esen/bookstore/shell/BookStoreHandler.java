package com.esen.bookstore.shell;

import com.esen.bookstore.repository.BookStoreRepository;
import com.esen.bookstore.service.BookService;
import com.esen.bookstore.service.BookstoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("BookStore related commands")
@RequiredArgsConstructor
public class BookStoreHandler {
    private final BookstoreService bookstoreService;
    private final BookStoreRepository bookStoreRepository;

    @ShellMethod(value = "create bookstore", key = "create bookstore")
    public void save(String location, Double priceModifier, Double moneyInCashRegister){
        bookstoreService.save(location, priceModifier, moneyInCashRegister);
    }

    @ShellMethod(value = "List bookstores", key = "list bookstores")
    public String list(){
        return bookstoreService.findAll().stream()
                .map(bookStore ->
                        "ID: %d, pricemodifier %f, moneycashregister: %f, location: %s"
                                .formatted(bookStore.getId(),
                                        bookStore.getPriceModifier(),
                                        bookStore.getMoneyInCashRegister(),
                                        bookStore.getLocation()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "update bookstore", value = "update bookstore")
    public void updateBookstore(Long id,
                                @ShellOption(defaultValue = ShellOption.NULL) String location,
                                @ShellOption(defaultValue = ShellOption.NULL) Double priceModifier,
                                @ShellOption(defaultValue = ShellOption.NULL) Double moneyInCashRegister){
        bookstoreService.update(id, location, priceModifier, moneyInCashRegister);
    }

    @ShellMethod(key = "Find prices", value = "Find prices")
    public String findPrices(Long bookIdd){
        return bookstoreService.findPrices(bookIdd).entrySet()
                .stream()
                .map(bookStore ->
                        "ID: %d, Location: %s, Price: %s Ft"
                                .formatted(bookStore.getKey().getId(), bookStore.getKey().getLocation(), bookStore.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "get stock", value = "get stock")
    public String getStock(Long id){
        return bookstoreService.getStock(id)
                .entrySet()
                .stream()
                .map(entry -> "Book ID: %s, Author %s, Title: %s - Copies: %s"
                        .formatted(entry.getKey().getId(),
                                entry.getKey().getAuthor(),
                                entry.getKey().getTitle(),
                                entry.getValue())).collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "Add stock", value = "Add stock")
    public void addStock(Long bookstoreId, Long bookId, int amount){
        bookstoreService.changeStock(bookstoreId, bookId, amount);
    }
}
