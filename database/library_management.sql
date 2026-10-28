-- ============================================================
-- Library Management System - MySQL Database Script
-- ============================================================
-- Version: 1.0
-- Description: Complete database schema with sample data
-- ============================================================

-- Create and select database
CREATE DATABASE IF NOT EXISTS library_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_management;

-- ============================================================
-- DROP TABLES (in reverse dependency order for clean reset)
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS fine_payments;
DROP TABLE IF EXISTS fines;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS publishers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS library_settings;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- TABLE: library_settings
-- ============================================================
CREATE TABLE library_settings (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    setting_key     VARCHAR(100) NOT NULL UNIQUE,
    setting_value   VARCHAR(500) NOT NULL,
    description     VARCHAR(500),
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: users
-- ============================================================
CREATE TABLE users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(150)  NOT NULL,
    email           VARCHAR(200)  NOT NULL UNIQUE,
    phone           VARCHAR(20),
    password_hash   VARCHAR(255)  NOT NULL,
    role            ENUM('ADMIN','LIBRARIAN','STUDENT') NOT NULL DEFAULT 'STUDENT',
    department      VARCHAR(100),
    student_id      VARCHAR(50)   UNIQUE,
    status          ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    profile_image   VARCHAR(300),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_status (status),
    INDEX idx_users_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: categories
-- ============================================================
CREATE TABLE categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: authors
-- ============================================================
CREATE TABLE authors (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)  NOT NULL,
    biography   TEXT,
    country     VARCHAR(100),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_authors_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: publishers
-- ============================================================
CREATE TABLE publishers (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)  NOT NULL UNIQUE,
    address     VARCHAR(300),
    email       VARCHAR(200),
    phone       VARCHAR(30),
    website     VARCHAR(200),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_publishers_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: books
-- ============================================================
CREATE TABLE books (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    isbn                VARCHAR(20)   UNIQUE,
    title               VARCHAR(300)  NOT NULL,
    description         TEXT,
    author_id           INT           NOT NULL,
    category_id         INT           NOT NULL,
    publisher_id        INT,
    publication_year    YEAR,
    language            VARCHAR(50)   DEFAULT 'English',
    total_copies        INT           NOT NULL DEFAULT 1,
    available_copies    INT           NOT NULL DEFAULT 1,
    rack_number         VARCHAR(20),
    cover_image         VARCHAR(300),
    status              ENUM('AVAILABLE','UNAVAILABLE','DISCONTINUED') NOT NULL DEFAULT 'AVAILABLE',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_copies CHECK (available_copies >= 0 AND available_copies <= total_copies),
    CONSTRAINT chk_total_copies CHECK (total_copies >= 0),

    FOREIGN KEY (author_id)    REFERENCES authors(id)    ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (category_id)  REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE SET NULL  ON UPDATE CASCADE,

    INDEX idx_books_isbn (isbn),
    INDEX idx_books_title (title),
    INDEX idx_books_author (author_id),
    INDEX idx_books_category (category_id),
    INDEX idx_books_status (status),
    FULLTEXT INDEX ft_books_search (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: transactions (book issue/return)
-- ============================================================
CREATE TABLE transactions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT           NOT NULL,
    book_id         INT           NOT NULL,
    issued_by       INT,
    returned_by     INT,
    issue_date      DATE          NOT NULL,
    due_date        DATE          NOT NULL,
    return_date     DATE,
    status          ENUM('ISSUED','RETURNED','OVERDUE','LOST') NOT NULL DEFAULT 'ISSUED',
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)      REFERENCES users(id)  ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (book_id)      REFERENCES books(id)  ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (issued_by)    REFERENCES users(id)  ON DELETE SET NULL  ON UPDATE CASCADE,
    FOREIGN KEY (returned_by)  REFERENCES users(id)  ON DELETE SET NULL  ON UPDATE CASCADE,

    INDEX idx_transactions_user (user_id),
    INDEX idx_transactions_book (book_id),
    INDEX idx_transactions_status (status),
    INDEX idx_transactions_due_date (due_date),
    INDEX idx_transactions_issue_date (issue_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: fines
-- ============================================================
CREATE TABLE fines (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id  INT           NOT NULL UNIQUE,
    user_id         INT           NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    paid_amount     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    overdue_days    INT           NOT NULL DEFAULT 0,
    status          ENUM('PENDING','PARTIALLY_PAID','PAID','WAIVED') NOT NULL DEFAULT 'PENDING',
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_fine_amount CHECK (amount >= 0),
    CONSTRAINT chk_paid_amount CHECK (paid_amount >= 0),

    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id)        REFERENCES users(id)        ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_fines_user (user_id),
    INDEX idx_fines_status (status),
    INDEX idx_fines_transaction (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: fine_payments
-- ============================================================
CREATE TABLE fine_payments (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    fine_id         INT           NOT NULL,
    amount_paid     DECIMAL(10,2) NOT NULL,
    payment_date    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    received_by     INT,
    notes           TEXT,

    CONSTRAINT chk_payment_amount CHECK (amount_paid > 0),

    FOREIGN KEY (fine_id)     REFERENCES fines(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (received_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,

    INDEX idx_fine_payments_fine (fine_id),
    INDEX idx_fine_payments_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: reservations
-- ============================================================
CREATE TABLE reservations (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT           NOT NULL,
    book_id             INT           NOT NULL,
    reservation_date    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    expiry_date         DATE,
    status              ENUM('ACTIVE','FULFILLED','CANCELLED','EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    notes               TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Prevent duplicate active reservations for same user+book
    UNIQUE KEY uq_active_reservation (user_id, book_id, status),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE  ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE  ON UPDATE CASCADE,

    INDEX idx_reservations_user (user_id),
    INDEX idx_reservations_book (book_id),
    INDEX idx_reservations_status (status),
    INDEX idx_reservations_date (reservation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: notifications
-- ============================================================
CREATE TABLE notifications (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT           NOT NULL,
    title       VARCHAR(200)  NOT NULL,
    message     TEXT          NOT NULL,
    type        ENUM('ISSUE','RETURN','DUE_SOON','OVERDUE','FINE','RESERVATION','GENERAL') NOT NULL DEFAULT 'GENERAL',
    is_read     BOOLEAN       NOT NULL DEFAULT FALSE,
    reference_id    INT,
    reference_type  VARCHAR(50),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (is_read),
    INDEX idx_notifications_type (type),
    INDEX idx_notifications_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Library Settings
INSERT INTO library_settings (setting_key, setting_value, description) VALUES
('fine_per_day',        '2.00',   'Fine amount per overdue day (in rupees)'),
('max_borrow_limit',    '5',      'Maximum number of books a student can borrow at once'),
('loan_period_days',    '14',     'Default loan period in days'),
('reservation_expiry',  '3',      'Days before a reservation expires after book becomes available'),
('library_name',        'College Central Library', 'Name of the library'),
('library_email',       'library@college.edu',    'Library contact email'),
('library_phone',       '+91-9876543210',          'Library contact phone'),
('library_address',     '123 College Road, Academic City - 560001', 'Library address');

-- Users
-- Passwords are BCrypt hashes of: Admin@123, Librarian@123, Student@123
-- For demo: admin@library.com / Admin@123
--           librarian@library.com / Librarian@123
--           student@library.com / Student@123
INSERT INTO users (full_name, email, phone, password_hash, role, department, student_id, status) VALUES
('System Administrator', 'admin@library.com',      '+91-9000000001',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewEFbFY1RbsaGo5K',
 'ADMIN',     'Administration',   NULL,       'ACTIVE'),

('Dr. Priya Sharma',     'librarian@library.com',  '+91-9000000002',
 '$2a$12$8K4N1vH3mX7pQ2wL9cRe4.YkZmNs1TcAhJ6oP5dWfUbGxVeI3rK8S',
 'LIBRARIAN', 'Library Services', 'LIB001',   'ACTIVE'),

('Rahul Verma',          'student@library.com',    '+91-9000000003',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Computer Science', 'CS2021001', 'ACTIVE'),

('Ananya Patel',         'ananya@college.edu',     '+91-9000000004',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Electronics',      'EC2021002', 'ACTIVE'),

('Karthik Reddy',        'karthik@college.edu',    '+91-9000000005',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Mechanical',       'ME2021003', 'ACTIVE'),

('Sneha Iyer',           'sneha@college.edu',      '+91-9000000006',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Computer Science', 'CS2022001', 'ACTIVE'),

('Arjun Nair',           'arjun@college.edu',      '+91-9000000007',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Civil',            'CE2022002', 'ACTIVE'),

('Meera Krishnan',       'meera@college.edu',      '+91-9000000008',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Information Tech', 'IT2021001', 'ACTIVE'),

('Vijay Kumar',          'vijay@college.edu',      '+91-9000000009',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Computer Science', 'CS2020001', 'ACTIVE'),

('Divya Menon',          'divya@college.edu',      '+91-9000000010',
 '$2a$12$3RqE8vT2nY5sB1uM4aXk7.WjHlNp9CcDhK2oQ6eFbZiGwVdJ4tM1A',
 'STUDENT',   'Mathematics',      'MA2022001', 'ACTIVE');

-- Categories
INSERT INTO categories (name, description) VALUES
('Computer Science',    'Programming, algorithms, data structures and computing fundamentals'),
('Artificial Intelligence', 'Machine learning, deep learning, neural networks and AI systems'),
('Database Systems',    'Relational databases, NoSQL, SQL, database design and administration'),
('Networking',          'Computer networks, protocols, security and distributed systems'),
('Mathematics',         'Pure and applied mathematics, statistics and numerical methods'),
('Fiction',             'Novels, short stories and creative literary works'),
('Science',             'Physics, chemistry, biology and general sciences'),
('Web Development',     'Frontend, backend, full-stack web technologies and frameworks');

-- Authors
INSERT INTO authors (name, biography, country) VALUES
('James Gosling',       'Father of Java programming language, former VP at Sun Microsystems.',                  'Canada'),
('Martin Fowler',       'Software developer and author known for refactoring and enterprise patterns.',         'UK'),
('Robert C. Martin',    'Software craftsman, author of Clean Code and SOLID principles advocate.',              'USA'),
('Andrew S. Tanenbaum', 'Professor and author of classic textbooks on operating systems and networks.',         'USA'),
('Abraham Silberschatz','Database researcher and author of the classic Database System Concepts.',              'USA'),
('Thomas H. Cormen',    'Professor at Dartmouth and co-author of Introduction to Algorithms.',                  'USA'),
('Jon Duckett',         'Web designer and author of popular HTML, CSS, and JavaScript books.',                  'UK'),
('Yuval Noah Harari',   'Israeli historian and bestselling author of Sapiens and Homo Deus.',                   'Israel'),
('Stuart Russell',      'AI researcher and co-author of Artificial Intelligence: A Modern Approach.',           'USA'),
('Bill Bryson',         'American-British author of science and travel books including A Short History.',       'USA');

-- Publishers
INSERT INTO publishers (name, address, email, phone, website) VALUES
('Pearson Education',   'One Lake Street, Upper Saddle River, NJ',  'info@pearson.com',       '+1-800-922-0579',  'https://www.pearson.com'),
('O\'Reilly Media',     '1005 Gravenstein Highway North, Sebastopol, CA', 'info@oreilly.com', '+1-707-827-7019',  'https://www.oreilly.com'),
('McGraw-Hill',         '2 Penn Plaza, New York, NY 10121',          'info@mcgraw-hill.com',   '+1-646-766-2000',  'https://www.mheducation.com'),
('Prentice Hall',       '1 Lake Street, Upper Saddle River, NJ',     'info@prenticehall.com',  '+1-201-236-7000',  'https://www.prenticehall.com'),
('Penguin Random House','1745 Broadway, New York, NY 10019',         'info@penguinrandom.com', '+1-212-782-9000',  'https://www.penguinrandomhouse.com');

-- Books (20+ books with variety)
INSERT INTO books (isbn, title, description, author_id, category_id, publisher_id, publication_year, language, total_copies, available_copies, rack_number, status) VALUES
('978-0-13-468599-1',
 'Effective Java',
 'A guide to Java programming best practices. Covers generics, enums, annotations, autoboxing, the for-each loop, varargs, concurrency utilities, and much more.',
 1, 1, 1, 2018, 'English', 5, 4, 'A-101', 'AVAILABLE'),

('978-0-201-63361-0',
 'Design Patterns: Elements of Reusable Object-Oriented Software',
 'The classic Gang of Four book on object-oriented design patterns. Essential reading for every software developer.',
 2, 1, 4, 1994, 'English', 3, 2, 'A-102', 'AVAILABLE'),

('978-0-13-235088-4',
 'Clean Code: A Handbook of Agile Software Craftsmanship',
 'A guide to writing clean, maintainable code. Covers naming, functions, comments, formatting, objects, error handling, and more.',
 3, 1, 4, 2008, 'English', 4, 3, 'A-103', 'AVAILABLE'),

('978-0-13-468600-4',
 'The Clean Coder: A Code of Conduct for Professional Programmers',
 'Robert Martin explains what it means to be a professional programmer. Covers commitment, estimation, pressure, collaboration, and more.',
 3, 1, 4, 2011, 'English', 3, 3, 'A-104', 'AVAILABLE'),

('978-0-13-110362-7',
 'The C Programming Language',
 'The definitive reference for the C programming language by its creators. Essential for systems programmers.',
 4, 1, 4, 1988, 'English', 2, 2, 'A-105', 'AVAILABLE'),

('978-0-07-352332-0',
 'Database System Concepts',
 'Comprehensive introduction to database systems covering relational model, SQL, normalization, transaction processing, and more.',
 5, 3, 3, 2019, 'English', 6, 5, 'B-101', 'AVAILABLE'),

('978-0-07-252395-3',
 'Operating System Concepts',
 'The classic dinosaur book on operating systems. Covers processes, threads, scheduling, memory management, and file systems.',
 4, 1, 3, 2018, 'English', 4, 3, 'A-106', 'AVAILABLE'),

('978-0-262-03384-8',
 'Introduction to Algorithms',
 'The comprehensive textbook on algorithms known as CLRS. Covers sorting, graphs, dynamic programming, and much more.',
 6, 5, 1, 2009, 'English', 5, 4, 'C-101', 'AVAILABLE'),

('978-0-13-359162-9',
 'Computer Networks',
 'A top-down approach to computer networking covering application, transport, network, and link layers.',
 4, 4, 4, 2021, 'English', 4, 4, 'D-101', 'AVAILABLE'),

('978-0-9576832-0-7',
 'HTML and CSS: Design and Build Websites',
 'A beginner-friendly visual guide to HTML5 and CSS3 for building modern websites. Richly illustrated.',
 7, 8, 2, 2011, 'English', 3, 2, 'E-101', 'AVAILABLE'),

('978-0-9576832-1-4',
 'JavaScript and JQuery: Interactive Front-End Development',
 'A visual guide to JavaScript and jQuery for adding interactivity to websites.',
 7, 8, 2, 2014, 'English', 3, 3, 'E-102', 'AVAILABLE'),

('978-0-06-231609-7',
 'Sapiens: A Brief History of Humankind',
 'A sweeping history of humanity from the Stone Age to the 21st century. A global bestseller.',
 8, 6, 5, 2015, 'English', 5, 5, 'F-101', 'AVAILABLE'),

('978-0-06-264001-5',
 'Homo Deus: A Brief History of Tomorrow',
 'A look at the future of humanity exploring how we will deal with death, happiness, and intelligence.',
 8, 6, 5, 2017, 'English', 3, 3, 'F-102', 'AVAILABLE'),

('978-0-13-604259-1',
 'Artificial Intelligence: A Modern Approach',
 'The leading textbook on artificial intelligence. Covers search, planning, learning, natural language processing, and more.',
 9, 2, 4, 2020, 'English', 5, 4, 'G-101', 'AVAILABLE'),

('978-0-262-03516-3',
 'Deep Learning',
 'The comprehensive textbook on deep learning by Goodfellow, Bengio and Courville.',
 9, 2, 1, 2016, 'English', 3, 2, 'G-102', 'AVAILABLE'),

('978-0-7679-2255-3',
 'A Short History of Nearly Everything',
 'Bill Bryson''s entertaining and educational journey through science and the history of scientific discovery.',
 10, 7, 5, 2004, 'English', 4, 4, 'H-101', 'AVAILABLE'),

('978-0-7432-7356-5',
 'The Body: A Guide for Occupants',
 'Bill Bryson''s fascinating exploration of the human body and what happens inside it.',
 10, 7, 5, 2019, 'English', 3, 3, 'H-102', 'AVAILABLE'),

('978-0-596-51774-8',
 'JavaScript: The Good Parts',
 'Douglas Crockford distills the best of JavaScript into a concise guide for writing better JavaScript.',
 7, 8, 2, 2008, 'English', 4, 4, 'E-103', 'AVAILABLE'),

('978-0-13-235088-5',
 'Refactoring: Improving the Design of Existing Code',
 'Martin Fowler''s catalog of refactorings for improving code structure without changing its behavior.',
 2, 1, 4, 2018, 'English', 3, 3, 'A-107', 'AVAILABLE'),

('978-1-491-91205-8',
 'Python Data Science Handbook',
 'A comprehensive guide to data science tools in Python including NumPy, Pandas, Matplotlib, and scikit-learn.',
 3, 2, 2, 2016, 'English', 4, 3, 'G-103', 'AVAILABLE'),

('978-0-13-468601-1',
 'Spring in Action',
 'A comprehensive guide to the Spring framework for Java enterprise application development.',
 2, 1, 4, 2019, 'English', 3, 2, 'A-108', 'AVAILABLE'),

('978-1-491-91209-6',
 'Learning SQL',
 'A gentle introduction to SQL for relational database management.',
 5, 3, 2, 2009, 'English', 5, 5, 'B-102', 'AVAILABLE');

-- Sample Transactions (some current, some historical)
INSERT INTO transactions (user_id, book_id, issued_by, issue_date, due_date, return_date, status) VALUES
-- Active (ISSUED)
(3, 1,  2, CURDATE() - INTERVAL 5  DAY, CURDATE() + INTERVAL 9  DAY, NULL, 'ISSUED'),
(3, 14, 2, CURDATE() - INTERVAL 3  DAY, CURDATE() + INTERVAL 11 DAY, NULL, 'ISSUED'),
(4, 6,  2, CURDATE() - INTERVAL 7  DAY, CURDATE() + INTERVAL 7  DAY, NULL, 'ISSUED'),
(5, 8,  2, CURDATE() - INTERVAL 2  DAY, CURDATE() + INTERVAL 12 DAY, NULL, 'ISSUED'),
-- Overdue
(6, 3,  2, CURDATE() - INTERVAL 20 DAY, CURDATE() - INTERVAL 6  DAY, NULL, 'OVERDUE'),
(7, 2,  2, CURDATE() - INTERVAL 18 DAY, CURDATE() - INTERVAL 4  DAY, NULL, 'OVERDUE'),
-- Returned
(3, 12, 2, CURDATE() - INTERVAL 30 DAY, CURDATE() - INTERVAL 16 DAY, CURDATE() - INTERVAL 17 DAY, 'RETURNED'),
(4, 10, 2, CURDATE() - INTERVAL 25 DAY, CURDATE() - INTERVAL 11 DAY, CURDATE() - INTERVAL 12 DAY, 'RETURNED'),
(8, 7,  2, CURDATE() - INTERVAL 40 DAY, CURDATE() - INTERVAL 26 DAY, CURDATE() - INTERVAL 28 DAY, 'RETURNED'),
(9, 18, 2, CURDATE() - INTERVAL 35 DAY, CURDATE() - INTERVAL 21 DAY, CURDATE() - INTERVAL 22 DAY, 'RETURNED');

-- Update available copies for active transactions
UPDATE books SET available_copies = available_copies - 1 WHERE id = 1;
UPDATE books SET available_copies = available_copies - 1 WHERE id = 14;
UPDATE books SET available_copies = available_copies - 1 WHERE id = 6;
UPDATE books SET available_copies = available_copies - 1 WHERE id = 8;
UPDATE books SET available_copies = available_copies - 1 WHERE id = 3;
UPDATE books SET available_copies = available_copies - 1 WHERE id = 2;

-- Sample Fines for overdue transactions
INSERT INTO fines (transaction_id, user_id, amount, paid_amount, overdue_days, status) VALUES
(5, 6, 12.00, 0.00,  6, 'PENDING'),
(6, 7,  8.00, 0.00,  4, 'PENDING');

-- Sample Reservations
INSERT INTO reservations (user_id, book_id, expiry_date, status) VALUES
(8,  1, CURDATE() + INTERVAL 7 DAY, 'ACTIVE'),
(9,  3, CURDATE() + INTERVAL 5 DAY, 'ACTIVE'),
(10, 14, CURDATE() + INTERVAL 3 DAY, 'ACTIVE');

-- Sample Notifications
INSERT INTO notifications (user_id, title, message, type, is_read, reference_id, reference_type) VALUES
(3, 'Book Issued Successfully',
 'You have successfully issued "Effective Java". Due date: ' || DATE_FORMAT(CURDATE() + INTERVAL 9 DAY, '%d-%m-%Y'),
 'ISSUE', FALSE, 1, 'transaction'),

(3, 'Book Issued Successfully',
 'You have successfully issued "Artificial Intelligence: A Modern Approach". Due date: ' || DATE_FORMAT(CURDATE() + INTERVAL 11 DAY, '%d-%m-%Y'),
 'ISSUE', FALSE, 2, 'transaction'),

(6, 'Book Overdue Alert',
 'Your book "Clean Code" is overdue. Fine: Rs. 12.00. Please return immediately.',
 'OVERDUE', FALSE, 5, 'transaction'),

(7, 'Book Overdue Alert',
 'Your book "Design Patterns" is overdue. Fine: Rs. 8.00. Please return immediately.',
 'OVERDUE', FALSE, 6, 'transaction'),

(8, 'Reservation Confirmed',
 'Your reservation for "Effective Java" has been placed. You will be notified when it is available.',
 'RESERVATION', TRUE, 1, 'reservation'),

(9, 'Reservation Confirmed',
 'Your reservation for "Clean Code" has been placed.',
 'RESERVATION', FALSE, 2, 'reservation'),

(3, 'Welcome to the Library!',
 'Welcome Rahul! Your library account is now active. You can borrow up to 5 books at a time.',
 'GENERAL', TRUE, NULL, NULL),

(4, 'Welcome to the Library!',
 'Welcome Ananya! Your library account is now active.',
 'GENERAL', TRUE, NULL, NULL);

-- Fix notification concat for MySQL (CONCAT instead of ||)
-- (The inserts above will be corrected below via UPDATE)
UPDATE notifications SET message = CONCAT('You have successfully issued "Effective Java". Due date: ', DATE_FORMAT(CURDATE() + INTERVAL 9 DAY, '%d-%m-%Y')) WHERE id = 1;
UPDATE notifications SET message = CONCAT('You have successfully issued "Artificial Intelligence: A Modern Approach". Due date: ', DATE_FORMAT(CURDATE() + INTERVAL 11 DAY, '%d-%m-%Y')) WHERE id = 2;

-- ============================================================
-- VIEWS for common queries
-- ============================================================

CREATE OR REPLACE VIEW v_active_transactions AS
SELECT
    t.id,
    t.issue_date,
    t.due_date,
    t.status,
    u.id AS user_id,
    u.full_name AS user_name,
    u.email AS user_email,
    b.id AS book_id,
    b.title AS book_title,
    b.isbn,
    DATEDIFF(CURDATE(), t.due_date) AS overdue_days
FROM transactions t
JOIN users u ON t.user_id = u.id
JOIN books b ON t.book_id = b.id
WHERE t.status IN ('ISSUED', 'OVERDUE');

CREATE OR REPLACE VIEW v_pending_fines AS
SELECT
    f.id AS fine_id,
    f.amount,
    f.paid_amount,
    (f.amount - f.paid_amount) AS balance,
    f.overdue_days,
    f.status,
    u.id AS user_id,
    u.full_name AS user_name,
    u.email AS user_email,
    b.id AS book_id,
    b.title AS book_title,
    t.due_date,
    t.return_date
FROM fines f
JOIN transactions t ON f.transaction_id = t.id
JOIN users u ON f.user_id = u.id
JOIN books b ON t.book_id = b.id
WHERE f.status IN ('PENDING','PARTIALLY_PAID');

-- ============================================================
-- END OF SCRIPT
-- ============================================================
SELECT 'Library Management System database created successfully!' AS status;
