# Secure Student Management REST API

## Project Description

The Secure Student Management REST API is a backend application developed using Java and Spring Boot. It provides secure APIs for managing students and users.

The system uses JWT authentication and Role-Based Access Control (RBAC) to control access to protected endpoints.

## Technologies Used

* Java 21
* Spring Boot
* Spring Security
* JWT (JSON Web Token)
* JJWT
* PostgreSQL
* Hibernate / JPA
* Maven
* Postman / Thunder Client
* Git and GitHub

## System Roles

The system has three user roles:

### ADMIN

* Create users
* View students
* Add students
* Update students
* Delete students

### TEACHER

* View students
* Add students
* Update students
* Delete students

### STUDENT

* View students
* Cannot add students
* Cannot update students
* Cannot delete students

## Authentication

The API uses JWT Bearer Token authentication.

After successful login, the user receives an access token. The access token must be included when accessing protected endpoints.

Example:

```text
Authorization: Bearer <access-token>
```

## API Endpoints

### Authentication

#### Register User

```http
POST /api/auth/register
```

#### Login

```http
POST /api/auth/login
```

### User Management

#### Create User

```http
POST /api/users
```

Only ADMIN users are allowed to create users.

### Student Management

#### Get All Students

```http
GET /api/students
```

#### Get Student by ID

```http
GET /api/students/{id}
```

#### Create Student

```http
POST /api/students
```

ADMIN and TEACHER users are allowed.

#### Update Student

```http
PUT /api/students/{id}
```

ADMIN and TEACHER users are allowed.

#### Delete Student

```http
DELETE /api/students/{id}
```

ADMIN and TEACHER users are allowed.

## Student Request Example

```json
{
  "registrationNumber": "DIT/COE/003",
  "fullName": "Michael Student",
  "email": "michael@gmail.com"
}
```

## Security Testing

The API was tested using different user roles.

| Role    | GET     | POST      | PUT       | DELETE    |
| ------- | ------- | --------- | --------- | --------- |
| ADMIN   | Allowed | Allowed   | Allowed   | Allowed   |
| TEACHER | Allowed | Allowed   | Allowed   | Allowed   |
| STUDENT | Allowed | Forbidden | Forbidden | Forbidden |

Unauthorized operations return:

```text
403 Forbidden
```

Requests without valid authentication return:

```text
401 Unauthorized
```

## Database

The application uses PostgreSQL as the database management system.

Database configuration is stored in:

```text
src/main/resources/application.properties
```

## Project Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL Database
```

Security is handled using Spring Security and JWT.

## How to Run the Project

### 1. Clone the repository

```bash
git clone <repository-url>
```

### 2. Open the project

Open the project using Visual Studio Code or another Java IDE.

### 3. Configure PostgreSQL

Create the required PostgreSQL database and update the database configuration in `application.properties`.

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## Testing

The API can be tested using:

* Postman
* Thunder Client

Authentication should be performed first to obtain a JWT access token.

The token should then be added to protected requests using:

```text
Authorization: Bearer <access-token>
```

## Project Structure

```text
secure-student-management/
│
├── .gitignore
├── README.md
├── pom.xml
│
└── src/
    └── main/
        ├── java/
        │   └── com/example/student_management_system/
        │       ├── auth/
        │       ├── security/
        │       ├── user/
        │       └── student/
        │
        └── resources/
            └── application.properties
```

## Conclusion

The Secure Student Management REST API provides a secure backend for managing students and users. JWT authentication, password hashing, Spring Security, and role-based authorization are used to protect the system and control access to different operations.
