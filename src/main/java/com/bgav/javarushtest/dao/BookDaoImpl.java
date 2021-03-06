package com.bgav.javarushtest.dao;

import com.bgav.javarushtest.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 */

@Repository
public class BookDaoImpl implements BookDao {

    private static final Logger logger = LoggerFactory.getLogger(BookDaoImpl.class);
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addBook(Book book) {
        Session session = this.sessionFactory.getCurrentSession();
        System.out.println(book);
//        Transaction tr = session.beginTransaction();
        session.persist(book);
//        tr.commit();
        logger.info("Book added. Book details: " + book);
    }

    public void updateBook(Book book) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(book);
        logger.info("Book updated. Book info: " + book);
    }

    public void removeBook(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Book book = (Book) session.load(Book.class, id);
        if (book != null) session.delete(book);
        logger.info("Book removed. Book info: " + book);
    }

    public Book getBookById(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Book book = (Book) session.load(Book.class, id);
        logger.info("Book loaded. Book info: " + book);
        return book;
    }

    @SuppressWarnings("unchecked")
    public List<Book> listBooks() {
        Session session = this.sessionFactory.getCurrentSession();
        List<Book> listBooks = session.createQuery("from Book").list();
        for (Book book : listBooks) logger.info("List. Book: " + book);
        return listBooks;
    }
}
