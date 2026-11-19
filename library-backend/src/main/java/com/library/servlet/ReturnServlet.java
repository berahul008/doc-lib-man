package com.library.servlet;

import com.library.dao.TransactionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/return")
public class ReturnServlet extends HttpServlet {
    private TransactionDAO transactionDAO;

    @Override
    public void init() {
        transactionDAO = new TransactionDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        int transactionId = Integer.parseInt(request.getParameter("transactionId"));
        int bookId = Integer.parseInt(request.getParameter("bookId"));

        boolean success = transactionDAO.returnBook(transactionId, bookId);

        if (success) {
            response.getWriter().write("Book returned successfully!");
        } else {
            response.getWriter().write("Failed to return book.");
        }
    }
}
