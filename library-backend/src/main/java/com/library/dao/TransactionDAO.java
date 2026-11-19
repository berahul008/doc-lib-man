package com.library.dao;

import com.library.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    // Borrow a book: insert transaction and decrement available copies
    public boolean borrowBook(int userId, int bookId) {
        String insertTxSQL = "INSERT INTO transactions (user_id, book_id, borrow_date, due_date, status) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'BORROWED')";
        String updateBookSQL = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ? AND available_copies > 0";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Update book availability
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSQL)) {
                updateStmt.setInt(1, bookId);
                int affectedRows = updateStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false; // No copies available
                }
            }

            // 2. Insert transaction record
            try (PreparedStatement insertStmt = conn.prepareStatement(insertTxSQL)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, bookId);
                insertStmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // Return a book: update transaction status, set return date, and increment available copies
    public boolean returnBook(int transactionId, int bookId) {
        String updateTxSQL = "UPDATE transactions SET status = 'RETURNED', return_date = NOW() WHERE id = ? AND status = 'BORROWED'";
        String updateBookSQL = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Update transaction status
            try (PreparedStatement updateTxStmt = conn.prepareStatement(updateTxSQL)) {
                updateTxStmt.setInt(1, transactionId);
                int affectedRows = updateTxStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false; // Transaction not found or already returned
                }
            }

            // 2. Increment book available copies
            try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSQL)) {
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
