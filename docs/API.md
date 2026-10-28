# Library Management System - API Documentation

## Base URL
```
http://localhost:8080/library-management/api
```

## Authentication
All protected endpoints require a Bearer JWT token in the Authorization header:
```
Authorization: Bearer <token>
```
Obtain the token via the `/api/auth/login` endpoint.

---

## 1. Authentication Endpoints

### POST `/api/auth/login`
Login and receive a JWT token.

**Request Body:**
```json
{
  "email": "admin@library.com",
  "password": "Admin@123"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "fullName": "System Administrator",
      "email": "admin@library.com",
      "role": "ADMIN",
      "status": "ACTIVE"
    }
  }
}
```

---

### POST `/api/auth/register`
Register a new student account.

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@college.edu",
  "phone": "+91-9876543210",
  "password": "Password@123",
  "department": "Computer Science",
  "studentId": "CS2024001"
}
```

**Response (201):**
```json
{ "success": true, "data": { ...user object... } }
```

---

### GET `/api/auth/me`
Get current authenticated user's profile.

**Response (200):**
```json
{ "success": true, "data": { ...user object... } }
```

---

### GET `/api/auth/health`
Health check endpoint.

**Response (200):**
```json
{ "success": true, "data": { "status": "UP" } }
```

---

## 2. Books Endpoints

### GET `/api/books`
List all books with search, filters, sorting, and pagination.

**Query Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| search | string | Search by title, ISBN, or author |
| categoryId | int | Filter by category |
| authorId | int | Filter by author |
| publisherId | int | Filter by publisher |
| availability | string | `available` or `unavailable` |
| language | string | Filter by language |
| yearFrom | int | Publication year from |
| yearTo | int | Publication year to |
| sortBy | string | `title`, `author`, `year`, `available` |
| sortDir | string | `asc` or `desc` |
| page | int | Page number (default: 1) |
| pageSize | int | Results per page (default: 10) |

**Response (200):**
```json
{
  "success": true,
  "data": [...books...],
  "page": 1,
  "pageSize": 10,
  "totalCount": 50,
  "totalPages": 5
}
```

---

### GET `/api/books/{id}`
Get a book by ID.

---

### POST `/api/books`
Create a new book. **Requires ADMIN or LIBRARIAN role.**

**Request Body:**
```json
{
  "isbn": "978-0-13-468599-1",
  "title": "Effective Java",
  "description": "Best practices for Java programming",
  "authorId": 1,
  "categoryId": 1,
  "publisherId": 1,
  "publicationYear": 2018,
  "language": "English",
  "totalCopies": 5,
  "availableCopies": 5,
  "rackNumber": "A-101",
  "status": "AVAILABLE"
}
```

---

### PUT `/api/books/{id}`
Update a book. **Requires ADMIN or LIBRARIAN role.**

---

### DELETE `/api/books/{id}`
Delete a book. **Requires ADMIN role.** Fails if book has active transactions.

---

## 3. Authors Endpoints

### GET `/api/authors`
List all authors (paginated).

**Query Parameters:** `search`, `page`, `pageSize`

### GET `/api/authors/{id}`
Get author by ID.

### POST `/api/authors`
Create author. **Requires ADMIN/LIBRARIAN.**

**Request Body:**
```json
{
  "name": "Robert C. Martin",
  "biography": "Software craftsman...",
  "country": "USA"
}
```

### PUT `/api/authors/{id}`
Update author.

### DELETE `/api/authors/{id}`
Delete author. **Requires ADMIN.** Fails if author has books.

---

## 4. Categories Endpoints

### GET `/api/categories`
List all categories.

### POST `/api/categories`
**Requires ADMIN/LIBRARIAN.**
```json
{ "name": "Computer Science", "description": "..." }
```

### PUT `/api/categories/{id}` | DELETE `/api/categories/{id}`

---

## 5. Publishers Endpoints

### GET `/api/publishers`
### POST `/api/publishers`
```json
{
  "name": "Pearson",
  "address": "...",
  "email": "info@pearson.com",
  "phone": "+1-800-922-0579",
  "website": "https://pearson.com"
}
```
### PUT `/api/publishers/{id}` | DELETE `/api/publishers/{id}`

---

## 6. Users Endpoints

### GET `/api/users`
List users. **Requires ADMIN.**

**Query Parameters:** `search`, `role` (ADMIN/LIBRARIAN/STUDENT), `page`, `pageSize`

### GET `/api/users/{id}`
### POST `/api/users`
**Requires ADMIN.**
```json
{
  "fullName": "Jane Doe",
  "email": "jane@college.edu",
  "phone": "...",
  "password": "Password@123",
  "role": "STUDENT",
  "department": "CS",
  "studentId": "CS2024001"
}
```

### PUT `/api/users/{id}`
Update user. Can only update own profile (or ADMIN can update any).

### DELETE `/api/users/{id}`
Deactivate user. **Requires ADMIN.**

### PUT `/api/users/{id}/password`
Change password.
```json
{
  "currentPassword": "OldPass@123",
  "newPassword": "NewPass@123"
}
```

---

## 7. Transactions Endpoints

### GET `/api/transactions`
List all transactions. **Requires ADMIN/LIBRARIAN.**

**Query Parameters:** `search`, `status` (ISSUED/RETURNED/OVERDUE/LOST), `userId`, `bookId`, `dateFrom`, `dateTo`, `page`, `pageSize`

### GET `/api/transactions/{id}`
### POST `/api/transactions/issue`
Issue a book. **Requires ADMIN/LIBRARIAN.**
```json
{
  "userId": 3,
  "bookId": 1
}
```

**Response:**
```json
{
  "success": true,
  "message": "Book issued successfully.",
  "data": {
    "id": 11,
    "userId": 3,
    "bookId": 1,
    "issueDate": "2024-01-15",
    "dueDate": "2024-01-29",
    "status": "ISSUED"
  }
}
```

### POST `/api/transactions/return`
Return a book. **Requires ADMIN/LIBRARIAN.**
```json
{ "transactionId": 11 }
```

### GET `/api/transactions/my`
Get current student's transactions.

---

## 8. Fines Endpoints

### GET `/api/fines`
List all fines. **Requires ADMIN/LIBRARIAN.**

**Query Parameters:** `search`, `status`, `userId`, `page`, `pageSize`

### GET `/api/fines/{id}`
### POST `/api/fines/{id}/pay`
Record a fine payment.
```json
{ "amount": 10.00 }
```

### PUT `/api/fines/{id}/waive`
Waive a fine. **Requires ADMIN.**

### GET `/api/fines/my`
Get current student's fines.

---

## 9. Reservations Endpoints

### GET `/api/reservations`
List all reservations. **Requires ADMIN/LIBRARIAN.**

### POST `/api/reservations`
Create a reservation (student action).
```json
{ "bookId": 1 }
```

### DELETE `/api/reservations/{id}`
Cancel a reservation.

### GET `/api/reservations/my`
Get current student's reservations.

---

## 10. Notifications Endpoints

### GET `/api/notifications/my`
Get current user's notifications (paginated).

### PUT `/api/notifications/{id}/read`
Mark a notification as read.

### PUT `/api/notifications/read-all`
Mark all notifications as read.

### GET `/api/notifications/unread-count`
Get count of unread notifications.
```json
{ "success": true, "data": 3 }
```

---

## 11. Dashboard Endpoints

### GET `/api/dashboard/admin`
Admin/Librarian dashboard stats. **Requires ADMIN/LIBRARIAN.**

**Response:**
```json
{
  "success": true,
  "data": {
    "totalBooks": 150,
    "availableBooks": 98,
    "issuedBooks": 45,
    "overdueBooks": 7,
    "totalStudents": 250,
    "pendingFines": 12,
    "pendingFinesAmount": 240.00,
    "activeReservations": 8,
    "booksPerCategory": [
      { "name": "Computer Science", "count": 45 },
      ...
    ],
    "monthlyStats": [
      { "month": "2024-01", "issued": 32, "returned": 28 },
      ...
    ],
    "mostBorrowed": [
      { "title": "Effective Java", "count": 15 },
      ...
    ]
  }
}
```

### GET `/api/dashboard/student`
Student personal dashboard.

---

## 12. Recommendations Endpoint

### GET `/api/recommendations/my`
Get personalized book recommendations.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "book": { ...book object... },
      "reason": "Based on your interest in Computer Science"
    },
    ...
  ]
}
```

