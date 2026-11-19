const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;
const BACKEND_URL = process.env.BACKEND_URL || 'http://lib-net:8080/library-backend';

// Serve static files (HTML, CSS) from the 'public' folder
app.use(express.static(path.join(__dirname, 'public')));

// Proxy /api/* requests to the Java backend container
app.use('/api', async (req, res) => {
    const targetUrl = `${BACKEND_URL}${req.url}`;

    try {
        const headers = {};
        if (req.headers['content-type']) {
            headers['Content-Type'] = req.headers['content-type'];
        }

        const options = {
            method: req.method,
            headers
        };

        if (req.method !== 'GET' && req.method !== 'HEAD') {
            const chunks = [];
            for await (const chunk of req) {
                chunks.push(chunk);
            }
            options.body = Buffer.concat(chunks);
        }

        const backendRes = await fetch(targetUrl, options);
        const bodyText = await backendRes.text();

        res.status(backendRes.status);
        const contentType = backendRes.headers.get('content-type');
        if (contentType) {
            res.set('Content-Type', contentType);
        }
        res.send(bodyText);
    } catch (err) {
        console.error('Error forwarding request to backend:', err);
        res.status(502).send('Backend service unavailable');
    }
});

// Route for Login page
app.get('/login', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

// Route for Register page
app.get('/register', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'register.html'));
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

// Route for Borrowed Books page
app.get('/borrowed-books', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'borrowed-books.html'));
});

app.listen(PORT, () => {
    console.log(`Frontend server running on port ${PORT}`);
});
