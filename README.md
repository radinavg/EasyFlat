# EasyFlat

EasyFlat is a comprehensive solution designed to enhance shared living experiences by providing tools for organization, finance tracking, meal planning, and household task management. Whether you need to manage groceries, split expenses, or coordinate chores, EasyFlat streamlines the process.

## About the Project

This project was developed as part of a university coursework assignment, following Agile principles, specifically the SCRUM methodology. Our team worked under the guidance of academic advisors to ensure an efficient and scalable solution for communal living.

## Features

### Digital Storage
- Maintain a virtual inventory of kitchen supplies.
- Track available ingredients to reduce waste and improve meal planning.

### Barcode Scanner
- Quickly add groceries by scanning barcodes.
- Ensure accurate and fast inventory updates.

### Recipe Suggestions
- Get personalized recipe recommendations based on available ingredients.
- Integrated with an external database for diverse meal options.

### Expense Tracking
- Record and split household expenses.
- Keep a transparent financial record among flatmates.

### Event and Calendar Management
- Plan and schedule shared activities.
- Keep everyone informed with a shared calendar.

### Chore Management
- Assign and track household tasks efficiently.
- Generate structured weekly schedules automatically.

### User & Group Management
- Easily manage user access and permissions.
- Secure and organized group coordination.

## Technology Stack

### Frontend
- Angular for a dynamic and interactive UI
- Bootstrap for responsive design
- TypeScript for scalable development
- HTML5 & CSS3 for structured and styled components

### Backend
- Spring Boot for backend services
- Java for core application logic
- Hibernate for database management
- Spring Security for user authentication and authorization
- H2 Database for lightweight storage
- Maven for dependency management
- REST API for seamless frontend-backend communication
- JUnit 5 & Mockito for testing

## Development Process

Using Agile methodologies, the team followed structured sprints, daily stand-ups, and iterative feedback integration to refine the product continuously.

## Installation Guide

### Frontend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/IAndreev1/EasyFlat.git
   ```
2. Navigate to the frontend directory:
   ```bash
   cd EasyFlat/frontend
   ```
3. Install dependencies:
   ```bash
   npm install
   ```
4. Run the application:
   ```bash
   ng serve
   ```
   The frontend will be available at [http://localhost:4200](http://localhost:4200).

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd EasyFlat/backend
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   The backend will be accessible at [http://localhost:8080](http://localhost:8080).

## Testing & Quality Assurance

To ensure reliability, the application undergoes extensive testing:
- Unit Testing with JUnit 5 and Mockito
- Integration Testing for component interactions
- Test-Driven Development (TDD) principles

## Get Started

Clone the repository, set up the environment, and experience a more organized and efficient approach to shared living.

# EasyFlat