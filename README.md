
<p align="center">
   <img src="https://secret-stuffs.netlify.app/assets/icon-B0a1EbX8.png" width="200" height="200" alt="Logo"></img>
</p>

# Secret Stuffs

The **Secret Stuffs** platform is designed as part of **CSCI 5308 - Advanced Topics in Software Development** at Dalhousie University. It addresses the issue of unclaimed items left on curbsides in Nova Scotia by facilitating item donations and claims.

---

## Features

- **User Authentication and Profile Management**: Secure login, signup, and personalized profiles.
- **Item Donation Management**: Post item donations with detailed descriptions and images.
- **Real-Time Chat**: Enable instant communication between donors and claimants.
- **Advanced Search and Filters**: Search donations by categories like item condition or type.
- **Email Notifications**: Receive account verification and password reset emails.
- **Responsive Design**: Fully optimized for both desktop and mobile devices.

---

## Tech Stack

- **Frontend**: React, TypeScript, React Router, Ant Design
- **Backend**: Java, Spring Boot
- **Database**: PostgreSQL
- **Storage**: Firebase Storage
- **Deployment**: Docker, GitHub Actions

---

## Prerequisites

- **Node.js**: Version 18 or above
- **Java JDK**: Version 21
- **Docker**: With Docker Compose (optional)
- **PostgreSQL**: Required if running without Docker
- **Bun**: For managing frontend development

---

## Dependencies

### Frontend Dependencies

#### Core Dependencies
- **@tanstack/react-query**: ^5.59.20
- **antd**: ^5.21.3
- **axios**: ^1.7.7
- **firebase**: ^10.14.1
- **framer-motion**: ^11.11.9
- **js-cookie**: ^3.0.5
- **react**: ^18.3.1
- **react-dom**: ^18.3.1
- **react-router-dom**: ^6.26.2
- **sockjs-client**: ^1.6.1
- **stompjs**: ^2.3.3

#### Development Dependencies
- **@eslint/js**, **eslint-plugin-react-hooks**, **typescript**, and other tools for code linting and quality assurance.

### Backend Dependencies

#### Core Dependencies
- **Spring Boot**: 3.3.4
- **ModelMapper**, **Lombok**, **Spring Boot Starter WebSocket**, and more.

#### Security & Testing Dependencies
- **JWT**: For token-based authentication.
- **JUnit**, **Hibernate Validator**, and other dependencies for validation and testing.

---

## Installation and Setup

### Using Docker (Recommended)

1. Clone the repository:
   ```bash
   git clone https://github.com/CSCI5308/course-project-g04.git
   cd course-project-g04
   ```

2. Set up environment variables in a `.env` file:
   ```bash
   POSTGRES_PASSWORD=your_password
   DOCKER_USERNAME=your_docker_username
   VITE_API_BASE_URL=http://localhost:8082
   ```

3. Build and run the project using Docker Compose:
   ```bash
   docker-compose up --build
   ```

4. Access the application:
   - **Frontend**: http://localhost
   - **Backend**: http://localhost (via nginx proxy to port 8082)
   - **Database**: localhost:5432

---

### Without Docker

#### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Configure PostgreSQL:
   - Install PostgreSQL locally.
   - Create a database named `secret_stuffs`.
   - Update `application-dev.properties` with database credentials.

3. Run the backend server:
   ```bash
   ./mvnw spring-boot:run
   ```

#### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   bun install
   ```

3. Create a `.env` file with your environment variables:
   ```bash
   VITE_API_BASE_URL=http://localhost:8082
   VITE_FIREBASE_API_KEY=your_firebase_api_key
   VITE_FIREBASE_AUTH_DOMAIN=your_firebase_auth_domain
   VITE_FIREBASE_PROJECT_ID=your_firebase_project_id
   VITE_FIREBASE_STORAGE_BUCKET=your_firebase_storage_bucket
   VITE_FIREBASE_MESSAGING_SENDER_ID=your_firebase_messaging_sender_id
   VITE_FIREBASE_APP_ID=your_firebase_app_id
   ```

4. Start the development server:
   ```bash
   bun run dev
   ```

---

## Project Structure

```bash
secret-stuffs/
├── frontend/ # React frontend application
├── backend/ # Spring Boot backend application
├── nginx.conf # Nginx configuration for reverse proxy
├── docker-compose.yml # Docker Compose setup
└── README.md # Project documentation
```

---

## API Documentation

API documentation is currently being developed and will be available in future updates.

---

## Smell Analysis Report
[Smell Analysis Report](https://docs.google.com/spreadsheets/d/1B9_XtVrcggt3jFnji45uywuoq30r-wwgFnXwG2ve9yc/edit?usp=sharing)

---

## Code coverage Report
[Code Coverage Report](https://chandbud5.github.io/g4-smell-report/)

---

## Contributing

1. Fork the repository.
2. Create a new branch for your feature:
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add amazing feature"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/amazing-feature
   ```
5. Open a Pull Request for review.

---

## Team Members

- Kandarp Patel
- Chand Bud
- Jeel Jasani
- Shiyu Huang
- Falgun Patel

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

Special thanks to:
- Dalhousie University
- CSCI 5308 Course Staff

## User Stories

### Story 1: The Moving Student
Sarah, a graduating student from Dalhousie University, is moving out of her apartment. She has a perfectly good desk and some textbooks but can't take them home due to luggage restrictions. Instead of leaving these items on the curbside where they might get damaged by rain or go to waste, she:
- Takes photos of her items
- Creates detailed posts on Secret Stuffs
- Connects with other students who need these items
- Coordinates convenient pickup times through the in-app chat

### Story 2: The Eco-Conscious Resident
Mike recently upgraded his living room furniture. Rather than adding his old couch to landfill waste or leaving it on the curbside where it might get ruined by weather, he:
- Posts clear pictures of the couch on Secret Stuffs
- Describes its condition honestly
- Receives multiple inquiries through the platform
- Safely arranges handover with a local family in need

### Story 3: The New Arrival
Priya, a new international student arriving in Halifax, needs to furnish her apartment on a budget. Instead of buying everything new, she:
- Browses Secret Stuffs by category
- Filters for items near her neighborhood
- Chats with donors to verify item conditions
- Saves money while giving items a second life

### Story 4: The Community Center
The Halifax Community Center regularly receives more donations than they can handle. Using Secret Stuffs, they:
- Create an organizational profile
- Post surplus items regularly
- Reach a wider audience of people in need
- Manage distribution more efficiently

### Story 5: The Seasonal Cleaner
John does his annual spring cleaning and finds his children's outgrown but well-maintained toys and clothes. Instead of throwing them away, he:
- Groups similar items together
- Posts them on Secret Stuffs
- Specifies they're available for bulk collection
- Helps other parents while decluttering sustainably

### Story 6: The Temporary Resident
Lisa, a co-op student in Halifax for 8 months, needs temporary furniture. Through Secret Stuffs, she:
- Finds donated items for her short stay
- Uses them during her internship
- Posts them back on the platform before leaving
- Continues the cycle of reuse

These real-world scenarios demonstrate how Secret Stuffs addresses the common problem of waste and unused items in Nova Scotia, while building a more sustainable and connected community.
