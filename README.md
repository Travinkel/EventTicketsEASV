This `README.md` file provides an overview of the project, its features, technologies used, project structure, and instructions for getting started.

# Event Ticket System

## Overview
The Event Ticket System is a Java-based application designed to manage events, tickets, users, and roles. It provides functionalities for event coordinators and administrators to handle various operations related to events and ticketing.

## Features
- **Event Management**: Create, update, delete, and find events.
- **Ticket Management**: Create, update, delete, and find tickets.
- **User Management**: Create, update, delete, and find users.
- **Role Management**: Assign roles to users and manage user roles.
- **PDF Handling**: Send and print PDF tickets.

## Technologies Used
- **Java**
- **SQL**
- **Maven**
- **JavaFX**

## Project Structure
## Project Structure
- `dal/connection`: Manages the database connection.
- `dal/dao`: Data Access Objects (DAOs) for database operations.
    - `EventDAO.java`: Handles `Event` entity operations.
    - `TicketDAO.java`: Handles `Ticket` entity operations.
    - `UserDAO.java`: Handles `User` entity operations.
    - `UserRoleDAO.java`: Handles `UserRole` entity operations.
- `dal/models`: Data models representing the entities.
    - `Event.java`: Represents an event.
    - `Ticket.java`: Represents a ticket.
    - `User.java`: Represents a user.
    - `UserRole.java`: Represents a user role.
- `dal/repositories`: Repositories for handling business logic.
    - `AdminRepository.java`: Manages administrative tasks.
    - `EventCoordinatorRepository.java`: Manages event coordinators.
    - `IEventCoordinatorRepository.java`: Interface for `EventCoordinatorRepository`.
- `utils`: Utility classes and dependency injection.
    - `di/Injectable.java`: For dependency injection.
    - `PDFGenerator.java`: For generating PDF tickets.
- `Main.java`: The main entry point of the application.
- `resources`: Configuration files.
    - `application.properties`: Configuration file for database connection.
    - 
## Architecture
The system follows a layered architecture with the following layers:
1. **Presentation Layer**: JavaFX-based user interface.
2. **Service Layer**: Business logic and service classes.
3. **Data Access Layer**: DAOs for database operations.
4. **Database Layer**: MSSQL Server for data persistence.

## Design Patterns Used
- **DAO Pattern**: For data access operations.
- **Singleton Pattern**: For database connection management.
- **Dependency Injection**: For managing dependencies between classes.

## Database Schema
The database schema includes the following tables:
- **Users**: Stores user information.
- **Events**: Stores event information.
- **Tickets**: Stores ticket information.
- **UserRoles**: Stores user roles.

## DAOs
- **EventDAO**: Handles database operations related to `Event` entities.
- **UserDAO**: Handles database operations related to `User` entities.
- **TicketDAO**: Handles database operations related to `Ticket` entities.
- **UserRoleDAO**: Handles database operations related to `UserRole` entities.

## Repositories
- **EventCoordinatorRepository**: Manages event coordinators and their related operations.
- **AdminRepository**: Manages administrative tasks including user and role management.

## Getting Started## Getting Started
### Prerequisites
- Java 11 or higher
- Maven
- A database (e.g., MSSQL Server)

### Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/event-ticket-system.git