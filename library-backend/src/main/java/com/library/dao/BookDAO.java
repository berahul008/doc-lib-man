package com.library.dao;

import com.library.config.DBConnection;
import com.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Helper class or DTO structure if needed, or we can format directly/extend Book model. 
    // For simplicity, let's return books along with active borrower names using a custom SQL result structure or map.
    
    public List<String[]> getAllBooksWithBorrowers() {
        List<String[]> bookList = new ArrayList<>();
        // SQL query joining books, active transactions, and users to find who borrowed what
        String sql = "SELECT b.id, b.title, b.author, b.total_copies, b.available_copies, " +
                     "u.username AS borrower_name, t.borrow_date, t.due_date " +
                     "FROM books b " +
                     "LEFT JOIN transactions t ON b.id = t.book_id AND t.status = 'BORROWED' " +
                     "LEFT JOIN users u ON t.user_id = u.id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] data = new String[8];
                data[0] = String.valueOf(rs.getInt("id"));
                data[1] = rs.getString("title");
                data[2] = rs.getString("author");
                data[3] = String.valueOf(rs.getInt("total_copies"));
                data[4] = String.valueOf(rs.getInt("available_copies"));
                data[5] = rs.getString("borrower_name"); // Will be null if not borrowed
                data[6] = rs.getString("borrow_date");
                data[7] = rs.getString("due_date");
                bookList.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setTotalCopies(rs.getInt("total_copies"));
                book.setAvailableCopies(rs.getInt("available_copies"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setInt(4, book.getTotalCopies());
            stmt.setInt(5, book.getAvailableCopies());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
