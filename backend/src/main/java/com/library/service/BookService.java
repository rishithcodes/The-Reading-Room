package com.library.service;

import com.library.dao.BookDao;
import com.library.model.Book;
import java.util.HashMap;
import java.util.Map;

public class BookService {
    private final BookDao bookDao = new BookDao();

    public Map<String, Object> getAllBooks(String search, Integer categoryId, Integer authorId, Integer publisherId, String availability, String language, Integer yearFrom, Integer yearTo, String sortBy, String sortDir, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("books", bookDao.findAll(search, categoryId, authorId, publisherId, availability, language, yearFrom, yearTo, sortBy, sortDir, page, pageSize));
        result.put("totalCount", bookDao.countAll(search, categoryId, authorId, publisherId, availability, language, yearFrom, yearTo));
        return result;
    }

    public Book getBookById(int id) {
        return bookDao.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void createBook(Book book) {
        if (book.getTitle() == null || book.getAuthorId() == null || book.getCategoryId() == null) {
            throw new IllegalArgumentException("Title, author, and category are required");
        }
        bookDao.insert(book);
    }

    public void updateBook(Book book) {
        bookDao.update(book);
    }

    public void deleteBook(int id) {
        // Here we could add a check if it has active transactions
        if (!bookDao.delete(id)) {
            throw new RuntimeException("Could not delete book. May have active transactions.");
        }
    }
}
