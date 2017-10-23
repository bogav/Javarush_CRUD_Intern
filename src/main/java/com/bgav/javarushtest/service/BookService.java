package com.bgav.javarushtest.service;

import com.bgav.javarushtest.model.Book;

import java.util.List;

/**
 * Created by promoscow on 20.06.17.
 */
public interface BookService {

    public void addBook(Book book);

    public void updateBook(Book book);

    public void removeBook(int id);

    public Book getBookById(int id);

    public List<Book> listBooks();
}
