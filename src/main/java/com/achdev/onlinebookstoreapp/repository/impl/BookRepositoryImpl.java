package com.achdev.onlinebookstoreapp.repository.impl;

import com.achdev.onlinebookstoreapp.exception.DataProcessingException;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't save book: " + book, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return book;
    }

    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Book", Book.class).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find all books", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Book> bookQuery = session.createQuery("FROM Book b WHERE b.id = :id",
                    Book.class);
            bookQuery.setParameter("id", id);
            return bookQuery.uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find book by id: " + id);
        }
    }
}
