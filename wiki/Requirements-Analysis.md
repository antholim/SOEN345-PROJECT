# Requirements Analysis

## 1. Purpose

This document defines the requirements for the **Cloud-based Ticket Reservation Application** described in the project README. The goal is to provide a clear and justified set of functional and non-functional requirements for a system that allows customers to browse events, reserve tickets, cancel reservations, and receive digital confirmations, while administrators manage the event catalogue.

## 2. Project Context

The application is intended for:

- **Customers** who want to discover events and reserve tickets.
- **Administrators / event organizers** who create, update, and cancel events.

Based on the README and the provided database design, the system is assumed to be a **browser-based web application backed by a cloud-hosted service and relational database**.

## 3. Scope

### In Scope

- User registration and account management
- Event browsing, search, and filtering
- Ticket reservation and cancellation
- Digital confirmations through email or SMS
- Administrative management of events

### Out of Scope

- Payment gateway integration with third-party processors
- Seat-level allocation for reserved seating venues
- Refund processing with banking systems
- Analytics dashboards beyond operational reporting

## 4. Stakeholders

| Stakeholder | Interest in the System |
| --- | --- |
| Customers | Want a fast, simple, and reliable way to reserve tickets |
| Administrators | Need tools to publish, update, and cancel events accurately |
| Course team / evaluators | Need a complete, justified requirements baseline and system design |
| Notification providers | Deliver confirmations by email or SMS |
| System operators | Maintain availability, data integrity, and performance |

## 5. Assumptions and Constraints

- The client application is a browser-based web app with a simple, responsive user interface.
- Core business data is stored in a relational database.
- Event inventory is managed centrally by the cloud backend.
- Email and SMS confirmations are sent through external messaging services.
- Only authenticated administrators can create, edit, or cancel events.
- The system must remain easy to use for non-technical customers.

## 6. Functional Requirements

| ID | Requirement | Priority | Justification |
| --- | --- | --- | --- |
| FR-01 | The system shall allow a user to register with an email address or phone number. | Must | This is explicitly required in the README and is necessary to identify customers and send confirmations. |
| FR-02 | The system shall authenticate registered users before they can make or cancel reservations. | Must | Reservation records must be tied to a valid user account for accountability and confirmation delivery. |
| FR-03 | The system shall display a list of available events with title, category, venue, date, price, and remaining tickets. | Must | Customers need visibility into the event catalogue before making a reservation. |
| FR-04 | The system shall let users search and filter events by date, location, or category. | Must | This is explicitly stated in the README and improves findability when many events exist. |
| FR-05 | The system shall show a detailed event page containing description, date, venue, ticket price, and availability. | Must | Customers need enough information to decide whether to reserve tickets. |
| FR-06 | The system shall allow a customer to reserve one or more tickets for an active event. | Must | Ticket reservation is the main business goal of the application. |
| FR-07 | The system shall validate ticket availability before confirming a reservation and update the remaining ticket count. | Must | Prevents overselling and preserves data integrity under concurrent use. |
| FR-08 | The system shall calculate the total reservation cost based on ticket quantity and event price. | Must | The reservation record needs a reliable total for confirmation and future billing support. |
| FR-09 | The system shall allow customers to cancel an existing reservation. | Must | This is explicitly required in the README and supports common user workflows. |
| FR-10 | The system shall send a digital confirmation for successful reservations through email or SMS. | Must | This is explicitly required in the README and provides proof of reservation. |
| FR-11 | The system shall allow administrators to add new events. | Must | This is explicitly required for event lifecycle management. |
| FR-12 | The system shall allow administrators to edit existing event information. | Must | Administrators need to correct details such as time, price, or ticket availability. |
| FR-13 | The system shall allow administrators to cancel events and prevent new reservations for cancelled events. | Must | This is explicitly required and ensures customers do not reserve invalid events. |
| FR-14 | The system shall notify affected customers when an event they reserved is cancelled. | Should | This extends the README requirement for confirmations and is necessary for a complete cancellation flow. |
| FR-15 | The system shall maintain a history of reservation status changes such as confirmed and cancelled. | Should | This supports auditing, support requests, and accurate confirmation records. |

