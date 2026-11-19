package com.library.servlet;

import com.library.config.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/borrowed-books")
public class BorrowedBooksServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("text/html;charset=UTF-8");

        String userIdParam = request.getParameter("userId");
        String role = request.getParameter("role");

        StringBuilder sql = new StringBuilder(
            "SELECT t.id AS transaction_id, b.id AS book_id, b.title, b.author, t.borrow_date, t.due_date, u.username " +
            "FROM transactions t " +
            "JOIN books b ON t.book_id = b.id " +
            "JOIN users u ON t.user_id = u.id " +
            "WHERE t.status = 'BORROWED'"
        );

        // If regular customer, show only their books. If admin, show all active borrowings.
        if (!"ADMIN".equalsIgnoreCase(role) && userIdParam != null) {
            sql.append(" AND t.user_id = ").append(userIdParam);
        }

        StringBuilder html = new StringBuilder();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                int txId = rs.getInt("transaction_id");
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String borrower = rs.getString("username");
                String dueDate = rs.getString("due_date");

                html.append("<li>")
                    .append("<div><b>").append(title).append("</b> by ").append(author)
                    .append("<br><small>Borrowed by: <b>").append(borrower).append("</b> | Due: ").append(dueDate != null ? dueDate.substring(0, 10) : "N/A").append("</small>")
                    .append("</div>")
                    .append("<button onclick=\"returnBook(").append(txId).append(", ").append(bookId).append(")\" style=\"background-color: #dc3545; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer;\">Return</button>")
                    .append("</li>");
            }

            if (!hasRows) {
                html.append("<p style=\"text-align: center; color: #666;\">No active borrowed books found.</p>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            html.append("<p style=\"color: red;\">Database error loading borrowed books.</p>");
        }

        response.getWriter().write(html.toString());
    }
}
