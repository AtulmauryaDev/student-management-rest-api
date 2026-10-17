# Student Management REST API

A beginner-friendly Spring Boot REST API project using Java 21, Spring Data JPA, and MySQL.

## Requirements

- JDK 21 or newer
- MySQL Server
- IntelliJ IDEA (or another Java IDE)

## Configure MySQL

1. Create the database:

   ```sql
   CREATE DATABASE student_db;
   ```

2. Open `src/main/resources/application.properties` and configure your local MySQL username and password. Do not commit real credentials to Git.
3. Start MySQL.
4. Run `StudentmanagementApplication.java` from IntelliJ, or run `mvn spring-boot:run` from the project directory.

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, so it creates/updates the `students` table. This is convenient for a learning project; production projects should use database migrations.

## API endpoints

Base URL: `http://localhost:8080/api/students`

| Method | Endpoint | Purpose | Successful status |
|---|---|---|---|
| POST | `/api/students` | Create a student | `201 Created` |
| GET | `/api/students?page=0&size=10` | List students with pagination | `200 OK` |
| GET | `/api/students?sortBy=name&direction=desc` | Sort the student list | `200 OK` |
| GET | `/api/students/search?name=rah&page=0&size=5` | Search by partial name, case-insensitive | `200 OK` |
| GET | `/api/students/3` | Get one student by ID | `200 OK` or `404 Not Found` |
| PUT | `/api/students/3` | Update a student | `200 OK` or `404 Not Found` |
| DELETE | `/api/students/3` | Delete a student | `204 No Content` or `404 Not Found` |

Sorting is allowed only by `id`, `name`, `email`, and `course`. Page numbers start at `0`; page size must be between `1` and `100`.

## Example request body

Use this JSON body for POST and PUT requests:

```json
{
  "name": "Rahul Kumar",
  "email": "rahul@example.com",
  "course": "MCA"
}
```

The request DTO validates that name, email, and course are provided and that email has a valid format.

## Search examples

- `GET /api/students/search?name=Rahul` — finds names containing `Rahul`.
- `GET /api/students/search?name=rah` — also finds `Rahul`, because search ignores case.
- `GET /api/students/search?name=Rah&page=0&size=5&sortBy=name&direction=asc` — combines search, pagination, and sorting.
- A search with no matches returns `200 OK` with an empty `content` array and `totalElements: 0`.

## Interview architecture explanation

1. **Controller** receives HTTP requests, validates request input, and returns HTTP responses.
2. **DTOs** define the request/response shape without exposing the persistence entity as the API contract.
3. **Service** contains the application-level operations and calls the repository.
4. **Repository** extends `JpaRepository`, so Spring Data JPA provides CRUD operations and derives the name-search query from `findByNameContainingIgnoreCase`.
5. **Entity** maps the `Student` Java class to the `students` database table.
6. **GlobalExceptionHandler** converts bean-validation failures into HTTP `400 Bad Request` responses.

## Suggested manual test order in Postman

1. POST a student using the JSON body above.
2. GET `/api/students?page=0&size=5`.
3. GET `/api/students/search?name=Rah`.
4. GET `/api/students/{id}` using the ID returned by POST.
5. PUT `/api/students/{id}` with updated JSON.
6. Try invalid input (for example, an invalid email) and check for `400 Bad Request`.
7. DELETE `/api/students/{id}` and confirm `204 No Content`.
8. Try the same ID again and confirm `404 Not Found`.

This is an educational project, not a production-secured service. Authentication, authorization, API documentation, and database migrations can be added as separate enhancements if the role requires them.
