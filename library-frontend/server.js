const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Serve static files (HTML, CSS) from the 'public' folder
app.use(express.static(path.join(__dirname, 'public')));

// Route for Login page
app.get('/login', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

// Route for Register page
app.get('/register', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'register.html'));
});

app.listen(PORT, () => {
    console.log(`Frontend server running at http://192.168.31.179:${PORT}`);
});

// Route for Catalog page
app.get('/catalog', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'catalog.html'));
});

// Route for Add Book page
app.get('/add-book', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'add-book.html'));
});

// Route for Dashboard page
app.get('/dashboard', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'dashboard.html'));
});

app.get('/borrowed-books', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'borrowed-books.html'));
});
