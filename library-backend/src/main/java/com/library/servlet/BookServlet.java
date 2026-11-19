package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {
    private BookDAO bookDAO;

    @Override
    public void init() {
        bookDAO = new BookDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("text/html;charset=UTF-8");
        
        String roleParam = request.getParameter("role");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(roleParam);

        List<String[]> booksWithBorrowers = bookDAO.getAllBooksWithBorrowers();
        
        StringBuilder html = new StringBuilder("<h2>Book Catalog</h2><ul>");
        for (String[] b : booksWithBorrowers) {
            html.append("<li><div><b>").append(b[1]).append("</b> by ").append(b[2])
                .append("<br><small>[Available: ").append(b[4]).append("/").append(b[3]).append("]</small>");
            
            // Show borrower info ONLY if the viewer is an Admin
            if (isAdmin && b[5] != null) {
                html.append("<br><span style=\"color: #d9534f; font-weight: bold;\">Borrowed by: ").append(b[5])
                    .append(" (Due: ").append(b[7] != null ? b[7].substring(0, 10) : "N/A").append(")</span>");
            }
            html.append("</div>");

            // Borrow button for available copies
            if (Integer.parseInt(b[4]) > 0) {
                html.append("<button onclick=\"borrowBook(").append(b[0]).append(")\" style=\"background-color: #28a745; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer;\">Borrow</button>");
            } else {
                html.append("<span style=\"color: red; font-weight: bold;\">Out of Stock</span>");
            }
            html.append("</li>");
        }
        html.append("</ul>");
        response.getWriter().write(html.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        int copies = Integer.parseInt(request.getParameter("copies"));

        Book newBook = new Book(title, author, isbn, copies, copies);
        boolean success = bookDAO.addBook(newBook);

        if (success) {
            response.getWriter().write("Book added successfully!");
        } else {
            response.getWriter().write("Failed to add book. ISBN might already exist.");
        }
    }
}
