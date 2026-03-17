# Group Project: Cloud-based Ticket Reservation Application  
## PROJECT DESCRIPTION:  
In this course students are required to work on a software development  
and testing project and write a professional high-quality report.  
The main objection of the application is to develop a ticket booking for events such as movies, concerts, travel, or sports.  
The Ticket Reservation Application allows users to browse events, reserve tickets, and receive confirmations digitally.  
The programming language used in this project is Java.  
For frontend, we have used React
For backend, we have used Gradle and PostgreSQL
## INTENDED USERS
a. Customers (end users)  
b. Event organizers / administrators  

## FUNCTIONAL REQUIREMENTS
Users should be able to:  
a. register using email or phone number  
b. view a list of available events  
c. search and filter events by date, location, or category  
d. cancel reservations  
e. receive confirmations via email or SMS  
### Administrators should be able to:  
a. add new event  
b. edit an existing event  
c. cancel an event  
## NON-FUNCTIONAL REQUIREMENTS  
a. The system should support concurrent users without performance degradation  
b. The system should be cloud based that ensures high availability  
c. The UI should be simple and user-friendly  

## Run Locally

### Simplest Option

1. Copy the example environment file:
   `Copy-Item .env.dev.example.ps1 .env.dev.ps1`
2. Put the real database values into `.env.dev.ps1`
3. From the project root, run:
   `.\start-dev.ps1`

This opens:
- frontend on `http://127.0.0.1:5173`
- backend on `http://127.0.0.1:8081`

### Requirements

- Java 23
- Node.js and npm
