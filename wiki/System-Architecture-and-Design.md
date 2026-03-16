# System Architecture and Design

## 1. Purpose

This document presents the proposed architecture and design for the **Cloud-based Ticket Reservation Application**. It is based on the project README, the intended web-based delivery model for the project, and the database schema supplied in the project brief.

## 2. Architectural Overview

The system follows a **layered cloud architecture**:

- **Presentation layer**: browser-based web client for customers and administrators
- **Application layer**: cloud-hosted services that handle authentication, event management, reservations, and confirmations
- **Data layer**: relational database storing users, events, venues, categories, reservations, and confirmations
- **Integration layer**: external email/SMS services for digital confirmations

This structure supports the README requirements for concurrency, high availability, and a user-friendly interface.

## 3. High-Level Architecture Diagram

```mermaid
flowchart LR
    Customer[Customer]
    Admin[Administrator]
    App[Web Application]
    API[Cloud API / Application Services]
    Auth[Authentication Service]
    EventSvc[Event Management Service]
    ResSvc[Reservation Service]
    ConfirmSvc[Confirmation Service]
    DB[(Relational Database)]
    Notify[Email / SMS Provider]

    Customer --> App
    Admin --> App
    App --> API
    API --> Auth
    API --> EventSvc
    API --> ResSvc
    API --> ConfirmSvc
    Auth --> DB
    EventSvc --> DB
    ResSvc --> DB
    ConfirmSvc --> DB
    ConfirmSvc --> Notify
```

## 4. Main Components

| Component | Responsibility |
| --- | --- |
| Web Application | Provides pages for registration, browsing, search, reservation, cancellation, and admin event management |
| Authentication Service | Registers users, verifies credentials, and enforces role-based access |
| Event Management Service | Creates, updates, cancels, and retrieves event information |
| Reservation Service | Validates availability, creates reservations, updates ticket counts, and handles cancellations |
| Confirmation Service | Builds and records confirmation messages, then sends them by email or SMS |
| Relational Database | Stores all business entities and preserves transactional consistency |

## 5. Use Case View

```mermaid
flowchart LR
    Customer[Customer]
    Admin[Administrator]

    UC1([Register Account])
    UC2([Browse Events])
    UC3([Search and Filter Events])
    UC4([View Event Details])
    UC5([Reserve Tickets])
    UC6([Cancel Reservation])
    UC7([Receive Confirmation])
    UC8([Add Event])
    UC9([Edit Event])
    UC10([Cancel Event])

    Customer --- UC1
    Customer --- UC2
    Customer --- UC3
    Customer --- UC4
    Customer --- UC5
    Customer --- UC6
    Customer --- UC7

    Admin --- UC8
    Admin --- UC9
    Admin --- UC10
    Admin --- UC2
    Admin --- UC4
```

## 6. Domain Class Diagram

```mermaid
classDiagram
    class User {
        +int userId
        +String firstName
        +String lastName
        +String email
        +String phoneNumber
        +String passwordHash
        +Role role
        +DateTime createdAt
    }

    class Event {
        +int eventId
        +String title
        +String description
        +DateTime eventDate
        +int availableTickets
        +decimal price
        +EventStatus status
        +create()
        +update()
        +cancel()
    }

    class Venue {
        +int venueId
        +String venueName
        +String address
        +String city
        +int capacity
    }

    class Category {
        +int categoryId
        +String categoryName
    }

    class Reservation {
        +int reservationId
        +int numberOfTickets
        +DateTime reservationDate
        +ReservationStatus status
        +decimal totalPrice
        +confirm()
        +cancel()
    }

    class Confirmation {
        +int confirmationId
        +ConfirmationType type
        +DateTime sentAt
        +String confirmationMessage
        +send()
    }

    User "1" --> "0..*" Reservation : makes
    User "1" --> "0..*" Event : creates
    Venue "1" --> "0..*" Event : hosts
    Category "1" --> "0..*" Event : classifies
    Event "1" --> "0..*" Reservation : contains
    Reservation "1" --> "0..*" Confirmation : generates
```

## 7. Reservation Sequence Diagram

```mermaid
sequenceDiagram
    actor Customer
    participant App as Web Application
    participant API as Cloud API
    participant EventSvc as Event Service
    participant ResSvc as Reservation Service
    participant DB as Database
    participant ConfirmSvc as Confirmation Service
    participant Notify as Email/SMS Provider

    Customer->>App: Select event and ticket quantity
    App->>API: Submit reservation request
    API->>EventSvc: Check event status and availability
    EventSvc->>DB: Read event record
    DB-->>EventSvc: Event details and available tickets
    EventSvc-->>API: Event can be reserved
    API->>ResSvc: Create reservation
    ResSvc->>DB: Insert reservation and decrement tickets
    DB-->>ResSvc: Reservation saved
    ResSvc-->>API: Reservation confirmed
    API->>ConfirmSvc: Request confirmation
    ConfirmSvc->>DB: Store confirmation entry
    ConfirmSvc->>Notify: Send email or SMS
    Notify-->>ConfirmSvc: Delivery result
    ConfirmSvc-->>API: Confirmation completed
    API-->>App: Return success response
    App-->>Customer: Show confirmation details
```

## 8. Database Design

The following ER diagram is the wiki-ready version of the provided database image.

![Ticket Reservation ER Diagram](images/ticket-reservation-er-diagram.svg)

### Entity Summary

| Entity | Purpose |
| --- | --- |
| Users | Stores customer and administrator identities |
| Venues | Stores physical event locations and capacities |
| Categories | Classifies events such as movie, concert, travel, or sports |
| Events | Stores the core event catalogue and availability information |
| Reservations | Records ticket bookings and their status |
| Confirmations | Stores confirmation messages sent for reservations |

### Key Relationships

- One user can create many reservations.
- One administrator can create many events.
- One venue can host many events.
- One category can classify many events.
- One event can have many reservations.
- One reservation can have one or more confirmation records.

## 9. Design Decisions

### Layered Separation

Separating the web client, services, and database improves maintainability and supports future testing and deployment changes.

### Relational Data Model

A relational schema fits the project well because reservations, events, and confirmations have clear relationships and transactional constraints.

### Role-Based Access

The `role` attribute on the user entity supports two main actor types: customer and administrator.

### Event Status Control

Using status fields such as `ACTIVE`, `CONFIRMED`, and `CANCELLED` simplifies business rules and auditing.

## 10. Deployment View

```mermaid
flowchart TB
    Browser[User Web Browser]
    Cloud[Cloud Application Server]
    Database[(Managed Relational Database)]
    Messaging[Email/SMS Gateway]

    Browser --> Cloud
    Cloud --> Database
    Cloud --> Messaging
```

## 11. Risks and Mitigations

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Concurrent reservations for the same event | Oversold tickets | Use transactional updates and availability checks in the reservation service |
| Notification service outage | Missing confirmations | Retry failed messages and keep confirmation records in the database |
| Incorrect admin edits | Bad event data or cancelled bookings | Enforce role checks and maintain audit timestamps |
| High demand near event release times | Slow response times | Scale cloud services horizontally and optimize event queries |

## 12. Conclusion

The proposed architecture supports the project's functional goals while addressing the README's non-functional expectations for concurrency, availability, and usability. The design also provides the UML and database views expected for a complete system architecture submission.
