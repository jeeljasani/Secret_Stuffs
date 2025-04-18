# Secret Stuffs Backend Service

This is the backend service for Secret Stuffs platform, built with Spring Boot. It provides RESTful APIs for item donations and claims management, user authentication, real-time chat, and email notifications.

## Technology Stack

- **Framework**: Spring Boot 3.3.4
- **Language**: Java 21
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Email Service**: Spring Mail
- **WebSocket**: Spring WebSocket
- **Build Tool**: Maven
- **API Documentation**: Coming Soon

## Project Structure

```bash
backend
├── src
│   ├── main
│   │   ├── java
│   │   │   └── secretstuffs
│   │   │       ├── application # Application layer with business logic
│   │   │       │   ├── exception # Custom exceptions and error handling
│   │   │       │   ├── helpers # Utility classes
│   │   │       │   ├── services # Business logic services
│   │   │       │   └── useCases # Use case implementations for auth, items, users
│   │   │       ├── controllers # REST API endpoints and request handling
│   │   │       ├── domain # Domain layer - core business objects
│   │   │       │   ├── configurations # Spring Boot configurations
│   │   │       │   ├── dtos # Data Transfer Objects for commands and responses
│   │   │       │   ├── entities # JPA entities and domain models
│   │   │       │   ├── enums # Enum types
│   │   │       │   ├── models # Request and response models for API endpoints
│   │   │       │   └── validation # Validation rules
│   │   │       └── infrastructure
│   │   │           └── repositories # JPA repositories
│   │   └── resources 
│   │       ├── Static # Static resources
│   │       └── Templates # Email templates
│   └── test # Test cases
└── target # Build output
```

## Key Features

- User authentication and authorization with JWT
- Item donation post management
- Real-time chat using WebSocket
- Email notifications for:
  - Account verification
  - Password reset
  - Password change confirmation
- File upload support
- Cross-Origin Resource Sharing (CORS) configuration
- Global exception handling
- Database integration with PostgreSQL

## Environment Profiles

The application supports multiple environment profiles:

- **Development** (`application-dev.properties`)
  - Local development settings
  - Debug mode enabled
  - CORS configured for local frontend

- **Production** (`application-prod.properties`)
  - Production-ready settings
  - Enhanced security
  - Configured for containerized deployment

## Security

- Password encryption using BCrypt
- JWT-based authentication
- Secure email service configuration
- CORS policy enforcement
- Input validation

## Logging

- Different log levels for various components
- Structured logging format

## TDD Implementation
Our project follows Test-Driven Development principles. Below are key commits that demonstrate our TDD approach:
| Functionality | Commit | Description |
| --- | --- | --- |
| Get Item Posts | [Link](https://github.com/CSCI5308/course-project-g04/pull/121/commits/2fd7e2d818a7d2b9c5e5ae65e84db5a69fa3d88f) | initial tests for the get item posts |
| Get Item Post | [Link](https://github.com/CSCI5308/course-project-g04/pull/121/commits/c4c553896b9f88b7bf7c392cd57ac665e4a3db1e) | tests for invalid item post ID |
| Get Item Post | [Link](https://github.com/CSCI5308/course-project-g04/pull/121/commits/f794fd36af3913bc774b0875f197de1cb2be6667) | implementation for get item post by ID |
| Get Item Post | [Link](https://github.com/CSCI5308/course-project-g04/pull/121/commits/9bd7bd34ec5820b25c194fadd200d74cbd2c31bf) | Refactoring and cleanup |
| Get Item Post | [Link](https://github.com/CSCI5308/course-project-g04/pull/121/commits/74e2d2896a60caa55aa8a0a6675527563a82f8ac) | Refactor controller and service |
| Fetch Recipients | [Link](https://github.com/CSCI5308/course-project-g04/pull/143/commits/015a4853872e272a82f03c4de7696d939dac52a2) | Add tests for fetch recipients list |
| Fetch Recipients | [Link](https://github.com/CSCI5308/course-project-g04/pull/143/commits/8b8a3c660b7fdcd7f251da5e35d9c1e7047c637e) | Add implementation for fetch recipients list |
| Fetch Recipients | [Link](https://github.com/CSCI5308/course-project-g04/pull/143/commits/c970ba17e6390810f1206eb4fb73d10a55078b1d) | refactoring fetch recipients list |


### TDD Principles Applied
- Write failing test first
- Implement minimum code to pass the test
- Refactor and improve code while maintaining test coverage

## Contributing

Please refer to the main project repository's contributing guidelines.

## License

This project is licensed under the MIT License - see the LICENSE file for details.