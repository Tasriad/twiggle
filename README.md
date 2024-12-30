# Twiggle - Urban Garden Planner (Backend)

Twiggle is a web application designed to help urban gardeners plan and manage their gardens. It provides personalized gardening suggestions, space-optimized layout designing, and resource management for urban environments, making gardening accessible and enjoyable for everyone.

---

## Project Setup

This project uses **Spring Boot** to build the backend application. Below are the steps to set up the backend on your local machine.

### Prerequisites

- **Java 21** or above
- **Maven** (version 3.6+ recommended)
- **MongoDB** (for NoSQL database)
- **PostgreSQL** (for Supabase database)

### Getting Started

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/twiggle.git
   cd twiggle
   Install Dependencies
   ```

This project uses Maven as the build tool. To install the necessary dependencies, run:

bash
Copy code
mvn clean install
Configure Application Properties

Configure your database connection settings and other properties in src/main/resources/application.properties or src/main/resources/application.yml.

Example for MongoDB:

properties
Copy code
spring.data.mongodb.uri=mongodb://localhost:27017/twiggle
Example for PostgreSQL:

properties
Copy code
spring.datasource.url=jdbc:postgresql://localhost:5432/twiggle
spring.datasource.username=your-username
spring.datasource.password=your-password
Run the Application

Once all dependencies are installed and the configuration is set up, run the application using the following command:

bash
Copy code
mvn spring-boot:run
Access the Application

After the application starts, you can access the backend at:

arduino
Copy code
http://localhost:8080
Maven Dependencies
The project includes the following initial dependencies for backend development:

Spring Web
Spring Data MongoDB
Spring Data JPA
Spring Boot DevTools
Spring Security
JWT (JSON Web Token) Support
PostgreSQL Driver
Flyway (Database Migrations)
Spring Boot Actuator
Micrometer
Spring Boot Starter Test
Spring Boot Starter Logging
Springfox Swagger
Spring Boot Starter Validation
Spring Boot Starter Mail
API Documentation
The backend API is documented using Swagger/OpenAPI. You can access the Swagger UI at:

bash
Copy code
http://localhost:8080/swagger-ui/
Testing
This project includes unit and integration tests using JUnit and Mockito. To run the tests, execute:

bash
Copy code
mvn test
Logging and Monitoring
The application includes Spring Boot Actuator for health checks, metrics, and monitoring. You can access the health endpoints at:

bash
Copy code
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
Deployment
This application can be packaged and deployed as a JAR file:

bash
Copy code
mvn clean package
Once packaged, you can deploy the twiggle.jar to your preferred environment.

Contribution
Contributions are welcome! Please fork the repository, make your changes, and submit a pull request.

License
This project is licensed under the MIT License - see the LICENSE file for details.

Contact
For any questions or inquiries, please contact us at contact@solace.dev.

markdown
Copy code

### Key Points:

- **Structured and clear sections** for setup, dependencies, testing, logging, etc.
- **API Documentation** section with a link to Swagger UI.
- **Deployment** instructions for building a JAR file.
- **Contribution guidelines** for collaboration.

This template provides a good starting point for the backend setup and can be expanded further as the project progresses.
