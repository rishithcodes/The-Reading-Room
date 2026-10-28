package com.library.controller;

import com.library.filter.AuthFilter;
import com.library.model.Book;
import com.library.model.User;
import com.library.service.BookService;
import com.library.util.JsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/books/*")
public class BookServlet extends HttpServlet {
    private final BookService bookService = new BookService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int pageSize = 10;
                JsonResponse.sendSuccess(resp,  200,  bookService.getAllBooks(req.getParameter("search"),  null,  null,  null,  null,  null,  null,  null,  "id",  "asc",  page,  pageSize),  "Success");
            } else {
                int id = Integer.parseInt(path.substring(1));
                JsonResponse.sendSuccess(resp,  200,  bookService.getBookById(id),  "Success");
            }
        } catch (Exception e) {
            JsonResponse.sendError(resp, 500, "Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthFilter.hasRole(req, User.Role.ADMIN, User.Role.LIBRARIAN)) { JsonResponse.sendError(resp, 403, "Forbidden"); return; }
        try {
            Book book = mapper.readValue(req.getInputStream(), Book.class);
            bookService.createBook(book);
            JsonResponse.sendMessage(resp,  201,  "Created");
        } catch (Exception e) { JsonResponse.sendError(resp, 400, e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthFilter.hasRole(req, User.Role.ADMIN, User.Role.LIBRARIAN)) { JsonResponse.sendError(resp, 403, "Forbidden"); return; }
        try {
            Book book = mapper.readValue(req.getInputStream(), Book.class);
            bookService.updateBook(book);
            JsonResponse.sendMessage(resp,  200,  "Updated");
        } catch (Exception e) { JsonResponse.sendError(resp, 400, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthFilter.hasRole(req, User.Role.ADMIN)) { JsonResponse.sendError(resp, 403, "Forbidden"); return; }
        try {
            int id = Integer.parseInt(req.getPathInfo().substring(1));
            bookService.deleteBook(id);
            JsonResponse.sendMessage(resp,  200,  "Deleted");
        } catch (Exception e) { JsonResponse.sendError(resp, 400, e.getMessage()); }
    }
}