---

## 13. Reports Endpoints

All require ADMIN or LIBRARIAN role.

### GET `/api/reports/books`
All books report.

### GET `/api/reports/issued`
Issued books report. **Query params:** `dateFrom`, `dateTo`, `userId`

### GET `/api/reports/overdue`
Overdue books report.

### GET `/api/reports/fines`
Fine report. **Query params:** `dateFrom`, `dateTo`, `status`

### GET `/api/reports/transactions`
Full transaction report.

### GET `/api/reports/most-borrowed`
Most borrowed books. **Query params:** `limit` (default: 10)

### GET `/api/reports/export/csv`
Export report as CSV.

**Query params:** `type` (books|issued|overdue|fines|transactions)

**Response:** CSV file download (`Content-Type: text/csv`)

---

## 14. Settings Endpoints

### GET `/api/settings`
Get all library settings.

**Response:**
```json
{
  "success": true,
  "data": [
    { "key": "fine_per_day", "value": "2.00", "description": "Fine per overdue day (₹)" },
    { "key": "max_borrow_limit", "value": "5", "description": "Max books a student can borrow" },
    { "key": "loan_period_days", "value": "14", "description": "Default loan period in days" }
  ]
}
```

### PUT `/api/settings/{key}`
Update a setting. **Requires ADMIN.**
```json
{ "value": "3.00" }
```

---

## Error Response Format
All errors follow this format:
```json
{
  "success": false,
  "error": "Descriptive error message"
}
```

## HTTP Status Codes
| Code | Meaning |
|------|---------|
| 200 | OK |
| 201 | Created |
| 400 | Bad Request / Business Rule Violation |
| 401 | Unauthorized (no token or invalid token) |
| 403 | Forbidden (insufficient role) |
| 404 | Not Found |
| 500 | Internal Server Error |
