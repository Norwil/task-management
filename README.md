# Task Management API

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

A robust RESTful API for managing tasks, built with Spring Boot and following REST best practices. This project demonstrates clean architecture, comprehensive testing, and RESTful API design principles.

![Task Management API](https://i.postimg.cc/pLfg4yRw/Task-Management.png)


## Features

- **Task Management**: Create, read, update, and delete tasks
- **Filtering**: Filter tasks by completion status and priority
- **Validation**: Comprehensive input validation
- **Error Handling**: Meaningful error responses
- **In-Memory Database**: H2 database with Hibernate
- **API Documentation**: Self-documenting with OpenAPI (Swagger)
- **Unit & Integration Tests**: Comprehensive test coverage

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (in-memory)
- **Testing**: JUnit 5, Mockito
- **Documentation**: OpenAPI 3.0

## API Endpoints

### Tasks
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task

### Filters
- `GET /api/tasks/completed?completed={boolean}` - Filter by completion status
- `GET /api/tasks/priority/{priority}` - Filter by priority (HIGH, MEDIUM, LOW)

## Getting Started

### Prerequisites
- Java 17 or later
- Maven 3.6.3 or later

### Installation
1. Clone the repository
2. Build the project: `mvn clean install`
3. Run the application: `mvn spring-boot:run`

The application will start on `http://localhost:8080`

## Project Structure

```
src/
├── main/
│   ├── java/com/TaskManagement/TaskManagement/
│   │   ├── controller/    # REST controllers
│   │   ├── entity/        # JPA entities
│   │   ├── repository/    # Data access layer
│   │   ├── service/       # Business logic
│   │   ├── exception/     # Exception handling
│   │   └── TaskManagementApplication.java
│   └── resources/         # Configuration files
└── test/                  # Test files
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [H2 Database](https://www.h2database.com/)
- [JUnit 5](https://junit.org/junit5/)
- [OpenAPI](https://swagger.io/specification/)

## Best Practices

- RESTful API design
- Proper exception handling
- Input validation
- Unit and integration testing
- Clean code architecture
- Proper logging

## Future Enhancements

- Add user authentication and authorization
- Implement pagination and sorting
- Add more comprehensive documentation
- Add rate limiting
- Containerization with Docker

## License
This project is licensed under the MIT License - see the LICENSE file for details.

