# The Reading Room

A warm, role-based library app for managing a college collection and its circulation. This workspace contains the React/Vite interface and the existing Java/MySQL API. The interface has been refreshed with a paper-and-forest palette, serif display type, responsive login page, and shared dashboard styling.

## Features

- Role-based Admin, Librarian, and Student workspaces
- Book catalog, circulation, reservations, fines, notifications, dashboards, and reports
- Responsive React interface with search, forms, tables, charts, and QR utilities
- Java 17, MySQL, and React/Vite

## Setup on macOS

### 1. Check prerequisites

```zsh
java -version
mvn -v
node -v && npm -v
mysql --version
```

Use JDK 17 or newer. If Maven is missing, run `brew install maven`. If MySQL is not running, run `brew services start mysql`.

### 2. Create the database

```zsh
mysql -u root -proot < database/reading_room.sql
mysql -u root -proot -e "USE reading_room; SHOW TABLES;"
```

If you imported an earlier version of this seed, update the demo account password hashes without dropping your tables:

```zsh
mysql -u root -proot < database/fix_demo_passwords.sql
```

### 3. Start the backend

```zsh
cd backend
cp src/main/resources/application.properties.example src/main/resources/application.properties
mvn spring-boot:run
```

Backend API: `http://localhost:8080/api`.

### 4. Start the frontend in another Terminal tab

```zsh
cd frontend
cp .env.example .env
npm install
npm run dev
```

Open `http://localhost:5173`. The API base URL is set in `frontend/.env` as `VITE_API_URL=http://localhost:8080/api`.

## Demo accounts

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@readingroom.com` | `Admin@123` |
| Librarian | `librarian@readingroom.com` | `Librarian@123` |
| Student | `student@readingroom.com` | `Student@123` |

## Project structure

```text
backend/       Java Maven application and API
database/      MySQL schema and demo seed
frontend/      React/Vite app, shared components, pages, and styles
```

## Troubleshooting (macOS)

- **MySQL is stopped:** run `brew services start mysql`. For root access errors, check the local MySQL root password and update `spring.datasource.password` in `application.properties`.
- **Port 8080 or 5173 is busy:** inspect with `lsof -i :8080` or `lsof -i :5173`, then stop the listed process with `kill <PID>`.
- **Wrong Java version:** check with `java -version`; select an installed JDK with `export JAVA_HOME=$(/usr/libexec/java_home -v 17)` and reopen the terminal if needed.
- **Maven not found:** install it with `brew install maven`, then verify using `mvn -v`.

## Current implementation note

The backend conversion is still partial. Spring Boot provides database-backed login, student registration, session-token lookup, logout, and health endpoints. The documented admin, librarian, and student demo sign-ins have been verified against the local database. Other existing servlet/JDBC modules have not yet been replaced with JPA repositories and secured controllers, so circulation, role enforcement, and the other library APIs are not confirmed to work.
