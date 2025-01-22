
# Spring Security with JWT

This project demonstrates the use of **Spring Security** and **JWT (JSON Web Tokens)** for securing RESTful APIs in a Spring Boot application. It includes functionalities such as user authentication, registration, role-based access control, password reset, and token refresh.

---

## Features
- **User Authentication**: Secure user login using JWT.
- **Role-Based Access Control**: Restricts access to endpoints based on roles (`ADMIN`, `USER`).
- **Password Management**: Generates and validates password reset codes.
- **Token Management**: Generates, validates, and refreshes JWT tokens.
- **RESTful API**: Provides structured endpoints for client interaction.

---

## Technologies Used
- **Spring Boot**: Simplifies application development.
- **Spring Security**: Handles authentication and authorization.
- **JWT**: Implements stateless authentication.
- **Hibernate/JPA**: Manages data persistence.
- **MySQL Database**: Provides a database.
- **JavaMailSender**: Sends email notifications.
- **Maven**: Manages dependencies and builds the project.

---

## Prerequisites
- **Java 17+**
- **Maven**
- **Git**
- **Postman** (Optional for testing APIs)

---

## API Endpoints

| HTTP Method | Endpoint                             | Description                                | Access Level  |
|-------------|-------------------------------------|--------------------------------------------|---------------|
| **POST**    | `/api/auth/register`                | Registers a new user.                      | Public        |
| **POST**    | `/api/auth/login`                   | Authenticates a user and returns a JWT.    | Public        |
| **POST**    | `/api/auth/refresh-token`           | Refreshes the JWT using a refresh token.   | Public        |
| **GET**     | `/api/auth/send-reset-code/{username}` | Sends a password reset code to the user. | Public        |
| **POST**    | `/api/auth/change-password`         | Changes the user's password.               | Public        |
| **GET**     | `/api/users`                        | Retrieves a list of all users.             | ADMIN only    |
| **PUT**     | `/api/users/{userId}`               | Updates user information by user ID.       | ADMIN/USER    |
| **DELETE**  | `/api/users/{userId}`               | Deletes a user by user ID.                 | ADMIN only    |

---

## Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/anandakmagar/Spring-Security-with-JWT.git
   cd Spring-Security-with-JWT
