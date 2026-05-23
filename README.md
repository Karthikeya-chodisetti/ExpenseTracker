# *FinSight*– Personal Finance Manager
A RESTful Expense Tracker API built with **Spring Boot 3**, secured with **JWT**, backed by **MySQL**, and documented via **Swagger UI**.

---

## Features

- 🔐 **JWT Authentication** — secure register/login with stateless token-based auth
- 💰 **Expense Management** — full CRUD with category and date support
- 🔁 **Recurring Expenses** — schedule and auto-track repeated transactions
- 📊 **Budget Management** — set spending limits and monitor usage per category
- 🧱 **DTO Layer** — clean separation between API contracts and internal entities
- ⚠️ **Global Exception Handling** — consistent, structured error responses across all endpoints
- 📖 **Swagger UI** — interactive API documentation out of the box
- 🖥️ **Frontend Dashboard** *(coming soon)* — visual insights and spending analytics

---

## Tech Stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.7 |
| Security | Spring Security + JWT |
| Database | MySQL 8 + Spring Data JPA |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Build | Maven |

---

## Getting Started

### Prerequisites
- Java 21+, MySQL 8+

### Setup

```bash
git clone https://github.com/Karthikeya-chodisetti/ExpenseTracker.git
cd ExpenseTracker
```

Create a MySQL database and configure `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

app.jwt.secret=YOUR_SECRET_KEY
app.jwt.expiration-ms=86400000
```

### Run

```bash
./mvnw spring-boot:run
```

---

## API Documentation

Swagger UI → **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Click **Authorize**, enter `Bearer <your_token>`, and explore all endpoints interactively.

---

> Built by [Karthikeya Chodisetti](https://github.com/Karthikeya-chodisetti)
