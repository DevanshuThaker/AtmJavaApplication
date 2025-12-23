# ATM Java Application

A full-stack **ATM Management System** built using **Java, Spring Boot, MySQL, and Thymeleaf**.  
This project simulates basic ATM operations with user authentication and database-backed transactions.

---

## 🚀 Features

- User account creation
- Secure login using User ID and PIN
- Check account balance
- Deposit money
- Withdraw money
- Transfer money to another account
- Transaction handling with database persistence
- Web-based UI with light/dark theme

---

## 🛠 Tech Stack

- **Backend:** Java, Spring Boot
- **Frontend:** Thymeleaf, HTML, CSS, JavaScript
- **Database:** MySQL
- **ORM:** Spring Data JPA
- **Security:** Spring Security
- **Build Tool:** Maven
- **Container Support:** Docker Compose (MySQL)

---

## 📂 Project Structure

AtmJava/
├── src/main/java
│ └── com.example.atmjava
│ ├── controller
│ ├── service
│ ├── repository
│ ├── model
│ ├── security
│ └── config
├── src/main/resources
│ ├── templates
│ ├── static
│ └── application.properties
├── pom.xml
└── compose.yaml

## ⚙️ Setup & Run Instructions

### 1️⃣ Prerequisites
- Java 17+
- Maven
- MySQL Server
- Git (optional)

---

### 2️⃣ Database Setup

Create a MySQL database:
```sql
CREATE DATABASE atm;
Update credentials in application.properties:

properties
Copy code
spring.datasource.url=jdbc:mysql://localhost:3306/atm
spring.datasource.username=root
spring.datasource.password=your_password