## 7. Functional Use Cases by Actor

### Customer Use Cases

- Create an account
- Log in
- Browse and search events
- View event details
- Reserve tickets
- Receive confirmation
- Cancel reservation

### Administrator Use Cases

- Log in with administrative privileges
- Add an event
- Edit event details
- Cancel an event
- Monitor reservation status for managed events

## 8. Business Rules

| ID | Rule |
| --- | --- |
| BR-01 | A reservation cannot be confirmed if the requested ticket quantity exceeds available tickets. |
| BR-02 | Only events with status `ACTIVE` can accept new reservations. |
| BR-03 | When a reservation is cancelled, the event's available ticket count must be restored. |
| BR-04 | Only users with the `ADMIN` role can create, edit, or cancel events. |
| BR-05 | Confirmation messages must reference an existing reservation record. |
| BR-06 | Each reservation belongs to exactly one user and one event. |

## 9. Non-Functional Requirements

| ID | Requirement | Measure / Acceptance Target | Justification |
| --- | --- | --- | --- |
| NFR-01 | Performance | Common operations such as browsing events, filtering, and opening event details should respond within 2 seconds under normal load. | The README requires an efficient user experience and support for multiple users. |
| NFR-02 | Concurrency | The system shall preserve correct ticket counts when multiple users reserve at the same time. | This is explicitly required in the README and is critical to avoid overselling. |
| NFR-03 | Availability | The cloud-based backend should target at least 99.5% service availability. | The README requires a cloud-based system with high availability. |
| NFR-04 | Usability | A first-time user should be able to browse events and complete a reservation without training. | The README requires a simple and user-friendly UI. |
| NFR-05 | Security | Passwords must be stored as hashes, and access to admin functions must require authorization. | The provided schema already includes `password_hash`, which implies secure credential handling. |
| NFR-06 | Reliability | Reservation, cancellation, and confirmation data must remain consistent even if a request fails midway. | Reservation systems require transactional integrity and recoverable operations. |
| NFR-07 | Scalability | The architecture should allow backend services and the database layer to scale as the number of users and events grows. | A cloud-based reservation system must support increased traffic during peak event demand. |
| NFR-08 | Maintainability | The system should separate UI, business logic, and persistence concerns to simplify updates and testing. | Clean architecture reduces long-term development cost and risk. |
| NFR-09 | Interoperability | The platform should support integration with email and SMS providers through service interfaces or APIs. | Digital confirmation is a core requirement and depends on external services. |
| NFR-10 | Auditability | Important actions such as reservations, cancellations, and event updates should be traceable through timestamps and status fields. | This supports support requests, troubleshooting, and accountability. |

## 10. Traceability Summary

| Requirement Area | Supported By |
| --- | --- |
| User onboarding | FR-01, FR-02, NFR-05 |
| Event discovery | FR-03, FR-04, FR-05, NFR-01, NFR-04 |
| Reservation flow | FR-06, FR-07, FR-08, FR-10, NFR-02, NFR-06 |
| Cancellation flow | FR-09, FR-13, FR-14, BR-03, NFR-10 |
| Administration | FR-11, FR-12, FR-13, BR-04, NFR-08 |
| Cloud operation | NFR-03, NFR-07, NFR-09 |

## 11. Acceptance Criteria Summary

- A customer can register with email or phone information and authenticate successfully.
- Event listings can be searched and filtered by date, location, and category.
- A valid reservation decreases available tickets and creates a confirmation record.
- A cancelled reservation restores ticket inventory and updates reservation status.
- An administrator can create, update, and cancel events.
- Cancelled events cannot accept new reservations.
- The UI remains simple enough for a first-time user to complete the main flow.
- The backend protects data integrity during concurrent reservation attempts.

## 12. Conclusion

The proposed requirements define a complete baseline for the ticket reservation project described in the README. They cover the core customer journey, the administrator workflow, the main business rules, and measurable quality attributes needed for a cloud-based reservation system.
