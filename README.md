# Java Task Management System

A Java Swing desktop application for managing employees, hierarchical tasks, workloads, and task completion statistics.

## Project Overview

The application provides a graphical interface for creating, organizing, and assigning tasks to employees.

The Composite design pattern allows simple and complex tasks to be managed through a common abstraction, supporting nested task hierarchies and recursive duration calculations.

## Features

- Employee creation and management
- Simple and complex task creation
- Hierarchical task organization and nesting
- Task assignment to employees
- Task completion status management
- Recursive task duration estimation
- Employee workload calculation
- Identification of employees exceeding 40 hours of completed work
- Employee task completion statistics
- Persistent data storage using Java serialization

## Software Architecture

| Package | Responsibility |
|---------|----------------|
| `application` | Swing graphical user interface |
| `model` | Employee and task entities |
| `service` | Task management and business logic |
| `util` | Workload analysis and task statistics |

## Composite Design Pattern

The task hierarchy consists of three main classes:

- `Task` - Abstract sealed class defining the common task structure and behavior.
- `SimpleTask` - Individual task with a duration calculated from its start and end times.
- `ComplexTask` - Composite task containing a collection of simple or complex subtasks.

Complex task durations are calculated recursively by aggregating the durations of their subtasks.

## Technical Implementation

- Object-oriented programming
- Composite design pattern
- Abstraction, inheritance, and polymorphism
- Sealed classes
- Recursive data structures and traversal
- Java Collections Framework
- Lambda expressions and comparator-based sorting
- Event-driven graphical user interface
- Object serialization and deserialization

## Technologies

- Java 25
- Java Swing
- Maven
- IntelliJ IDEA

## Repository Structure

- `src/main/java/application/` - Graphical user interface
- `src/main/java/model/` - Domain models
- `src/main/java/service/` - Business logic
- `src/main/java/util/` - Statistical operations
- `pom.xml` - Maven configuration

## Running the Application

1. Clone or download the repository.
2. Open the project in IntelliJ IDEA.
3. Import the project using its Maven configuration.
4. Configure JDK 25 as the project SDK.
5. Run the `application.TasksGUI` main class.

The application saves employee and task data to a local `tasks.dat` file and restores previously saved data when reopened.

## Future Improvements

- Database-backed persistence
- Automated unit testing
- Improved input validation and exception handling
- Cycle prevention in nested task hierarchies
- Enhanced reporting and filtering capabilities
