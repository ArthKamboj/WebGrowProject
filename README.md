# WebGrowProject

# Event Management Website

Welcome to the **Event Management Website**, a robust platform designed to streamline the organization and participation of events. This application supports two user roles:

- **Host**: Organizers who create, manage, and oversee events.
- **Participant**: Attendees who register, join teams, participate in quizzes, and engage with the events.

This project is built using **Spring Boot** and adheres to modern development practices, including **role-based authentication** and **scalable REST APIs**.

---

## Key Features

### General Features
- **Authentication System**: 
  - User registration with role assignment (Host/Participant).
  - Login/Logout functionality.
  - Password recovery via email (OTP-based validation).
  - Secure password reset and validation.
- **Role-Based Authorization**:
  - Granular control over features accessible to each role.
  - JWT-based authentication ensures secure access.

---

### Host Features
1. **Event Management**:
   - Create, update, and delete events.
   - View a detailed dashboard of event statistics.
   - Set event preferences like team creation, minimum/maximum team size.
2. **Room Management**:
   - Allocate and manage rooms for events.
   - Specify the number of rooms and customize room names (e.g., *Room-1*, *Room-2*).
   - Mark rooms as *Vacant* or *Filled* during events (visible only to the host).
3. **Quiz Creation**:
   - Create quizzes with multiple-choice questions for event participants.
   - Configure quiz start times, durations, and scoring rules.
4. **Participant Tracking**:
   - View and manage event participants.
   - Monitor participation in real-time during events.

---

### Participant Features
1. **Event Registration**:
   - Register for available events.
   - View event details, schedule, and rules.
2. **Team Management**:
   - Create or join teams for events (if allowed by the host).
   - Adhere to host-defined team size constraints.
3. **Quiz Participation**:
   - Participate in quizzes created by the host.
   - Submit answers and view results.
4. **Profile Management**:
   - Update personal details like name, email, and password.

---

## Technology Stack
- **Backend**: Spring Boot (3.1.5)
- **Database**: CockroachDB
- **Security**: JWT-based authentication
- **Frontend**: (To be integrated or as per requirements)
- **Tools**: IntelliJ IDEA, Maven

---

## Prerequisites
- Java 17+
- Maven
- CockroachDB setup
- IntelliJ IDEA or any IDE of your choice

