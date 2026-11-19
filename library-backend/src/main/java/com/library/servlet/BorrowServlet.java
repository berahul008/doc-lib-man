package com.library.servlet;

import com.library.dao.TransactionDAO;
import com.library.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/borrow")
public class BorrowServlet extends HttpServlet {
    private TransactionDAO transactionDAO;

    @Override
    public void init() {
        transactionDAO = new TransactionDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Fallback for demo: if session isn't bound across ports cleanly yet, read userId from request parameter or session
        String userIdParam = request.getParameter("userId");
        int userId = (user != null) ? user.getId() : (userIdParam != null ? Integer.parseInt(userIdParam) : 1);

        int bookId = Integer.parseInt(request.getParameter("bookId"));

        boolean success = transactionDAO.borrowBook(userId, bookId);

        if (success) {
            response.getWriter().write("Book borrowed successfully!");
        } else {
            response.getWriter().write("Failed to borrow book. Either out of stock or database error.");
        }
    }
}
